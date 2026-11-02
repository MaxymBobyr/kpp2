package ua.edu.nupp.kpp.server;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

@WebServlet("/service-record")
public class ServiceRecordServlet extends HttpServlet {

    private final ServiceRecordService service = new ServiceRecordService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        out.println("<h1>Service Records</h1>");
        out.println("<ul>");
        try {
            for (ServiceRecord r : service.getAll()) {
                out.println("<li>#" + r.getId() + " request=" + r.getRequestId() + " specialist=" + r.getSpecialistId() + " - " + r.getWorkDescription() + " (" + r.getTimeSpent() + " min)</li>");
            }
        } catch (SQLException e) {
            out.println("<li>Error: " + e.getMessage() + "</li>");
        }
        out.println("</ul>");
    }
}
