package com.bank.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import com.google.gson.annotations.Expose;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"address", "accounts", "services"})
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Expose
    private Long id;

    @Column(name = "full_name", nullable = false, length = 100)
    @Expose
    private String fullName;

    @Column(unique = true)
    @Expose
    private String email;

    @Expose
    private String phone;

    // One-to-One with Address
    @OneToOne(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Expose
    private Address address;

    // One-to-Many with Account
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Account> accounts = new ArrayList<>();

    // Many-to-Many with Service
    @ManyToMany
    @JoinTable(name = "customer_service",
            joinColumns = @JoinColumn(name = "customer_id"),
            inverseJoinColumns = @JoinColumn(name = "service_id"))
    @JsonIgnore
    private List<Service> services = new ArrayList<>();

    // Helper methods
    public void addAccount(Account account) {
        accounts.add(account);
        account.setCustomer(this);
    }

    public void addService(Service service) {
        services.add(service);
        service.getCustomers().add(this);
    }

    public void removeService(Service service) {
        services.remove(service);
        service.getCustomers().remove(this);
    }
}