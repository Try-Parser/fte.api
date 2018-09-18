/**
 * 
 */
package fte.entity;

import java.io.Serializable;
import java.sql.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

/**
 * @author Try-Parser
 *
 */
@MappedSuperclass
public abstract class Defaults implements Serializable {
	
	private static final long serialVersionUID = 9098563142871729601L;

	@Id
	@GeneratedValue(generator="UUID")
	@GenericGenerator( name="UUID", strategy="org.hibernate.id.UUIDGenerator")
	@Column(name="ID", updatable = false, nullable = false)
	private UUID id;

	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="CREATED_AT", nullable = false, updatable=false)
	private Date created_at;

	@NotNull
	@Column(name="CREATED_BY",updatable = false)
	private Integer created_by;
	
	@UpdateTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="UPDATED_AT")
	private Date updated_at;
	
	@NotNull
	@Column(name="UPDATED_BY", nullable = false)
	private Integer updated_by;

	@Column(name="DELETED", columnDefinition = "boolean default false")
	private Boolean deleted = false;
	
	public Boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public Date getCreated_at() {
		return created_at;
	}

	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}

	public Integer getCreated_by() {
		return created_by;
	}

	public void setCreated_by(Integer created_by) {
		this.created_by = created_by;
	}

	public Date getUpdated_at() {
		return updated_at;
	}

	public void setUpdated_at(Date updated_at) {
		this.updated_at = updated_at;
	}

	public Integer getUpdated_by() {
		return updated_by;
	}

	public void setUpdated_by(Integer updated_by) {
		this.updated_by = updated_by;
	}
}
