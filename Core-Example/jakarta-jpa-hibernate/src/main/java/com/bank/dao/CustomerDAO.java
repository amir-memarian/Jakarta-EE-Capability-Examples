package com.bank.dao;

import com.bank.entity.Customer;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

public class CustomerDAO extends GenericDAO<Customer, Long> {

    public CustomerDAO() {
        super(Customer.class);
    }

    public Optional<Customer> findByEmail(String email) {
        openEntityManager();
        try {
            TypedQuery<Customer> query = em.createQuery(
                    "SELECT c FROM Customer c WHERE c.email = :email", Customer.class);
            query.setParameter("email", email);
            return query.getResultStream().findFirst();
        } finally {
            closeEntityManager();
        }
    }

    public List<Customer> findByNameContaining(String name) {
        openEntityManager();
        try {
            TypedQuery<Customer> query = em.createQuery(
                    "SELECT c FROM Customer c WHERE c.fullName LIKE :name ORDER BY c.id DESC",
                    Customer.class);
            query.setParameter("name", "%" + name + "%");
            return query.getResultList();
        } finally {
            closeEntityManager();
        }
    }

    public List<Customer> findWithAccounts() {
        openEntityManager();
        try {
            return em.createQuery(
                    "SELECT DISTINCT c FROM Customer c LEFT JOIN FETCH c.accounts",
                    Customer.class).getResultList();
        } finally {
            closeEntityManager();
        }
    }

    public List<Customer> findWithServices() {
        openEntityManager();
        try {
            return em.createQuery(
                    "SELECT DISTINCT c FROM Customer c LEFT JOIN FETCH c.services",
                    Customer.class).getResultList();
        } finally {
            closeEntityManager();
        }
    }

    public boolean existsByEmail(String email) {
        openEntityManager();
        try {
            Long count = em.createQuery(
                            "SELECT COUNT(c) FROM Customer c WHERE c.email = :email", Long.class)
                    .setParameter("email", email)
                    .getSingleResult();
            return count > 0;
        } finally {
            closeEntityManager();
        }
    }
}