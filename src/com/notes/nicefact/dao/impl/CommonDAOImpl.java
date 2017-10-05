package com.notes.nicefact.dao.impl;

/**
 *
 */

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.log4j.Logger;

import com.notes.nicefact.dao.CommonDAO;
import com.notes.nicefact.entity.CommonEntity;
import com.notes.nicefact.util.CurrentContext;
import com.notes.nicefact.util.GridRequestParser;
import com.notes.nicefact.util.Pagination;

/**
 * CommonDaoImpl is an abstract class and it perform common CRUD operation and
 * all DAO need to extend this class
 * 
 * @author Dheeraj Kumar
 * 
 * @param <E>
 */
public abstract class CommonDAOImpl<E extends CommonEntity> implements CommonDAO<E> {

	protected final Logger log = Logger.getLogger(getClass().getName());

	protected Class<E> clazz;
	
	EntityManager em;

	public EntityManager getEntityManager() {
		//em = EntityManagerHelper.getDefaulteEntityManager();
		return em;
	}

	public CommonDAOImpl(EntityManager em) {
		this.em = em;
		Type type = getClass().getGenericSuperclass();
		if (type instanceof ParameterizedType) {
			Type[] types = ((ParameterizedType) type).getActualTypeArguments();
			clazz = (Class<E>) types[0];
		}
	}

	/**
	 * Create an object in the persistence storage.
	 * 
	 * @param object
	 *            to persist.
	 * @return persisted object.
	 */

	public E upsert(E object) {
		EntityManager pm = getEntityManager();
		EntityTransaction tx = pm.getTransaction();
		try {

			tx.begin();
			if (object.getId() == null || object.getId() < 0) {
				pm.persist(object);
			} else {
				object = pm.merge(object);
			}
			tx.commit();
		} catch (Exception e) {
			e.printStackTrace();
			if (tx.isActive()) {
				tx.rollback();
			}
		} finally {
			//
		}
		return object;

	}

	/**
	 * Create objects at once in the persistence storage.
	 * 
	 * @param entities
	 *            the collection objects to persist.
	 * @return collection of persisted objects.
	 */

	public Collection<E> upsertAll(Collection<E> entities) {
		log.info("Create method started for all entities in collection.");
		EntityManager pm = getEntityManager();
		try {
			pm.getTransaction().begin();
			for (E object : entities) {
				if (object.getId() == null) {
					pm.persist(object);
				} else {
					pm.merge(object);
				}
			}
			pm.getTransaction().commit();
		} catch (Exception e) {
			e.printStackTrace();
			pm.getTransaction().rollback();
		} finally {
			
		}
		return entities;
	}

	public boolean remove(Long id) {
		boolean success = false;
		EntityManager pm = getEntityManager();
		try {
			pm.getTransaction().begin();
			E e = pm.find(clazz, id);
			pm.remove(e);
			pm.getTransaction().commit();
			success = true;
		} catch (Exception e) {
			e.printStackTrace();
			pm.getTransaction().rollback();
		} finally {
			
		}
		return success;
	}

	/**
	 * Remove an object from the persistence storage, Big Table in this case.
	 * 
	 * @param E
	 *            to remove.
	 */
	public boolean remove(E object) {
		boolean success = false;
		EntityManager pm = getEntityManager();
		try {
			pm.getTransaction().begin();
			pm.remove(object);
			pm.getTransaction().commit();
			success = true;
		} catch (Exception e) {
			e.printStackTrace();
			pm.getTransaction().rollback();
		} finally {
			
		}
		return success;
	}

	// TODO
	public boolean removeAll(Collection<E> object) {
		boolean success = false;
		/*
		 * EntityManager pm = getEntityManager(); try {
		 * pm.deletePersistentAll(object); success = true; } finally {
		 *  }
		 */
		return success;
	}

	public boolean softDeleteAll(List<Long> ids) {
		boolean success = true;
		E object = null;
		EntityManager pm = getEntityManager();
		pm.getTransaction().begin();
		try {
			for (Long id : ids) {
				object = (E) pm.find(clazz, id);
				object.setIsDeleted(true);
				pm.merge(object);
			}
			pm.getTransaction().commit();
		} catch (NoResultException nre) {
			success = false;
		} catch (Exception e) {
			success = false;
			pm.getTransaction().rollback();
			e.printStackTrace();
		} finally {
			
		}
		return success;
	}

	public boolean softDelete(long id) {
		boolean success = true;
		E object = null;
		EntityManager pm = getEntityManager();
		pm.getTransaction().begin();
		try {
			object = (E) pm.find(clazz, id);
			object.setIsDeleted(true);
			pm.merge(object);
			pm.getTransaction().commit();
		} catch (NoResultException nre) {
			success = false;
		} catch (Exception e) {
			success = false;
			pm.getTransaction().rollback();
			e.printStackTrace();
		} finally {
			
		}
		return success;
	}

	public E get(Long id) {
		E object = null;
		EntityManager pm = getEntityManager();
		try {
			object = (E) pm.find(clazz, id);
		} catch (NoResultException nre) {
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			//
		}
		return object;

	}


	public int getTotalEntityCount() {
		int count = 0;
		EntityManager pm = getEntityManager();
		try {

			Query query = pm.createQuery("select count(*) from " + this.clazz.getSimpleName() + " a where  a.isDeleted = :isDeleted");
			query.setParameter("isDeleted", false);
			Number result = (Number) query.getSingleResult();
			count = result.intValue();
		} catch (NoResultException nre) {
		} finally {
			
		}
		return count;
	}

