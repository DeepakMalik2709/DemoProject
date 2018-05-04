package com.notes.nicefact.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.notes.nicefact.util.CurrentContext;

/**
 * An abstract class a persistence Entity. All persistence entities need to
 * extend this class
 * 
 */

@MappedSuperclass
public abstract class CommonEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2083070150477071082L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected Long id;

	@Basic
	protected String createdBy;
	
	@Basic
	protected String createdByName;

	@Basic
	protected String updatedBy;
	
	@Basic
	protected String updatedByName;

	@Temporal(TemporalType.TIMESTAMP)
	protected Date createdTime;

	@Temporal(TemporalType.TIMESTAMP)
	protected Date updatedTime;

	@Basic
	protected Boolean isDeleted = false;

	@Basic
	protected Boolean isActive = true;

	/**
	 * @return the createdBy
	 */
	public String getCreatedBy() {
		return createdBy;
	}

	/**
	 * @param createdBy
	 *            the createdBy to set
	 */
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	/**
	 * @return the updatedBy
	 */
	public String getUpdatedBy() {
		return updatedBy;
	}

	/**
	 * @param updatedBy
	 *            the updatedBy to set
	 */
	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	/**
	 * @return the createdTime
	 */
	public Date getCreatedTime() {
		return createdTime;
	}

	public Boolean getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}


	/**
	 * @param createdTime
	 *            the createdTime to set
	 */
	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	/**
	 * @return the updatedTime
	 */
	public Date getUpdatedTime() {
		return updatedTime;
	}

	/**
	 * @param updatedTime
	 *            the updatedTime to set
	 */
	public void setUpdatedTime(Date updatedTime) {
		this.updatedTime = updatedTime;
	}


	
	public String getUpdatedByName() {
		return updatedByName;
	}

	public void setUpdatedByName(String updatedByName) {
		this.updatedByName = updatedByName;
	}

	@PrePersist
	@PreUpdate
	public void preStore() {
		Date today = new Date();
		if (createdTime == null) {
			createdTime = today;
			updatedTime = today;
			if(CurrentContext.getAppUser()!=null){
				updatedBy = CurrentContext.getEmail();
				createdBy = CurrentContext.getEmail();
				createdByName = CurrentContext.getAppUser().getDisplayName();
				updatedByName = CurrentContext.getAppUser().getDisplayName();
			}
		} else {
			updatedTime = today;
			if(CurrentContext.getAppUser()!=null){
				updatedBy = CurrentContext.getEmail();
				updatedByName = CurrentContext.getAppUser().getDisplayName();
			}
			
		}
	}

	public String getCreatedByName() {
		return createdByName;
	}

	public void setCreatedByName(String createdByName) {
		this.createdByName = createdByName;
	}

	public CommonEntity() {
		super();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

}
