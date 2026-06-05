package com.bank.dao;

import com.bank.entity.Account;
import jakarta.persistence.TypedQuery;
import java.util.List;

public class AccountDAO extends GenericDAO<Account, Long> {

    public AccountDAO() {
        super(Account.class);
    }

    public Account findByAccountNumber(String accountNumber) {
        openEntityManager();
        try {
            TypedQuery<Account> query = em.createQuery(
                    "SELECT a FROM Account a WHERE a.accountNumber = :accNum", Account.class);
            query.setParameter("accNum", accountNumber);
            return query.getSingleResult();
        } finally {
            closeEntityManager();
        }
    }

    public List<Account> findByCustomerId(Long customerId) {
        openEntityManager();
        try {
            TypedQuery<Account> query = em.createQuery(
                    "SELECT a FROM Account a WHERE a.customer.id = :custId", Account.class);
            query.setParameter("custId", customerId);
            return query.getResultList();
        } finally {
            closeEntityManager();
        }
    }

    public List<Account> findByAccountType(String accountType) {
        openEntityManager();
        try {
            TypedQuery<Account> query = em.createQuery(
                    "SELECT a FROM Account a WHERE a.accountType = :type", Account.class);
            query.setParameter("type", accountType);
            return query.getResultList();
        } finally {
            closeEntityManager();
        }
    }

    public void deposit(Long accountId, Double amount) {
        openEntityManager();
        try {
            em.getTransaction().begin();
            Account account = em.find(Account.class, accountId);
            if (account != null) {
                account.setBalance(account.getBalance() + amount);
                em.merge(account);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            closeEntityManager();
        }
    }

    public void withdraw(Long accountId, Double amount) {
        openEntityManager();
        try {
            em.getTransaction().begin();
            Account account = em.find(Account.class, accountId);
            if (account != null && account.getBalance() >= amount) {
                account.setBalance(account.getBalance() - amount);
                em.merge(account);
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