package com.bank.servlet;

import com.bank.entity.Account;
import com.bank.entity.Customer;
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

@WebServlet("/accounts")
public class AccountServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        String action = req.getParameter("action");

        if ("delete".equals(action) && req.getParameter("id") != null) {
            //deleteAccount(req, resp);
        } else if ("customer".equals(action) && req.getParameter("customerId") != null) {
            showCustomerAccounts(req, resp);
        } else {
            listAllAccounts(req, resp);
        }
    }

    // نمایش همه حساب‌ها
    private void listAllAccounts(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        resp.setContentType("text/html");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        EntityManager em = JpaUtil.getEntityManager();
        try {
            List<Account> accounts = em.createQuery(
                    "SELECT a FROM Account a ORDER BY a.id DESC", Account.class).getResultList();

            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<meta charset='UTF-8'>");
            out.println("<title>مدیریت حساب‌ها</title>");
            out.println("<style>");
            out.println("body { font-family: Arial; margin: 20px; direction: rtl; background: #f0f2f5; }");
            out.println(".container { max-width: 1200px; margin: auto; background: white; border-radius: 10px; padding: 20px; }");
            out.println("h1 { color: #333; text-align: center; }");
            out.println("table { width: 100%; border-collapse: collapse; margin-top: 20px; }");
            out.println("th, td { padding: 12px; text-align: right; border-bottom: 1px solid #ddd; }");
            out.println("th { background: #4CAF50; color: white; }");
            out.println(".btn-back { background: #2196F3; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px; display: inline-block; margin-bottom: 20px; }");
            out.println(".btn-delete { background: #f44336; color: white; border: none; padding: 5px 10px; border-radius: 3px; cursor: pointer; }");
            out.println(".balance-positive { color: green; font-weight: bold; }");
            out.println(".balance-negative { color: red; }");
            out.println("</style>");
            out.println("</head>");
            out.println("<body>");
            out.println("<div class='container'>");

            out.println("<h1>💰 مدیریت حساب‌های بانکی</h1>");
            out.println("<a href='" + req.getContextPath() + "/customers' class='btn-back'>← بازگشت به لیست مشتریان</a>");

            out.println("<table>");
            out.println("<thead>");
            out.println("<td><th>ID</th><th>شماره حساب</th><th>نوع حساب</th><th>موجودی</th><th>تاریخ افتتاح</th><th>مشتری</th><th>عملیات</th></tr>");
            out.println("</thead>");
            out.println("<tbody>");

            if (accounts.isEmpty()) {
                out.println("<tr><td colspan='7' style='text-align: center'>هیچ حسابی یافت نشد</td></tr>");
            } else {
                for (Account acc : accounts) {
                    String balanceClass = acc.getBalance() >= 0 ? "balance-positive" : "balance-negative";
                    out.println("<tr>");
                    out.println("<td>" + acc.getId() + "</td>");
                    out.println("<td>" + acc.getAccountNumber() + "</td>");
                    out.println("<td>" + acc.getAccountTypePersian() + "</td>");
                    out.println("<td><span class='" + balanceClass + "'>" + String.format("%,.0f", acc.getBalance()) + " تومان</span></td>");
                    out.println("<td>" + acc.getOpeningDate() + "</td>");
                    out.println("<td>" + (acc.getCustomer() != null ? acc.getCustomer().getFullName() : "-") + "</td>");
                    out.println("<td>");
                    out.println("<button class='btn-delete' onclick='deleteAccount(" + acc.getId() + ")'>🗑️ حذف</button>");
                    out.println("</td>");
                    out.println("</tr>");
                }
            }

            out.println("</tbody>");
            out.println("</table>");

            out.println("<script>");
            out.println("function deleteAccount(id) {");
            out.println("    if(confirm('آیا از حذف این حساب مطمئن هستید؟')) {");
            out.println("        fetch('" + req.getContextPath() + "/accounts?action=delete&id=' + id, { method: 'DELETE' })");
            out.println("        .then(() => location.reload());");
            out.println("    }");
            out.println("}");
            out.println("</script>");

            out.println("</div>");
            out.println("</body>");
            out.println("</html>");

        } finally {
            em.close();
        }
    }

    // نمایش حساب‌های یک مشتری خاص
    private void showCustomerAccounts(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        Long customerId = Long.parseLong(req.getParameter("customerId"));

        resp.setContentType("text/html");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        EntityManager em = JpaUtil.getEntityManager();
        try {
            Customer customer = em.find(Customer.class, customerId);
            List<Account> accounts = customer != null ? customer.getAccounts() : List.of();

            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<meta charset='UTF-8'>");
            out.println("<title>حساب‌های " + (customer != null ? customer.getFullName() : "") + "</title>");
            out.println("<style>");
            out.println("body { font-family: Arial; margin: 20px; direction: rtl; background: #f0f2f5; }");
            out.println(".container { max-width: 1000px; margin: auto; background: white; border-radius: 10px; padding: 20px; }");
            out.println("h1 { color: #333; text-align: center; }");
            out.println("table { width: 100%; border-collapse: collapse; margin-top: 20px; }");
            out.println("th, td { padding: 12px; text-align: right; border-bottom: 1px solid #ddd; }");
            out.println("th { background: #4CAF50; color: white; }");
            out.println(".btn-back { background: #2196F3; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px; display: inline-block; margin-bottom: 20px; }");
            out.println("</style>");
            out.println("</head>");
            out.println("<body>");
            out.println("<div class='container'>");

            out.println("<h1>💰 حساب‌های " + (customer != null ? customer.getFullName() : "نامشخص") + "</h1>");
            out.println("<a href='" + req.getContextPath() + "/customers' class='btn-back'>← بازگشت</a>");

            out.println("<table>");
            out.println("<thead>");
            out.println("<tr><th>ID</th><th>شماره حساب</th><th>نوع حساب</th><th>موجودی</th><th>تاریخ افتتاح</th><th>وضعیت</th></tr>");
            out.println("</thead>");
            out.println("<tbody>");

            if (accounts.isEmpty()) {
                out.println("<tr><td colspan='6' style='text-align: center'>هیچ حسابی برای این مشتری یافت نشد</td></tr>");
            } else {
                for (Account acc : accounts) {
                    String status = acc.getIsActive() ? "فعال" : "غیرفعال";
                    out.println("<tr>");
                    out.println("<td>" + acc.getId() + "</td>");
                    out.println("<td>" + acc.getAccountNumber() + "</td>");
                    out.println("<td>" + acc.getAccountTypePersian() + "</td>");
                    out.println("<td>" + String.format("%,.0f", acc.getBalance()) + " تومان</td>");
                    out.println("<td>" + acc.getOpeningDate() + "</td>");
                    out.println("<td>" + status + "</td>");
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

    // ایجاد حساب جدید (POST از فرم مشتری)
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        Long customerId = Long.parseLong(req.getParameter("customerId"));
        String accountNumber = req.getParameter("accountNumber");
        String accountType = req.getParameter("accountType");
        Double balance = Double.parseDouble(req.getParameter("balance"));

        EntityManager em = JpaUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            Customer customer = em.find(Customer.class, customerId);

            if (customer == null) {
                resp.sendRedirect(req.getContextPath() + "/customers?error=مشتری یافت نشد");
                return;
            }

            Account account = new Account(accountNumber, accountType, balance);
            account.setCustomer(customer);
            customer.getAccounts().add(account);

            em.persist(account);
            tx.commit();

            resp.sendRedirect(req.getContextPath() + "/customers?success=حساب بانکی با موفقیت اضافه شد");

        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            resp.sendRedirect(req.getContextPath() + "/customers?error=خطا در ایجاد حساب: " + e.getMessage());
        } finally {
            em.close();
        }
    }

    // حذف حساب
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        Long id = Long.parseLong(req.getParameter("id"));
        EntityManager em = JpaUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            Account account = em.find(Account.class, id);
            if (account != null) {
                em.remove(account);
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
}