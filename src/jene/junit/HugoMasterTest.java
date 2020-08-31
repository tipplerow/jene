
package jene.junit;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import jene.ensembl.EnsemblGeneID;
import jene.hugo.HugoMaster;
import jene.hugo.HugoSymbol;

import org.junit.*;
import static org.junit.Assert.*;

public class HugoMasterTest {
    private static final HugoSymbol A1BG = HugoSymbol.instance("A1BG");
    private static final HugoSymbol A1CF = HugoSymbol.instance("A1CF");
    private static final HugoSymbol AATF = HugoSymbol.instance("AATF");
    private static final HugoSymbol ABAT = HugoSymbol.instance("ABAT");
    private static final HugoSymbol XYZ  = HugoSymbol.instance("XYZ");

    private static final EnsemblGeneID A1BG_GENE = EnsemblGeneID.instance("ENSG00000121410");
    private static final EnsemblGeneID A1CF_GENE = EnsemblGeneID.instance("ENSG00000148584");
    private static final EnsemblGeneID ABAT_GENE = EnsemblGeneID.instance("ENSG00000183044");

    private static final EnsemblGeneID AATF_GENE1 = EnsemblGeneID.instance("ENSG00000108270");
    private static final EnsemblGeneID AATF_GENE2 = EnsemblGeneID.instance("ENSG00000275700");

    private static final HugoMaster master = HugoMaster.load("data/test/hugo_master_test.tsv");

    @Test public void testContains() {
        assertTrue(master.contains(A1BG));
        assertTrue(master.contains(A1CF));
        assertTrue(master.contains(AATF));
        assertTrue(master.contains(ABAT));
        assertFalse(master.contains(XYZ));
    }

    @Test public void testGet() {
        assertGenes(A1BG, A1BG_GENE);
        assertGenes(A1CF, A1CF_GENE);
        assertGenes(ABAT, ABAT_GENE);
        assertGenes(AATF, AATF_GENE1, AATF_GENE2);
        assertGenes(XYZ);
    }

    private void assertGenes(HugoSymbol symbol, EnsemblGeneID... genes) {
        assertEquals(Set.of(genes), new HashSet<EnsemblGeneID>(master.get(symbol)));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jene.junit.HugoMasterTest");
    }
}
