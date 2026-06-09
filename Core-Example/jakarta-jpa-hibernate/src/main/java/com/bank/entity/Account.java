package com.bank.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;


@Entity
@Table(name = "accounts")
@Getter
@Setter
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_number", unique = true, nullable = false, length = 20)
    private String accountNumber;

    @Column(name = "account_type")
    private String accountType; // "SAVINGS", "CHECKING", "BUSINESS"

    private Double balance = 0.0;

    @Column(name = "opening_date")
    private LocalDate openingDate;

    @Column(name = "is_active")
    private Boolean isActive = true;

    // رابطه Many-to-One با Customer
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    public Account() {
        this.openingDate = LocalDate.now();
    }

    public Account(String accountNumber, String accountType, Double balance) {
        this();
        this.accountNumber = accountNumber;
        this.accountType = accountType;
        this.balance = balance;
    }

    // Getters and Setters
    public void setCustomer(Customer customer) {
        this.customer = customer;
        if (customer != null && !customer.getAccounts().contains(this)) {
            customer.getAccounts().add(this);
        }
    }

    // متدهای کمکی
    public void deposit(Double amount) {
        this.balance += amount;
    }

    public boolean withdraw(Double amount) {
        if (this.balance >= amount) {
            this.balance -= amount;
            return true;
        }
        return false;
    }

    public String getAccountTypePersian() {
        switch (accountType) {
            case "SAVINGS": return "پس‌انداز";
            case "CHECKING": return "جاری";
            case "BUSINESS": return "تجاری";
            default: return accountType;
        }
    }

    @Override
    public String toString() {
        return "Account{id=" + id + ", number='" + accountNumber + "', type=" + accountType + ", balance=" + balance + "}";
    }
}