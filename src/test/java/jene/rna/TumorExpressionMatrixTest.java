
package jene.rna;

import java.util.List;

import jam.junit.NumericTestBase;

import jene.hugo.HugoSymbol;
import jene.tcga.TumorBarcode;

import org.junit.*;
import static org.junit.Assert.*;

public class TumorExpressionMatrixTest extends NumericTestBase {
    private static final TumorBarcode tumor1 = TumorBarcode.instance("OR-A5J1");
    private static final TumorBarcode tumor2 = TumorBarcode.instance("OR-A5J2");
    private static final TumorBarcode tumor3 = TumorBarcode.instance("OR-A5J3");

    private static final HugoSymbol gene1 = HugoSymbol.instance("A1BG");
    private static final HugoSymbol gene2 = HugoSymbol.instance("A1CF");
    private static final HugoSymbol gene3 = HugoSymbol.instance("A2BP1");
    private static final HugoSymbol gene4 = HugoSymbol.instance("A2LD1");
    private static final HugoSymbol gene5 = HugoSymbol.instance("A2M");

    private static final TumorBarcode bad_tumor = TumorBarcode.instance("bad_tumor");
    private static final HugoSymbol   bad_gene  = HugoSymbol.instance("bad_gene");

    private static final TumorExpressionMatrix matrix =
        TumorExpressionMatrix.load("data/test/tumor_expression.csv");

    @Test public void testAll() {
        assertTrue(matrix.contains(tumor1));
        assertTrue(matrix.contains(tumor2));
        assertTrue(matrix.contains(tumor3));

        assertTrue(matrix.contains(gene1));
        assertTrue(matrix.contains(gene2));
        assertTrue(matrix.contains(gene3));
        assertTrue(matrix.contains(gene4));
        assertTrue(matrix.contains(gene5));

        assertExpression(  16.3305, matrix.get(tumor1, gene1));
        assertExpression(   5.6368, matrix.get(tumor2, gene3));
        assertExpression(7201.84,   matrix.get(tumor3, gene5));

        assertFalse(matrix.contains(bad_tumor));
        assertFalse(matrix.contains(bad_gene));

        assertNull(matrix.get(tumor1, bad_gene));
        assertNull(matrix.get(bad_tumor, gene1));
        assertNull(matrix.get(bad_tumor, bad_gene));

        assertEquals(List.of(tumor1, tumor2, tumor3), matrix.viewBarcodes());
        assertEquals(List.of(gene1, gene2, gene3, gene4, gene5), matrix.viewSymbols());

        ExpressionProfile profile = matrix.get(tumor3);

        assertExpression(  20.7377, profile.get(gene1));
        assertExpression(   0.5925, profile.get(gene2));
        assertExpression(   8.8876, profile.get(gene3));
        assertExpression( 138.883,  profile.get(gene4));
        assertExpression(7201.84,   profile.get(gene5));
    }

    private void assertExpression(double expected, Expression actual) {
        assertEquals(expected, actual.doubleValue(), 0.0001);
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jene.rna.TumorExpressionMatrixTest");
    }
}
