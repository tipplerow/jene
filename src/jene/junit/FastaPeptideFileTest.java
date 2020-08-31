
package jene.junit;

import java.util.List;

import jene.fasta.FastaPeptideFile;
import jene.fasta.FastaPeptideRecord;
import jene.peptide.Peptide;

import org.junit.*;
import static org.junit.Assert.*;

public class FastaPeptideFileTest {
    private static final String ENSEMBL_FILE = "data/test/ensembl_test1.fa";

    @Test public void testEnsembl() {
        List<FastaPeptideRecord> records = FastaPeptideFile.read(ENSEMBL_FILE);
        assertEquals(4, records.size());

        assertEquals("ENSP00000487941.1", records.get(0).getKey());
        assertEquals("pep gene:ENSG00000282431.1", records.get(0).getComment());
        assertEquals("GTGG", records.get(0).getPeptide().formatString());

        assertEquals("VLXLRLGELSLY", records.get(1).getPeptide().formatString());

        String pep2 = records.get(2).getPeptide().formatString();
        String pep3 = records.get(3).getPeptide().formatString();

        assertEquals(313, pep2.length());
        assertEquals("MPKLNSTFVTEFLFEGFSSFRRQHKLVFFVVFLTLYLLTLSGNVIIMTIIRLDHHLHTPM", pep2.substring(0, 60));
        assertEquals("SAQSRGAKNSVSL", pep2.substring(300, 313));

        assertEquals(374, pep3.length());
        assertEquals("MSLMVIIMACVGFFLLQGAWPQEEVHRKPSFLALPGHLVKSEETVILQCWSDVMFEHFLL", pep3.substring(0, 60));
        assertEquals("QNRVASSHVPAAGI", pep3.substring(360, 374));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jene.junit.FastaPeptideFileTest");
    }
}
