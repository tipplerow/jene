
package jene.junit;

import java.util.List;

import jam.data.DataMatrix;
import jam.data.DenseDataMatrix;
import jam.junit.NumericTestBase;

import jene.hla.Allele;
import jene.hla.ExpressionClassI;
import jene.hla.Genotype;
import jene.hla.Locus;
import jene.hugo.HugoSymbol;
import jene.rna.Expression;
import jene.rna.TumorExpressionMatrix;
import jene.tcga.TumorBarcode;

import org.junit.*;
import static org.junit.Assert.*;

public class ExpressionClassITest extends NumericTestBase {
    private static final Allele A1 = Allele.instance("A0201");
    private static final Allele A2 = Allele.instance("A3010");

    private static final Allele B1 = Allele.instance("B0702");
    private static final Allele B2 = Allele.instance("B3501");

    private static final Allele C1 = Allele.instance("C0103");
    private static final Allele C2 = Allele.instance("C0702");

    private static final Genotype homoA2 = Genotype.instance(A2, A2, B1, B2, C1, C2);
    private static final Genotype homoB1 = Genotype.instance(A1, A2, B1, B1, C1, C2);
    private static final Genotype hetero = Genotype.instance(A1, A2, B1, B2, C1, C2);

    private static final TumorBarcode tumor1 = TumorBarcode.instance("Tumor1");

    private static final HugoSymbol hlaA = Locus.A.getHugoSymbol();
    private static final HugoSymbol hlaB = Locus.B.getHugoSymbol();
    private static final HugoSymbol hlaC = Locus.C.getHugoSymbol();

    private static final double exprA = 30000.0;
    private static final double exprB = 50000.0;
    private static final double exprC = 20000.0;

    private final ExpressionClassI expr;

    public ExpressionClassITest() {
        this.expr = createExpression();
    }

    private ExpressionClassI createExpression() {
        List<TumorBarcode> barcodes = List.of(tumor1);
        List<HugoSymbol>   symbols  = List.of(hlaA, hlaB, hlaC);
        
        DataMatrix<TumorBarcode, HugoSymbol> dataMatrix =
            DenseDataMatrix.create(barcodes, symbols);

        dataMatrix.set(tumor1, hlaA, exprA);
        dataMatrix.set(tumor1, hlaB, exprB);
        dataMatrix.set(tumor1, hlaC, exprC);

        TumorExpressionMatrix exprMatrix = new TumorExpressionMatrix(dataMatrix);
        return ExpressionClassI.create(tumor1, exprMatrix);
    }

    @Test public void testExpression() {
        assertEquals(Expression.valueOf(exprA), expr.get(Locus.A));
        assertEquals(Expression.valueOf(exprB), expr.get(Locus.B));
        assertEquals(Expression.valueOf(exprC), expr.get(Locus.C));
    }

    @Test public void testDiversity() {
        assertDouble(0.95625, expr.diversity(homoA2));
        assertDouble(0.85625, expr.diversity(homoB1));
        assertDouble(0.97200, expr.diversity(hetero));
    }

    @Test public void testHerfindahl() {
        assertDouble(0.235, expr.herfindahl(homoA2));
        assertDouble(0.315, expr.herfindahl(homoB1));
        assertDouble(0.190, expr.herfindahl(hetero));
    }

    @Test public void testNormalize() {
        //
        // Alleles at a homozygous locus get the full gene expression;
        // at heterozygous loci, the expression is split evenly...
        //
        assertDouble(0.30, expr.normalize(A2, homoA2));
        assertDouble(0.25, expr.normalize(B1, homoA2));
        assertDouble(0.25, expr.normalize(B2, homoA2));
        assertDouble(0.10, expr.normalize(C1, homoA2));
        assertDouble(0.10, expr.normalize(C2, homoA2));

        assertDouble(0.15, expr.normalize(A1, homoB1));
        assertDouble(0.15, expr.normalize(A2, homoB1));
        assertDouble(0.50, expr.normalize(B1, homoB1));
        assertDouble(0.10, expr.normalize(C1, homoB1));
        assertDouble(0.10, expr.normalize(C2, homoB1));

        assertDouble(0.15, expr.normalize(A1, hetero));
        assertDouble(0.15, expr.normalize(A2, hetero));
        assertDouble(0.25, expr.normalize(B1, hetero));
        assertDouble(0.25, expr.normalize(B2, hetero));
        assertDouble(0.10, expr.normalize(C1, hetero));
        assertDouble(0.10, expr.normalize(C2, hetero));
    }

    @Test public void testTotal() {
        assertEquals(Expression.valueOf(100000.0), expr.total());
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jene.junit.ExpressionClassITest");
    }
}
