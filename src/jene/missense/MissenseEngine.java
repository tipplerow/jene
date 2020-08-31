
package jene.missense;

import java.util.ArrayList;
import java.util.List;

import jam.lang.JamException;
import jam.util.ListUtil;

import jene.ensembl.EnsemblProteinDb;
import jene.ensembl.EnsemblProteinRecord;
import jene.ensembl.EnsemblTranscriptID;
import jene.hugo.HugoMaster;
import jene.hugo.HugoSymbol;
import jene.maf.MAFFastaRecord;
import jene.peptide.Peptide;
import jene.peptide.ProteinChange;
import jene.tcga.CellFraction;
import jene.tcga.TumorBarcode;

/**
 * Processes MAF files and generates the protein structures generated
 * by missense mutations.
 */
final class MissenseEngine {
    private final HugoSymbol symbol;
    private final TumorBarcode barcode;

    private final CellFraction ccfThreshold;
    private final MissenseTable missenseTable;

    private final HugoMaster hugoMaster;
    private final EnsemblProteinDb ensemblDb;

    private List<MissenseRecord> missenseRecords;

    MissenseEngine(TumorBarcode     barcode,
                   HugoSymbol       symbol,
                   CellFraction     ccfThreshold,
                   MissenseTable    missenseTable,
                   HugoMaster       hugoMaster,
                   EnsemblProteinDb ensemblDb) {
        this.symbol = symbol;
        this.barcode = barcode;

        this.ccfThreshold = ccfThreshold;
        this.missenseTable = missenseTable;

        this.ensemblDb = ensemblDb;
        this.hugoMaster = hugoMaster;
    }

    MAFFastaRecord process() {
        missenseRecords = missenseTable.lookup(barcode, symbol);

        if (missenseRecords.isEmpty())
            throw JamException.runtime("No missense mutation records.");

        missenseRecords = MissenseRecord.filterCellFraction(missenseRecords, ccfThreshold);

        if (missenseRecords.isEmpty())
            return null;

        Peptide germline = getGermlinePeptide();
        Peptide mutated  = germline.mutate(getProteinChanges());

        return new MAFFastaRecord(barcode, symbol, CellFraction.UNIT, mutated);
    }

    private Peptide getGermlinePeptide() {
        if (haveTranscripts())
            return getGermlinePeptide(getTranscript());
        else
            return matchNativePeptide();
    }

    private boolean haveTranscripts() {
        //
        // Most data sets (TCGA) have Ensembl transcripts, but
        // some do not (Liu, Nature Medicine).  We can process
        // either case, but not one with partially missing
        // transcripts...
        //
        boolean firstHasTranscript = missenseRecords.get(0).hasTranscriptID();

        for (int index = 1; index < missenseRecords.size(); ++index)
            if (missenseRecords.get(index).hasTranscriptID() != firstHasTranscript)
                throw JamException.runtime("Missing transcript ID.");

        // Either all records had transcripts or none did...
        return firstHasTranscript;
    }

    private EnsemblTranscriptID getTranscript() {
        missenseRecords = MissenseRecord.filterPrimaryTranscript(missenseRecords);

        if (missenseRecords.isEmpty())
            throw JamException.runtime("No primary transcript.");
        else
            return missenseRecords.get(0).getTranscriptID();
    }

    private Peptide getGermlinePeptide(EnsemblTranscriptID transcriptID) {
        EnsemblProteinRecord ensemblRecord = ensemblDb.get(transcriptID);

        if (ensemblRecord != null)
            return ensemblRecord.getPeptide();
        else
            throw JamException.runtime("Unmapped transcript: [%s].", transcriptID.getKey());
    }

    private Peptide matchNativePeptide() {
        //
        // Okay, no transcript identifier, so we use the first peptide
        // with a sequence that is consistent with the protein changes...
        //
        List<EnsemblProteinRecord> ensemblRecords = getEnsemblRecords();
        List<ProteinChange> proteinChanges = getProteinChanges();

        for (EnsemblProteinRecord ensemblRecord : ensemblRecords) {
            Peptide peptide = ensemblRecord.getPeptide();

            if (ProteinChange.isNative(peptide, proteinChanges))
                return peptide;
        }

        throw JamException.runtime("No consistent native Ensembl records.");
    }

    private List<EnsemblProteinRecord> getEnsemblRecords() {
        //
        // Two ways to match HUGO symbols with Ensembl records:
        // through the HUGO master table and through the Ensembl
        // database itself...
        //
        List<EnsemblProteinRecord> ensemblRecords = new ArrayList<EnsemblProteinRecord>();

        ensemblRecords.addAll(ensemblDb.get(symbol));
        ensemblRecords.addAll(ensemblDb.get(hugoMaster.get(symbol)));

        if (ensemblRecords.isEmpty())
            throw JamException.runtime("No matching Ensembl records.");

        return ensemblRecords;
    }

    private List<ProteinChange> getProteinChanges() {
        return ListUtil.apply(missenseRecords, x -> x.getProteinChange());
    }
}
