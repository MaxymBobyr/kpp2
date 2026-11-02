package ua.edu.nupp.kpp.server;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

@WebServlet("/equipment")
public class EquipmentServlet extends HttpServlet {

    private final EquipmentService service = new EquipmentService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        out.println("<h1>Equipment</h1>");
        out.println("<ul>");
        try {
            for (Equipment e : service.getAll()) {
                out.println("<li>" + e.getId() + ": " + e.getName() + " (" + e.getStatus() + ") - " + e.getLocation() + "</li>");
            }
        } catch (SQLException e) {
            out.println("<li>Error: " + e.getMessage() + "</li>");
        }
        out.println("</ul>");
    }
}
