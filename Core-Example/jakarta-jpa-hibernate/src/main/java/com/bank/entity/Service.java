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

    @Column(unique = true, nullable = false, length = 50)
    private String name;

    @Column(length = 255)
    private String description;

    private Double fee;

    // رابطه Many-to-Many با Customer
    @ManyToMany(mappedBy = "services", fetch = FetchType.LAZY)
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

    // متد کمکی برای اضافه کردن مشتری
    public void addCustomer(Customer customer) {
        customers.add(customer);
        if (!customer.getServices().contains(this)) {
            customer.getServices().add(this);
        }
    }

    // متد کمکی برای حذف مشتری
    public void removeCustomer(Customer customer) {
        customers.remove(customer);
        customer.getServices().remove(this);
    }

    @Override
    public String toString() {
        return "Service{id=" + id + ", name='" + name + "', fee=" + fee + "}";
    }
}