	public Object search() {
		Object results = null;
		EntityManager pm = getEntityManager();

		String queryStr = GridRequestParser.paginationToSearchQuery(this.clazz.getSimpleName());
		Pagination pagination = CurrentContext.getPagination();
		Query query = pm.createQuery(queryStr);
		query.setFirstResult(pagination.getOffset());
		query.setMaxResults(pagination.getLimit());
		try {
			results = query.getResultList();
		} catch (NoResultException nre) {
			log.warn("0 result for : " + queryStr);
		} finally {
			
		}
		return results;
	}

	public E getActiveByField(String fieldName, Object fieldValue) {
		EntityManager pm = getEntityManager();
		Query query = pm.createQuery("select h from " + this.clazz.getSimpleName() + " h where h." + fieldName + " = :param and isActive = true and isDeleted = false");
		query.setParameter("param", fieldValue);
		query.setMaxResults(1);
		E obj = null;
		try {
			obj = (E) query.getSingleResult();
			pm.detach(obj);
		} catch (NoResultException nre) {
			log.warn(nre.getMessage());
		} catch (Exception e) {
			log.warn(e.getMessage());
		} finally {
			
		}
		return obj;
	}

	public List<E> getActiveListByField(String fieldName, Object fieldValue, int start, int max, String sort) {
		EntityManager pm = getEntityManager();
		Query query = pm.createQuery("select h from " + this.clazz.getSimpleName() + " h where h." + fieldName + " = :param and isActive = true and isDeleted = false order by " + sort);
		query.setParameter("param", fieldValue);
		query.setFirstResult(start);
		query.setMaxResults(max);
		List<E> objects = null;
		try {
			objects = (List<E>) query.getResultList();
			for (E e : objects) {
				pm.detach(e);
			}

		} catch (NoResultException nre) {
			log.warn(nre.getMessage());
			objects = new ArrayList<>();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			
		}
		return objects;
	}

	public E getByField(String fieldName, Object fieldValue) {
		EntityManager pm = getEntityManager();
		Query query = pm.createQuery("select h from " + this.clazz.getSimpleName() + " h where h." + fieldName + " = :param and isDeleted = false");
		query.setParameter("param", fieldValue);
		query.setMaxResults(1);
		E obj = null;
		try {
			obj = (E) query.getSingleResult();
			pm.detach(obj);
		} catch (NoResultException nre) {
			log.warn(nre.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			
		}
		return obj;
	}

	/** get list regardless of active or deleted */
	public List<E> getListByField(String fieldName, Object fieldValue, int start, int max, String sort) {
		EntityManager pm = getEntityManager();
		Query query = pm.createQuery("select h from " + this.clazz.getSimpleName() + " h where h." + fieldName + " = :param order by " + sort);
		query.setParameter("param", fieldValue);
		query.setFirstResult(start);
		query.setMaxResults(max);
		List<E> objects = null;
		try {
			objects = (List<E>) query.getResultList();
			for (E e : objects) {
				pm.detach(e);
			}

		} catch (NoResultException nre) {
			log.warn(nre.getMessage());
			objects = new ArrayList<>();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			
		}
		return objects;
	}

	public List<E> getActiveListByList(String fieldName, Collection fieldValues, int start, int max, String sort) {
		EntityManager pm = getEntityManager();
		Query query = pm.createQuery("select h from " + this.clazz.getSimpleName() + " h where h." + fieldName + " IN (:param) and isActive = true and isDeleted = false");
		query.setParameter("param", (ArrayList<Long>) fieldValues);
		query.setFirstResult(start);
		query.setMaxResults(max);
		List<E> objects = null;
		try {
			objects = (List<E>) query.getResultList();
			for (E e : objects) {
				pm.detach(e);
			}

		} catch (NoResultException nre) {
			log.warn(nre.getMessage());
			objects = new ArrayList<>();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			
		}
		return objects;
	}
	public boolean softDeleteAll(long[] ids) {
		boolean success = true;
		E object = null;
		EntityManager pm = getEntityManager();
		pm.getTransaction().begin();
		try {
			for (long id : ids) {
				object = (E) pm.find(clazz, id);
				object.setIsDeleted(true);
				pm.merge(object);
			}
			pm.getTransaction().commit();
		} catch (NoResultException nre) {
			log.warn(nre.getMessage());
			success = false;
		} catch (Exception e) {
			success = false;
			pm.getTransaction().rollback();
			log.error(e.getMessage(), e);
		} finally {
			
		}
		return success;
	}
	public List<E> getAll() {
		 List<E> list;
		int count = 0;
		EntityManager pm = getEntityManager();
		try {

			Query query = pm
					.createQuery("select a from "
							+ this.clazz.getSimpleName() + " a");
			list = query.getResultList();
		} catch (NoResultException nre) {
			list = new ArrayList<>();
			log.warn(nre.getMessage());
		} finally {
			
		}
		return list;
	}
	
	public List<E> getByKeys(Collection<Long> keys) {
		EntityManager pm = getEntityManager();
		Query query = pm.createQuery("select h from " + this.clazz.getSimpleName() + " h where h.id in (:keys)");
		query.setParameter("keys", keys);
		List<E> objs = null;
		try {
			objs = (List<E>) query.getResultList();
			for (E obj : objs) {
				em.detach(obj);
			}
		} catch (NoResultException nre) {
			log.warn(nre.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			
		}
		return objs;
	}
}