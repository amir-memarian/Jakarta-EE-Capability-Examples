package com.bank.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "services")
public class Service {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name; // "LOAN", "INSURANCE", "INVESTMENT", "CREDIT_CARD"

    private String description;

    private Double fee;

    // Many-to-Many با Customer
    @ManyToMany(mappedBy = "services")
    private List<Customer> customers = new ArrayList<>();

    // Constructors
    public Service() {}

    public Service(String name, String description, Double fee) {
        this.name = name;
        this.description = description;
        this.fee = fee;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getFee() { return fee; }
    public void setFee(Double fee) { this.fee = fee; }

    public List<Customer> getCustomers() { return customers; }
    public void setCustomers(List<Customer> customers) { this.customers = customers; }
}