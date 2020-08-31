
package jene.junit;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import jene.hugo.HugoSymbol;
import jene.hugo.HugoPeptideTable;
import jene.peptide.Peptide;

import org.junit.*;
import static org.junit.Assert.*;

public class HugoPeptideTableTest {
    private static final String PEPTIDE_FILE = "data/test/hugo_peptide_table.tsv";

    private static final HugoSymbol A1BG  = HugoSymbol.instance("A1BG");
    private static final HugoSymbol XXXX  = HugoSymbol.instance("XXXX");
    private static final HugoSymbol ZZEF1 = HugoSymbol.instance("ZZEF1");

    private static final Peptide AAPPPPVLM  = Peptide.instance("AAPPPPVLM");
    private static final Peptide AAPPPPVLMH = Peptide.instance("AAPPPPVLMH");
    private static final Peptide ADSANYSCV  = Peptide.instance("ADSANYSCV");
    private static final Peptide YWSLLTSLV  = Peptide.instance("YWSLLTSLV");
    private static final Peptide VVVVVVVVV  = Peptide.instance("VVVVVVVVV");

    private static final HugoPeptideTable table = HugoPeptideTable.load(PEPTIDE_FILE);

    @Test public void testContainsPeptide() {
        assertTrue(table.contains(AAPPPPVLM));
        assertTrue(table.contains(AAPPPPVLMH));
        assertTrue(table.contains(ADSANYSCV));
        assertTrue(table.contains(YWSLLTSLV));
        assertFalse(table.contains(VVVVVVVVV));
    }

    @Test public void testContainsSymbol() {
        assertTrue(table.contains(A1BG));
        assertTrue(table.contains(ZZEF1));
        assertFalse(table.contains(XXXX));
    }

    @Test public void getGet() {
        assertTrue(table.get(XXXX).isEmpty());
        assertEquals(Set.of(ADSANYSCV, YWSLLTSLV), new HashSet<Peptide>(table.get(ZZEF1)));
        assertEquals(Set.of(AAPPPPVLM, AAPPPPVLMH, ADSANYSCV), new HashSet<Peptide>(table.get(A1BG)));
    }

    @Test public void testViewPeptides() {
        assertEquals(Set.of(AAPPPPVLM, AAPPPPVLMH, ADSANYSCV, YWSLLTSLV), table.viewPeptides());
    }

    @Test public void testViewSymbols() {
        assertEquals(Set.of(A1BG, ZZEF1), table.viewSymbols());
    }

    @Test public void testStore() {
        File testFile = new File("data/test/_tmp_hugo_peptide_table.tsv.gz");
        testFile.deleteOnExit();

        table.store(testFile);

        HugoPeptideTable table2 = HugoPeptideTable.load(testFile);

        assertEquals(Set.of(ADSANYSCV, YWSLLTSLV), new HashSet<Peptide>(table2.get(ZZEF1)));
        assertEquals(Set.of(AAPPPPVLM, AAPPPPVLMH, ADSANYSCV), new HashSet<Peptide>(table2.get(A1BG)));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jene.junit.HugoPeptideTableTest");
    }
}
