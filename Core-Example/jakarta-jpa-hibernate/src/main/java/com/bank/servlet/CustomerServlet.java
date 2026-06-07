package com.bank.servlet;

import com.bank.entity.Customer;
import com.bank.util.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/customers")
public class CustomerServlet extends HttpServlet {

    // ======================== Show all customers ========================
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        resp.setContentType("text/html");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        String action = req.getParameter("action");

        // اگر action=edit بود، فرم ویرایش را نمایش بده
        if ("edit".equals(action) && req.getParameter("id") != null) {
            showEditForm(req, resp);
            return;
        }

        // در غیر این صورت، لیست مشتریان را نمایش بده
        listCustomers(req, resp);
    }

    private void listCustomers(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        resp.setContentType("text/html");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        EntityManager em = JpaUtil.getEntityManager();
        try {
            TypedQuery<Customer> query = em.createQuery(
                    "SELECT c FROM Customer c ORDER BY c.id DESC", Customer.class);
            List<Customer> customers = query.getResultList();

            // پیام‌ها (برای نمایش خطا یا موفقیت)
            String error = req.getParameter("error");
            String success = req.getParameter("success");

            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<meta charset='UTF-8'>");
            out.println("<title>مدیریت مشتریان</title>");
            out.println("<style>");
            out.println("body { font-family: Arial, sans-serif; margin: 20px; direction: rtl; }");
            out.println(".container { max-width: 1200px; margin: auto; }");
            out.println("table { border-collapse: collapse; width: 100%; margin-top: 20px; }");
            out.println("th, td { border: 1px solid #ddd; padding: 12px; text-align: right; }");
            out.println("th { background-color: #4CAF50; color: white; }");
            out.println("tr:hover { background-color: #f5f5f5; }");
            out.println(".form-group { margin: 10px 0; }");
            out.println("label { display: inline-block; width: 80px; }");
            out.println("input[type='text'], input[type='email'] { width: 250px; padding: 8px; margin: 5px; }");
            out.println("button { padding: 8px 15px; margin: 5px; cursor: pointer; background-color: #4CAF50; color: white; border: none; border-radius: 4px; }");
            out.println(".delete-btn { background-color: #f44336; }");
            out.println(".edit-btn { background-color: #2196F3; }");
            out.println(".success { background-color: #dff0d8; color: #3c763d; padding: 10px; margin: 10px 0; border-radius: 4px; }");
            out.println(".error { background-color: #f2dede; color: #a94442; padding: 10px; margin: 10px 0; border-radius: 4px; }");
            out.println("h1 { color: #333; text-align: center; }");
            out.println("h3 { color: #555; margin-top: 30px; }");
            out.println("</style>");
            out.println("</head>");
            out.println("<body>");
            out.println("<div class='container'>");

            out.println("<h1>🏦 سیستم مدیریت مشتریان بانک</h1>");

            // نمایش پیام‌ها
            if (success != null) {
                out.println("<div class='success'>✅ " + success + "</div>");
            }
            if (error != null) {
                out.println("<div class='error'>❌ " + error + "</div>");
            }

            // فرم افزودن مشتری جدید
            out.println("<h3>➕ افزودن مشتری جدید</h3>");
            out.println("<form method='post' action='customers'>");
            out.println("<div class='form-group'>");
            out.println("<label>نام کامل:</label>");
            out.println("<input type='text' name='fullName' required>");
            out.println("</div>");
            out.println("<div class='form-group'>");
            out.println("<label>ایمیل:</label>");
            out.println("<input type='email' name='email'>");
            out.println("</div>");
            out.println("<div class='form-group'>");
            out.println("<label>تلفن:</label>");
            out.println("<input type='text' name='phone'>");
            out.println("</div>");
            out.println("<button type='submit'>افزودن مشتری</button>");
            out.println("</form>");

            // جدول نمایش مشتریان
            out.println("<h3>📋 لیست مشتریان</h3>");
            out.println("<table>");
            out.println("<thead>");
            out.println("<tr>");
            out.println("<th>ID</th>");
            out.println("<th>نام کامل</th>");
            out.println("<th>ایمیل</th>");
            out.println("<th>تلفن</th>");
            out.println("<th>تاریخ ایجاد</th>");
            out.println("<th>عملیات</th>");
            out.println("</tr>");
            out.println("</thead>");
            out.println("<tbody>");

            if (customers.isEmpty()) {
                out.println("<tr><td colspan='6' style='text-align: center'>هیچ مشتری یافت نشد</td></tr>");
            } else {
                for (Customer customer : customers) {
                    out.println("<tr>");
                    out.println("<td>" + customer.getId() + "</td>");
                    out.println("<td>" + escapeHtml(customer.getFullName()) + "</td>");
                    out.println("<td>" + (customer.getEmail() != null ? escapeHtml(customer.getEmail()) : "-") + "</td>");
                    out.println("<td>" + (customer.getPhone() != null ? escapeHtml(customer.getPhone()) : "-") + "</td>");
                    out.println("<td>" + customer.getCreatedAt() + "</td>");
                    out.println("<td>");
                    out.println("<a href='customers?action=edit&id=" + customer.getId() + "'>");
                    out.println("<button type='button' class='edit-btn'>✏️ ویرایش</button>");
                    out.println("</a>");
                    out.println("<a href='customers?action=delete&id=" + customer.getId() + "' onclick='return confirm(\"آیا از حذف این مشتری مطمئن هستید؟\")'>");
                    out.println("<button type='button' class='delete-btn'>🗑️ حذف</button>");
                    out.println("</a>");
                    out.println("</td>");
                    out.println("</tr>");
                }
            }

            out.println("</tbody>");
            out.println("</table>");

            out.println("</div>");
            out.println("</body>");
            out.println("</html>");

        } finally {
            em.close();
        }
    }

    private void showEditForm(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        resp.setContentType("text/html");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        Long id = Long.parseLong(req.getParameter("id"));
        EntityManager em = JpaUtil.getEntityManager();

        try {
            Customer customer = em.find(Customer.class, id);

            if (customer == null) {
                resp.sendRedirect(req.getContextPath() + "/customers?error=مشتری یافت نشد");
                return;
            }

            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<meta charset='UTF-8'>");
            out.println("<title>ویرایش مشتری</title>");
            out.println("<style>");
            out.println("body { font-family: Arial; margin: 20px; direction: rtl; }");
            out.println(".container { max-width: 500px; margin: auto; }");
            out.println(".form-group { margin: 15px 0; }");
            out.println("label { display: inline-block; width: 80px; }");
            out.println("input[type='text'], input[type='email'] { width: 250px; padding: 8px; }");
            out.println("button { padding: 8px 15px; margin: 5px; cursor: pointer; }");
            out.println(".save-btn { background-color: #4CAF50; color: white; border: none; }");
            out.println(".cancel-btn { background-color: #ccc; color: black; border: none; }");
            out.println("h1 { color: #333; }");
            out.println("</style>");
            out.println("</head>");
            out.println("<body>");
            out.println("<div class='container'>");

            out.println("<h1>✏️ ویرایش اطلاعات مشتری</h1>");

            out.println("<form method='post' action='customers'>");
            out.println("<input type='hidden' name='action' value='update'>");
            out.println("<input type='hidden' name='id' value='" + customer.getId() + "'>");

            out.println("<div class='form-group'>");
            out.println("<label>نام کامل:</label>");
            out.println("<input type='text' name='fullName' value='" + escapeHtml(customer.getFullName()) + "' required>");
            out.println("</div>");

            out.println("<div class='form-group'>");
            out.println("<label>ایمیل:</label>");
            out.println("<input type='email' name='email' value='" + (customer.getEmail() != null ? escapeHtml(customer.getEmail()) : "") + "'>");
            out.println("</div>");

            out.println("<div class='form-group'>");
            out.println("<label>تلفن:</label>");
            out.println("<input type='text' name='phone' value='" + (customer.getPhone() != null ? escapeHtml(customer.getPhone()) : "") + "'>");
            out.println("</div>");

            out.println("<button type='submit' class='save-btn'>💾 ذخیره تغییرات</button>");
            out.println("<a href='customers'><button type='button' class='cancel-btn'>❌ انصراف</button></a>");

            out.println("</form>");
            out.println("</div>");
            out.println("</body>");
            out.println("</html>");

        } finally {
            em.close();
        }
    }

    // ======================== POST processing (create and edit) ========================
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        String action = req.getParameter("action");

        if ("update".equals(action)) {
            updateCustomer(req, resp);
        } else {
            createCustomer(req, resp);
        }
    }

    private void createCustomer(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        String fullName = req.getParameter("fullName");
        String email = req.getParameter("email");
        String phone = req.getParameter("phone");

        // Validation
        if (fullName == null || fullName.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/customers?error=نام کامل الزامی است");
            return;
        }

        EntityManager em = JpaUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            // Checking for duplicate emails
            if (email != null && !email.trim().isEmpty()) {
                TypedQuery<Long> query = em.createQuery(
                        "SELECT COUNT(c) FROM Customer c WHERE c.email = :email", Long.class);
                query.setParameter("email", email);
                Long count = query.getSingleResult();

                if (count > 0) {
                    resp.sendRedirect(req.getContextPath() + "/customers?error=ایمیل تکراری است");
                    return;
                }
            }

            tx.begin();
            Customer customer = new Customer(fullName, email, phone);
            em.persist(customer);
            tx.commit();

            resp.sendRedirect(req.getContextPath() + "/customers?success=مشتری با موفقیت اضافه شد");

        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            resp.sendRedirect(req.getContextPath() + "/customers?error=خطا در افزودن مشتری: " + e.getMessage());
        } finally {
            em.close();
        }
    }

    private void updateCustomer(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        Long id = Long.parseLong(req.getParameter("id"));
        String fullName = req.getParameter("fullName");
        String email = req.getParameter("email");
        String phone = req.getParameter("phone");

        if (fullName == null || fullName.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/customers?error=نام کامل الزامی است");
            return;
        }

        EntityManager em = JpaUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            Customer customer = em.find(Customer.class, id);

            if (customer == null) {
                resp.sendRedirect(req.getContextPath() + "/customers?error=مشتری یافت نشد");
                return;
            }

            // اگر ایمیل تغییر کرده، بررسی تکراری نبودن
            if (email != null && !email.trim().isEmpty() && !email.equals(customer.getEmail())) {
                TypedQuery<Long> query = em.createQuery(
                        "SELECT COUNT(c) FROM Customer c WHERE c.email = :email", Long.class);
                query.setParameter("email", email);
                Long count = query.getSingleResult();

                if (count > 0) {
                    resp.sendRedirect(req.getContextPath() + "/customers?error=ایمیل تکراری است");
                    return;
                }
                customer.setEmail(email);
            }

            customer.setFullName(fullName);
            customer.setPhone(phone);

            em.merge(customer);
            tx.commit();

            resp.sendRedirect(req.getContextPath() + "/customers?success=مشتری با موفقیت ویرایش شد");

        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            resp.sendRedirect(req.getContextPath() + "/customers?error=خطا در ویرایش مشتری");
        } finally {
            em.close();
        }
    }

    // ======================== DELETE processing ========================
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        Long id = Long.parseLong(req.getParameter("id"));
        EntityManager em = JpaUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            Customer customer = em.find(Customer.class, id);

            if (customer != null) {
                em.remove(customer);
            }
            tx.commit();

            resp.setStatus(HttpServletResponse.SC_OK);

        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } finally {
            em.close();
        }
    }

    // ======================== GET processing for DELETE ========================
    protected void doGetForDelete(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        String action = req.getParameter("action");

        if ("delete".equals(action) && req.getParameter("id") != null) {
            Long id = Long.parseLong(req.getParameter("id"));
            EntityManager em = JpaUtil.getEntityManager();
            EntityTransaction tx = em.getTransaction();

            try {
                tx.begin();
                Customer customer = em.find(Customer.class, id);
                if (customer != null) {
                    em.remove(customer);
                }
                tx.commit();
                resp.sendRedirect(req.getContextPath() + "/customers?success=مشتری با موفقیت حذف شد");
            } catch (Exception e) {
                if (tx.isActive()) tx.rollback();
                resp.sendRedirect(req.getContextPath() + "/customers?error=خطا در حذف مشتری");
            } finally {
                em.close();
            }
        } else {
            listCustomers(req, resp);
        }
    }

    // Helper method to prevent XSS
    private String escapeHtml(String input) {
        if (input == null) return "";
        return input.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}