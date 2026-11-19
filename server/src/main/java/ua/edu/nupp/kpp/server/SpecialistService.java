package ua.edu.nupp.kpp.server;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class SpecialistService {

    private static final Logger LOG = Logger.getLogger(SpecialistService.class.getName());

    private final SpecialistDAO dao = new SpecialistDAO();

    public List<Specialist> getAll() throws SQLException {
        return dao.getAll();
    }

    public Specialist getById(long id) throws SQLException {
        return dao.getById(id);
    }

    public void add(Specialist specialist) throws SQLException {
        validate(specialist);
        dao.add(specialist);
        LOG.info("Додано фахівця");
    }

    public void update(Specialist specialist) throws SQLException {
        validate(specialist);
        dao.update(specialist);
        LOG.info("Оновлено фахівця");
    }

    public void delete(long id) throws SQLException {
        dao.delete(id);
        LOG.info("Видалено фахівця");
    }

    private void validate(Specialist specialist) {
        if (specialist == null) {
            throw new IllegalArgumentException("Фахівця не може бути null");
        }
        if (blank(specialist.getFullName())) {
            throw new IllegalArgumentException("Поле fullName є обов'язковим");
        }
        if (blank(specialist.getAvailabilityStatus())) {
            throw new IllegalArgumentException("Поле availabilityStatus є обов'язковим");
        }
    }

    private static boolean blank(String v) {
        return v == null || v.isBlank();
    }
}
