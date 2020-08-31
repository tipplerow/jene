
package jene.junit;

import java.util.HashSet;
import java.util.Set;

import jene.hugo.HugoSymbol;
import jene.peptide.Peptide;
import jene.tcga.TumorBarcode;
import jene.tcga.TumorGenePeptideTable;

import org.junit.*;
import static org.junit.Assert.*;

public class TumorGenePeptideTableTest {
    private static final String PEPTIDE_FILE = "data/test/tumor_gene_peptide_table.tsv";

    private static final TumorBarcode Tumor1 = TumorBarcode.instance("Tumor1");
    private static final TumorBarcode Tumor2 = TumorBarcode.instance("Tumor2");
    private static final TumorBarcode TumorX = TumorBarcode.instance("TumorX");

    private static final HugoSymbol A1CF  = HugoSymbol.instance("A1CF");
    private static final HugoSymbol A2M   = HugoSymbol.instance("A2M");
    private static final HugoSymbol A4GNT = HugoSymbol.instance("A4GNT");
    private static final HugoSymbol GeneX = HugoSymbol.instance("GeneX");

    private static final Peptide AAA = Peptide.instance("AAA");
    private static final Peptide VVV = Peptide.instance("VVV");
    private static final Peptide FSF = Peptide.instance("FSF");
    private static final Peptide WRE = Peptide.instance("WRE");
    private static final Peptide DVP = Peptide.instance("DVP");
    private static final Peptide EAE = Peptide.instance("EAE");
    private static final Peptide END = Peptide.instance("END");

    private static final TumorGenePeptideTable table = TumorGenePeptideTable.load(PEPTIDE_FILE);

    @Test public void testContainsBarcode() {
        assertTrue(table.contains(Tumor1));
        assertTrue(table.contains(Tumor2));
        assertFalse(table.contains(TumorX));
    }

    @Test public void testContainsSymbol() {
        assertTrue(table.contains(Tumor1, A1CF));
        assertTrue(table.contains(Tumor1, A2M));
        assertFalse(table.contains(Tumor1, A4GNT));
        assertFalse(table.contains(Tumor1, GeneX));

        assertTrue(table.contains(Tumor2, A1CF));
        assertTrue(table.contains(Tumor2, A4GNT));
        assertFalse(table.contains(Tumor2, A2M));
        assertFalse(table.contains(Tumor2, GeneX));

        assertFalse(table.contains(TumorX, A1CF));
        assertFalse(table.contains(TumorX, A2M));
        assertFalse(table.contains(TumorX, A4GNT));
    }

    @Test public void testGet() {
        assertTrue(table.get(Tumor1, GeneX).isEmpty());

        testGet(Tumor1, A1CF,  AAA, VVV);
        testGet(Tumor1, A2M,   FSF);
        testGet(Tumor2, A1CF,  WRE);
        testGet(Tumor2, A4GNT, DVP, EAE, END);
    }

    private void testGet(TumorBarcode barcode, HugoSymbol symbol, Peptide... expected) {
        assertEquals(Set.of(expected), new HashSet<Peptide>(table.get(barcode, symbol)));
    }

    @Test public void testSize() {
        assertEquals(7, table.size());
    }

    @Test public void testViewBarcodes() {
        assertEquals(Set.of(Tumor1, Tumor2), table.viewBarcodes());
    }

    @Test public void testViewSymbols() {
        assertEquals(Set.of(A1CF, A2M), table.viewSymbols(Tumor1));
        assertEquals(Set.of(A1CF, A4GNT), table.viewSymbols(Tumor2));
        assertEquals(Set.of(), table.viewSymbols(TumorX));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jene.junit.TumorGenePeptideTableTest");
    }
}
