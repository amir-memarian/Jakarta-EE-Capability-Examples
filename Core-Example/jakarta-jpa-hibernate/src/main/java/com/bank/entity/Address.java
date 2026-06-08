package com.bank.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "addresses")
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
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getZipCode() { return zipCode; }
    public void setZipCode(String zipCode) { this.zipCode = zipCode; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = (country != null && !country.isEmpty()) ? country : "ایران"; }

    public Customer getCustomer() { return customer; }

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