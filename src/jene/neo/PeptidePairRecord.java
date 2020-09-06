
package jene.neo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;

import jam.io.Delimiter;
import jam.lang.JamException;
import jam.math.UnitIndexRange;
import jam.report.LineBuilder;

import jene.hugo.HugoSymbol;
import jene.peptide.Peptide;
import jene.tcga.TumorBarcode;
import jene.tcga.TumorGeneRecord;

/**
 * Associates a neo-peptide with the tumor sample, gene, and
 * self-peptide from which it originated.
 */
public final class PeptidePairRecord extends TumorGeneRecord {
    private final PeptidePair peptidePair;
    private final UnitIndexRange peptideRange;

    private PeptidePairRecord(TumorBarcode   tumorBarcode,
                              HugoSymbol     hugoSymbol,
                              UnitIndexRange peptideRange,
                              PeptidePair    peptidePair) {
        super(tumorBarcode, hugoSymbol);

        this.peptidePair = peptidePair;
        this.peptideRange = peptideRange;
    }

    /**
     * The standard delimiter for flat files containing peptide pair
     * records.
     */
    public static final Delimiter DELIM = Delimiter.TAB;

    /**
     * A comparator that orders records by tumor barcode first, HUGO
     * symbol second, and peptide range third.
     */
    public static final Comparator<PeptidePairRecord> COMPARATOR =
        new Comparator<PeptidePairRecord>() {
            @Override public int compare(PeptidePairRecord rec1, PeptidePairRecord rec2) {
                int barcodeSymbolCmp = BARCODE_SYMBOL_COMPARATOR.compare(rec1, rec2);

                if (barcodeSymbolCmp != 0)
                    return barcodeSymbolCmp;
                else
                    return UnitIndexRange.BOUND_COMPARATOR.compare(rec1.peptideRange, rec2.peptideRange);
            }
        };

    /**
     * Returns a peptide pair record with fixed components.
     *
     * @param tumorBarcode the tumor where the mutation occurred.
     *
     * @param hugoSymbol the HUGO symbol of the mutated gene.
     *
     * @param peptideRange the unit-offset range of the amino acid
     * positions in the peptide fragments.
     *
     * @param peptidePair the peptide pair resulting from the
     * mutation.
     *
     * @return the peptide pair record with the specified
     * components.
     */
    public static PeptidePairRecord instance(TumorBarcode   tumorBarcode,
                                             HugoSymbol     hugoSymbol,
                                             UnitIndexRange peptideRange,
                                             PeptidePair    peptidePair) {
        return new PeptidePairRecord(tumorBarcode, hugoSymbol, peptideRange, peptidePair);
    }

    /**
     * Returns a peptide pair record with fixed components.
     *
     * @param tumorBarcode the tumor where the mutation occurred.
     *
     * @param hugoSymbol the HUGO symbol of the mutated gene.
     *
     * @param peptideRange the unit-offset range of the amino acid
     * positions in the peptide fragments.
     *
     * @param selfPeptide the germline self-peptide.
     *
     * @param neoPeptide the neo-peptide generated by somatic
     * mutation.
     *
     * @return the peptide pair record with the specified
     * components.
     */
    public static PeptidePairRecord instance(TumorBarcode   tumorBarcode,
                                             HugoSymbol     hugoSymbol,
                                             UnitIndexRange peptideRange,
                                             SelfPeptide    selfPeptide,
                                             NeoPeptide     neoPeptide) {
        return instance(tumorBarcode, hugoSymbol, peptideRange,
                        PeptidePair.instance(selfPeptide, neoPeptide));
    }                                            

    /**
     * Returns the header line for flat files containing peptide pair
     * records.
     *
     * @return the header line for flat files containing peptide pair
     * records.
     */
    public static String header() {
        return header(DELIM);
    }

    /**
     * Returns the header line for flat files containing peptide pair
     * records.
     *
     * @param delimiter the flat file field delimiter.
     *
     * @return the header line for flat files containing peptide pair
     * records.
     */
    public static String header(Delimiter delimiter) {
        LineBuilder builder = new LineBuilder(delimiter);

        builder.append(TumorBarcode.COLUMN_NAME);
        builder.append(HugoSymbol.COLUMN_NAME);
        builder.append("Range_Lower");
        builder.append("Range_Upper");
        builder.append("Self_Peptide");
        builder.append("Neo_Peptide");

        return builder.toString();
    }

