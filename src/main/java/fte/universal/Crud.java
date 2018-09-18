/**
 * 
 */
package fte.universal;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import fte.http.Page;
import fte.http.Response;

/**
 * @author Try-Parser
 *
 */
public interface Crud <T, I extends Serializable> {
	
	/**
	 * @param first offset 
	 * @param max length
	 * @return Page
	 * @see fte.http.Page
	 */
	public Page<T> paginate(int first, int max);
	
	/**
	 * @return List
	 */
	public List<T> get();
	
	/**
	 * @param id PrimaryKey
	 * @return Optional
	 */
	public Optional<T> get(I id);
	
	/**
	 * @param t inherited Class
	 * @return Response
	 * @see fte.http.Response
	 */
	public List<Response> saveOrUpdate(T t);
	
	/**
	 * @param id PrimaryKey
	 * @param flag boolean
	 * @return Boolean
	 */
	public List<Response> disable(Boolean flag, I id);
}
