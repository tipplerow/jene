
package jene.chem;

import java.util.Arrays;

import jam.junit.NumericTestBase;

import org.junit.*;
import static org.junit.Assert.*;

public class ConcentrationTest extends NumericTestBase {
    @Test public void testDecay() {
        HalfLife halfLife = HalfLife.valueOf(2.5);

        Concentration original  = Concentration.valueOf(2.0);
        Concentration actual1   = original.decay(halfLife, 2.5);
        Concentration actual2   = original.decay(halfLife, 5.0);
        Concentration expected1 = Concentration.valueOf(1.0);
        Concentration expected2 = Concentration.valueOf(0.5);

        assertEquals(expected1, actual1);
        assertEquals(expected2, actual2);
    }

    @Test public void testFormatParse() {
        Concentration conc1 = Concentration.valueOf(0.1);
        Concentration conc2 = Concentration.parse(" 0.1");

        assertEquals(conc1, conc2);
        assertEquals(conc1, Concentration.parse(conc1.format()));
    }

    @Test public void testMinus() {
        Concentration conc1 = Concentration.valueOf(0.1);
        Concentration conc2 = Concentration.valueOf(0.2);
        Concentration conc3 = Concentration.valueOf(0.3);

        assertEquals(conc1, conc3.minus(conc2));
        assertEquals(conc2, conc3.minus(conc1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMinusInvalid() {
        Concentration conc1 = Concentration.valueOf(0.1);
        Concentration conc2 = Concentration.valueOf(0.2);

        conc1.minus(conc2);
    }

    @Test public void testPlus() {
        Concentration conc1 = Concentration.valueOf(0.1);
        Concentration conc2 = Concentration.valueOf(0.2);
        Concentration conc3 = Concentration.valueOf(0.3);

        assertEquals(conc3, conc1.plus(conc2));
        assertEquals(conc3, conc2.plus(conc1));
    }

    @Test public void testRatio() {
        Concentration conc1 = Concentration.valueOf(0.1);
        Concentration conc2 = Concentration.valueOf(0.2);
        Concentration conc3 = Concentration.valueOf(0.4);

        assertDouble(1.00, Concentration.ratio(conc1, conc1));
        assertDouble(0.50, Concentration.ratio(conc1, conc2));
        assertDouble(0.25, Concentration.ratio(conc1, conc3));

        assertDouble(2.00, Concentration.ratio(conc2, conc1));
        assertDouble(1.00, Concentration.ratio(conc2, conc2));
        assertDouble(0.50, Concentration.ratio(conc2, conc3));

        assertDouble(4.00, Concentration.ratio(conc3, conc1));
        assertDouble(2.00, Concentration.ratio(conc3, conc2));
        assertDouble(1.00, Concentration.ratio(conc3, conc3));
    }

    @Test public void testTimes() {
        Concentration conc1 = Concentration.valueOf(1.5);
        Concentration conc2 = Concentration.valueOf(3.0);

        assertEquals(conc2, conc1.times(2.0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTimesInvalid() {
        Concentration.valueOf(0.1).times(-1.0);
    }

    @Test public void testTotal() {
        Concentration conc1 = Concentration.valueOf(0.1);
        Concentration conc2 = Concentration.valueOf(0.2);
        Concentration conc3 = Concentration.valueOf(0.3);
        Concentration total = Concentration.valueOf(0.6);

        assertEquals(total, Concentration.total(Arrays.asList(conc1, conc2, conc3)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNegativeConcentration() {
        Concentration.valueOf(-1.0);
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jene.chem.ConcentrationTest");
    }
}
