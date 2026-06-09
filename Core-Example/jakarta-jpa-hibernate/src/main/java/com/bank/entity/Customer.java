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

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Account> accounts = new ArrayList<>();

    // رابطه Many-to-Many با Service (جدید)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "customer_service",
            joinColumns = @JoinColumn(name = "customer_id"),
            inverseJoinColumns = @JoinColumn(name = "service_id")
    )
    private List<Service> services = new ArrayList<>();

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

    public void addAccount(Account account) {
        accounts.add(account);
        account.setCustomer(this);
    }

    public void removeAccount(Account account) {
        accounts.remove(account);
        account.setCustomer(null);
    }

    public Double getTotalBalance() {
        return accounts.stream()
                .filter(Account::getIsActive)
                .mapToDouble(Account::getBalance)
                .sum();
    }

    public int getActiveAccountsCount() {
        return (int) accounts.stream().filter(Account::getIsActive).count();
    }

    // متدهای کمکی برای Service (جدید)
    public void addService(Service service) {
        services.add(service);
        if (!service.getCustomers().contains(this)) {
            service.getCustomers().add(this);
        }
    }

    public void removeService(Service service) {
        services.remove(service);
        service.getCustomers().remove(this);
    }

    public String getServicesNames() {
        return services.stream()
                .map(Service::getName)
                .reduce((a, b) -> a + "، " + b)
                .orElse("هیچ");
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