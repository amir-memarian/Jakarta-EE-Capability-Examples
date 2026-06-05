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

    private String name; // "Loan", "Investment", "Insurance"
    private String description;

    @ManyToMany(mappedBy = "services")
    private List<Customer> customers = new ArrayList<>();

    // Constructors, Getters, Setters

    public Service(Long id, String name, String description, List<Customer> customers) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.customers = customers;
    }

    public Service() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Customer> getCustomers() {
        return customers;
    }

    public void setCustomers(List<Customer> customers) {
        this.customers = customers;
    }
}