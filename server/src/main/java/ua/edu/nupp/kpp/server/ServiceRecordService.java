package ua.edu.nupp.kpp.server;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class ServiceRecordService {

    private static final Logger LOG = Logger.getLogger(ServiceRecordService.class.getName());

    private final ServiceRecordDAO dao = new ServiceRecordDAO();
    private final ServiceRequestDAO serviceRequestDAO = new ServiceRequestDAO();
    private final SpecialistDAO specialistDAO = new SpecialistDAO();

    public List<ServiceRecord> getAll() throws SQLException {
        return dao.getAll();
    }

    public ServiceRecord getById(long id) throws SQLException {
        return dao.getById(id);
    }

    public void add(ServiceRecord record) throws SQLException {
        validate(record);
        requireServiceRequest(record.getRequestId());
        requireSpecialist(record.getSpecialistId());
        dao.add(record);
        LOG.info("Додано сервісний запис");
    }

    public void update(ServiceRecord record) throws SQLException {
        validate(record);
        requireServiceRequest(record.getRequestId());
        requireSpecialist(record.getSpecialistId());
        dao.update(record);
        LOG.info("Оновлено сервісний запис");
    }

    public void delete(long id) throws SQLException {
        dao.delete(id);
        LOG.info("Видалено сервісний запис");
    }

    private void validate(ServiceRecord record) {
        if (record == null) {
            throw new IllegalArgumentException("Сервісний запис не може бути null");
        }
        if (blank(record.getWorkDescription())) {
            throw new IllegalArgumentException("Поле workDescription є обов'язковим");
        }
        if (record.getTimeSpent() <= 0) {
            throw new IllegalArgumentException("timeSpent має бути більше 0");
        }
        if (record.getRequestId() <= 0) {
            throw new IllegalArgumentException("requestId має бути більше 0");
        }
        if (record.getSpecialistId() <= 0) {
            throw new IllegalArgumentException("specialistId має бути більше 0");
        }
    }

    private void requireServiceRequest(long requestId) {
        if (serviceRequestDAO.getById(requestId) == null) {
            throw new IllegalArgumentException("Заявку не знайдено");
        }
    }

    private void requireSpecialist(long specialistId) {
        if (specialistDAO.getById(specialistId) == null) {
            throw new IllegalArgumentException("Фахівця не знайдено");
        }
    }

    private static boolean blank(String v) {
        return v == null || v.isBlank();
    }
}
