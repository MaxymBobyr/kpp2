package ua.edu.nupp.kpp.server;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

@WebServlet("/specialist")
public class SpecialistServlet extends HttpServlet {

    private final SpecialistService service = new SpecialistService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        out.println("<h1>Specialists</h1>");
        out.println("<ul>");
        try {
            for (Specialist s : service.getAll()) {
                out.println("<li>" + s.getId() + ": " + s.getFullName() + " - " + s.getSpecialization() + " (" + s.getAvailabilityStatus() + ")</li>");
            }
        } catch (SQLException e) {
            out.println("<li>Error: " + e.getMessage() + "</li>");
        }
        out.println("</ul>");
    }
}
