package ua.edu.nupp.kpp.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ServiceRequestDAO {

    private static final String SQL_FIND_ALL =
        "SELECT id, equipment_id, description, priority, status, created_date FROM service_request ORDER BY id";
    private static final String SQL_FIND_BY_ID =
        "SELECT id, equipment_id, description, priority, status, created_date FROM service_request WHERE id = ?";
    private static final String SQL_INSERT =
        "INSERT INTO service_request (equipment_id, description, priority, status, created_date) VALUES (?, ?, ?, ?, ?)";
    private static final String SQL_UPDATE =
        "UPDATE service_request SET equipment_id = ?, description = ?, priority = ?, status = ?, created_date = ? WHERE id = ?";
    private static final String SQL_DELETE =
        "DELETE FROM service_request WHERE id = ?";

    public List<ServiceRequest> getAll() {
        List<ServiceRequest> list = new ArrayList<>();
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(SQL_FIND_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public ServiceRequest getById(long id) {
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(SQL_FIND_BY_ID)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public void add(ServiceRequest request) {
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(SQL_INSERT, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, request.getEquipmentId());
            ps.setString(2, request.getDescription());
            ps.setString(3, request.getPriority());
            ps.setString(4, request.getState() != null ? request.getState().name() : null);
            ps.setDate(5, java.sql.Date.valueOf(request.getCreatedDate()));
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    request.setId(keys.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void update(ServiceRequest request) {
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(SQL_UPDATE)) {
            ps.setLong(1, request.getEquipmentId());
            ps.setString(2, request.getDescription());
            ps.setString(3, request.getPriority());
            ps.setString(4, request.getState() != null ? request.getState().name() : null);
            ps.setDate(5, java.sql.Date.valueOf(request.getCreatedDate()));
            ps.setLong(6, request.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(long id) {
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(SQL_DELETE)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateState(long id, ServiceRequestState state) {
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement("UPDATE service_request SET status = ? WHERE id = ?")) {
            ps.setString(1, state.name());
            ps.setLong(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private ServiceRequest mapRow(ResultSet rs) throws SQLException {
        ServiceRequest r = new ServiceRequest();
        r.setId(rs.getLong("id"));
        r.setEquipmentId(rs.getLong("equipment_id"));
        r.setDescription(rs.getString("description"));
        r.setPriority(rs.getString("priority"));
        String state = rs.getString("status");
        r.setState(state != null ? ServiceRequestState.valueOf(state) : null);
        java.sql.Date d = rs.getDate("created_date");
        r.setCreatedDate(d != null ? d.toLocalDate() : null);
        return r;
    }
}
