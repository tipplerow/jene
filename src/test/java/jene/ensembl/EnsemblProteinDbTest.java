
package jene.ensembl;

import jene.hugo.HugoSymbol;

import org.junit.*;
import static org.junit.Assert.*;

public class EnsemblProteinDbTest {
    @Test public void testSample2() {
        EnsemblProteinDb db = EnsemblProteinDb.load("data/test/ensembl_test2.fa");

        HugoSymbol BRAF_Hugo = HugoSymbol.instance("BRAF");
        HugoSymbol KRAS_Hugo = HugoSymbol.instance("KRAS");

        EnsemblGeneID BRAF_Gene = EnsemblGeneID.instance("ENSG00000157764");
        EnsemblGeneID KRAS_Gene = EnsemblGeneID.instance("ENSG00000133703");

        EnsemblTranscriptID BRAF_Trans1 = EnsemblTranscriptID.instance("ENST00000496384");
        EnsemblTranscriptID BRAF_Trans2 = EnsemblTranscriptID.instance("ENST00000644969");
        EnsemblTranscriptID BRAF_Trans3 = EnsemblTranscriptID.instance("ENST00000646891");

        EnsemblTranscriptID KRAS_Trans1 = EnsemblTranscriptID.instance("ENST00000311936");
        EnsemblTranscriptID KRAS_Trans2 = EnsemblTranscriptID.instance("ENST00000256078");

        assertEquals(5, db.size());

        assertEquals(2, db.count(KRAS_Hugo));
        assertEquals(3, db.count(BRAF_Hugo));

        assertEquals(2, db.count(KRAS_Gene));
        assertEquals(3, db.count(BRAF_Gene));

        assertEquals(BRAF_Hugo, db.getHugo(BRAF_Gene));
        assertEquals(KRAS_Hugo, db.getHugo(KRAS_Gene));

        assertEquals(BRAF_Hugo, db.getHugo(BRAF_Trans1));
        assertEquals(BRAF_Hugo, db.getHugo(BRAF_Trans2));
        assertEquals(BRAF_Hugo, db.getHugo(BRAF_Trans3));
        assertEquals(KRAS_Hugo, db.getHugo(KRAS_Trans1));
        assertEquals(KRAS_Hugo, db.getHugo(KRAS_Trans2));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jene.ensembl.EnsemblProteinDbTest");
    }
}
