
package jene.junit;

import jene.rna.Expression;
import jene.rna.LinearConcentrationModel;

import org.junit.*;
import static org.junit.Assert.*;

public class LinearConcentrationModelTest {
    private static final LinearConcentrationModel MODEL = LinearConcentrationModel.global();

    @Test public void testAll() {
        assertConcentration(0.0, 0.0);
        assertConcentration(0.0, 0.12);

        assertConcentration(    0.14,    0.14);
        assertConcentration(    1.0,     1.0);
        assertConcentration(  100.0,   100.0);
        assertConcentration(10000.0, 10000.0);

        assertConcentration(1.0E+05,  100000.0);
        assertConcentration(1.0E+05, 9999999.0);
    }

    private void assertConcentration(double expected, double expression) {
        double actual = MODEL.translate(Expression.valueOf(expression)).doubleValue();
        assertEquals(expected, actual, 0.0001);
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jene.junit.LinearConcentrationModelTest");
    }
}
