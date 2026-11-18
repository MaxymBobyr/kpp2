package ua.edu.nupp.kpp.server;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RiskCalculatorTest {

    @Test
    void factor1_concentrationAboveLimit_returnsPositive() {
        double f1 = RiskCalculator.factor1(70.0, 40.0);
        assertEquals(75.0, f1);
    }

    @Test
    void factor1_concentrationEqualLimit_returnsZero() {
        double f1 = RiskCalculator.factor1(40.0, 40.0);
        assertEquals(0.0, f1);
    }

    @Test
    void factor1_concentrationBelowLimit_returnsZero() {
        double f1 = RiskCalculator.factor1(20.0, 40.0);
        assertEquals(0.0, f1);
    }

    @Test
    void factor1_negativeConcentration_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> RiskCalculator.factor1(-10.0, 40.0));
    }

    @Test
    void factor1_zeroLimit_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> RiskCalculator.factor1(10.0, 0.0));
    }

    @Test
    void factor1_veryHighConcentration_clampsTo100() {
        double f1 = RiskCalculator.factor1(1000.0, 40.0);
        assertEquals(100.0, f1);
    }

    @Test
    void calculate_normalValues_returnsWeightedSum() {
        double result = RiskCalculator.calculate(50.0, 60.0, 70.0);
        assertEquals(58.5, result);
    }

    @Test
    void calculate_allZeros_returnsZero() {
        double result = RiskCalculator.calculate(0.0, 0.0, 0.0);
        assertEquals(0.0, result);
    }

    @Test
    void calculate_allHundreds_returnsHundred() {
        double result = RiskCalculator.calculate(100.0, 100.0, 100.0);
        assertEquals(100.0, result);
    }

    @Test
    void calculate_negativeFactor_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> RiskCalculator.calculate(-1.0, 50.0, 50.0));
    }

    @Test
    void calculate_factorAbove100_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> RiskCalculator.calculate(50.0, 101.0, 50.0));
    }

    @Test
    void calculate_roundsToTwoDecimals() {
        double result = RiskCalculator.calculate(33.33, 33.33, 33.33);
        assertEquals(33.33, result);
    }
}
