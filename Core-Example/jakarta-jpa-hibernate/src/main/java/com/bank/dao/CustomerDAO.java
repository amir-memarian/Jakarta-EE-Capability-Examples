package com.bank.dao;

import com.bank.entity.Customer;
import jakarta.persistence.TypedQuery;
import java.util.List;

public class CustomerDAO extends GenericDAO<Customer, Long> {

    public CustomerDAO() {
        super(Customer.class);
    }

    public Customer findByEmail(String email) {
        openEntityManager();
        try {
            TypedQuery<Customer> query = em.createQuery(
                    "SELECT c FROM Customer c WHERE c.email = :email", Customer.class);
            query.setParameter("email", email);
            return query.getSingleResult();
        } finally {
            closeEntityManager();
        }
    }

    public List<Customer> findByNameContaining(String name) {
        openEntityManager();
        try {
            TypedQuery<Customer> query = em.createQuery(
                    "SELECT c FROM Customer c WHERE c.fullName LIKE :name", Customer.class);
            query.setParameter("name", "%" + name + "%");
            return query.getResultList();
        } finally {
            closeEntityManager();
        }
    }
}