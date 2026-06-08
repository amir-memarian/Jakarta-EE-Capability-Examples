package com.bank.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "customers",
        uniqueConstraints = @UniqueConstraint(columnNames = "email"))
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

    @OneToOne(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Address address;

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
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Address getAddress() { return address; }

    public void setAddress(Address address) {
        this.address = address;
        if (address != null && address.getCustomer() != this) {
            address.setCustomer(this);
        }
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