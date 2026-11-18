package ua.edu.nupp.kpp.server;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class ServiceRequestService {

    private static final Logger LOG = Logger.getLogger(ServiceRequestService.class.getName());

    private final ServiceRequestDAO dao = new ServiceRequestDAO();
    private final EquipmentDAO equipmentDAO = new EquipmentDAO();

    public List<ServiceRequest> getAll() throws SQLException {
        return dao.getAll();
    }

    public ServiceRequest getById(long id) throws SQLException {
        return dao.getById(id);
    }

    public void add(ServiceRequest request) throws SQLException {
        validate(request);
        requireEquipment(request.getEquipmentId());
        request.setIntegralIndicator(calculateIntegralIndicator(request.getF1(), request.getF2(), request.getF3()));
        dao.add(request);
        LOG.info("Додано заявку на обслуговування");
    }

    public void update(ServiceRequest request) throws SQLException {
        validate(request);
        requireEquipment(request.getEquipmentId());
        request.setIntegralIndicator(calculateIntegralIndicator(request.getF1(), request.getF2(), request.getF3()));
        if (request.getId() != null) {
            ServiceRequest current = dao.getById(request.getId());
            if (current != null
                    && current.getState() != null
                    && request.getState() != null
                    && current.getState() != request.getState()
                    && !isAllowedTransition(current.getState(), request.getState())) {
                LOG.warning("Недопустимий перехід стану (update): " + current.getState() + " -> " + request.getState());
                throw new IllegalArgumentException("Недопустимий перехід стану");
            }
        }
        dao.update(request);
        LOG.info("Оновлено заявку на обслуговування");
    }

    public void delete(long id) throws SQLException {
        dao.delete(id);
        LOG.info("Видалено заявку на обслуговування");
    }

    public static boolean isAllowedTransition(ServiceRequestState from, ServiceRequestState to) {
        return (from == ServiceRequestState.NEW && to == ServiceRequestState.DIAGNOSED)
            || (from == ServiceRequestState.DIAGNOSED && to == ServiceRequestState.IN_PROGRESS)
            || (from == ServiceRequestState.IN_PROGRESS && to == ServiceRequestState.COMPLETED);
    }

    public void changeState(long id, ServiceRequestState next) throws SQLException {
        ServiceRequest current = dao.getById(id);

        if (current == null) {
            throw new IllegalArgumentException("Заявку не знайдено");
        }

        if (!isAllowedTransition(current.getState(), next)) {
            LOG.warning("Недопустимий перехід стану: " + current.getState() + " -> " + next);
            throw new IllegalArgumentException("Недопустимий перехід стану");
        }

        dao.updateState(id, next);
        LOG.info("Змінено стан заявки: " + current.getState() + " -> " + next);
    }

    public double calculateIntegralIndicator(double f1, double f2, double f3) {
        return RiskCalculator.calculate(f1, f2, f3);
    }

    private void validate(ServiceRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Заявку не може бути null");
        }
        if (blank(request.getDescription())) {
            throw new IllegalArgumentException("Поле description є обов'язковим");
        }
        if (request.getF1() < 0 || request.getF1() > 100) {
            throw new IllegalArgumentException("F1 має бути в діапазоні 0..100");
        }
        if (request.getF2() < 0 || request.getF2() > 100) {
            throw new IllegalArgumentException("F2 має бути в діапазоні 0..100");
        }
        if (request.getF3() < 0 || request.getF3() > 100) {
            throw new IllegalArgumentException("F3 має бути в діапазоні 0..100");
        }
        if (request.getState() == null) {
            throw new IllegalArgumentException("Поле state є обов'язковим");
        }
        if (request.getEquipmentId() <= 0) {
            throw new IllegalArgumentException("equipmentId має бути більше 0");
        }
    }

    private void requireEquipment(long equipmentId) {
        if (equipmentDAO.getById(equipmentId) == null) {
            throw new IllegalArgumentException("Обладнання не знайдено");
        }
    }

    private static boolean blank(String v) {
        return v == null || v.isBlank();
    }
}
