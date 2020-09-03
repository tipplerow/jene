
package jene.neo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import jam.app.JamLogger;
import jam.io.LineReader;
import jam.util.PairKeyTable;

import jene.hugo.HugoSymbol;
import jene.tcga.TumorBarcode;

/**
 * Indexes peptide pair records by tumor barcode and HUGO symbol.
 */
public final class PeptidePairTable {
    //
    // Class-specific container...
    //
    private static final class RecordList extends ArrayList<PeptidePairRecord> {}

    // All records indexed by barcode (outer) and symbol (inner)...
    private final PairKeyTable<TumorBarcode, HugoSymbol, RecordList> table = PairKeyTable.hash();

    // Total number of records in the table...
    private int count = 0;

    private PeptidePairTable(Collection<PeptidePairRecord> records) {
        fillMap(records);
    }

    private void fillMap(Collection<PeptidePairRecord> records) {
        for (PeptidePairRecord record : records)
            addRecord(record);
    }

    private void addRecord(PeptidePairRecord record) {
        HugoSymbol   symbol  = record.getHugoSymbol();
        TumorBarcode barcode = record.getTumorBarcode();

        ++count;
        recordList(barcode, symbol).add(record);
    }

    private RecordList recordList(TumorBarcode barcode, HugoSymbol symbol) {
        RecordList recordList = table.get(barcode, symbol);

        if (recordList == null) {
            recordList = new RecordList();
            table.put(barcode, symbol, recordList);
        }

        return recordList;
    }

    /**
     * Populates a table by reading all records from a given file.
     *
     * @param fileName the path to the missense mutation file.
     *
     * @return a table containing all records in the given file.
     *
     * @throws RuntimeException unless the file can be opened for
     * reading and contains properly formatted records.
     */
    public static PeptidePairTable load(String fileName) {
        List<PeptidePairRecord> records =
            new ArrayList<PeptidePairRecord>();

        try (LineReader reader = LineReader.open(fileName)) {
            // Skip header line...
            reader.next();

            for (String line : reader)
                records.add(PeptidePairRecord.parse(line));
        }

        JamLogger.info("PeptidePairTable: Loaded [%d] records.", records.size());
        return load(records);
    }

    /**
     * Populates a table from a collection of records.
     *
     * @param records the records to be indexed in the table.
     *
     * @return a table containing all records in the given collection.
     */
    public static PeptidePairTable load(Collection<PeptidePairRecord> records) {
        return new PeptidePairTable(records);
    }

    /**
     * Identifies tumor-gene pairs contained in this mutation table.
     *
     * @param barcode the tumor barcode of interest.
     *
     * @param symbol the gene of interest.
     *
     * @return {@code true} iff this table contains records for the
     * specified tumor-gene pair.
     */
    public boolean contains(TumorBarcode barcode, HugoSymbol symbol) {
        return table.contains(barcode, symbol);
    }

    /**
     * Returns the total number of records in this table.
     *
     * @return the total number of records in this table.
     */
    public int count() {
        return count;
    }

    /**
     * Counts the total number of peptide pair records for a given
     * tumor.
     *
     * @param barcode the tumor barcode of interest.
     *
     * @return the total number of peptide pair records for the
     * specified tumor.
     */
    public int count(TumorBarcode barcode) {
        int total = 0;

        for (HugoSymbol symbol : table.viewInnerKeys(barcode))
            total += count(barcode, symbol);

        return total;
    }

    /**
     * Counts the number of peptide pair records for a given tumor
     * and gene.
     *
     * @param barcode the tumor barcode of interest.
     *
     * @param symbol the gene of interest.
     *
     * @return the number of peptide pair records for the specified
     * tumor and gene.
     */
    public int count(TumorBarcode barcode, HugoSymbol symbol) {
        return lookup(barcode, symbol).size();
    }

    /**
     * Returns all peptide pair records for a given tumor.
     *
     * @param barcode the tumor barcode of interest.
     *
     * @return an immutable list containing all peptide pair records
     * for the specified tumor (an empty list if there are no matching
     * records).
     */
    public List<PeptidePairRecord> lookup(TumorBarcode barcode) {
        List<PeptidePairRecord> records =
            new ArrayList<PeptidePairRecord>();

        for (HugoSymbol symbol : viewSymbols(barcode))
            records.addAll(lookup(barcode, symbol));

        return records;
    }

    /**
     * Returns all peptide pair records for a given tumor and gene.
     *
     * @param barcode the tumor barcode of interest.
     *
     * @param symbol the HUGO symbol of interest.
     *
     * @return an immutable list containing all peptide pair records
     * for the specified tumor and gene (or an empty list if there are
     * no matching records).
     */
    public List<PeptidePairRecord> lookup(TumorBarcode barcode, HugoSymbol symbol) {
        RecordList recordList = table.get(barcode, symbol);

        if (recordList != null)
            return Collections.unmodifiableList(recordList);
        else
            return Collections.emptyList();
    }

    /**
     * Returns a read-only view of all tumor barcodes in this table.
     *
     * @return a read-only view of all tumor barcodes in this table.
     */
    public Set<TumorBarcode> viewBarcodes() {
        return table.viewOuterKeys();
    }

    /**
     * Returns a read-only view of all mutated genes for a given tumor.
     *
     * @param barcode the tumor barcode of interest.
     *
     * @return a read-only view of all mutated genes for the specified
     * tumor.
     */
    public Set<HugoSymbol> viewSymbols(TumorBarcode barcode) {
        return table.viewInnerKeys(barcode);
    }
}
