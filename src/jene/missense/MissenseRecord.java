
package jene.missense;

import java.util.List;
import java.util.Set;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import jam.lang.JamException;
import jam.lang.ObjectUtil;
import jam.util.CollectionUtil;
import jam.util.ListUtil;
import jam.util.MultisetUtil;

import jene.ensembl.EnsemblTranscriptID;
import jene.hugo.HugoSymbol;
import jene.peptide.ProteinChange;
import jene.tcga.TumorBarcode;
import jene.tcga.TumorGeneRecordBase;
import jene.tcga.CellFraction;

/**
 * Encapsulates information contained in one record from a Mutation
 * Annotation Format (MAF) file that describes only <b>missense</b>
 * mutations.
 */
public final class MissenseRecord extends TumorGeneRecordBase {
    private final CellFraction cellFraction;
    private final ProteinChange proteinChange;
    private final EnsemblTranscriptID transcriptID;

    /**
     * Creates a new missense mutation record.
     *
     * @param tumorBarcode the tumor in which the mutation occurred.
     *
     * @param transcriptID the Ensembl identifier for the mutated RNA
     * transcript.
     *
     * @param hugoSymbol the HUGO symbol for the mutated gene.
     *
     * @param proteinChange the description of the single-residue change.
     *
     * @param cellFraction the cancer cell fraction of the mutation.
     */
    public MissenseRecord(TumorBarcode        tumorBarcode,
                          EnsemblTranscriptID transcriptID,
                          HugoSymbol          hugoSymbol,
                          ProteinChange       proteinChange,
                          CellFraction        cellFraction) {
        super(tumorBarcode, hugoSymbol);

        this.cellFraction  = cellFraction;
        this.proteinChange = proteinChange;
        this.transcriptID  = transcriptID;
    }

    /**
     * Filters a list of missense records and retains only those with
     * cell fractions above a threshold.
     *
     * @param records the records to filter.
     *
     * @param ccfThreshold the cancer cell fraction threshold.
     *
     * @return a list containing only records with cell fractions
     * above the specified threshold.
     */
    public static List<MissenseRecord> filterCellFraction(List<MissenseRecord> records, CellFraction ccfThreshold) {
        return ListUtil.filter(records, record -> record.getCellFraction().above(ccfThreshold));
    }

    /**
     * Filters a list of missense records and retains only those with
     * Ensembl transcripts that match the most common transcript.
     *
     * <p>If all records have the same transcript, then the input list
     * is returned unaltered.  If the list does not contain a unique
     * primary transcript (no single transcript occurs more than any
     * other), then an empty list is returned.
     *
     * @param records the records to filter.
     *
     * @return a list containing only those records having the primary
     * transcript.
     */
    public static List<MissenseRecord> filterPrimaryTranscript(List<MissenseRecord> records) {
        if (records.size() < 2)
            return records;

        Multiset<EnsemblTranscriptID> transcripts = HashMultiset.create();

        for (MissenseRecord record : records)
            transcripts.add(record.getTranscriptID());

        if (MultisetUtil.countUnique(transcripts) == 1)
            return records;

        Set<EnsemblTranscriptID> primary = MultisetUtil.mostCommon(transcripts);

        if (primary.size() == 1)
            return filterTranscript(records, CollectionUtil.peek(primary));
        else
            return List.of();
    }

    /**
     * Filters a list of missense records and retains only those whose
     * Ensembl transcript matches a target.
     *
     * @param records the records to filter.
     *
     * @param transcriptID the target transcript to match.
     *
     * @return a list containing only those records whose transcript
     * matches the target.
     */
    public static List<MissenseRecord> filterTranscript(List<MissenseRecord> records, EnsemblTranscriptID transcriptID) {
        if (transcriptID == null)
            return ListUtil.filter(records, record -> !record.hasTranscriptID());
        else
            return ListUtil.filter(records, record -> record.getTranscriptID().equals(transcriptID));
    }

    /**
     * Given duplicate or conflicting mutation records, this method
     * elects the record with the higher cancer cell fraction.
     *
     * @param rec1 the first duplicate or conflicting record.
     *
     * @param rec2 the second duplicate or conflicting record.
     *
     * @return the record with the higher cancer cell fraction.
     *
     * @throws RuntimeException if the records refer to different
     * tumor barcodes, genes, peptide positions, or native residues
     * or if the records have identical cancer cell fractions.
     */
    public static MissenseRecord resolveDuplicate(MissenseRecord rec1, MissenseRecord rec2) {
        if (!rec1.tumorBarcode.equals(rec2.tumorBarcode))
            throw JamException.runtime("Inconsistent barcodes.");

        if (!rec1.hugoSymbol.equals(rec2.hugoSymbol))
            throw JamException.runtime("Inconsistent symbols.");

        if (!ObjectUtil.equals(rec1.transcriptID, rec2.transcriptID))
            throw JamException.runtime("Inconsistent transcripts.");

        if (rec1.proteinChange.getPosition() != rec2.proteinChange.getPosition())
            throw JamException.runtime("Inconsistent positions.");

        if (!rec1.proteinChange.getNative().equals(rec2.proteinChange.getNative()))
            throw JamException.runtime("Inconsistent native residues.");

        if (rec1.cellFraction.GT(rec2.cellFraction))
            return rec1;

        if (rec2.cellFraction.GT(rec1.cellFraction))
            return rec2;

        // Cell fractions are equal: cannot resolve unless both
        // mutated residues agree...
        if (rec1.proteinChange.getMutated().equals(rec2.proteinChange.getMutated()))
            return rec1;
        else
            throw JamException.runtime("Cannot resolve conflicting mutations: [%s, %s].", rec1, rec2);
    }

    /**
     * Returns the description of the single-residue change.
     *
     * @return the description of the single-residue change.
     */
    public ProteinChange getProteinChange() {
        return proteinChange;
    }

    /**
     * Returns the Ensembl identifier for the mutated RNA transcript.
     *
     * @return the Ensembl identifier for the mutated RNA transcript.
     */
    public EnsemblTranscriptID getTranscriptID() {
        return transcriptID;
    }

    /**
     * Returns the cancer cell fraction for the mutation.
     *
     * @return the cancer cell fraction for the mutation.
     */
    public CellFraction getCellFraction() {
        return cellFraction;
    }

    /**
     * Identifies records with non-{@code null} Ensembl transcript
     * identifiers.
     *
     * @return {@code true} iff this record has a non-{@code null}
     * Ensembl transcript identifier.
     */
    public boolean hasTranscriptID() {
        return transcriptID != null;
    }

    @Override public String toString() {
        return String.format("MissenseRecord(%s, %s, %s, %s, %.2f)",
                             tumorBarcode.getKey(),
                             hugoSymbol.getKey(),
                             transcriptID != null ? transcriptID.getKey() : "null",
                             proteinChange.format(),
                             cellFraction.doubleValue());
    }
}
