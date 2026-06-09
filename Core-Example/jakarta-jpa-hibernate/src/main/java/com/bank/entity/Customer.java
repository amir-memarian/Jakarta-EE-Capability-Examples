package com.bank.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customers",
        uniqueConstraints = @UniqueConstraint(columnNames = "email"))
@Getter
@Setter
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(unique = true)
    private String email;

    private String phone;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToOne(mappedBy = "customer", cascade = CascadeType.ALL)
    private Address address;

    // رابطه One-to-Many با Account (جدید)
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Account> accounts = new ArrayList<>();


    public Customer() {
        this.createdAt = LocalDateTime.now();
    }

    public Customer(String fullName, String email, String phone) {
        this();
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
    }

    // Getters and Setters
    public void setAddress(Address address) {
        this.address = address;
        if (address != null && address.getCustomer() != this) {
            address.setCustomer(this);
        }
    }

    public List<Account> getAccounts() { return accounts; }
    public void setAccounts(List<Account> accounts) { this.accounts = accounts; }

    // متد کمکی برای اضافه کردن حساب
    public void addAccount(Account account) {
        accounts.add(account);
        account.setCustomer(this);
    }

    // متد کمکی برای حذف حساب
    public void removeAccount(Account account) {
        accounts.remove(account);
        account.setCustomer(null);
    }

    // متد کمکی برای گرفتن موجودی کل
    public Double getTotalBalance() {
        return accounts.stream()
                .filter(Account::getIsActive)
                .mapToDouble(Account::getBalance)
                .sum();
    }

    // متد کمکی برای تعداد حساب‌های فعال
    public int getActiveAccountsCount() {
        return (int) accounts.stream().filter(Account::getIsActive).count();
    }

    public String getFullAddress() {
        if (address == null) return "آدرسی ثبت نشده";
        return address.toString();
    }

    public boolean hasAddress() {
        return address != null;
    }

    @Override
    public String toString() {
        return "Customer{id=" + id + ", fullName='" + fullName + "', email='" + email + "'}";
    }
}