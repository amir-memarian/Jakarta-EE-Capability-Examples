package com.bank.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class JpaUtil {

    private static final EntityManagerFactory emf;

    static {
        System.out.println("=========================================");
        System.out.println("🔧 JPA Initialization Starting...");
        System.out.println("=========================================");

        EntityManagerFactory temp = null;
        try {
            System.out.println("1. Loading MySQL Driver...");
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("   ✅ Driver loaded");

            System.out.println("2. Creating EntityManagerFactory...");
            temp = Persistence.createEntityManagerFactory("bankPU");
            System.out.println("   ✅ EntityManagerFactory created");

            System.out.println("3. Testing connection...");
            EntityManager testEm = temp.createEntityManager();
            testEm.createNativeQuery("SELECT 1").getSingleResult();
            testEm.close();
            System.out.println("   ✅ Database connection successful");

            System.out.println("=========================================");
            System.out.println("✅ JPA Initialized SUCCESSFULLY!");
            System.out.println("=========================================");

        } catch (ClassNotFoundException e) {
            System.err.println("❌ MySQL Driver not found!");
            System.err.println("Add mysql-connector-java dependency to pom.xml");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("=========================================");
            System.err.println("❌ JPA Initialization FAILED!");
            System.err.println("Error: " + e.getMessage());
            System.err.println("=========================================");
            e.printStackTrace();
        }
        emf = temp;
    }

    public static EntityManager getEntityManager() {
        if (emf == null) {
            throw new RuntimeException("JPA not initialized! Check logs and persistence.xml");
        }
        return emf.createEntityManager();
    }

    public static void close() {
        if (emf != null && emf.isOpen()) {
            emf.close();
            System.out.println("✅ EntityManagerFactory closed.");
        }
    }
}