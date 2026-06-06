package com.bank.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "services")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "customers")
public class Service {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    private String description;
    private Double fee;

    @ManyToMany(mappedBy = "services")
    private List<Customer> customers = new ArrayList<>();

    public Service(String name, String description, Double fee) {
        this.name = name;
        this.description = description;
        this.fee = fee;
    }
}