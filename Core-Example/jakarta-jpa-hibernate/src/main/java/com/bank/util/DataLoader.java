package com.bank.util;

import com.bank.entity.Service;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class DataLoader implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("=========================================");
        System.out.println("📀 Loading initial services data...");
        System.out.println("=========================================");

        EntityManager em = JpaUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            // بررسی وجود داده
            Long count = em.createQuery("SELECT COUNT(s) FROM Service s", Long.class)
                    .getSingleResult();

            if (count == 0) {
                tx.begin();
                System.out.println("Adding default services...");

                // ایجاد خدمات پیش‌فرض
                Service loan = new Service("وام شخصی", "دریافت وام با کارمزد پایین", 5.0);
                Service insurance = new Service("بیمه عمر", "پوشش کامل بیمه عمر", 10.0);
                Service investment = new Service("سرمایه‌گذاری", "مدیریت سرمایه در بازار سرمایه", 2.5);
                Service creditCard = new Service("کارت اعتباری", "کارت اعتباری پلاتینیوم", 0.0);
                Service mortgage = new Service("تسهیلات مسکن", "وام خرید مسکن", 3.5);
                Service exchange = new Service("تبدیل ارز", "تبدیل ارزهای خارجی", 1.0);

                em.persist(loan);
                em.persist(insurance);
                em.persist(investment);
                em.persist(creditCard);
                em.persist(mortgage);
                em.persist(exchange);

                tx.commit();
                System.out.println("✅ 6 default services added successfully!");
            } else {
                System.out.println("ℹ️ Services already exist. Skipping data load.");
            }
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            System.err.println("❌ Error loading data: " + e.getMessage());
        } finally {
            em.close();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("🛑 Application shutting down...");
    }
}