
package jene.neo;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import jam.app.JamLogger;
import jam.math.UnitIndexRange;
import jam.util.ListUtil;

import jene.ensembl.EnsemblProteinDb;
import jene.hugo.HugoMaster;
import jene.hugo.HugoSymbol;
import jene.missense.MissenseGroup;
import jene.missense.MissenseTable;
import jene.peptide.Peptide;
import jene.peptide.ProteinChange;
import jene.tcga.TumorBarcode;

/**
 * Generates the self/neo-peptide pairs corresponding to missense
 * mutations.
 */
public final class PeptidePairEngine {
    private final int[] peptideLengths;
    private final HugoSymbol hugoSymbol;
    private final TumorBarcode tumorBarcode;
    private final MissenseGroup missenseGroup;

    private static HugoMaster hugoMaster = null;
    private static EnsemblProteinDb ensemblDb = null;

    private PeptidePairEngine(MissenseGroup missenseGroup, int... peptideLengths) {
        this.missenseGroup = missenseGroup;
        this.peptideLengths = peptideLengths;

        this.hugoSymbol = missenseGroup.getHugoSymbol();
        this.tumorBarcode = missenseGroup.getTumorBarcode();
    }

    /**
     * Assigns the shared data structures that are used to process all
     * mutation groups.  This method must be called before any groups
     * are processed.
     *
     * @param hugoMaster the mapping from HUGO symbols to Ensembl genes.
     *
     * @param ensemblDb the Ensembl protein database.
     */
    public static void initialize(HugoMaster hugoMaster, EnsemblProteinDb ensemblDb) {
        PeptidePairEngine.ensemblDb = ensemblDb;
        PeptidePairEngine.hugoMaster = hugoMaster;
    }

    /**
     * Generates the self/neo-peptide pairs corresponding to a single
     * group of missense mutations.
     *
     * @param missenseGroup a group of missense mutations observed in
     * the same tumor sample and gene.
     *
     * @param peptideLengths the desired lengths of the seld-peptide
     * and neo-peptide fragments.
     *
     * @return a list of self/neo-peptide pair records for the input
     * mutation group.
     *
     * @throws RuntimeException if the Ensembl database and HUGO
     * master have not been initialized or if the native peptide
     * cannot be resolved.
     */
    public static List<PeptidePairRecord> generate(MissenseGroup missenseGroup, int... peptideLengths) {
        if (!isInitialized())
            throw new IllegalStateException("The PeptidePairEngine has not been initialized.");

        PeptidePairEngine engine =
            new PeptidePairEngine(missenseGroup, peptideLengths);

        try {
            return engine.generate();
        }
        catch (RuntimeException ex) {
            JamLogger.warn(ex);
            return List.of();
        }
    }

    /**
     * Generates the self/neo-peptide pairs corresponding to each
     * group of missense mutations in a patient cohort.
     *
     * @param missenseTable a table of missense mutations observed in
     * a patient cohort.
     *
     * @param peptideLengths the desired lengths of the seld-peptide
     * and neo-peptide fragments.
     *
     * @return a list of self/neo-peptide pair records for the input
     * mutation table.
     *
     * @throws RuntimeException if the Ensembl database and HUGO
     * master have not been initialized or if any native peptides
     * cannot be resolved.
     */
    public static List<PeptidePairRecord> generate(MissenseTable missenseTable, int... peptideLengths) {
        List<MissenseGroup> missenseGroups = missenseTable.group();
        missenseGroups.sort(MissenseGroup.BARCODE_SYMBOL_COMPARATOR);

        List<PeptidePairRecord> pairRecords =
            new ArrayList<PeptidePairRecord>();

        for (MissenseGroup missenseGroup : missenseGroups)
            pairRecords.addAll(generate(missenseGroup, peptideLengths));

        return pairRecords;
    }

    private static boolean isInitialized() {
        return ensemblDb != null && hugoMaster != null;
    }

    private List<PeptidePairRecord> generate() {
        JamLogger.info("Generating peptide pairs: [%s, %s]...",
                       tumorBarcode.getKey(), hugoSymbol.getKey());

        Peptide nativePeptide = missenseGroup.resolveNative(ensemblDb, hugoMaster);
        Peptide mutatedPeptide = missenseGroup.mutate(nativePeptide);
        Set<UnitIndexRange> fragmentRanges = resolveFragmentRanges(nativePeptide.length());

        List<PeptidePairRecord> pairRecords =
            new ArrayList<PeptidePairRecord>(fragmentRanges.size());
        
        for (UnitIndexRange fragmentRange : fragmentRanges) {
            NeoPeptide neoPeptide = NeoPeptide.instance(mutatedPeptide.fragment(fragmentRange));
            SelfPeptide selfPeptide = SelfPeptide.instance(nativePeptide.fragment(fragmentRange));

            PeptidePairRecord pairRecord =
                PeptidePairRecord.instance(tumorBarcode,
                                           hugoSymbol,
                                           fragmentRange,
                                           selfPeptide,
                                           neoPeptide);

            pairRecords.add(pairRecord);
        }

        return pairRecords;
    }

    private Set<UnitIndexRange> resolveFragmentRanges(int nativeLength) {
        //
        // Multiple mutations may occur within the same fragment (if
        // their positions are separated by a length smaller than the
        // fragment length), so we accumulate all fragments in a set
        // to avoid duplication...
        //
        Set<UnitIndexRange> fragmentRanges =
            new TreeSet<UnitIndexRange>(UnitIndexRange.BOUND_COMPARATOR);

        for (ProteinChange proteinChange : missenseGroup.getProteinChanges())
            for (int peptideLength : peptideLengths)
                fragmentRanges.addAll(proteinChange.resolveFragments(peptideLength, nativeLength));

        return fragmentRanges;
    }
}
