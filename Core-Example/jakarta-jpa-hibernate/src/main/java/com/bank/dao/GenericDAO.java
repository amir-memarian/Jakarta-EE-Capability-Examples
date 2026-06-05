package com.bank.dao;

import com.bank.util.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;

public abstract class GenericDAO<T, ID> {
    protected EntityManager em;
    private final Class<T> entityClass;

    public GenericDAO(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    protected void openEntityManager() {
        em = JpaUtil.getEntityManager();
    }

    protected void closeEntityManager() {
        if (em != null && em.isOpen()) {
            em.close();
        }
    }

    public T create(T entity) {
        openEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(entity);
            em.getTransaction().commit();
            return entity;
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            closeEntityManager();
        }
    }

    public T findById(ID id) {
        openEntityManager();
        try {
            return em.find(entityClass, id);
        } finally {
            closeEntityManager();
        }
    }

    public List<T> findAll() {
        openEntityManager();
        try {
            TypedQuery<T> query = em.createQuery("SELECT e FROM " + entityClass.getSimpleName() + " e", entityClass);
            return query.getResultList();
        } finally {
            closeEntityManager();
        }
    }

    public T update(T entity) {
        openEntityManager();
        try {
            em.getTransaction().begin();
            T merged = em.merge(entity);
            em.getTransaction().commit();
            return merged;
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            closeEntityManager();
        }
    }

    public void delete(ID id) {
        openEntityManager();
        try {
            em.getTransaction().begin();
            T entity = em.find(entityClass, id);
            if (entity != null) {
                em.remove(entity);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            closeEntityManager();
        }
    }
}