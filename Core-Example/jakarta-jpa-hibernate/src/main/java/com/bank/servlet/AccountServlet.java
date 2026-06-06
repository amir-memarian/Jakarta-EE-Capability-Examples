package com.bank.servlet;

import com.bank.dao.AccountDAO;
import com.bank.dao.CustomerDAO;
import com.bank.dto.ApiResponse;
import com.bank.entity.Account;
import com.bank.entity.Customer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.java.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.logging.Level;

@Log
@WebServlet("/api/accounts/*")
public class AccountServlet extends HttpServlet {

    private AccountDAO accountDAO;
    private CustomerDAO customerDAO;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        accountDAO = new AccountDAO();
        customerDAO = new CustomerDAO();
        gson = new GsonBuilder().setPrettyPrinting().create();
        log.info("✅ AccountServlet initialized");
    }

    // GET /api/accounts - لیست همه حساب‌ها
    // GET /api/accounts/{id} - دریافت یک حساب
    // GET /api/accounts/customer/{customerId} - حساب‌های یک مشتری
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        String pathInfo = req.getPathInfo();

        try {
            // GET /api/accounts/customer/{customerId}
            if (pathInfo != null && pathInfo.startsWith("/customer/")) {
                Long customerId = Long.parseLong(pathInfo.substring(10));
                List<Account> accounts = accountDAO.findByCustomerId(customerId);
                out.print(gson.toJson(ApiResponse.success(accounts)));
                return;
            }

            // GET /api/accounts/type/{type}
            if (pathInfo != null && pathInfo.startsWith("/type/")) {
                String typeStr = pathInfo.substring(6);
                Account.AccountType type = Account.AccountType.valueOf(typeStr);
                List<Account> accounts = accountDAO.findByAccountType(type);
                out.print(gson.toJson(ApiResponse.success(accounts)));
                return;
            }

            // GET /api/accounts/{id}
            if (pathInfo != null && pathInfo.matches("/\\d+")) {
                Long id = Long.parseLong(pathInfo.substring(1));
                accountDAO.findById(id)
                        .ifPresentOrElse(
                                account -> out.print(gson.toJson(ApiResponse.success(account))),
                                () -> {
                                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                                    out.print(gson.toJson(ApiResponse.error("Account not found")));
                                }
                        );
                return;
            }

            // GET /api/accounts - همه حساب‌ها
            List<Account> accounts = accountDAO.findAll();
            out.print(gson.toJson(ApiResponse.success(accounts)));

        } catch (IllegalArgumentException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.toJson(ApiResponse.error("Invalid account type")));
        } catch (Exception e) {
            log.log(Level.SEVERE, "Error in GET request", e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.toJson(ApiResponse.error(e.getMessage())));
        }
    }

    // POST /api/accounts - ایجاد حساب جدید
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        try {
            BufferedReader reader = req.getReader();
            JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);

            String accountNumber = jsonObject.get("accountNumber").getAsString();
            String accountTypeStr = jsonObject.get("accountType").getAsString();
            Double balance = jsonObject.get("balance").getAsDouble();
            Long customerId = jsonObject.get("customerId").getAsLong();

            // اعتبارسنجی
            if (accountDAO.findByAccountNumber(accountNumber).isPresent()) {
                resp.setStatus(HttpServletResponse.SC_CONFLICT);
                out.print(gson.toJson(ApiResponse.error("Account number already exists")));
                return;
            }

            Customer customer = customerDAO.findById(customerId)
                    .orElseThrow(() -> new RuntimeException("Customer not found"));

            Account.AccountType accountType = Account.AccountType.valueOf(accountTypeStr);
            Account account = new Account(accountNumber, accountType, balance);
            account.setCustomer(customer);

            Account created = accountDAO.create(account);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            out.print(gson.toJson(ApiResponse.success("Account created successfully", created)));

        } catch (RuntimeException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            out.print(gson.toJson(ApiResponse.error(e.getMessage())));
        } catch (Exception e) {
            log.log(Level.SEVERE, "Error in POST request", e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.toJson(ApiResponse.error(e.getMessage())));
        }
    }

    // PATCH /api/accounts/deposit - واریز وجه
    // PATCH /api/accounts/withdraw - برداشت وجه
    @Override
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        String pathInfo = req.getPathInfo();

        try {
            BufferedReader reader = req.getReader();
            JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
            Long accountId = jsonObject.get("accountId").getAsLong();
            Double amount = jsonObject.get("amount").getAsDouble();
            String description = jsonObject.has("description") ?
                    jsonObject.get("description").getAsString() : "";

            if (pathInfo.equals("/deposit")) {
                var transaction = accountDAO.deposit(accountId, amount, description);
                out.print(gson.toJson(ApiResponse.success("Deposit successful", transaction)));
            } else if (pathInfo.equals("/withdraw")) {
                var transaction = accountDAO.withdraw(accountId, amount, description);
                out.print(gson.toJson(ApiResponse.success("Withdrawal successful", transaction)));
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(gson.toJson(ApiResponse.error("Invalid operation")));
            }

        } catch (RuntimeException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.toJson(ApiResponse.error(e.getMessage())));
        } catch (Exception e) {
            log.log(Level.SEVERE, "Error in PATCH request", e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.toJson(ApiResponse.error(e.getMessage())));
        }
    }

    // DELETE /api/accounts/{id} - حذف حساب
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        String pathInfo = req.getPathInfo();

        if (pathInfo == null || !pathInfo.matches("/\\d+")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();

        try {
            Long id = Long.parseLong(pathInfo.substring(1));
            accountDAO.delete(id);
            out.print(gson.toJson(ApiResponse.success("Account deleted successfully", null)));

        } catch (Exception e) {
            log.log(Level.SEVERE, "Error in DELETE request", e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.toJson(ApiResponse.error(e.getMessage())));
        }
    }
}