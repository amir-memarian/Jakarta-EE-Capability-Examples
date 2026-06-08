package com.bank.servlet;

import com.bank.util.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/test")
public class TestServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();

        out.println("<html><body><h1>JPA Diagnostic Test</h1>");

        try {
            out.println("<p>Testing JpaUtil...</p>");
            EntityManager em = JpaUtil.getEntityManager();
            out.println("<p style='color:green'>✅ JpaUtil.getEntityManager() SUCCESS</p>");

            Object result = em.createNativeQuery("SELECT 1").getSingleResult();
            out.println("<p style='color:green'>✅ Database query SUCCESS: " + result + "</p>");

            em.close();
            out.println("<p style='color:green'>✅ Connection closed</p>");
            out.println("<p style='color:green;font-size:20px'>✅ ALL TESTS PASSED! JPA is working.</p>");

        } catch (Exception e) {
            out.println("<p style='color:red'>❌ ERROR: " + e.getMessage() + "</p>");
            out.println("<pre>");
            e.printStackTrace(out);
            out.println("</pre>");
        }

        out.println("</body></html>");
    }
}