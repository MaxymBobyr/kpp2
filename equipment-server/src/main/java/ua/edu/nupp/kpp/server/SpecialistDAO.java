package ua.edu.nupp.kpp.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SpecialistDAO {

    private static final String SQL_FIND_ALL =
        "SELECT id, full_name, specialization, phone, availability_status FROM specialist ORDER BY id";
    private static final String SQL_FIND_BY_ID =
        "SELECT id, full_name, specialization, phone, availability_status FROM specialist WHERE id = ?";
    private static final String SQL_INSERT =
        "INSERT INTO specialist (full_name, specialization, phone, availability_status) VALUES (?, ?, ?, ?)";
    private static final String SQL_UPDATE =
        "UPDATE specialist SET full_name = ?, specialization = ?, phone = ?, availability_status = ? WHERE id = ?";
    private static final String SQL_DELETE =
        "DELETE FROM specialist WHERE id = ?";

    public List<Specialist> getAll() {
        List<Specialist> list = new ArrayList<>();
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

    public Specialist getById(long id) {
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

    public void add(Specialist specialist) {
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(SQL_INSERT, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, specialist.getFullName());
            ps.setString(2, specialist.getSpecialization());
            ps.setString(3, specialist.getPhone());
            ps.setString(4, specialist.getAvailabilityStatus());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    specialist.setId(keys.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void update(Specialist specialist) {
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(SQL_UPDATE)) {
            ps.setString(1, specialist.getFullName());
            ps.setString(2, specialist.getSpecialization());
            ps.setString(3, specialist.getPhone());
            ps.setString(4, specialist.getAvailabilityStatus());
            ps.setLong(5, specialist.getId());
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

    private Specialist mapRow(ResultSet rs) throws SQLException {
        Specialist s = new Specialist();
        s.setId(rs.getLong("id"));
        s.setFullName(rs.getString("full_name"));
        s.setSpecialization(rs.getString("specialization"));
        s.setPhone(rs.getString("phone"));
        s.setAvailabilityStatus(rs.getString("availability_status"));
        return s;
    }
}
