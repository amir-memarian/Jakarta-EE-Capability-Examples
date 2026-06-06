package com.bank.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.annotations.Expose;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "accounts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"customer", "transactions"})
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Expose
    private Long id;

    @Column(unique = true, nullable = false)
    @Expose
    private String accountNumber;

    @Enumerated(EnumType.STRING)
    @Expose
    private AccountType accountType;

    @Expose
    private Double balance = 0.0;

    @Temporal(TemporalType.DATE)
    @Expose
    private Date openingDate;

    @Expose
    private Boolean isActive = true;

    // Many-to-One with Customer
    @ManyToOne
    @JoinColumn(name = "customer_id")
    @JsonIgnore
    private Customer customer;

    // One-to-Many with Transaction
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Transaction> transactions = new ArrayList<>();

    public enum AccountType {
        SAVINGS("حساب پس‌انداز"),
        CHECKING("حساب جاری"),
        BUSINESS("حساب تجاری");

        private final String persianName;

        AccountType(String persianName) {
            this.persianName = persianName;
        }

        public String getPersianName() {
            return persianName;
        }
    }

    public Account(String accountNumber, AccountType accountType, Double balance) {
        this.accountNumber = accountNumber;
        this.accountType = accountType;
        this.balance = balance;
        this.openingDate = new Date();
    }

    // Business methods
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
}