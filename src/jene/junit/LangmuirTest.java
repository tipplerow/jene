
package jene.junit;

import jam.math.Probability;

import jene.chem.Concentration;
import jene.chem.Langmuir;

import org.junit.*;
import static org.junit.Assert.*;

public class LangmuirTest  {
    private static double TOLERANCE = 0.000001;

    @Test public void testInRange() {
        assertEquals(Probability.valueOf(0.0),  Langmuir.probability(Concentration.valueOf(0.0)));
        assertEquals(Probability.valueOf(0.50), Langmuir.probability(Concentration.valueOf(1.0)));
        assertEquals(Probability.valueOf(0.75), Langmuir.probability(Concentration.valueOf(3.0)));
        assertEquals(Probability.valueOf(0.90), Langmuir.probability(Concentration.valueOf(9.0)));

        assertEquals(0.000100, Langmuir.evaluate(Concentration.valueOf(0.0001)), TOLERANCE);
        assertEquals(0.999001, Langmuir.evaluate(Concentration.valueOf(1000.0)), TOLERANCE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNegative() {
        Langmuir.INSTANCE.evaluate(-1.0);
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jene.junit.LangmuirTest");
    }
}
