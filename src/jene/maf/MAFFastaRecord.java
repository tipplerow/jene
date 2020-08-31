
package jene.maf;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import jam.lang.JamException;
import jam.util.RegexUtil;

import jene.fasta.FastaPeptideRecord;
import jene.hugo.HugoSymbol;
import jene.peptide.Peptide;
import jene.tcga.CellFraction;
import jene.tcga.TumorBarcode;

/**
 * Encodes the protein sequence arising from one or more mutations in
 * a gene.
 *
 * <p><b>Record format.</b> {@code MAFFastaRecord} objects are created
 * from FASTA records with header lines formatted as follows:
 * <pre>
       &gt;Tumor_Barcode:BARCODE Hugo_Symbol:SYMBOL CCF:0.123
 * </pre>
 *
 * <p><b>Natural ordering.</b> The natural ordering imposed by the
 * {@code compareTo} method considers the tumor barcode first, then
 * HUGO symbol second.
 */
public final class MAFFastaRecord implements Comparable<MAFFastaRecord> {
    private final TumorBarcode tumorBarcode;
    private final HugoSymbol   hugoSymbol;
    private final CellFraction cellFraction;
    private final Peptide      peptide;

    /**
     * A comparator that orders records by tumor barcode (breaking
     * ties with the HUGO symbol).
     */
    public static final Comparator<MAFFastaRecord> BARCODE_COMPARATOR = new BarcodeComparator();

    private static final class BarcodeComparator implements Comparator<MAFFastaRecord> {
        @Override public int compare(MAFFastaRecord rec1, MAFFastaRecord rec2) {
            int result = rec1.tumorBarcode.compareTo(rec2.tumorBarcode);

            if (result != 0)
                return result;
            else
                return rec1.hugoSymbol.compareTo(rec2.hugoSymbol);
        }
    }

    /**
     * A comparator that orders records by HUGO symbol (breaking ties
     * with the tumor barcode).
     */
    public static final Comparator<MAFFastaRecord> SYMBOL_COMPARATOR = new SymbolComparator();

    private static final class SymbolComparator implements Comparator<MAFFastaRecord> {
        @Override public int compare(MAFFastaRecord rec1, MAFFastaRecord rec2) {
            int result = rec1.hugoSymbol.compareTo(rec2.hugoSymbol);

            if (result != 0)
                return result;
            else
                return rec1.tumorBarcode.compareTo(rec2.tumorBarcode);
        }
    }

    /**
     * Creates a new FASTA record from the individual attributes.
     *
     * @param tumorBarcode the tumor barcode for the record.
     *
     * @param hugoSymbol the HUGO symbol of the mutated gene.
     *
     * @param cellFraction the cancer cell fraction for the mutated
     * peptide.
     *
     * @param peptide the mutated amino acid sequence.
     */
    public MAFFastaRecord(TumorBarcode tumorBarcode,
                          HugoSymbol   hugoSymbol,
                          CellFraction cellFraction,
                          Peptide      peptide) {
        this.tumorBarcode = tumorBarcode;
        this.hugoSymbol   = hugoSymbol;
        this.cellFraction = cellFraction;
        this.peptide      = peptide;
    }

    /**
     * Creates a new {@code MAFFastaRecord} by parsing the key and
     * comment fields of a FASTA record.
     *
     * <p>The key of the input record must contain the tumor barcode
     * formatted as {@code Tumor_Barcode:BARCODE}; the comment string
     * must contain the HUGO symbol and cancer cell fraction in the
     * format {@code Hugo_Symbol:SYMBOL CCF:0.123}.
     *
     * @param record the FASTA record to parse.
     *
     * @return a new {@code MAFFastaRecord} with the tumor barcode,
     * HUGO symbol, cell fraction, and peptide sequence encoded in
     * the input record.
     *
     * @throws RuntimeException unless the input record contains a
     * properly formatted barcode, HUGO symbol, and cell fraction.
     */
    public static MAFFastaRecord parse(FastaPeptideRecord record) {
        TumorBarcode tumorBarcode = parseTumorBarcode(record);
        HugoSymbol   hugoSymbol   = parseHugoSymbol(record);
        CellFraction cellFraction = parseCellFraction(record);

        return new MAFFastaRecord(tumorBarcode, hugoSymbol, cellFraction, record.getPeptide());
    }

    private static TumorBarcode parseTumorBarcode(FastaPeptideRecord record) {
        return TumorBarcode.instance(parseField(record.getKey(), TumorBarcode.COLUMN_NAME));
    }

    private static String parseField(String field, String tag) {
        String[] fields = RegexUtil.split(RegexUtil.COLON, field, 2);

        if (!fields[0].equals(tag))
            throw JamException.runtime("Invalid [%s] field: [%s].", tag, field);

        return fields[1];
    }

    private static HugoSymbol parseHugoSymbol(FastaPeptideRecord record) {
        return HugoSymbol.instance(parseField(getCommentField(record, 0), HugoSymbol.COLUMN_NAME));
    }

    private static String getCommentField(FastaPeptideRecord record, int index) {
        String[] fields = splitComment(record);
        return fields[index];
    }

    private static String[] splitComment(FastaPeptideRecord record) {
        return RegexUtil.split(RegexUtil.MULTI_WHITE_SPACE, record.getComment());
    }

    private static CellFraction parseCellFraction(FastaPeptideRecord record) {
        return CellFraction.valueOf(parseField(getCommentField(record, 1), CellFraction.COLUMN_NAME));
    }

    /**
     * Formats this record for output to a FASTA file.
     *
     * @return this record formatted as a single string.
     */
    public String format() {
        return toFastaPeptideRecord().format();
    }

    /**
     * Returns the tumor barcode for this record.
     *
     * @return the tumor barcode for this record.
     */
    public TumorBarcode getTumorBarcode() {
        return tumorBarcode;
    }

    /**
     * Returns the symbol of the mutated gene for this record.
     *
     * @return the symbol of the mutated gene for this record.
     */
    public HugoSymbol getHugoSymbol() {
        return hugoSymbol;
    }

    /**
     * Returns the cancer cell fraction for this record.
     *
     * @return the cancer cell fraction for this record.
     */
    public CellFraction getCellFraction() {
        return cellFraction;
    }

    /**
     * Returns the peptide sequence for this record.
     *
     * @return the peptide sequence for this record.
     */
    public Peptide getPeptide() {
        return peptide;
    }

    /**
     * Returns a {@code FastaPeptideRecord} with the key and comment fields
     * formatted to encode the mutation attributes of this record.
     *
     * @return a {@code FastaPeptideRecord} with the key and comment fields
     * formatted to encode the mutation attributes of this record.
     */
    public FastaPeptideRecord toFastaPeptideRecord() {
        return new FastaPeptideRecord(formatKey(), formatComment(), peptide);
    }

    private String formatKey() {
        return String.format("%s:%s", TumorBarcode.COLUMN_NAME, tumorBarcode.getKey());
    }

    private String formatComment() {
        return String.format("%s:%s %s:%.2f",
                             HugoSymbol.COLUMN_NAME,
                             hugoSymbol.getKey(),
                             CellFraction.COLUMN_NAME,
                             cellFraction.doubleValue());
    }

    @Override public int compareTo(MAFFastaRecord that) {
        return BARCODE_COMPARATOR.compare(this, that);
    }

    @Override public String toString() {
        return format();
    }
}
