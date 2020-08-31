
package jene.junit;

import jene.chem.Concentration;
import jene.rna.Expression;
import jene.rna.StepConcentrationModel;

import org.junit.*;
import static org.junit.Assert.*;

public class StepConcentrationModelTest {
    @Test public void testAll() {
        Concentration zero = Concentration.valueOf(0.0);
        Concentration unit = Concentration.valueOf(1.0);

        StepConcentrationModel model = StepConcentrationModel.global();

        assertEquals(zero, model.translate(Expression.valueOf(0.0)));
        assertEquals(zero, model.translate(Expression.valueOf(0.12)));
        assertEquals(unit, model.translate(Expression.valueOf(0.14)));
        assertEquals(unit, model.translate(Expression.valueOf(10.0)));
        assertEquals(unit, model.translate(Expression.valueOf(9999.0)));
        assertEquals(unit, model.translate(Expression.valueOf(999999.0)));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jene.junit.StepConcentrationModelTest");
    }
}
