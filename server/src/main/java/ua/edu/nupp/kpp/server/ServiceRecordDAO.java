package ua.edu.nupp.kpp.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ServiceRecordDAO {

    private static final String SQL_FIND_ALL =
        "SELECT id, request_id, specialist_id, work_description, completion_date, time_spent FROM service_record ORDER BY id";
    private static final String SQL_FIND_BY_ID =
        "SELECT id, request_id, specialist_id, work_description, completion_date, time_spent FROM service_record WHERE id = ?";
    private static final String SQL_INSERT =
        "INSERT INTO service_record (request_id, specialist_id, work_description, completion_date, time_spent) VALUES (?, ?, ?, ?, ?)";
    private static final String SQL_UPDATE =
        "UPDATE service_record SET request_id = ?, specialist_id = ?, work_description = ?, completion_date = ?, time_spent = ? WHERE id = ?";
    private static final String SQL_DELETE =
        "DELETE FROM service_record WHERE id = ?";

    public List<ServiceRecord> getAll() {
        List<ServiceRecord> list = new ArrayList<>();
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

    public ServiceRecord getById(long id) {
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

    public void add(ServiceRecord record) {
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(SQL_INSERT, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, record.getRequestId());
            ps.setLong(2, record.getSpecialistId());
            ps.setString(3, record.getWorkDescription());
            ps.setDate(4, java.sql.Date.valueOf(record.getCompletionDate()));
            ps.setInt(5, record.getTimeSpent());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    record.setId(keys.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void update(ServiceRecord record) {
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(SQL_UPDATE)) {
            ps.setLong(1, record.getRequestId());
            ps.setLong(2, record.getSpecialistId());
            ps.setString(3, record.getWorkDescription());
            ps.setDate(4, java.sql.Date.valueOf(record.getCompletionDate()));
            ps.setInt(5, record.getTimeSpent());
            ps.setLong(6, record.getId());
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

    private ServiceRecord mapRow(ResultSet rs) throws SQLException {
        ServiceRecord r = new ServiceRecord();
        r.setId(rs.getLong("id"));
        r.setRequestId(rs.getLong("request_id"));
        r.setSpecialistId(rs.getLong("specialist_id"));
        r.setWorkDescription(rs.getString("work_description"));
        java.sql.Date d = rs.getDate("completion_date");
        r.setCompletionDate(d != null ? d.toLocalDate() : null);
        r.setTimeSpent(rs.getInt("time_spent"));
        return r;
    }
}
