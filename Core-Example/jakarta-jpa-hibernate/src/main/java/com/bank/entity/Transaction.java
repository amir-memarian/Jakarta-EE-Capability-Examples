package com.bank.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    private Date transactionDate;
    private Double amount;
    private String description;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    // Constructors, Getters, Setters

    public Transaction(Long id, Date transactionDate, Double amount, String description, Account account) {
        this.id = id;
        this.transactionDate = transactionDate;
        this.amount = amount;
        this.description = description;
        this.account = account;
    }

    public Transaction() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}