package com.bank.servlet;

import com.bank.entity.Address;
import com.bank.entity.Customer;
import com.bank.util.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/address")
public class AddressServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        resp.setContentType("text/html");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        // ========== نمایش پیام تست ==========
        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<title>AddressServlet Test</title>");
        out.println("</head>");
        out.println("<body>");
        out.println("<h1>AddressServlet is WORKING!</h1>");

        String customerIdParam = req.getParameter("customerId");

        out.println("<p>customerId parameter: <strong>" + (customerIdParam != null ? customerIdParam : "NULL") + "</strong></p>");

        if (customerIdParam == null || customerIdParam.isEmpty()) {
            out.println("<p style='color:red'>❌ Please provide customerId parameter!</p>");
            out.println("<p>Example: <code>/address?customerId=1</code></p>");
            out.println("</body></html>");
            return;
        }

        try {
            Long customerId = Long.parseLong(customerIdParam);
            out.println("<p style='color:green'>✅ customerId: " + customerId + "</p>");

            EntityManager em = JpaUtil.getEntityManager();
            Customer customer = em.find(Customer.class, customerId);

            if (customer == null) {
                out.println("<p style='color:red'>❌ Customer not found with ID: " + customerId + "</p>");
                em.close();
                out.println("</body></html>");
                return;
            }

            out.println("<p>✅ Customer: " + customer.getFullName() + "</p>");
            em.close();

            // نمایش فرم آدرس
            showAddressForm(req, resp, customerId);
            return;

        } catch (NumberFormatException e) {
            out.println("<p style='color:red'>❌ Invalid customerId format</p>");
            out.println("</body></html>");
        }
    }

    private void showAddressForm(HttpServletRequest req, HttpServletResponse resp, Long customerId)
            throws IOException {

        resp.setContentType("text/html");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        EntityManager em = JpaUtil.getEntityManager();
        try {
            Customer customer = em.find(Customer.class, customerId);
            Address address = customer.getAddress();

            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<meta charset='UTF-8'>");
            out.println("<title>مدیریت آدرس</title>");
            out.println("<style>");
            out.println("body { font-family: Arial; margin: 20px; direction: rtl; background: #f0f2f5; }");
            out.println(".container { max-width: 600px; margin: auto; background: white; padding: 30px; border-radius: 15px; }");
            out.println("h1 { color: #333; text-align: center; }");
            out.println(".info { background: #e7f3ff; padding: 15px; border-radius: 10px; margin-bottom: 25px; }");
            out.println(".form-group { margin-bottom: 20px; }");
            out.println("label { display: block; margin-bottom: 8px; font-weight: bold; }");
            out.println("input { width: 100%; padding: 10px; border: 1px solid #ddd; border-radius: 8px; box-sizing: border-box; }");
            out.println("button { padding: 12px; background: #4CAF50; color: white; border: none; border-radius: 8px; cursor: pointer; width: 100%; }");
            out.println(".btn-group { display: flex; gap: 10px; margin-top: 25px; }");
            out.println(".btn-group a { flex: 1; }");
            out.println(".cancel { background: #6c757d; text-align: center; display: inline-block; }");
            out.println(".cancel a { color: white; text-decoration: none; display: block; }");
            out.println("</style>");
            out.println("</head>");
            out.println("<body>");
            out.println("<div class='container'>");

            out.println("<h1>📍 مدیریت آدرس</h1>");
            out.println("<div class='info'>");
            out.println("👤 <strong>" + escapeHtml(customer.getFullName()) + "</strong> (کد: " + customer.getId() + ")");
            if (address != null && address.getCity() != null) {
                out.println("<br>📍 آدرس فعلی: " + escapeHtml(address.getCity()));
            } else {
                out.println("<br>⭕ آدرسی ثبت نشده است");
            }
            out.println("</div>");

            out.println("<form method='post' action='" + req.getContextPath() + "/address'>");
            out.println("<input type='hidden' name='customerId' value='" + customerId + "'>");

            out.println("<div class='form-group'>");
            out.println("<label>خیابان:</label>");
            out.println("<input type='text' name='street' value='" + (address != null ? escapeHtml(address.getStreet()) : "") + "' placeholder='مثال: خیابان ولیعصر، پلاک 123'>");
            out.println("</div>");

            out.println("<div class='form-group'>");
            out.println("<label>شهر:</label>");
            out.println("<input type='text' name='city' value='" + (address != null ? escapeHtml(address.getCity()) : "") + "' placeholder='مثال: تهران' required>");
            out.println("</div>");

            out.println("<div class='form-group'>");
            out.println("<label>استان:</label>");
            out.println("<input type='text' name='state' value='" + (address != null ? escapeHtml(address.getState()) : "") + "' placeholder='مثال: تهران'>");
            out.println("</div>");

            out.println("<div class='form-group'>");
            out.println("<label>کد پستی:</label>");
            out.println("<input type='text' name='zipCode' value='" + (address != null ? escapeHtml(address.getZipCode()) : "") + "' placeholder='مثال: 1234567890'>");
            out.println("</div>");

            out.println("<div class='form-group'>");
            out.println("<label>کشور:</label>");
            out.println("<input type='text' name='country' value='" + (address != null ? escapeHtml(address.getCountry()) : "ایران") + "'>");
            out.println("</div>");

            out.println("<div class='btn-group'>");
            out.println("<button type='submit'>💾 ذخیره آدرس</button>");
            out.println("<a href='" + req.getContextPath() + "/customers' style='flex:1'><button type='button' class='cancel'>❌ بازگشت</button></a>");
            out.println("</div>");

            out.println("</form>");
            out.println("</div>");
            out.println("</body>");
            out.println("</html>");

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

        String customerIdParam = req.getParameter("customerId");
        String street = req.getParameter("street");
        String city = req.getParameter("city");
        String state = req.getParameter("state");
        String zipCode = req.getParameter("zipCode");
        String country = req.getParameter("country");

        System.out.println("=== AddressServlet - Saving Address ===");
        System.out.println("Customer ID: " + customerIdParam);
        System.out.println("Street: " + street);
        System.out.println("City: " + city);

        if (customerIdParam == null || customerIdParam.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/customers?error=شناسه مشتری یافت نشد");
            return;
        }

        EntityManager em = JpaUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            Long customerId = Long.parseLong(customerIdParam);

            tx.begin();
            Customer customer = em.find(Customer.class, customerId);

            if (customer == null) {
                tx.rollback();
                resp.sendRedirect(req.getContextPath() + "/customers?error=مشتری یافت نشد");
                return;
            }

            Address address = customer.getAddress();

            if (address == null) {
                address = new Address();
                address.setCustomer(customer);
                customer.setAddress(address);
                em.persist(address);
                System.out.println("✅ Creating NEW address");
            } else {
                System.out.println("✏️ Updating EXISTING address");
            }

            address.setStreet(street != null ? street : "");
            address.setCity(city != null ? city : "");
            address.setState(state);
            address.setZipCode(zipCode);
            address.setCountry((country != null && !country.isEmpty()) ? country : "ایران");

           // em.merge(customer);
            em.persist(customer);
            em.persist(address);
            tx.commit();

            System.out.println("✅ Address saved successfully!");
            resp.sendRedirect(req.getContextPath() + "/customers?success=آدرس با موفقیت ذخیره شد");

        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
            resp.sendRedirect(req.getContextPath() + "/customers?error=خطا: " + e.getMessage());
        } finally {
            em.close();
        }
    }

    private String escapeHtml(String input) {
        if (input == null) return "";
        return input.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}