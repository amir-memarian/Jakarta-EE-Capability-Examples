package com.bank.dao;

import com.bank.util.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

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
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error creating entity: " + e.getMessage(), e);
        } finally {
            closeEntityManager();
        }
    }

    public Optional<T> findById(ID id) {
        openEntityManager();
        try {
            return Optional.ofNullable(em.find(entityClass, id));
        } finally {
            closeEntityManager();
        }
    }

    public List<T> findAll() {
        openEntityManager();
        try {
            TypedQuery<T> query = em.createQuery(
                    "SELECT e FROM " + entityClass.getSimpleName() + " e ORDER BY e.id DESC",
                    entityClass);
            return query.getResultList();
        } finally {
            closeEntityManager();
        }
    }

    public List<T> findAll(int page, int size) {
        openEntityManager();
        try {
            TypedQuery<T> query = em.createQuery(
                    "SELECT e FROM " + entityClass.getSimpleName() + " e ORDER BY e.id DESC",
                    entityClass);
            query.setFirstResult(page * size);
            query.setMaxResults(size);
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
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error updating entity: " + e.getMessage(), e);
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
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error deleting entity: " + e.getMessage(), e);
        } finally {
            closeEntityManager();
        }
    }

    public long count() {
        openEntityManager();
        try {
            return em.createQuery(
                    "SELECT COUNT(e) FROM " + entityClass.getSimpleName() + " e",
                    Long.class).getSingleResult();
        } finally {
            closeEntityManager();
        }
    }

    public boolean exists(ID id) {
        return findById(id).isPresent();
    }
}