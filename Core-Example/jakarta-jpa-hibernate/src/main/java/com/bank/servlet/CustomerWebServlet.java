package com.bank.servlet;

import com.bank.entity.Customer;
import com.bank.util.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/customers")
public class CustomerWebServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String action = req.getParameter("action");

        if ("delete".equals(action)) {
            doDelete(req, resp);
        } else if ("edit".equals(action)) {
            showEditForm(req, resp);
        } else {
            listCustomers(req, resp);
        }
    }

    private void listCustomers(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        EntityManager em = JpaUtil.getEntityManager();
        try {
            TypedQuery<Customer> query = em.createQuery(
                    "SELECT c FROM Customer c ORDER BY c.id DESC", Customer.class);
            List<Customer> customers = query.getResultList();
            req.setAttribute("customers", customers);
            req.getRequestDispatcher("/WEB-INF/customers.jsp").forward(req, resp);
        } finally {
            em.close();
        }
    }

    private void showEditForm(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Long id = Long.parseLong(req.getParameter("id"));
        EntityManager em = JpaUtil.getEntityManager();
        try {
            Customer customer = em.find(Customer.class, id);
            req.setAttribute("customer", customer);
            req.getRequestDispatcher("/WEB-INF/customer-form.jsp").forward(req, resp);
        } finally {
            em.close();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        String id = req.getParameter("id");
        String fullName = req.getParameter("fullName");
        String email = req.getParameter("email");
        String phone = req.getParameter("phone");

        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();

            Customer customer;
            if (id == null || id.isEmpty()) {
                // Create new
                customer = new Customer();
                customer.setFullName(fullName);
                customer.setEmail(email);
                customer.setPhone(phone);
                em.persist(customer);
            } else {
                // Update existing
                customer = em.find(Customer.class, Long.parseLong(id));
                customer.setFullName(fullName);
                customer.setEmail(email);
                customer.setPhone(phone);
                em.merge(customer);
            }

            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }

        resp.sendRedirect(req.getContextPath() + "/customers");
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        Long id = Long.parseLong(req.getParameter("id"));
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Customer customer = em.find(Customer.class, id);
            if (customer != null) {
                if (customer.getAddress() != null) {
                    em.remove(customer.getAddress());
                }
                em.remove(customer);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }

        resp.sendRedirect(req.getContextPath() + "/customers");
    }
}