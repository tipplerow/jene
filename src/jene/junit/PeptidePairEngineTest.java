
package jene.junit;

import java.util.List;

import jam.math.UnitIndexRange;

import jene.ensembl.EnsemblProteinDb;
import jene.hugo.HugoMaster;
import jene.missense.MissenseGroup;
import jene.missense.MissenseTable;
import jene.neo.PeptidePairEngine;
import jene.neo.PeptidePairRecord;
import jene.peptide.Peptide;
import jene.tcga.TumorBarcode;

import org.junit.*;
import static org.junit.Assert.*;

public class PeptidePairEngineTest {
    @Test public void testGenerate() {
        HugoMaster hugoMaster = HugoMaster.load("data/test/hugo_master_test.tsv");
        EnsemblProteinDb ensemblDb = EnsemblProteinDb.load("data/test/ensembl_test2.fa");

        PeptidePairEngine.initialize(hugoMaster, ensemblDb);

        TumorBarcode barcode1 = TumorBarcode.instance("barcode1");
        TumorBarcode barcode2 = TumorBarcode.instance("barcode2");

        MissenseTable missenseTable = MissenseTable.load("data/test/ppe_missense.maf");
        List<MissenseGroup> missenseGroups = missenseTable.group(barcode1);

        assertEquals(1, missenseGroups.size());

        List<PeptidePairRecord> pairRecords =
            PeptidePairEngine.generate(missenseGroups.get(0), 9);

        assertEquals(21, pairRecords.size());

        assertEquals(UnitIndexRange.instance(9, 17), pairRecords.get(0).getPeptideRange());
        assertEquals(UnitIndexRange.instance(20, 28), pairRecords.get(11).getPeptideRange());
        assertEquals(UnitIndexRange.instance(168, 176), pairRecords.get(12).getPeptideRange());
        assertEquals(UnitIndexRange.instance(176, 184), pairRecords.get(20).getPeptideRange());

        missenseGroups = missenseTable.group(barcode2);
        pairRecords = PeptidePairEngine.generate(missenseGroups.get(0), 9);

        assertEquals(1, missenseGroups.size());
        assertEquals(16, pairRecords.size());

        assertEquals(UnitIndexRange.instance(1, 9), pairRecords.get(0).getPeptideRange());
        assertEquals(UnitIndexRange.instance(3, 11), pairRecords.get(2).getPeptideRange());
        assertEquals(UnitIndexRange.instance(168, 176), pairRecords.get(3).getPeptideRange());
        assertEquals(UnitIndexRange.instance(181, 189), pairRecords.get(15).getPeptideRange());

        pairRecords = PeptidePairEngine.generate(missenseTable, 9);

        assertEquals(37, pairRecords.size());

        assertEquals(barcode1, pairRecords.get(0).getTumorBarcode());
        assertEquals(UnitIndexRange.instance(9, 17), pairRecords.get(0).getPeptideRange());

        assertEquals(barcode2, pairRecords.get(36).getTumorBarcode());
        assertEquals(UnitIndexRange.instance(181, 189), pairRecords.get(36).getPeptideRange());
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jene.junit.PeptidePairEngineTest");
    }
}
