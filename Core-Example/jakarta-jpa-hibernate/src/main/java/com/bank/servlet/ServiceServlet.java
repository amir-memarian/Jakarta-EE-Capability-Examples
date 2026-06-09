package com.bank.servlet;

import com.bank.entity.Customer;
import com.bank.entity.Service;
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

@WebServlet("/services")
public class ServiceServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        String action = req.getParameter("action");

        if ("assign".equals(action)) {
            showAssignForm(req, resp);
        } else if ("customer".equals(action) && req.getParameter("customerId") != null) {
            showCustomerServices(req, resp);
        } else {
            listAllServices(req, resp);
        }
    }

    // نمایش همه خدمات
    private void listAllServices(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        resp.setContentType("text/html");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        EntityManager em = JpaUtil.getEntityManager();
        try {
            List<Service> services = em.createQuery(
                    "SELECT s FROM Service s ORDER BY s.id", Service.class).getResultList();

            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<meta charset='UTF-8'>");
            out.println("<title>مدیریت خدمات بانکی</title>");
            out.println("<style>");
            out.println("body { font-family: Arial; margin: 20px; direction: rtl; background: #f0f2f5; }");
            out.println(".container { max-width: 1000px; margin: auto; background: white; border-radius: 10px; padding: 20px; }");
            out.println("h1 { color: #333; text-align: center; }");
            out.println("table { width: 100%; border-collapse: collapse; margin-top: 20px; }");
            out.println("th, td { padding: 12px; text-align: right; border-bottom: 1px solid #ddd; }");
            out.println("th { background: #9C27B0; color: white; }");
            out.println(".btn-back { background: #2196F3; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px; display: inline-block; margin-bottom: 20px; }");
            out.println(".btn-add { background: #4CAF50; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px; display: inline-block; margin-bottom: 20px; margin-right: 10px; }");
            out.println(".fee { color: #9C27B0; font-weight: bold; }");
            out.println("</style>");
            out.println("</head>");
            out.println("<body>");
            out.println("<div class='container'>");

            out.println("<h1>🎯 مدیریت خدمات بانکی</h1>");
            out.println("<a href='" + req.getContextPath() + "/customers' class='btn-back'>← بازگشت به لیست مشتریان</a>");
            out.println("<a href='" + req.getContextPath() + "/services?action=assign' class='btn-add'>➕ اختصاص سرویس به مشتری</a>");

            out.println("<table>");
            out.println("<thead>");
            out.println("<tr><th>ID</th><th>نام سرویس</th><th>توضیحات</th><th>کارمزد</th><th>تعداد مشتریان</th></tr>");
            out.println("</thead>");
            out.println("<tbody>");

            if (services.isEmpty()) {
                out.println("<tr><td colspan='5' style='text-align: center'>هیچ خدماتی یافت نشد</td></tr>");
            } else {
                for (Service s : services) {
                    out.println("<tr>");
                    out.println("<td>" + s.getId() + "</td>");
                    out.println("<td>" + s.getName() + "</td>");
                    out.println("<td>" + (s.getDescription() != null ? s.getDescription() : "-") + "</td>");
                    out.println("<td><span class='fee'>" + s.getFee() + "%</span></td>");
                    out.println("<td>" + s.getCustomers().size() + "</td>");
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

    // نمایش فرم اختصاص سرویس به مشتری
    private void showAssignForm(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        resp.setContentType("text/html");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        EntityManager em = JpaUtil.getEntityManager();
        try {
            List<Customer> customers = em.createQuery(
                    "SELECT c FROM Customer c ORDER BY c.id", Customer.class).getResultList();
            List<Service> services = em.createQuery(
                    "SELECT s FROM Service s ORDER BY s.id", Service.class).getResultList();

            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<meta charset='UTF-8'>");
            out.println("<title>اختصاص سرویس</title>");
            out.println("<style>");
            out.println("body { font-family: Arial; margin: 20px; direction: rtl; background: #f0f2f5; }");
            out.println(".container { max-width: 500px; margin: auto; background: white; border-radius: 10px; padding: 30px; }");
            out.println("h1 { color: #333; text-align: center; }");
            out.println(".form-group { margin-bottom: 20px; }");
            out.println("label { display: block; margin-bottom: 8px; font-weight: bold; }");
            out.println("select, button { width: 100%; padding: 10px; border: 1px solid #ddd; border-radius: 5px; }");
            out.println("button { background: #4CAF50; color: white; border: none; cursor: pointer; margin-top: 10px; }");
            out.println(".btn-back { background: #2196F3; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px; display: inline-block; margin-top: 20px; }");
            out.println("</style>");
            out.println("</head>");
            out.println("<body>");
            out.println("<div class='container'>");

            out.println("<h1>🎯 اختصاص سرویس به مشتری</h1>");

            out.println("<form method='post' action='" + req.getContextPath() + "/services'>");

            out.println("<div class='form-group'>");
            out.println("<label>انتخاب مشتری:</label>");
            out.println("<select name='customerId' required>");
            out.println("<option value=''>انتخاب کنید...</option>");
            for (Customer c : customers) {
                out.println("<option value='" + c.getId() + "'>" + c.getFullName() + "</option>");
            }
            out.println("</select>");
            out.println("</div>");

            out.println("<div class='form-group'>");
            out.println("<label>انتخاب سرویس:</label>");
            out.println("<select name='serviceId' required>");
            out.println("<option value=''>انتخاب کنید...</option>");
            for (Service s : services) {
                out.println("<option value='" + s.getId() + "'>" + s.getName() + " (" + s.getFee() + "% کارمزد)</option>");
            }
            out.println("</select>");
            out.println("</div>");

            out.println("<button type='submit'>✅ اختصاص سرویس</button>");
            out.println("</form>");

            out.println("<a href='" + req.getContextPath() + "/services'><button class='btn-back'>← بازگشت</button></a>");
            out.println("</div>");
            out.println("</body>");
            out.println("</html>");

        } finally {
            em.close();
        }
    }

    // نمایش خدمات یک مشتری خاص
    private void showCustomerServices(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        Long customerId = Long.parseLong(req.getParameter("customerId"));

        resp.setContentType("text/html");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        EntityManager em = JpaUtil.getEntityManager();
        try {
            Customer customer = em.find(Customer.class, customerId);

            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<meta charset='UTF-8'>");
            out.println("<title>خدمات " + (customer != null ? customer.getFullName() : "") + "</title>");
            out.println("<style>");
            out.println("body { font-family: Arial; margin: 20px; direction: rtl; background: #f0f2f5; }");
            out.println(".container { max-width: 800px; margin: auto; background: white; border-radius: 10px; padding: 20px; }");
            out.println("h1 { color: #333; text-align: center; }");
            out.println("table { width: 100%; border-collapse: collapse; margin-top: 20px; }");
            out.println("th, td { padding: 12px; text-align: right; border-bottom: 1px solid #ddd; }");
            out.println("th { background: #9C27B0; color: white; }");
            out.println(".btn-back { background: #2196F3; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px; display: inline-block; margin-bottom: 20px; }");
            out.println("</style>");
            out.println("</head>");
            out.println("<body>");
            out.println("<div class='container'>");

            out.println("<h1>🎯 خدمات مشتری: " + (customer != null ? customer.getFullName() : "نامشخص") + "</h1>");
            out.println("<a href='" + req.getContextPath() + "/customers' class='btn-back'>← بازگشت</a>");

            out.println("<table>");
            out.println("<thead>");
            out.println("<tr><th>ID</th><th>نام سرویس</th><th>توضیحات</th><th>کارمزد</th></tr>");
            out.println("</thead>");
            out.println("<tbody>");

            if (customer == null || customer.getServices().isEmpty()) {
                out.println("<tr><td colspan='4' style='text-align: center'>هیچ خدماتی برای این مشتری یافت نشد</td></tr>");
            } else {
                for (Service s : customer.getServices()) {
                    out.println("<tr>");
                    out.println("<td>" + s.getId() + "</td>");
                    out.println("<td>" + s.getName() + "</td>");
                    out.println("<td>" + (s.getDescription() != null ? s.getDescription() : "-") + "</td>");
                    out.println("<td>" + s.getFee() + "%</td>");
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

    // اختصاص سرویس به مشتری (POST)
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        Long customerId = Long.parseLong(req.getParameter("customerId"));
        Long serviceId = Long.parseLong(req.getParameter("serviceId"));

        EntityManager em = JpaUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            Customer customer = em.find(Customer.class, customerId);
            Service service = em.find(Service.class, serviceId);

            if (customer == null || service == null) {
                resp.sendRedirect(req.getContextPath() + "/services?error=مشتری یا سرویس یافت نشد");
                return;
            }

            customer.addService(service);
            em.merge(customer);
            tx.commit();

            resp.sendRedirect(req.getContextPath() + "/customers?success=سرویس با موفقیت به مشتری اختصاص یافت");

        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            resp.sendRedirect(req.getContextPath() + "/services?error=خطا: " + e.getMessage());
        } finally {
            em.close();
        }
    }
}