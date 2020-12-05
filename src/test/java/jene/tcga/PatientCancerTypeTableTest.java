
package jene.tcga;

import org.junit.*;
import static org.junit.Assert.*;

public class PatientCancerTypeTableTest {
    private static final CancerType BLCA = CancerType.BLCA;
    private static final CancerType HNSC = CancerType.HNSC;
    private static final CancerType LUAD = CancerType.LUAD;

    private static final PatientID AU5884 = PatientID.instance("AU5884");

    private static final PatientID BLCA_001 = PatientID.instance("BLCA-001");
    private static final PatientID BLCA_002 = PatientID.instance("BLCA-002");

    private static final PatientID HNSCC_186 = PatientID.instance("HNSCC-186");
    private static final PatientID HNSCC_215 = PatientID.instance("HNSCC-215");

    static {
        System.setProperty(PatientCancerTypeTable.TABLE_FILE_PROPERTY, "data/test/patient_cancer_type_map.tsv");
    }

    @Test public void testAll() {
        PatientCancerTypeTable table =
            PatientCancerTypeTable.global();

        assertEquals(5, table.size());

        assertTrue(table.contains(AU5884));
        assertFalse(table.contains(PatientID.instance("Missing")));

        assertEquals(LUAD, table.require(AU5884));
        assertEquals(HNSC, table.require(HNSCC_186));
        assertEquals(HNSC, table.require(HNSCC_215));
        assertEquals(BLCA, table.require(BLCA_001));
        assertEquals(BLCA, table.require(BLCA_002));

        assertNull(table.lookup(PatientID.instance("Missing")));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jene.tcga.PatientCancerTypeTableTest");
    }
}
