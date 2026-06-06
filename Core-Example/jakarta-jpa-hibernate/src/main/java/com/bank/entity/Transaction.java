package com.bank.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    private Date transactionDate;

    private Double amount;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    private String description;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    public enum TransactionType {
        DEPOSIT("واریز"),
        WITHDRAWAL("برداشت"),
        TRANSFER("انتقال");

        private final String persianName;

        TransactionType(String persianName) {
            this.persianName = persianName;
        }

        public String getPersianName() {
            return persianName;
        }
    }
    public Transaction(Double amount, TransactionType transactionType, String description) {
        this.amount = amount;
        this.transactionType = transactionType;
        this.description = description;
        this.transactionDate = new Date();
    }
}