    /**
     * Creates a new peptide pair record by parsing a delimited line
     * from a flat file.
     *
     * @param line the line to parse.
     *
     * @return the peptide pair record encoded in the specified line.
     *
     * @throws RuntimeException unless the line contains a properly
     * formatted peptide pair record.
     */
    public static PeptidePairRecord parse(String line) {
        return parse(DELIM.split(line, 6), 0);
    }

    /**
     * Creates a new peptide pair record by parsing an array of
     * fields.
     *
     * <p>The fields must start at index {@code offset} and be
     * arranged sequentially and contiguously.
     *
     * @param fields an array of individual fields extracted from
     * a flat file.
     *
     * @param offset the index of the tumor barcode in the input
     * array.
     *
     * @return the peptide pair record encoded in the specified
     * fields.
     *
     * @throws RuntimeException unless the fields define a valid
     * peptide pair record.
     */
    public static PeptidePairRecord parse(String[] fields, int offset) {
        TumorBarcode tumorBarcode = TumorBarcode.instance(fields[offset]);
        HugoSymbol   hugoSymbol   = HugoSymbol.instance(fields[offset + 1]);
        int          rangeLower   = Integer.parseInt(fields[offset + 2]);
        int          rangeUpper   = Integer.parseInt(fields[offset + 3]);
        SelfPeptide  selfPeptide  = SelfPeptide.instance(fields[offset + 4]);
        NeoPeptide   neoPeptide   = NeoPeptide.instance(fields[offset + 5]);

        return instance(tumorBarcode, hugoSymbol,
                        UnitIndexRange.instance(rangeLower, rangeUpper),
                        PeptidePair.instance(selfPeptide, neoPeptide));
    }

    /**
     * Extracts the peptides from a collection of pair records.
     *
     * @param pairRecords the peptide pair records to process.
     *
     * @return the neo-peptides and self-peptides contained in the
     * specified records (in no particular order).
     */
    public static Collection<Peptide> peptides(Collection<PeptidePairRecord> pairRecords) {
        Collection<Peptide> peptides =
            new ArrayList<Peptide>(2 * pairRecords.size());

        for (PeptidePairRecord record : pairRecords) {
            peptides.add(record.getNeoPeptide());
            peptides.add(record.getSelfPeptide());
        }

        return peptides;
    }

    /**
     * Formats this record for output to a delimited flat file.
     *
     * @return a string containing the formatted text.
     */
    public String format() {
        return format(DELIM);
    }

    /**
     * Formats this record for output to a delimited flat file.
     *
     * @param delimiter the flat file field delimiter.
     *
     * @return a string containing the formatted text.
     */
    public String format(Delimiter delimiter) {
        LineBuilder builder = new LineBuilder(delimiter);

        builder.append(tumorBarcode.getKey());
        builder.append(hugoSymbol.getKey());
        builder.append(peptideRange.lower());
        builder.append(peptideRange.upper());
        builder.append(peptidePair.self().formatString());
        builder.append(peptidePair.neo().formatString());

        return builder.toString();
    }

    /**
     * Returns the neo/self peptide pair in this record.
     *
     * @return the neo/self peptide pair in this record.
     */
    public PeptidePair getPeptidePair() {
        return peptidePair;
    }

    /**
     * Returns the unit-offset range of the amino acid positions in
     * the peptide fragments.
     *
     * @return the unit-offset range of the amino acid positions in
     * the peptide fragments.
     */
    public UnitIndexRange getPeptideRange() {
        return peptideRange;
    }

    /**
     * Returns the neo-peptide derived from the self-peptide by
     * somatic mutation.
     *
     * @return the neo-peptide derived from the self-peptide by
     * somatic mutation.
     */
    public Peptide getNeoPeptide() {
        return peptidePair.neo();
    }

    /**
     * Returns the self-peptide derived from the germline genome.
     *
     * @return the self-peptide derived from the germline genome.
     */
    public Peptide getSelfPeptide() {
        return peptidePair.self();
    }

    @Override public String toString() {
        return String.format("PeptidePairRecord(%s, %s, [%d, %d]: %s => %s)",
                             tumorBarcode.getKey(),
                             hugoSymbol.getKey(),
                             peptideRange.lower(),
                             peptideRange.upper(),
                             getSelfPeptide().formatString(),
                             getNeoPeptide().formatString());
    }
}
