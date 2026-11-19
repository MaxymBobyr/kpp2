package ua.edu.nupp.kpp.server;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class EquipmentServlet extends HttpServlet {

    private final EquipmentService service = new EquipmentService();
    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        try {
            List<Equipment> list = service.getAll();
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
                Equipment e = new Equipment(
                        null,
                        req.getParameter("name"),
                        req.getParameter("model"),
                        req.getParameter("serialNumber"),
                        req.getParameter("status"),
                        req.getParameter("location")
                );
                service.add(e);
                writeJson(resp, e);
            } else if ("update".equals(action)) {
                Long id = Long.parseLong(req.getParameter("id"));
                Equipment e = new Equipment(
                        id,
                        req.getParameter("name"),
                        req.getParameter("model"),
                        req.getParameter("serialNumber"),
                        req.getParameter("status"),
                        req.getParameter("location")
                );
                service.update(e);
                writeJson(resp, e);
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
