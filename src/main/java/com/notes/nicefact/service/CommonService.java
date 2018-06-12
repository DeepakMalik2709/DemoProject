package com.notes.nicefact.service;

import java.util.Collection;
import java.util.List;

import com.notes.nicefact.dao.CommonDAO;

public abstract class CommonService<E> {


	protected abstract CommonDAO<E> getDAO();
	
	public E upsert(E entity) {
		entity = getDAO().upsert(entity);
		return entity;
	}

	public boolean remove(Long id) {
		return getDAO().remove(id);
	}

	public boolean remove(E entity) {
		return getDAO().remove(entity);
	}

	public Collection<E> upsertAll(Collection<E> entities) {
		return getDAO().upsertAll(entities);
	}

	public boolean removeAll(Collection<E> object) {
		return getDAO().removeAll(object);
	}

	public boolean softDeleteAll(long[] ids) {
		return getDAO().softDeleteAll(ids);
	}

	public boolean softDelete(long id) {
		return getDAO().softDelete(id);
	}

	public E get(Long id) {
		return getDAO().get(id);
	}

	public Object search() {
		return getDAO().search();
	}

	public int getTotalEntityCount() {
		return getDAO().getTotalEntityCount();
	}

	public List<E> getAll(){
		return getDAO().getAll();
	}
	public List<E> getByKeys(Collection<Long> keys){
		return getDAO().getByKeys(keys);
	}
	
}
