package com.bank.dao;

import com.bank.entity.Transaction;
import jakarta.persistence.TypedQuery;
import java.util.Date;
import java.util.List;

public class TransactionDAO extends GenericDAO<Transaction, Long> {

    public TransactionDAO() {
        super(Transaction.class);
    }

    public List<Transaction> findByAccountId(Long accountId) {
        openEntityManager();
        try {
            TypedQuery<Transaction> query = em.createQuery(
                    "SELECT t FROM Transaction t WHERE t.account.id = :accId ORDER BY t.transactionDate DESC",
                    Transaction.class);
            query.setParameter("accId", accountId);
            return query.getResultList();
        } finally {
            closeEntityManager();
        }
    }

    public List<Transaction> findByDateRange(Date startDate, Date endDate) {
        openEntityManager();
        try {
            TypedQuery<Transaction> query = em.createQuery(
                    "SELECT t FROM Transaction t WHERE t.transactionDate BETWEEN :start AND :end",
                    Transaction.class);
            query.setParameter("start", startDate);
            query.setParameter("end", endDate);
            return query.getResultList();
        } finally {
            closeEntityManager();
        }
    }
}