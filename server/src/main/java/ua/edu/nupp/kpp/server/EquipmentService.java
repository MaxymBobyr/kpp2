package ua.edu.nupp.kpp.server;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class EquipmentService {

    private static final Logger LOG = Logger.getLogger(EquipmentService.class.getName());

    private final EquipmentDAO dao = new EquipmentDAO();

    public List<Equipment> getAll() throws SQLException {
        return dao.getAll();
    }

    public Equipment getById(long id) throws SQLException {
        return dao.getById(id);
    }

    public void add(Equipment equipment) throws SQLException {
        validate(equipment);
        dao.add(equipment);
        LOG.info("Додано обладнання");
    }

    public void update(Equipment equipment) throws SQLException {
        validate(equipment);
        dao.update(equipment);
        LOG.info("Оновлено обладнання");
    }

    public void delete(long id) throws SQLException {
        dao.delete(id);
        LOG.info("Видалено обладнання");
    }

    private void validate(Equipment equipment) {
        if (equipment == null) {
            throw new IllegalArgumentException("Обладнання не може бути null");
        }
        if (blank(equipment.getName())) {
            throw new IllegalArgumentException("Поле name є обов'язковим");
        }
        if (blank(equipment.getSerialNumber())) {
            throw new IllegalArgumentException("Поле serialNumber є обов'язковим");
        }
        if (blank(equipment.getStatus())) {
            throw new IllegalArgumentException("Поле status є обов'язковим");
        }
    }

    private static boolean blank(String v) {
        return v == null || v.isBlank();
    }
}
