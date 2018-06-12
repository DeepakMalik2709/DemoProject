package com.notes.nicefact.entity;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Transient;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

@Entity
public class BackendTask extends CommonEntity{

	private static final long serialVersionUID = 1L;

	@Basic
	String path;
	
	public enum BackendTaskStatus{QUEUED,RUNNING,  COMPLETED, FAILED};
	
	@Enumerated(EnumType.STRING)
	BackendTaskStatus status = BackendTaskStatus.QUEUED;
	
	@Lob @Basic(fetch = FetchType.LAZY)
	@Column(length=100000)
	private byte[] payload;
	
	@Basic
	private String params;
	
	@Transient
	private Map<String, Object> paramsMap;
	
	@Basic
	Integer retries;
	

	public Map<String, Object> getParamsMap() {
		if((null ==paramsMap || paramsMap.isEmpty()) && params !=null){
			paramsMap =  new JSONDeserializer< Map<String, Object>>().deserialize(params); // (Map<String, String>) SerializationUtils.deserialize(params);
		}
		if(null ==paramsMap){
			 paramsMap = new HashMap<>();
		}
		return paramsMap;
	}

	public Integer getRetries() {
		if(null == retries){
			return 0;
		}
		return retries;
	}
	
	public void incrementRetries(){
		this.retries = getRetries() + 1 ;
	}

	public void setRetries(Integer retries) {
		this.retries = retries;
	}

	public void addToParamsMap(String key, Object value) {
		getParamsMap().put(key, value);
	}

	public byte[] getPayload() {
		return payload;
	}

	public void setPayload(byte[] payload) {
		this.payload = payload;
	}


	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public BackendTaskStatus getStatus() {
		return status;
	}

	public void setStatus(BackendTaskStatus status) {
		this.status = status;
	}
	
	@PrePersist
	@PreUpdate
	void preUpdate(){
		if(null !=paramsMap && !paramsMap.isEmpty()){
			this.params = new JSONSerializer().exclude("class").serialize(paramsMap);
		}
	}

	@Override
	public String toString() {
		return "BackendTask [path=" + path + ", status=" + status + ", payload is null =" + (null == payload) + ", params=" + params + "]";
	}

}
