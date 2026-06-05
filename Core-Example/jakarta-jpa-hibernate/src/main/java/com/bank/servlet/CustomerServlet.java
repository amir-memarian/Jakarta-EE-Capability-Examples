package com.bank.servlet;

import com.bank.entity.Customer;
import com.bank.util.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/customers/*")
public class CustomerServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();

        EntityManager em = JpaUtil.getEntityManager();

        // Read all customers
        List<Customer> customers = em.createQuery("SELECT c FROM Customer c", Customer.class)
                .getResultList();

        out.println("<html><body>");
        out.println("<h2>Customer List</h2>");
        out.println("<table border='1'>");
        out.println("<tr><th>ID</th><th>Full Name</th></tr>");

        for (Customer c : customers) {
            out.printf("<tr><td>%d</td><td>%s</td></tr>%n",
                    c.getId(), c.getFullName());
        }
        out.println("</table>");

        out.println("<h3>Add New Customer</h3>");
        out.println("<form method='post' action='customers'>");
        out.println("Name: <input type='text' name='name' required/>");
        out.println("<input type='submit' value='Add'/>");
        out.println("</form>");

        out.println("</body></html>");
        em.close();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String name = req.getParameter("name");

        if (name != null && !name.trim().isEmpty()) {
            EntityManager em = JpaUtil.getEntityManager();
            try {
                em.getTransaction().begin();
                Customer customer = new Customer(name);
                em.persist(customer);
                em.getTransaction().commit();
            } finally {
                em.close();
            }
        }

        resp.sendRedirect("customers");
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String idParam = req.getParameter("id");
        if (idParam != null) {
            Long id = Long.parseLong(idParam);
            EntityManager em = JpaUtil.getEntityManager();
            try {
                em.getTransaction().begin();
                Customer customer = em.find(Customer.class, id);
                if (customer != null) {
                    em.remove(customer);
                }
                em.getTransaction().commit();
            } finally {
                em.close();
            }
        }
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}