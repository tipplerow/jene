
package jene.rna;

import java.io.File;
import java.util.List;

import jene.hugo.HugoSymbol;
import jene.tcga.TumorBarcode;

import org.junit.*;
import static org.junit.Assert.*;

public class ExpressionManagerTest {
    private static final HugoSymbol gene1 = HugoSymbol.instance("Gene1");
    private static final HugoSymbol gene2 = HugoSymbol.instance("Gene2");
    private static final HugoSymbol gene3 = HugoSymbol.instance("Gene3");

    private static final Expression expr1 = Expression.valueOf(1.0);
    private static final Expression expr2 = Expression.valueOf(2.0);
    private static final Expression expr3 = Expression.valueOf(3.0);

    private static final TumorBarcode code1 = TumorBarcode.instance("Barcode1");
    private static final TumorBarcode code2 = TumorBarcode.instance("Barcode2");

    private static final String DIR_NAME = "data/test";

    @Test public void testLoadStore() {
        File profileFile = new File(DIR_NAME, "Barcode1_expression_profile.csv.gz");
        profileFile.deleteOnExit();

        ExpressionManager manager =
            ExpressionManager.create(DIR_NAME);

        ExpressionProfile profile =
            ExpressionProfile.create(List.of(gene1, gene2, gene3),
                                     List.of(expr1, expr2, expr3));

        assertFalse(profileFile.exists());
        assertFalse(manager.exists(code1));

        manager.store(code1, profile);

        assertTrue(profileFile.exists());
        assertTrue(manager.exists(code1));

        profile = manager.load(code1);
        assertProfile(profile);

        assertNull(manager.load(code2));
    }

    private void assertProfile(ExpressionProfile profile) {
        assertEquals(expr1, profile.get(gene1));
        assertEquals(expr2, profile.get(gene2));
        assertEquals(expr3, profile.get(gene3));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jene.rna.ExpressionManagerTest");
    }
}
