package com.bank.dao;

import com.bank.entity.Service;
import java.util.List;

public class ServiceDAO extends GenericDAO<Service, Long> {

    public ServiceDAO() {
        super(Service.class);
    }

    public Service findByName(String name) {
        openEntityManager();
        try {
            return em.createQuery("SELECT s FROM Service s WHERE s.name = :name", Service.class)
                    .setParameter("name", name)
                    .getSingleResult();
        } finally {
            closeEntityManager();
        }
    }

    public List<Service> findByCustomerId(Long customerId) {
        openEntityManager();
        try {
            return em.createQuery("SELECT s FROM Service s JOIN s.customers c WHERE c.id = :custId", Service.class)
                    .setParameter("custId", customerId)
                    .getResultList();
        } finally {
            closeEntityManager();
        }
    }
}