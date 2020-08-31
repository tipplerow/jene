
package jene.junit;

import jene.rna.Expression;
import jene.rna.LogConcentrationModel;

import org.junit.*;
import static org.junit.Assert.*;

public class LogConcentrationModelTest {
    static {
        System.setProperty(LogConcentrationModel.ALPHA_PROPERTY, "2.0");
    }

    private static final LogConcentrationModel MODEL = LogConcentrationModel.global();

    @Test public void testAll() {
        assertConcentration(0.0, 0.0);
        assertConcentration(0.0, 0.12);

        assertConcentration( 0.0677,       0.14);
        assertConcentration( 0.4055,       1.0);
        assertConcentration( 3.9318,     100.0);
        assertConcentration( 8.5174,   10000.0);
        assertConcentration(10.8198,  100000.0);
        assertConcentration(10.8198, 9999999.0);
    }

    private void assertConcentration(double expected, double expression) {
        double actual = MODEL.translate(Expression.valueOf(expression)).doubleValue();
        assertEquals(expected, actual, 0.0001);
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jene.junit.LogConcentrationModelTest");
    }
}
