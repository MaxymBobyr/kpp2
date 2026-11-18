package ua.edu.nupp.kpp.server;

import java.sql.SQLException;

public class AlertService {

    private final ServiceRequestDAO measurementDAO = new ServiceRequestDAO();

    public double calculateRiskIndex(long requestId) throws SQLException {
        ServiceRequest request = measurementDAO.getById(requestId);
        if (request == null) {
            throw new IllegalArgumentException("Заявку не знайдено");
        }

        double f1 = RiskCalculator.factor1(
                request.getEquipmentId(),
                100.0
        );
        double f2 = 50.0;
        double f3 = 30.0;

        return RiskCalculator.calculate(f1, f2, f3);
    }

    public double recalculate(long requestId) throws SQLException {
        return calculateRiskIndex(requestId);
    }
}
