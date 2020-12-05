
package jene.missense;

import java.util.List;

import org.junit.*;
import static org.junit.Assert.*;

public class MissenseParserTest {
    private static final String MIAO_FILE = "data/test/Miao_missense.maf";

    @Test public void testMiao() {
        List<MissenseRecord> records = MissenseParser.parse(MIAO_FILE);
        assertEquals(8, records.size());

        assertRecord(records.get(5), "AC-DFCI_AC_PD1-1-Tumor-SM-9LRI9", "RXFP3", "ENST00000330120", "D296N", 0.60);
        assertRecord(records.get(7), "Y2087_T", "TTC39B", "ENST00000512701", "A47S", 0.80);
    }

    private void assertRecord(MissenseRecord record,
                              String tumorBarcode,
                              String hugoSymbol,
                              String transcriptID,
                              String proteinChange,
                              double cellFraction) {
        assertEquals(tumorBarcode,  record.getTumorBarcode().getKey());
        assertEquals(hugoSymbol,    record.getHugoSymbol().getKey());
        assertEquals(transcriptID,  record.getTranscriptID().getKey());
        assertEquals(proteinChange, record.getProteinChange().format());
        assertTrue(record.getCellFraction().equals(cellFraction));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jene.missense.MissenseParserTest");
    }
}
