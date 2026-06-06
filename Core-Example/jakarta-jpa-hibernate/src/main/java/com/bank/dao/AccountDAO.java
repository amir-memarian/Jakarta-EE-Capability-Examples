package com.bank.dao;

import com.bank.entity.Account;
import com.bank.entity.Transaction;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

public class AccountDAO extends GenericDAO<Account, Long> {

    public AccountDAO() {
        super(Account.class);
    }

    public Optional<Account> findByAccountNumber(String accountNumber) {
        openEntityManager();
        try {
            TypedQuery<Account> query = em.createQuery(
                    "SELECT a FROM Account a WHERE a.accountNumber = :accNum", Account.class);
            query.setParameter("accNum", accountNumber);
            return query.getResultStream().findFirst();
        } finally {
            closeEntityManager();
        }
    }

    public List<Account> findByCustomerId(Long customerId) {
        openEntityManager();
        try {
            TypedQuery<Account> query = em.createQuery(
                    "SELECT a FROM Account a WHERE a.customer.id = :custId ORDER BY a.id DESC",
                    Account.class);
            query.setParameter("custId", customerId);
            return query.getResultList();
        } finally {
            closeEntityManager();
        }
    }

    public List<Account> findByAccountType(Account.AccountType accountType) {
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

    public List<Account> findActiveAccounts() {
        openEntityManager();
        try {
            return em.createQuery(
                            "SELECT a FROM Account a WHERE a.isActive = true", Account.class)
                    .getResultList();
        } finally {
            closeEntityManager();
        }
    }

    public Transaction deposit(Long accountId, Double amount, String description) {
        openEntityManager();
        try {
            em.getTransaction().begin();
            Account account = em.find(Account.class, accountId);
            if (account == null) {
                throw new RuntimeException("Account not found");
            }

            account.deposit(amount);
            em.merge(account);

            Transaction transaction = new Transaction(
                    amount,
                    Transaction.TransactionType.DEPOSIT,
                    description
            );
            transaction.setAccount(account);
            em.persist(transaction);

            em.getTransaction().commit();
            return transaction;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error depositing: " + e.getMessage(), e);
        } finally {
            closeEntityManager();
        }
    }

    public Transaction withdraw(Long accountId, Double amount, String description) {
        openEntityManager();
        try {
            em.getTransaction().begin();
            Account account = em.find(Account.class, accountId);
            if (account == null) {
                throw new RuntimeException("Account not found");
            }

            if (!account.withdraw(amount)) {
                throw new RuntimeException("Insufficient balance");
            }
            em.merge(account);

            Transaction transaction = new Transaction(
                    amount,
                    Transaction.TransactionType.WITHDRAWAL,
                    description
            );
            transaction.setAccount(account);
            em.persist(transaction);

            em.getTransaction().commit();
            return transaction;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error withdrawing: " + e.getMessage(), e);
        } finally {
            closeEntityManager();
        }
    }

    public Double getTotalBalanceByCustomer(Long customerId) {
        openEntityManager();
        try {
            return em.createQuery(
                            "SELECT COALESCE(SUM(a.balance), 0) FROM Account a WHERE a.customer.id = :custId",
                            Double.class)
                    .setParameter("custId", customerId)
                    .getSingleResult();
        } finally {
            closeEntityManager();
        }
    }
}