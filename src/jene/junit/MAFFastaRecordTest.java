
package jene.junit;

import jene.fasta.FastaPeptideRecord;
import jene.hugo.HugoSymbol;
import jene.maf.MAFFastaRecord;
import jene.peptide.Peptide;
import jene.tcga.CellFraction;
import jene.tcga.TumorBarcode;

import org.junit.*;
import static org.junit.Assert.*;

public class MAFFastaRecordTest {
    @Test public void testParse() {
        Peptide peptide = Peptide.instance("VLXLRLGELSLY");

        FastaPeptideRecord record1 =
            new FastaPeptideRecord("Tumor_Barcode:BARCODE",
                                   "Hugo_Symbol:HUGO CCF:0.123",
                                   peptide);
                            
        MAFFastaRecord record2 =
            MAFFastaRecord.parse(record1);

        assertEquals(TumorBarcode.instance("BARCODE"), record2.getTumorBarcode());
        assertEquals(HugoSymbol.instance("HUGO"), record2.getHugoSymbol());
        assertTrue(record2.getCellFraction().equals(0.123));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jene.junit.MAFFastaRecordTest");
    }
}
