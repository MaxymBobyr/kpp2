package ua.edu.nupp.kpp.server;

import java.sql.SQLException;

public class AlertService {

    private final ServiceRequestDAO measurementDAO = new ServiceRequestDAO();

    public double calculateRiskIndex(long measurementId) throws SQLException {
        ServiceRequest measurement = measurementDAO.getById(measurementId);
        if (measurement == null) {
            throw new IllegalArgumentException("Вимірювання не знайдено");
        }

        double f1 = RiskCalculator.factor1(
                measurement.getEquipmentId(),
                100.0
        );
        double f2 = 50.0;
        double f3 = 30.0;

        return RiskCalculator.calculate(f1, f2, f3);
    }

    public double recalculate(long measurementId) throws SQLException {
        return calculateRiskIndex(measurementId);
    }
}
