package ua.edu.nupp.kpp.server;

import java.sql.SQLException;

/**
 * Сервіс для розрахунку інтегрального показника ризику.
 * У цьому варіанті немає окремої таблиці alert, тому сервіс демонструє
 * патерн обчислення факторів F1, F2, F3 та інтегрального показника I
 * на основі даних вимірювання (ServiceRequest).
 */
public class AlertService {

    private final ServiceRequestDAO measurementDAO = new ServiceRequestDAO();

    /**
     * Розраховує фактори та інтегральний показник для заданого вимірювання.
     * Користувач не вводить F1, F2, F3, riskIndex вручну — вони обчислюються
     * серверною частиною на основі даних ServiceRequest.
     *
     * @param measurementId ідентифікатор вимірювання (ServiceRequest)
     * @return розрахований riskIndex (інтегральний показник)
     * @throws SQLException якщо виникла помилка доступу до даних
     */
    public double calculateRiskIndex(long measurementId) throws SQLException {
        ServiceRequest measurement = measurementDAO.getById(measurementId);
        if (measurement == null) {
            throw new IllegalArgumentException("Вимірювання не знайдено");
        }

        // Отримання факторів з вимірювання (за власною предметною моделлю)
        double f1 = RiskCalculator.factor1(
                measurement.getEquipmentId(),  // F1 джерело (приклад)
                100.0                          // limitValue (приклад)
        );
        double f2 = 50.0; // F2 — нормований фактор (за варіантом)
        double f3 = 30.0; // F3 — нормований фактор (за варіантом)

        // Розрахунок інтегрального показника: I = 0.40·F1 + 0.35·F2 + 0.25·F3
        return RiskCalculator.calculate(f1, f2, f3);
    }

    /**
     * Перерахунок показника при зміні вимірювання.
     * Поточний стан зберігається, перераховуються лише F1, F2, F3, riskIndex.
     */
    public double recalculate(long measurementId) throws SQLException {
        return calculateRiskIndex(measurementId);
    }
}
