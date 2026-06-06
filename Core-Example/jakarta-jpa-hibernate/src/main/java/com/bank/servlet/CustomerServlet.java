package com.bank.servlet;

import com.bank.dao.CustomerDAO;
import com.bank.dto.ApiResponse;
import com.bank.dto.ErrorResponse;
import com.bank.entity.Customer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
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
@WebServlet("/api/customers/*")
public class CustomerServlet extends HttpServlet {

    private CustomerDAO customerDAO;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        customerDAO = new CustomerDAO();
        gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        log.info("✅ CustomerServlet initialized");
    }

    // GET /api/customers - لیست همه مشتریان
    // GET /api/customers/{id} - دریافت یک مشتری
    // GET /api/customers?search=name - جستجو
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        String pathInfo = req.getPathInfo();
        String searchParam = req.getParameter("search");

        try {
            // جستجو بر اساس نام
            if (searchParam != null && !searchParam.trim().isEmpty()) {
                List<Customer> customers = customerDAO.findByNameContaining(searchParam);
                out.print(gson.toJson(ApiResponse.success(customers)));
                return;
            }

            // دریافت صفحه‌بندی
            String pageParam = req.getParameter("page");
            String sizeParam = req.getParameter("size");

            if (pageParam != null && sizeParam != null) {
                int page = Integer.parseInt(pageParam);
                int size = Integer.parseInt(sizeParam);
                List<Customer> customers = customerDAO.findAll(page, size);
                out.print(gson.toJson(ApiResponse.success(customers)));
                return;
            }

            // دریافت یک مشتری با ID
            if (pathInfo != null && pathInfo.matches("/\\d+")) {
                Long id = Long.parseLong(pathInfo.substring(1));
                customerDAO.findById(id)
                        .ifPresentOrElse(
                                customer -> out.print(gson.toJson(ApiResponse.success(customer))),
                                () -> {
                                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                                    out.print(gson.toJson(ApiResponse.error("Customer not found")));
                                }
                        );
                return;
            }

            // دریافت همه مشتریان
            List<Customer> customers = customerDAO.findAll();
            out.print(gson.toJson(ApiResponse.success(customers)));

        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.toJson(ApiResponse.error("Invalid ID format")));
        } catch (Exception e) {
            log.log(Level.SEVERE, "Error in GET request", e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.toJson(ApiResponse.error(e.getMessage())));
        }
    }

    // POST /api/customers - ایجاد مشتری جدید
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        try {
            BufferedReader reader = req.getReader();
            Customer customer = gson.fromJson(reader, Customer.class);

            // اعتبارسنجی
            if (customer.getFullName() == null || customer.getFullName().trim().isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(gson.toJson(ApiResponse.error("Full name is required")));
                return;
            }

            // بررسی تکراری نبودن ایمیل
            if (customer.getEmail() != null && !customer.getEmail().isEmpty()) {
                if (customerDAO.existsByEmail(customer.getEmail())) {
                    resp.setStatus(HttpServletResponse.SC_CONFLICT);
                    out.print(gson.toJson(ApiResponse.error("Email already exists")));
                    return;
                }
            }

            Customer created = customerDAO.create(customer);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            out.print(gson.toJson(ApiResponse.success("Customer created successfully", created)));

        } catch (JsonSyntaxException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.toJson(ApiResponse.error("Invalid JSON format")));
        } catch (Exception e) {
            log.log(Level.SEVERE, "Error in POST request", e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.toJson(ApiResponse.error(e.getMessage())));
        }
    }

    // PUT /api/customers/{id} - ویرایش کامل مشتری
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        String pathInfo = req.getPathInfo();

        if (pathInfo == null || !pathInfo.matches("/\\d+")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.toJson(ApiResponse.error("Invalid endpoint. Use /api/customers/{id}")));
            return;
        }

        try {
            Long id = Long.parseLong(pathInfo.substring(1));

            Customer existing = customerDAO.findById(id)
                    .orElseThrow(() -> new RuntimeException("Customer not found"));

            BufferedReader reader = req.getReader();
            Customer updatedCustomer = gson.fromJson(reader, Customer.class);

            // به‌روزرسانی فیلدها
            if (updatedCustomer.getFullName() != null) {
                existing.setFullName(updatedCustomer.getFullName());
            }
            if (updatedCustomer.getEmail() != null) {
                existing.setEmail(updatedCustomer.getEmail());
            }
            if (updatedCustomer.getPhone() != null) {
                existing.setPhone(updatedCustomer.getPhone());
            }

            Customer updated = customerDAO.update(existing);
            out.print(gson.toJson(ApiResponse.success("Customer updated successfully", updated)));

        } catch (RuntimeException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            out.print(gson.toJson(ApiResponse.error(e.getMessage())));
        } catch (Exception e) {
            log.log(Level.SEVERE, "Error in PUT request", e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.toJson(ApiResponse.error(e.getMessage())));
        }
    }

    // DELETE /api/customers/{id} - حذف مشتری
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
            customerDAO.delete(id);
            out.print(gson.toJson(ApiResponse.success("Customer deleted successfully", null)));

        } catch (Exception e) {
            log.log(Level.SEVERE, "Error in DELETE request", e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.toJson(ApiResponse.error(e.getMessage())));
        }
    }
}