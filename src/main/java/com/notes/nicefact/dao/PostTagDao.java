package com.notes.nicefact.dao;

import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.log4j.Logger;

import com.notes.nicefact.entity.Post;
import com.notes.nicefact.entity.PostTag;

public class PostTagDao {
	
	private final Logger logger = Logger.getLogger(getClass().getName());

	private EntityManager em;
	
	public PostTagDao(EntityManager em) {
		this.em = em;
	}

	public PostTag save(PostTag postTag) {
		EntityTransaction tx = em.getTransaction();
		try {

			tx.begin();
			em.persist(postTag);
			tx.commit();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			if (tx.isActive()) {
				tx.rollback();
			}
		} finally {
		}
		return postTag;
	}
	
	public List<PostTag> getByPostId(Long postId) {
		Query query = em.createQuery("select p from PostTag p where p.post.id = :ID");
		query.setParameter("ID", postId);
		List<PostTag> postTags = null;
		try {
			postTags = (List<PostTag>) query.getResultList();
			for (PostTag p : postTags) {
				em.detach(p);
			}
		} catch (NoResultException nre) {
			logger.warn(nre.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			
		}
		return postTags;
	}
	
	public boolean remove(PostTag postTag) {
		boolean success = false;
		try {
			em.getTransaction().begin();
			em.remove(postTag);
			em.getTransaction().commit();
			success = true;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			em.getTransaction().rollback();
		} finally {
			
		}
		return success;
	}
	
	public boolean removeAll(Collection<PostTag> postTags) {
		boolean success = false;
		try {
			em.getTransaction().begin();
			for(PostTag postTag : postTags){
				em.remove(postTag);
			}
			em.getTransaction().commit();
			success = true;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			em.getTransaction().rollback();
		} finally {
			
		}
		return success;
	}
}
