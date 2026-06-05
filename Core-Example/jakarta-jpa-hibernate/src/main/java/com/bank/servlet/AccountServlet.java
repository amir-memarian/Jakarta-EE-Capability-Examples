package com.bank.servlet;

import com.bank.dao.AccountDAO;
import com.bank.dao.CustomerDAO;
import com.bank.entity.Account;
import com.bank.entity.Customer;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/api/accounts/*")
public class AccountServlet extends HttpServlet {

    private AccountDAO accountDAO;
    private CustomerDAO customerDAO;
    private Gson gson;

    @Override
    public void init() {
        accountDAO = new AccountDAO();
        customerDAO = new CustomerDAO();
        gson = new Gson();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();

        String pathInfo = req.getPathInfo();

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // GET /api/accounts - لیست همه حساب‌ها
                List<Account> accounts = accountDAO.findAll();
                out.print(gson.toJson(accounts));

            } else if (pathInfo.equals("/customer")) {
                // GET /api/accounts/customer?id=1 - حساب‌های یک مشتری
                Long customerId = Long.parseLong(req.getParameter("id"));
                List<Account> accounts = accountDAO.findByCustomerId(customerId);
                out.print(gson.toJson(accounts));

            } else if (pathInfo.equals("/type")) {
                // GET /api/accounts/type?type=SAVINGS - حساب‌ها بر اساس نوع
                String type = req.getParameter("type");
                List<Account> accounts = accountDAO.findByAccountType(type);
                out.print(gson.toJson(accounts));

            } else {
                // GET /api/accounts/1 - دریافت یک حساب با ID
                Long id = Long.parseLong(pathInfo.substring(1));
                Account account = accountDAO.findById(id);
                if (account != null) {
                    out.print(gson.toJson(account));
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print(gson.toJson(Map.of("error", "Account not found")));
                }
            }

        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.toJson(Map.of("error", e.getMessage())));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();

        try {
            // خواندن JSON از بدنه درخواست
            BufferedReader reader = req.getReader();
            Account account = gson.fromJson(reader, Account.class);

            // دریافت Customer از دیتابیس
            Customer customer = customerDAO.findById(account.getCustomer().getId());
            if (customer == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(gson.toJson(Map.of("error", "Customer not found")));
                return;
            }

            account.setCustomer(customer);
            Account created = accountDAO.create(account);

            resp.setStatus(HttpServletResponse.SC_CREATED);
            out.print(gson.toJson(created));

        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.toJson(Map.of("error", e.getMessage())));
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();

        try {
            BufferedReader reader = req.getReader();
            Account account = gson.fromJson(reader, Account.class);

            Account updated = accountDAO.update(account);
            out.print(gson.toJson(updated));

        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.toJson(Map.of("error", e.getMessage())));
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo != null && !pathInfo.equals("/")) {
            Long id = Long.parseLong(pathInfo.substring(1));
            accountDAO.delete(id);
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    // عملیات ویژه: واریز و برداشت
    @Override
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();

        String pathInfo = req.getPathInfo();

        try {
            BufferedReader reader = req.getReader();
            Map<String, Object> requestMap = gson.fromJson(reader, HashMap.class);

            if (pathInfo.equals("/deposit")) {
                Long accountId = ((Number) requestMap.get("accountId")).longValue();
                Double amount = (Double) requestMap.get("amount");
                accountDAO.deposit(accountId, amount);
                out.print(gson.toJson(Map.of("message", "Deposit successful")));

            } else if (pathInfo.equals("/withdraw")) {
                Long accountId = ((Number) requestMap.get("accountId")).longValue();
                Double amount = (Double) requestMap.get("amount");
                accountDAO.withdraw(accountId, amount);
                out.print(gson.toJson(Map.of("message", "Withdrawal successful")));
            }

        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.toJson(Map.of("error", e.getMessage())));
        }
    }
}