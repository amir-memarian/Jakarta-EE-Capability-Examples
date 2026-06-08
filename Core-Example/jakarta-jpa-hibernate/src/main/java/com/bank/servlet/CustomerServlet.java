package com.bank.servlet;

import com.bank.entity.Customer;
import com.bank.entity.Address;
import com.bank.util.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/customers")
public class CustomerServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        String action = req.getParameter("action");

        if ("edit".equals(action) && req.getParameter("id") != null) {
            showEditForm(req, resp);
        } else if ("delete".equals(action) && req.getParameter("id") != null) {
            deleteCustomer(req, resp);
        } else {
            listCustomers(req, resp);
        }
    }

    private void listCustomers(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        resp.setContentType("text/html");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        EntityManager em = JpaUtil.getEntityManager();
        try {
            List<Customer> customers = em.createQuery(
                    "SELECT DISTINCT c FROM Customer c LEFT JOIN FETCH c.address ORDER BY c.id DESC",
                    Customer.class).getResultList();

            String error = req.getParameter("error");
            String success = req.getParameter("success");

            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<meta charset='UTF-8'>");
            out.println("<title>مدیریت مشتریان</title>");
            out.println("<style>");
            out.println("body { font-family: Arial; margin: 20px; direction: rtl; background: #f0f2f5; }");
            out.println(".container { max-width: 1400px; margin: auto; background: white; border-radius: 10px; padding: 20px; }");
            out.println("h1 { color: #333; text-align: center; }");
            out.println(".form-card { background: #f8f9fa; padding: 20px; border-radius: 10px; margin-bottom: 30px; }");
            out.println(".form-group { display: inline-block; margin: 0 10px 10px 0; }");
            out.println("label { display: block; margin-bottom: 5px; font-size: 12px; color: #666; }");
            out.println("input { padding: 8px 12px; border: 1px solid #ddd; border-radius: 6px; width: 200px; }");
            out.println("button { padding: 8px 16px; background: #4CAF50; color: white; border: none; border-radius: 6px; cursor: pointer; }");
            out.println(".btn-edit { background: #2196F3; }");
            out.println(".btn-delete { background: #f44336; }");
            out.println(".btn-address { background: #FF9800; }");
            out.println(".success { background: #d4edda; color: #155724; padding: 10px; border-radius: 5px; margin-bottom: 20px; }");
            out.println(".error { background: #f8d7da; color: #721c24; padding: 10px; border-radius: 5px; margin-bottom: 20px; }");
            out.println("table { width: 100%; border-collapse: collapse; margin-top: 20px; }");
            out.println("th, td { padding: 10px; text-align: right; border-bottom: 1px solid #ddd; }");
            out.println("th { background: #4CAF50; color: white; }");
            out.println(".address-badge { background: #e8f5e9; color: #2e7d32; padding: 2px 8px; border-radius: 15px; font-size: 12px; }");
            out.println(".no-address { color: #999; font-style: italic; }");
            out.println("</style>");
            out.println("</head>");
            out.println("<body>");
            out.println("<div class='container'>");

            out.println("<h1>🏦 سیستم مدیریت مشتریان بانک</h1>");

            if (success != null) out.println("<div class='success'>✅ " + success + "</div>");
            if (error != null) out.println("<div class='error'>❌ " + error + "</div>");

            // فرم افزودن مشتری
            out.println("<div class='form-card'>");
            out.println("<h3>➕ افزودن مشتری جدید</h3>");
            out.println("<form method='post' action='" + req.getContextPath() + "/customers'>");
            out.println("<div class='form-group'><label>نام کامل</label><input type='text' name='fullName' required></div>");
            out.println("<div class='form-group'><label>ایمیل</label><input type='email' name='email'></div>");
            out.println("<div class='form-group'><label>تلفن</label><input type='text' name='phone'></div>");
            out.println("<div style='margin-top: 15px;'><h4>📍 آدرس (اختیاری)</h4>");
            out.println("<div class='form-group'><label>خیابان</label><input type='text' name='street'></div>");
            out.println("<div class='form-group'><label>شهر</label><input type='text' name='city'></div>");
            out.println("<div class='form-group'><label>استان</label><input type='text' name='state'></div>");
            out.println("<div class='form-group'><label>کد پستی</label><input type='text' name='zipCode'></div>");
            out.println("<div class='form-group'><label>کشور</label><input type='text' name='country' value='ایران'></div>");
            out.println("</div>");
            out.println("<button type='submit'>➕ افزودن مشتری</button>");
            out.println("</form>");
            out.println("</div>");

            // جدول مشتریان
            out.println("<h3>📋 لیست مشتریان</h3>");
            out.println("<table>");
            out.println("<thead><tr><th>ID</th><th>نام کامل</th><th>ایمیل</th><th>تلفن</th><th>آدرس</th><th>عملیات</th></tr></thead>");
            out.println("<tbody>");

            for (Customer customer : customers) {
                out.println("<tr>");
                out.println("<td>" + customer.getId() + "</td>");
                out.println("<td>" + escapeHtml(customer.getFullName()) + "</td>");
                out.println("<td>" + (customer.getEmail() != null ? escapeHtml(customer.getEmail()) : "-") + "</td>");
                out.println("<td>" + (customer.getPhone() != null ? escapeHtml(customer.getPhone()) : "-") + "</td>");

                out.println("<td>");
                Address addr = customer.getAddress();
                if (addr != null && addr.getCity() != null && !addr.getCity().isEmpty()) {
                    out.println("<span class='address-badge'>📍</span> " + escapeHtml(addr.getCity()) +
                            (addr.getStreet() != null ? "، " + escapeHtml(addr.getStreet()) : ""));
                } else {
                    out.println("<span class='no-address'>⭕ آدرسی ثبت نشده</span>");
                }
                out.println("</td>");

                out.println("<td>");
                out.println("<a href='" + req.getContextPath() + "/customers?action=edit&id=" + customer.getId() + "'><button class='btn-edit'>✏️ ویرایش</button></a> ");
                out.println("<a href='" + req.getContextPath() + "/address?customerId=" + customer.getId() + "'><button class='btn-address'>📍 آدرس</button></a> ");
                out.println("<a href='" + req.getContextPath() + "/customers?action=delete&id=" + customer.getId() + "' onclick='return confirm(\"آیا از حذف مطمئن هستید؟\")'><button class='btn-delete'>🗑️ حذف</button></a>");
                out.println("</td>");
                out.println("</tr>");
            }

            out.println("</tbody>");
            out.println("</table>");
            out.println("</div>");
            out.println("</body>");
            out.println("</html>");

        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(500, "خطا: " + e.getMessage());
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
            Customer customer = em.createQuery(
                    "SELECT c FROM Customer c LEFT JOIN FETCH c.address WHERE c.id = :id",
                    Customer.class).setParameter("id", id).getSingleResult();

            Address address = customer.getAddress();

            out.println("<!DOCTYPE html><html><head><meta charset='UTF-8'><title>ویرایش مشتری</title>");
            out.println("<style>");
            out.println("body{font-family:Arial;margin:20px;direction:rtl}.container{max-width:700px;margin:auto;background:#f9f9f9;padding:25px;border-radius:10px}");
            out.println(".form-group{margin-bottom:15px}label{display:inline-block;width:100px}input{width:300px;padding:8px}");
            out.println("button{padding:10px 20px;background:#4CAF50;color:white;border:none;cursor:pointer}hr{margin:20px 0}");
            out.println("</style></head><body><div class='container'>");
            out.println("<h1>✏️ ویرایش مشتری</h1>");
            out.println("<form method='post' action='" + req.getContextPath() + "/customers'>");
            out.println("<input type='hidden' name='action' value='update'><input type='hidden' name='id' value='" + customer.getId() + "'>");
            out.println("<div class='form-group'><label>نام کامل:</label><input type='text' name='fullName' value='" + escapeHtml(customer.getFullName()) + "' required></div>");
            out.println("<div class='form-group'><label>ایمیل:</label><input type='email' name='email' value='" + (customer.getEmail() != null ? escapeHtml(customer.getEmail()) : "") + "'></div>");
            out.println("<div class='form-group'><label>تلفن:</label><input type='text' name='phone' value='" + (customer.getPhone() != null ? escapeHtml(customer.getPhone()) : "") + "'></div>");
            out.println("<hr><h3>📍 آدرس</h3>");
            out.println("<div class='form-group'><label>خیابان:</label><input type='text' name='street' value='" + (address != null ? escapeHtml(address.getStreet()) : "") + "'></div>");
            out.println("<div class='form-group'><label>شهر:</label><input type='text' name='city' value='" + (address != null ? escapeHtml(address.getCity()) : "") + "'></div>");
            out.println("<div class='form-group'><label>استان:</label><input type='text' name='state' value='" + (address != null ? escapeHtml(address.getState()) : "") + "'></div>");
            out.println("<div class='form-group'><label>کد پستی:</label><input type='text' name='zipCode' value='" + (address != null ? escapeHtml(address.getZipCode()) : "") + "'></div>");
            out.println("<div class='form-group'><label>کشور:</label><input type='text' name='country' value='" + (address != null ? escapeHtml(address.getCountry()) : "ایران") + "'></div>");
            out.println("<hr><button type='submit'>💾 ذخیره</button>");
            out.println("<a href='" + req.getContextPath() + "/customers'><button type='button'>❌ انصراف</button></a>");
            out.println("</form></div></body></html>");

        } catch (Exception e) {
            e.printStackTrace();
            resp.sendRedirect(req.getContextPath() + "/customers?error=خطا");
        } finally {
            em.close();
        }
    }

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
        String street = req.getParameter("street");
        String city = req.getParameter("city");
        String state = req.getParameter("state");
        String zipCode = req.getParameter("zipCode");
        String country = req.getParameter("country");

        EntityManager em = JpaUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            Customer customer = new Customer(fullName, email, phone);
            em.persist(customer);

            if (street != null && !street.trim().isEmpty() && city != null && !city.trim().isEmpty()) {
                Address address = new Address();
                address.setStreet(street);
                address.setCity(city);
                address.setState(state);
                address.setZipCode(zipCode);
                address.setCountry((country != null && !country.isEmpty()) ? country : "ایران");
                address.setCustomer(customer);
                customer.setAddress(address);
                em.persist(address);
            }

            tx.commit();
            resp.sendRedirect(req.getContextPath() + "/customers?success=مشتری اضافه شد");
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            resp.sendRedirect(req.getContextPath() + "/customers?error=خطا");
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
        String street = req.getParameter("street");
        String city = req.getParameter("city");
        String state = req.getParameter("state");
        String zipCode = req.getParameter("zipCode");
        String country = req.getParameter("country");

        System.out.println("=== CustomerServlet - updateCustomer ===");
        System.out.println("Customer ID: " + id);
        System.out.println("Street: " + street);
        System.out.println("City: " + city);

        EntityManager em = JpaUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            Customer customer = em.find(Customer.class, id);

            if (customer == null) {
                resp.sendRedirect(req.getContextPath() + "/customers?error=مشتری یافت نشد");
                return;
            }

            // به‌روزرسانی اطلاعات پایه
            customer.setFullName(fullName);
            customer.setEmail(email);
            customer.setPhone(phone);

            if (street != null && !street.trim().isEmpty() && city != null && !city.trim().isEmpty()) {
                Address address = new Address();
                address.setStreet(street);
                address.setCity(city);
                address.setState(state);
                address.setZipCode(zipCode);
                address.setCountry((country != null && !country.isEmpty()) ? country : "ایران");
                address.setCustomer(customer);
                customer.setAddress(address);
                em.persist(address);
            }else{
                Address address = customer.getAddress();
                address.setStreet(street);
                address.setCity(city);
                address.setState(state);
                address.setZipCode(zipCode);
                address.setCountry((country != null && !country.isEmpty()) ? country : "ایران");
            }

            em.merge(customer);
            tx.commit();

            System.out.println("✅ Customer updated successfully");
            resp.sendRedirect(req.getContextPath() + "/customers?success=مشتری با موفقیت ویرایش شد");

        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
            resp.sendRedirect(req.getContextPath() + "/customers?error=خطا در ویرایش: " + e.getMessage());
        } finally {
            em.close();
        }
    }

    private void deleteCustomer(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        Long id = Long.parseLong(req.getParameter("id"));
        EntityManager em = JpaUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            Customer customer = em.find(Customer.class, id);
            if (customer != null) em.remove(customer);
            tx.commit();
            resp.sendRedirect(req.getContextPath() + "/customers?success=مشتری حذف شد");
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            resp.sendRedirect(req.getContextPath() + "/customers?error=خطا");
        } finally {
            em.close();
        }
    }

    private String escapeHtml(String input) {
        if (input == null) return "";
        return input.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&#39;");
    }
}