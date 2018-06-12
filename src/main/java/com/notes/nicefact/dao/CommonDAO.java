package com.notes.nicefact.dao;

import java.util.Collection;
import java.util.List;

/**
 * 
 *
 * @param <E>
 */
public interface CommonDAO<E> {

	/**
	 * Method to persist entity
	 * 
	 * @param entity
	 * @return
	 */
	public E upsert(E entity);

	/**
	 * Method to delete the entity for given key
	 * 
	 * @param clazz
	 * @param key
	 * @return
	 */
	public boolean remove(Long id);

	/**
	 * Method to Delete entity
	 * 
	 * @param entity
	 * @return
	 */
	public boolean remove(E entity);

	/**
	 * Method to create all given entities
	 * 
	 * @param entities
	 * @return
	 */
	public Collection<E> upsertAll(Collection<E> entities);

	/**
	 * Method to remove all given entities
	 * 
	 * @param object
	 * @return
	 */
	public boolean removeAll(Collection<E> object);

	public boolean softDeleteAll(long[] ids);

	public boolean softDelete(long id);

	/**
	 * Method to get given entity
	 * 
	 * @param type
	 * @param key
	 * @return
	 */
	public E get(Long id);

	public Object search();

	public int getTotalEntityCount();

	public List<E> getAll();

	public List<E> getByKeys(Collection<Long> keys);
}
