package com.bank.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "addresses")
@Getter
@Setter
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 255)
    private String street;

    @Column(nullable = false, length = 100)
    private String city;

    @Column(length = 100)
    private String state;

    @Column(name = "zip_code", length = 20)
    private String zipCode;

    @Column(length = 100)
    private String country;

    @OneToOne
    @JoinColumn(name = "customer_id", unique = true)
    private Customer customer;

    public Address() {
        this.country = "ایران";
    }

    public Address(String street, String city, String state, String zipCode, String country) {
        this.street = street;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
        this.country = (country != null && !country.isEmpty()) ? country : "ایران";
    }

    // Getters and Setters
    public void setCustomer(Customer customer) {
        this.customer = customer;
        if (customer != null && customer.getAddress() != this) {
            customer.setAddress(this);
        }
    }

    public String getFullAddress() {
        StringBuilder sb = new StringBuilder();
        if (street != null && !street.isEmpty()) sb.append(street);
        if (city != null && !city.isEmpty()) {
            if (sb.length() > 0) sb.append("، ");
            sb.append(city);
        }
        if (state != null && !state.isEmpty()) {
            if (sb.length() > 0) sb.append("، ");
            sb.append(state);
        }
        if (country != null && !country.isEmpty()) {
            if (sb.length() > 0) sb.append("، ");
            sb.append(country);
        }
        return sb.length() > 0 ? sb.toString() : "آدرس ناقص";
    }

    public boolean isComplete() {
        return street != null && !street.isEmpty() && city != null && !city.isEmpty();
    }

    @Override
    public String toString() {
        return getFullAddress();
    }
}