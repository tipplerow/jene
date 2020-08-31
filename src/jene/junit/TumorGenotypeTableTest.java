
package jene.junit;

import jene.hla.Allele;
import jene.hla.Genotype;
import jene.tcga.TumorBarcode;
import jene.tcga.TumorGenotypeTable;

import org.junit.*;
import static org.junit.Assert.*;

public class TumorGenotypeTableTest {
    private static final Allele A0201 = Allele.instance("A0201");
    private static final Allele A0301 = Allele.instance("A0301");
    private static final Allele A1101 = Allele.instance("A1101");
    private static final Allele A3201 = Allele.instance("A3201");
    private static final Allele B0702 = Allele.instance("B0702");
    private static final Allele B1501 = Allele.instance("B1501");
    private static final Allele B5201 = Allele.instance("B5201");
    private static final Allele C0303 = Allele.instance("C0303");
    private static final Allele C0702 = Allele.instance("C0702");
    private static final Allele C1202 = Allele.instance("C1202");

    private static final Genotype Geno1 = Genotype.instance(A0201, A3201, B1501, C0303);
    private static final Genotype Geno2 = Genotype.instance(A0301, A1101, B0702, B5201, C0702, C1202);
    
    private static final TumorBarcode Tumor1A = TumorBarcode.instance("Tumor1A");
    private static final TumorBarcode Tumor1B = TumorBarcode.instance("Tumor1B");
    private static final TumorBarcode Tumor2  = TumorBarcode.instance("Tumor2");
    private static final TumorBarcode Tumor3  = TumorBarcode.instance("Tumor3");

    private static final String TUMOR_PATIENT_FILE = "data/test/tumor_patient_table2.tsv";
    private static final String PATIENT_GENOTYPE_FILE = "data/test/patient_genotype_table2.csv";

    @Test public void testLoad() {
        TumorGenotypeTable table =
            TumorGenotypeTable.load(TUMOR_PATIENT_FILE,
                                    PATIENT_GENOTYPE_FILE);

        assertEquals(3, table.size());

        assertTrue(table.contains(Tumor1A));
        assertTrue(table.contains(Tumor1B));
        assertTrue(table.contains(Tumor2));
        assertFalse(table.contains(Tumor3));

        assertEquals(Geno1, table.lookup(Tumor1A));
        assertEquals(Geno1, table.lookup(Tumor1B));
        assertEquals(Geno2, table.lookup(Tumor2));
        assertNull(table.lookup(Tumor3));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jene.junit.TumorGenotypeTableTest");
    }
}
