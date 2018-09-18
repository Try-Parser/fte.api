/**
 * 
 */
package fte.universal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import fte.http.Error;
import fte.http.Page;
import fte.http.Response;
import fte.http.Success;

/**
 * @author Try-Parser
 *
 */
@Repository
@Transactional
public class CrudRepo<T extends Serializable, I extends Serializable> implements Crud<T, I> {

	public Class<T> type;

	@Autowired SessionFactory sessionFactory;    

	public Class<T> getType() {
        return type;
    }
	
	public Session getSessionFactory() {
		return sessionFactory.openSession();
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	/* (non-Javadoc)
	 * @see fte.universal.Crud#paginate(java.lang.Long, java.lang.Long)
	 * @param first offset of the Page.
	 * @param max maximum return length of the Page.
	 * @return Page<T> paginated serialize object.
	 * @see fte.http.Page
	 */
	@Override
	public Page<T> paginate(int first, int max) {
		Session session = this.getSessionFactory();
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<T> criteria = builder.createQuery(type);
		Root<T> root =  criteria.from(type);
		criteria.select(root).where(builder.equal(root.get("deleted"), false));
		Page<T> page = new Page<T>(); 
		
		CriteriaQuery<Long> total = builder.createQuery(Long.class);
		total.select(builder.count(total.from(type))).where(builder.equal(root.get("deleted"), false));

		page.setSuccess(true);
		page.setFirst(first);
		page.setMax(max);
		page.setResults(session.createQuery(criteria).setFirstResult(first).setMaxResults(max).getResultList());
		page.setTotal(session.createQuery(total).getSingleResult());
		session.close();
		return page;
	}

	/* (non-Javadoc)
	 * @see fte.universal.Crud#get()
	 * @return List<T> list of serialize object.
	 */
	@Override
	public List<T> get() {
		Session session = this.getSessionFactory();
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<T> criteria = builder.createQuery(type);
		Root<T> root = criteria.from(type);
		criteria.select(root);
		List<T> t = session.createQuery(criteria).getResultList();
		session.close();
		return t;
	}

	/* (non-Javadoc)
	 * @see fte.universal.Crud#get(java.lang.Object)
	 * @param id serializable type.
	 * @return Optional<T> optional serialize object. 
	 */
	@Override
	public Optional<T> get(I id) {
		Session session = this.getSessionFactory();
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<T> criteria = builder.createQuery(type);
		Root<T> root = criteria.from(type);
		criteria.select(root).where(builder.and(builder.equal(root.get("id"), id), builder.equal(root.get("deleted"), false)));
		
		Optional<T> single;
		
		try {
			single = Optional.of(session.createQuery(criteria).getSingleResult());
		} catch(NoResultException nre) {
			single = Optional.empty();
		}
		session.close();
		
		return single;
	}

	/* (non-Javadoc)
	 * @see fte.universal.Crud#saveOrUpdate()
	 * @param data serialize object.
	 * @return List<Response> Success or Errors.
	 * @see fte.http.Response 
	 * @see fte.http.Success
	 * @see fte.http.Errors
	 */
	@Override
	public List<Response> saveOrUpdate(T data) {
		Session session = this.getSessionFactory();
		Transaction tx = session.getTransaction();
		
		List<Response> response = new ArrayList<Response>();
		
		try {
			tx.begin();
			session.saveOrUpdate(data);
			tx.commit();
			response.add(new Success());
		} catch(ConstraintViolationException cve) {
			cve.getConstraintViolations().stream().forEach(o -> response.add(new Error().builder(o)));
			tx.rollback();
		} catch(PersistenceException e) {
			e.printStackTrace();
			Error err = new Error();
			err.setDefaultMessage(e.getLocalizedMessage());
			err.setCode("Error Duplicate");
			response.add(err);
		} finally {
			session.close();
		}
		
		return response;
	}

	/* (non-Javadoc)
	 * @see fte.universal.Crud#disable(java.lang.Boolean)
	 * @param flag Boolean
	 * @param id Generic
	 * @return Response List
	 * @see fte.http.Response
	 */
	@Override
	public List<Response> disable(Boolean flag, I id) {
		System.out.println("eeeeee");
		Session session = this.getSessionFactory();
		Transaction tx = session.getTransaction();
		
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaUpdate<T> criteria = builder.createCriteriaUpdate(type);
		
		Root<T> root = criteria.from(type); 
		criteria.set(root.get("deleted"), flag).where(builder.equal(root.get("id"), id));
		
		List<Response> response = new ArrayList<Response>();
		try {
			tx.begin();
			int sample = session.createQuery(criteria).executeUpdate();
			System.out.println(sample);
			tx.commit();
			response.add(new Success());
		} catch (Exception e) {
			e.printStackTrace();
			response.add(new Error());
			tx.rollback();
		} finally {
			session.close();
		}
		return response;
	}


}
