
package jene.chem;

import jam.junit.NumericTestBase;

import org.junit.*;
import static org.junit.Assert.*;

public class HalfLifeTest extends NumericTestBase {
    @Test public void testConsistency() {
        runConsistencyTest(0.1, 10.0);
        runConsistencyTest(0.1, 33.3);

        runConsistencyTest(1.0, 10.0);
        runConsistencyTest(1.0, 33.3);

        runConsistencyTest(10.0, 10.0);
        runConsistencyTest(10.0, 33.3);
    }

    private void runConsistencyTest(double halfLife, double quantity) {
        HalfLife obj = HalfLife.valueOf(halfLife);

        assertDouble(0.5 * quantity, obj.decay(quantity, halfLife));
        assertDouble((1.0 - obj.getRate()) * quantity, obj.decay(quantity, 1.0));
    }

    @Test public void testDecay() {
        HalfLife halfLife = HalfLife.valueOf(2.0);

        assertDouble(40.0, halfLife.decay(80.0,  2.0));
        assertDouble(20.0, halfLife.decay(80.0,  4.0));
        assertDouble(10.0, halfLife.decay(80.0,  6.0));
        assertDouble( 5.0, halfLife.decay(80.0,  8.0));
        assertDouble( 2.5, halfLife.decay(80.0, 10.0));

        halfLife = HalfLife.valueOf(Double.POSITIVE_INFINITY);

        assertDouble(80.0, halfLife.decay(80.0,   1.0));
        assertDouble(80.0, halfLife.decay(80.0,  10.0));
        assertDouble(80.0, halfLife.decay(80.0, 100.0));
    }

    @Test public void testFormatParse() {
        HalfLife halfLife1 = HalfLife.valueOf(2.5);
        HalfLife halfLife2 = HalfLife.parse(" 2.5");

        assertEquals(halfLife1, halfLife2);
        assertEquals(halfLife1, HalfLife.parse(halfLife1.format()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalid1() {
        HalfLife.valueOf(0.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalid2() {
        HalfLife.valueOf(-1.0);
    }

    @Test public void testRate() {
        assertDouble(0.75, HalfLife.computeRate(0.5));
        assertDouble(0.5, HalfLife.computeRate(1.0));
        assertDouble(1.0 - Math.sqrt(0.5), HalfLife.computeRate(2.0));
        assertDouble(0.0, HalfLife.computeRate(Double.POSITIVE_INFINITY));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jene.chem.HalfLifeTest");
    }
}
