
package jene.junit;

import jam.junit.NumericTestBase;

import jene.chem.Concentration;
import jene.chem.MultiLangmuir;

import org.junit.*;
import static org.junit.Assert.*;

public class MultiLangmuirTest extends NumericTestBase {

    @Test public void testMulti() {
        Concentration conc1 = Concentration.valueOf(0.5);
        Concentration conc2 = Concentration.valueOf(1.5);
        Concentration conc3 = Concentration.valueOf(2.0);

        MultiLangmuir lang = new MultiLangmuir(conc1, conc2, conc3);

        assertDouble(0.1, lang.evaluate(0));
        assertDouble(0.3, lang.evaluate(1));
        assertDouble(0.4, lang.evaluate(2));
    }

    @Test public void testOne() {
        Concentration conc1 = Concentration.valueOf(0.25);
        Concentration conc2 = Concentration.valueOf(1.00);
        Concentration conc3 = Concentration.valueOf(3.00);

        MultiLangmuir lang1 = new MultiLangmuir(conc1);
        MultiLangmuir lang2 = new MultiLangmuir(conc2);
        MultiLangmuir lang3 = new MultiLangmuir(conc3);

        assertDouble(0.20, lang1.evaluate(0));
        assertDouble(0.50, lang2.evaluate(0));
        assertDouble(0.75, lang3.evaluate(0));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jene.junit.MultiLangmuirTest");
    }
}
