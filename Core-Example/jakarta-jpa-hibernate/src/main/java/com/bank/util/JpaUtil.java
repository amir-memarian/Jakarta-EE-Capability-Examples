package com.bank.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class JpaUtil {
    private static final EntityManagerFactory emf;

    static {
        System.out.println("=== Initializing JpaUtil ===");
        try {
            System.out.println("Creating EntityManagerFactory...");
            emf = Persistence.createEntityManagerFactory("bankPU");
            System.out.println("EntityManagerFactory created successfully!");
        } catch (Throwable ex) {
            System.err.println("!!! FAILED to create EntityManagerFactory !!!");
            System.err.println("Error: " + ex.getMessage());
            ex.printStackTrace();
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static EntityManager getEntityManager() {
        if (emf == null) {
            throw new IllegalStateException("EntityManagerFactory is null!");
        }
        return emf.createEntityManager();
    }

    public static void close() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}