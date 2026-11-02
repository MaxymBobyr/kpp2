package ua.edu.nupp.kpp.server;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

@WebServlet("/service-request")
public class ServiceRequestServlet extends HttpServlet {

    private final ServiceRequestService service = new ServiceRequestService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        out.println("<h1>Service Requests</h1>");
        out.println("<ul>");
        try {
            for (ServiceRequest r : service.getAll()) {
                out.println("<li>#" + r.getId() + " equipment=" + r.getEquipmentId() + " state=" + r.getState() + " - " + r.getDescription() + "</li>");
            }
        } catch (SQLException e) {
            out.println("<li>Error: " + e.getMessage() + "</li>");
        }
        out.println("</ul>");
    }
}
