
package jene.tcga;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import jam.app.JamLogger;
import jam.io.TableReader;
import jam.util.PairKeyMultimap;

import jene.hugo.HugoSymbol;
import jene.peptide.Peptide;

/**
 * Indexes peptides by tumor barcode and HUGO symbol.
 */
public final class TumorGenePeptideTable {
    private final PairKeyMultimap<TumorBarcode, HugoSymbol, Peptide> table = PairKeyMultimap.hash();

    private static final int BARCODE_INDEX = 0;
    private static final int SYMBOL_INDEX  = 1;
    private static final int PEPTIDE_INDEX = 2;

    private TumorGenePeptideTable() {
    }

    /**
     * Reads a table from a data file.
     *
     * <p>The file must be delimited (comma, tab, or pipe), contain a
     * header line (which is ignored), and contain the tumor barcode
     * in the first column, the HUGO symbol in the second column, and
     * the peptide in the third column.
     *
     * @param fileName the name of the file to load.
     *
     * @return a table loaded with the data from the file.
     *
     * @throws RuntimeException if any errors occur.
     */
    public static TumorGenePeptideTable load(String fileName) {
        return load(new File(fileName));
    }

    /**
     * Reads a table from a data file.
     *
     * <p>The file must be delimited (comma, tab, or pipe), contain a
     * header line (which is ignored), and contain the tumor barcode
     * in the first column, the HUGO symbol in the second column, and
     * the peptide in the third column.
     *
     * @param file the file to load.
     *
     * @return a table loaded with the data from the file.
     *
     * @throws RuntimeException if any errors occur.
     */
    public static TumorGenePeptideTable load(File file) {
        TableReader reader = TableReader.open(file);
        TumorGenePeptideTable table = new TumorGenePeptideTable();

        table.load(reader);
        return table;
    }

    private void load(TableReader reader) {
        try {
            for (List<String> fields : reader)
                processFields(fields);

            JamLogger.info("TumorGenePeptideTable: Loaded [%d] records.", size());
        }
        finally {
            reader.close();
        }
    }

    private void processFields(List<String> fields) {
        TumorBarcode barcode = TumorBarcode.instance(fields.get(BARCODE_INDEX));
        HugoSymbol   symbol  = HugoSymbol.instance(fields.get(SYMBOL_INDEX));
        Peptide      peptide = Peptide.instance(fields.get(PEPTIDE_INDEX));

        table.put(barcode, symbol, peptide);
    }

    /**
     * Identifies tumors contained in this table.
     *
     * @param barcode a tumor barcode of interest.
     *
     * @return {@code true} iff this table contains the specified
     * barcode.
     */
    public boolean contains(TumorBarcode barcode) {
        return table.contains(barcode);
    }

    /**
     * Identifies tumors and genes contained in this table.
     *
     * @param barcode a tumor barcode of interest.
     *
     * @param symbol a HUGO symbol of interest.
     *
     * @return {@code true} iff this table contains the specified
     * tumor-gene pair.
     */
    public boolean contains(TumorBarcode barcode, HugoSymbol symbol) {
        return table.contains(barcode, symbol);
    }

    /**
     * Returns the peptides mapped to a given tumor and gene.
     *
     * @param barcode a tumor barcode of interest.
     *
     * @param symbol the HUGO symbol of interest.
     *
     * @return the peptides mapped to the specified tumor and gene
     * (an empty collection if there are none).
     */
    public Collection<Peptide> get(TumorBarcode barcode, HugoSymbol symbol) {
        return Collections.unmodifiableCollection(table.get(barcode, symbol));
    }

    /**
     * Returns the number of entries in this table.
     *
     * @return the number of entries in this table.
     */
    public int size() {
        return table.size();
    }

    /**
     * Returns a read-only view of the tumor barcodes in this table.
     *
     * @return a read-only view of the tumor barcodes in this table.
     */
    public Set<TumorBarcode> viewBarcodes() {
        return table.viewOuterKeys();
    }

    /**
     * Returns a read-only view of the HUGO symbols paired with a
     * given tumor.
     *
     * @param barcode a tumor barcode of interest.
     *
     * @return a read-only view of the HUGO symbols paired with the
     * specfied barcode.
     */
    public Set<HugoSymbol> viewSymbols(TumorBarcode barcode) {
        return table.viewInnerKeys(barcode);
    }
}
