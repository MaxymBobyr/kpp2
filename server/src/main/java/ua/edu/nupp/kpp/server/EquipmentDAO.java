package ua.edu.nupp.kpp.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EquipmentDAO {

    private static final String SQL_FIND_ALL =
        "SELECT id, name, model, serial_number, status, location FROM equipment ORDER BY id";
    private static final String SQL_FIND_BY_ID =
        "SELECT id, name, model, serial_number, status, location FROM equipment WHERE id = ?";
    private static final String SQL_INSERT =
        "INSERT INTO equipment (name, model, serial_number, status, location) VALUES (?, ?, ?, ?, ?)";
    private static final String SQL_UPDATE =
        "UPDATE equipment SET name = ?, model = ?, serial_number = ?, status = ?, location = ? WHERE id = ?";
    private static final String SQL_DELETE =
        "DELETE FROM equipment WHERE id = ?";

    public List<Equipment> getAll() {
        List<Equipment> list = new ArrayList<>();
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

    public Equipment getById(long id) {
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

    public void add(Equipment equipment) {
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(SQL_INSERT, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, equipment.getName());
            ps.setString(2, equipment.getModel());
            ps.setString(3, equipment.getSerialNumber());
            ps.setString(4, equipment.getStatus());
            ps.setString(5, equipment.getLocation());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    equipment.setId(keys.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void update(Equipment equipment) {
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(SQL_UPDATE)) {
            ps.setString(1, equipment.getName());
            ps.setString(2, equipment.getModel());
            ps.setString(3, equipment.getSerialNumber());
            ps.setString(4, equipment.getStatus());
            ps.setString(5, equipment.getLocation());
            ps.setLong(6, equipment.getId());
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

    private Equipment mapRow(ResultSet rs) throws SQLException {
        Equipment e = new Equipment();
        e.setId(rs.getLong("id"));
        e.setName(rs.getString("name"));
        e.setModel(rs.getString("model"));
        e.setSerialNumber(rs.getString("serial_number"));
        e.setStatus(rs.getString("status"));
        e.setLocation(rs.getString("location"));
        return e;
    }
}
