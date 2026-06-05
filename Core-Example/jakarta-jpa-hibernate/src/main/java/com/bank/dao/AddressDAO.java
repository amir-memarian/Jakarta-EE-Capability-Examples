package com.bank.dao;

import com.bank.entity.Address;

public class AddressDAO extends GenericDAO<Address, Long> {

    public AddressDAO() {
        super(Address.class);
    }

    public Address findByCustomerId(Long customerId) {
        openEntityManager();
        try {
            return em.createQuery("SELECT a FROM Address a WHERE a.customer.id = :custId", Address.class)
                    .setParameter("custId", customerId)
                    .getSingleResult();
        } finally {
            closeEntityManager();
        }
    }
}