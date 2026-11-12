package ua.edu.nupp.kpp.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class ServiceRecordServlet extends HttpServlet {

    private final ServiceRecordService service = new ServiceRecordService();
    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        try {
            List<ServiceRecord> list = service.getAll();
            mapper.writeValue(resp.getWriter(), list);
        } catch (RuntimeException e) {
            writeError(resp, 500, e.getMessage());
        } catch (SQLException e) {
            writeError(resp, 500, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String action = req.getParameter("action");

        try {
            if ("add".equals(action)) {
                ServiceRecord r = new ServiceRecord(
                        null,
                        Long.parseLong(req.getParameter("requestId")),
                        Long.parseLong(req.getParameter("specialistId")),
                        req.getParameter("workDescription"),
                        LocalDate.parse(req.getParameter("completionDate")),
                        Integer.parseInt(req.getParameter("timeSpent"))
                );
                service.add(r);
                writeJson(resp, r);
            } else if ("update".equals(action)) {
                Long id = Long.parseLong(req.getParameter("id"));
                ServiceRecord r = new ServiceRecord(
                        id,
                        Long.parseLong(req.getParameter("requestId")),
                        Long.parseLong(req.getParameter("specialistId")),
                        req.getParameter("workDescription"),
                        LocalDate.parse(req.getParameter("completionDate")),
                        Integer.parseInt(req.getParameter("timeSpent"))
                );
                service.update(r);
                writeJson(resp, r);
            } else if ("delete".equals(action)) {
                Long id = Long.parseLong(req.getParameter("id"));
                service.delete(id);
                writeJson(resp, "deleted");
            } else {
                writeError(resp, 400, "Unknown action: " + action);
            }
        } catch (NumberFormatException e) {
            writeError(resp, 400, "Invalid numeric parameter");
        } catch (IllegalArgumentException e) {
            writeError(resp, 400, e.getMessage());
        } catch (RuntimeException e) {
            writeError(resp, 500, e.getMessage());
        } catch (SQLException e) {
            writeError(resp, 500, e.getMessage());
        }
    }

    private void writeJson(HttpServletResponse resp, Object obj) throws IOException {
        mapper.writeValue(resp.getWriter(), obj);
    }

    private void writeError(HttpServletResponse resp, int status, String message) throws IOException {
        resp.setStatus(status);
        mapper.writeValue(resp.getWriter(), new ErrorResponse(message));
    }

    private static class ErrorResponse {
        private final String error;

        ErrorResponse(String error) {
            this.error = error;
        }

        public String getError() {
            return error;
        }
    }
}
