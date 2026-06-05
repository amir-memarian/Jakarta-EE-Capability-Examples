package com.bank.servlet;

import com.bank.entity.Customer;
import com.bank.entity.Address;
import com.bank.entity.Service;
import com.bank.util.JpaUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/api/customers/*")
public class CustomerServlet extends HttpServlet {

    private Gson gson;

    @Override
    public void init() throws ServletException {
        super.init();
        gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        System.out.println("✅ CustomerServlet initialized at: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }

    // ======================== GET ========================
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        String pathInfo = req.getPathInfo();
        EntityManager em = JpaUtil.getEntityManager();

        try {
            // GET /api/customers - لیست همه مشتریان
            if (pathInfo == null || pathInfo.equals("/")) {
                String searchName = req.getParameter("search");
                List<Customer> customers;

                if (searchName != null && !searchName.trim().isEmpty()) {
                    // جستجو بر اساس نام
                    TypedQuery<Customer> query = em.createQuery(
                            "SELECT c FROM Customer c WHERE c.fullName LIKE :name",
                            Customer.class);
                    query.setParameter("name", "%" + searchName + "%");
                    customers = query.getResultList();
                } else {
                    // دریافت همه مشتریان
                    TypedQuery<Customer> query = em.createQuery(
                            "SELECT c FROM Customer c ORDER BY c.id DESC",
                            Customer.class);
                    customers = query.getResultList();
                }

                // حذف روابط حلقوی برای JSON
                customers.forEach(c -> {
                    if (c.getAccounts() != null) c.getAccounts().size();
                    if (c.getServices() != null) c.getServices().size();
                });

                resp.setStatus(HttpServletResponse.SC_OK);
                out.print(gson.toJson(customers));

                // GET /api/customers/{id} - دریافت یک مشتری با ID
            } else if (pathInfo.matches("/\\d+")) {
                Long id = Long.parseLong(pathInfo.substring(1));
                Customer customer = em.find(Customer.class, id);

                if (customer != null) {
                    resp.setStatus(HttpServletResponse.SC_OK);
                    out.print(gson.toJson(customer));
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    Map<String, String> error = new HashMap<>();
                    error.put("error", "Customer with id " + id + " not found");
                    out.print(gson.toJson(error));
                }

                // GET /api/customers/email/{email} - جستجو با ایمیل
            } else if (pathInfo.startsWith("/email/")) {
                String email = pathInfo.substring(7);
                TypedQuery<Customer> query = em.createQuery(
                        "SELECT c FROM Customer c WHERE c.email = :email",
                        Customer.class);
                query.setParameter("email", email);

                List<Customer> customers = query.getResultList();
                if (!customers.isEmpty()) {
                    resp.setStatus(HttpServletResponse.SC_OK);
                    out.print(gson.toJson(customers.get(0)));
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    Map<String, String> error = new HashMap<>();
                    error.put("error", "Customer with email " + email + " not found");
                    out.print(gson.toJson(error));
                }

                // GET /api/customers/{id}/accounts - دریافت حساب‌های یک مشتری
            } else if (pathInfo.matches("/\\d+/accounts")) {
                String[] parts = pathInfo.split("/");
                Long customerId = Long.parseLong(parts[1]);

                Customer customer = em.find(Customer.class, customerId);
                if (customer != null) {
                    resp.setStatus(HttpServletResponse.SC_OK);
                    out.print(gson.toJson(customer.getAccounts()));
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    Map<String, String> error = new HashMap<>();
                    error.put("error", "Customer not found");
                    out.print(gson.toJson(error));
                }

                // GET /api/customers/{id}/services - دریافت خدمات یک مشتری
            } else if (pathInfo.matches("/\\d+/services")) {
                String[] parts = pathInfo.split("/");
                Long customerId = Long.parseLong(parts[1]);

                Customer customer = em.find(Customer.class, customerId);
                if (customer != null) {
                    resp.setStatus(HttpServletResponse.SC_OK);
                    out.print(gson.toJson(customer.getServices()));
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    Map<String, String> error = new HashMap<>();
                    error.put("error", "Customer not found");
                    out.print(gson.toJson(error));
                }

                // GET /api/customers/stats/summary - آمار کلی
            } else if (pathInfo.equals("/stats/summary")) {
                Long totalCustomers = em.createQuery(
                        "SELECT COUNT(c) FROM Customer c", Long.class).getSingleResult();

                Double averageAccounts = em.createQuery(
                        "SELECT AVG(SIZE(c.accounts)) FROM Customer c", Double.class).getSingleResult();

                Map<String, Object> stats = new HashMap<>();
                stats.put("totalCustomers", totalCustomers);
                stats.put("averageAccountsPerCustomer", averageAccounts);
                stats.put("serverTime", LocalDateTime.now().toString());

                resp.setStatus(HttpServletResponse.SC_OK);
                out.print(gson.toJson(stats));

            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                Map<String, String> error = new HashMap<>();
                error.put("error", "Invalid endpoint");
                out.print(gson.toJson(error));
            }

        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            out.print(gson.toJson(error));
            e.printStackTrace();
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    // ======================== POST (CREATE) ========================
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        String pathInfo = req.getPathInfo();
        EntityManager em = JpaUtil.getEntityManager();
        EntityTransaction transaction = em.getTransaction();

        try {
            // POST /api/customers - ایجاد مشتری جدید
            if (pathInfo == null || pathInfo.equals("/")) {
                // خواندن JSON از بدنه درخواست
                BufferedReader reader = req.getReader();
                Customer customer = gson.fromJson(reader, Customer.class);

                // اعتبارسنجی
                if (customer.getFullName() == null || customer.getFullName().trim().isEmpty()) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    Map<String, String> error = new HashMap<>();
                    error.put("error", "Full name is required");
                    out.print(gson.toJson(error));
                    return;
                }

                // بررسی تکراری نبودن ایمیل
                if (customer.getEmail() != null && !customer.getEmail().isEmpty()) {
                    TypedQuery<Long> query = em.createQuery(
                            "SELECT COUNT(c) FROM Customer c WHERE c.email = :email", Long.class);
                    query.setParameter("email", customer.getEmail());
                    Long count = query.getSingleResult();

                    if (count > 0) {
                        resp.setStatus(HttpServletResponse.SC_CONFLICT);
                        Map<String, String> error = new HashMap<>();
                        error.put("error", "Email already exists");
                        out.print(gson.toJson(error));
                        return;
                    }
                }

                transaction.begin();
                em.persist(customer);
                transaction.commit();

                resp.setStatus(HttpServletResponse.SC_CREATED);
                out.print(gson.toJson(customer));

                // POST /api/customers/{id}/address - افزودن آدرس به مشتری
            } else if (pathInfo.matches("/\\d+/address")) {
                String[] parts = pathInfo.split("/");
                Long customerId = Long.parseLong(parts[1]);

                BufferedReader reader = req.getReader();
                Address address = gson.fromJson(reader, Address.class);

                transaction.begin();
                Customer customer = em.find(Customer.class, customerId);

                if (customer == null) {
                    transaction.rollback();
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    Map<String, String> error = new HashMap<>();
                    error.put("error", "Customer not found");
                    out.print(gson.toJson(error));
                    return;
                }

                address.setCustomer(customer);
                em.persist(address);
                customer.setAddress(address);
                em.merge(customer);
                transaction.commit();

                resp.setStatus(HttpServletResponse.SC_CREATED);
                out.print(gson.toJson(address));

                // POST /api/customers/services - اختصاص سرویس به مشتری
            } else if (pathInfo.equals("/services")) {
                BufferedReader reader = req.getReader();
                JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);

                Long customerId = jsonObject.get("customerId").getAsLong();
                Long serviceId = jsonObject.get("serviceId").getAsLong();

                transaction.begin();
                Customer customer = em.find(Customer.class, customerId);
                Service service = em.find(Service.class, serviceId);

                if (customer == null || service == null) {
                    transaction.rollback();
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    Map<String, String> error = new HashMap<>();
                    error.put("error", "Customer or Service not found");
                    out.print(gson.toJson(error));
                    return;
                }

                customer.getServices().add(service);
                service.getCustomers().add(customer);
                em.merge(customer);
                em.merge(service);
                transaction.commit();

                Map<String, String> response = new HashMap<>();
                response.put("message", "Service assigned successfully");
                resp.setStatus(HttpServletResponse.SC_OK);
                out.print(gson.toJson(response));

            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                Map<String, String> error = new HashMap<>();
                error.put("error", "Invalid endpoint");
                out.print(gson.toJson(error));
            }

        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            out.print(gson.toJson(error));
            e.printStackTrace();
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    // ======================== PUT (UPDATE) ========================
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        String pathInfo = req.getPathInfo();
        EntityManager em = JpaUtil.getEntityManager();
        EntityTransaction transaction = em.getTransaction();

        try {
            // PUT /api/customers/{id} - ویرایش کامل مشتری
            if (pathInfo != null && pathInfo.matches("/\\d+")) {
                Long id = Long.parseLong(pathInfo.substring(1));

                BufferedReader reader = req.getReader();
                Customer updatedCustomer = gson.fromJson(reader, Customer.class);

                transaction.begin();
                Customer existingCustomer = em.find(Customer.class, id);

                if (existingCustomer == null) {
                    transaction.rollback();
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    Map<String, String> error = new HashMap<>();
                    error.put("error", "Customer not found");
                    out.print(gson.toJson(error));
                    return;
                }

                // به‌روزرسانی فیلدها
                if (updatedCustomer.getFullName() != null) {
                    existingCustomer.setFullName(updatedCustomer.getFullName());
                }
                if (updatedCustomer.getEmail() != null) {
                    existingCustomer.setEmail(updatedCustomer.getEmail());
                }
                if (updatedCustomer.getPhone() != null) {
                    existingCustomer.setPhone(updatedCustomer.getPhone());
                }

                em.merge(existingCustomer);
                transaction.commit();

                resp.setStatus(HttpServletResponse.SC_OK);
                out.print(gson.toJson(existingCustomer));

            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                Map<String, String> error = new HashMap<>();
                error.put("error", "Invalid endpoint. Use /api/customers/{id}");
                out.print(gson.toJson(error));
            }

        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            out.print(gson.toJson(error));
            e.printStackTrace();
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    // ======================== PATCH (PARTIAL UPDATE) ========================
    @Override
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        String pathInfo = req.getPathInfo();
        EntityManager em = JpaUtil.getEntityManager();
        EntityTransaction transaction = em.getTransaction();

        try {
            // PATCH /api/customers/{id} - به‌روزرسانی جزئی
            if (pathInfo != null && pathInfo.matches("/\\d+")) {
                Long id = Long.parseLong(pathInfo.substring(1));

                BufferedReader reader = req.getReader();
                JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);

                transaction.begin();
                Customer customer = em.find(Customer.class, id);

                if (customer == null) {
                    transaction.rollback();
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    Map<String, String> error = new HashMap<>();
                    error.put("error", "Customer not found");
                    out.print(gson.toJson(error));
                    return;
                }

                // به‌روزرسانی فقط فیلدهای ارسال شده
                if (jsonObject.has("fullName")) {
                    customer.setFullName(jsonObject.get("fullName").getAsString());
                }
                if (jsonObject.has("email")) {
                    customer.setEmail(jsonObject.get("email").getAsString());
                }
                if (jsonObject.has("phone")) {
                    customer.setPhone(jsonObject.get("phone").getAsString());
                }

                em.merge(customer);
                transaction.commit();

                resp.setStatus(HttpServletResponse.SC_OK);
                out.print(gson.toJson(customer));

            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                Map<String, String> error = new HashMap<>();
                error.put("error", "Invalid endpoint");
                out.print(gson.toJson(error));
            }

        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            out.print(gson.toJson(error));
            e.printStackTrace();
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    // ======================== DELETE ========================
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        String pathInfo = req.getPathInfo();
        EntityManager em = JpaUtil.getEntityManager();
        EntityTransaction transaction = em.getTransaction();

        try {
            // DELETE /api/customers/{id} - حذف مشتری
            if (pathInfo != null && pathInfo.matches("/\\d+")) {
                Long id = Long.parseLong(pathInfo.substring(1));

                transaction.begin();
                Customer customer = em.find(Customer.class, id);

                if (customer == null) {
                    transaction.rollback();
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    Map<String, String> error = new HashMap<>();
                    error.put("error", "Customer not found");
                    out.print(gson.toJson(error));
                    return;
                }

                // حذف ارتباطات قبل از حذف مشتری
                if (customer.getAddress() != null) {
                    em.remove(customer.getAddress());
                }

                em.remove(customer);
                transaction.commit();

                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);

            } else if (pathInfo != null && pathInfo.matches("/\\d+/services/\\d+")) {
                // DELETE /api/customers/{customerId}/services/{serviceId} - حذف سرویس از مشتری
                String[] parts = pathInfo.split("/");
                Long customerId = Long.parseLong(parts[1]);
                Long serviceId = Long.parseLong(parts[3]);

                transaction.begin();
                Customer customer = em.find(Customer.class, customerId);
                Service service = em.find(Service.class, serviceId);

                if (customer == null || service == null) {
                    transaction.rollback();
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    Map<String, String> error = new HashMap<>();
                    error.put("error", "Customer or Service not found");
                    out.print(gson.toJson(error));
                    return;
                }

                customer.getServices().remove(service);
                service.getCustomers().remove(customer);
                em.merge(customer);
                em.merge(service);
                transaction.commit();

                Map<String, String> response = new HashMap<>();
                response.put("message", "Service removed successfully");
                resp.setStatus(HttpServletResponse.SC_OK);
                out.print(gson.toJson(response));

            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                Map<String, String> error = new HashMap<>();
                error.put("error", "Invalid endpoint");
                out.print(gson.toJson(error));
            }

        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            out.print(gson.toJson(error));
            e.printStackTrace();
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
}