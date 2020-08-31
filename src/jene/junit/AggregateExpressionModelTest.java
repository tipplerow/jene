
package jene.junit;

import jene.hugo.HugoSymbol;
import jene.rna.AggregateExpressionModel;
import jene.rna.Expression;
import jene.rna.ExpressionModel;
import jene.tcga.TumorBarcode;

import org.junit.*;
import static org.junit.Assert.*;

public class AggregateExpressionModelTest {
    static {
        System.setProperty(ExpressionModel.MODEL_TYPE_PROPERTY, "AGGREGATE");
        System.setProperty(AggregateExpressionModel.PROFILE_FILE_NAME_PROPERTY, "data/test/RNA_Median.tsv");
    }

    @Test public void testAll() {
        TumorBarcode tumor1 = TumorBarcode.instance("tumor1");
        TumorBarcode tumor2 = TumorBarcode.instance("tumor2");

        HugoSymbol A1BG  = HugoSymbol.instance("A1BG");
        HugoSymbol A1CF  = HugoSymbol.instance("A1CF");
        HugoSymbol A2BP1 = HugoSymbol.instance("A2BP1");
        HugoSymbol A2LD1 = HugoSymbol.instance("A2LD1");
        HugoSymbol A2M   = HugoSymbol.instance("A2M");
        HugoSymbol BRAF  = HugoSymbol.instance("BRAF");

        ExpressionModel model = ExpressionModel.global();

        assertTrue(model.lookup(tumor1, A1BG).equals(   79.566));
        assertTrue(model.lookup(tumor1, A1CF).equals(    0.0));
        assertTrue(model.lookup(tumor1, A2BP1).equals(   0.544));
        assertTrue(model.lookup(tumor1, A2LD1).equals(  95.357));
        assertTrue(model.lookup(tumor1, A2M).equals(  9740.280));

        assertTrue(model.lookup(tumor2, A1BG).equals(   79.566));
        assertTrue(model.lookup(tumor2, A1CF).equals(    0.0));
        assertTrue(model.lookup(tumor2, A2BP1).equals(   0.544));
        assertTrue(model.lookup(tumor2, A2LD1).equals(  95.357));
        assertTrue(model.lookup(tumor2, A2M).equals(  9740.280));

        assertNull(model.lookup(tumor1, BRAF));
        assertNull(model.lookup(tumor2, BRAF));
    }

    @Test(expected = RuntimeException.class)
    public void testMissing() {
        HugoSymbol   BRAF   = HugoSymbol.instance("BRAF");
        TumorBarcode tumor1 = TumorBarcode.instance("tumor1");

        ExpressionModel.global().require(tumor1, BRAF);
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jene.junit.AggregateExpressionModelTest");
    }
}
