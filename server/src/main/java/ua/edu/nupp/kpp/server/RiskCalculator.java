package ua.edu.nupp.kpp.server;

public class RiskCalculator {

    private RiskCalculator() {
    }

    public static double factor1(double concentration, double limitValue) {
        if (concentration < 0) {
            throw new IllegalArgumentException("concentration не може бути від'ємним");
        }
        if (limitValue <= 0) {
            throw new IllegalArgumentException("limitValue має бути більше 0");
        }
        double f1 = (concentration - limitValue) / limitValue * 100.0;
        if (f1 < 0) {
            f1 = 0;
        }
        if (f1 > 100) {
            f1 = 100;
        }
        return f1;
    }

    public static double calculate(double f1, double f2, double f3) {
        if (f1 < 0 || f1 > 100 || f2 < 0 || f2 > 100 || f3 < 0 || f3 > 100) {
            throw new IllegalArgumentException("Фактори мають бути в діапазоні 0..100");
        }
        double result = 0.40 * f1 + 0.35 * f2 + 0.25 * f3;
        return Math.round(result * 100.0) / 100.0;
    }
}
