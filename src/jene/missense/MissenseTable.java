
package jene.missense;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import jam.app.JamLogger;
import jam.util.PairKeyTable;

import jene.hugo.HugoSymbol;
import jene.tcga.CellFraction;
import jene.tcga.TumorBarcode;

/**
 * Indexes missesnse mutations by tumor barcode and HUGO symbol.
 */
public final class MissenseTable {
    //
    // Class-specific containers...
    //
    private static final class RecordList extends ArrayList<MissenseRecord> {}

    // All mutations indexed by barcode (outer) and symbol (inner)...
    private final PairKeyTable<TumorBarcode, HugoSymbol, RecordList> table = PairKeyTable.hash();

    // Total number of records in the table...
    private int count = 0;

    private MissenseTable(Collection<MissenseRecord> records, CellFraction threshold) {
        fillMap(records, threshold);
        JamLogger.info("MissenseTable: Retained [%d] records.", count);
    }

    private void fillMap(Collection<MissenseRecord> records, CellFraction threshold) {
        for (MissenseRecord record : records)
            if (record.getCellFraction().above(threshold))
                addRecord(record);
    }

    private void addRecord(MissenseRecord record) {
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
     * Populates a table by reading all missense mutation records from
     * a given file.
     *
     * @param fileName the path to the missense mutation file.
     *
     * @return a table containing all missense mutation records in the
     * given file.
     *
     * @throws RuntimeException unless the file can be opened for
     * reading and contains properly formatted records.
     */
    public static MissenseTable load(String fileName) {
        return load(fileName, CellFraction.ZERO);
    }

    /**
     * Populates a table by reading missense mutation records from a
     * given file and retaining those above a cell fraction threshold.
     *
     * @param fileName the path to the missense mutation file.
     *
     * @param threshold the minimum cancer cell fraction required to
     * be included in the table.
     *
     * @return a table containing missense mutation records in the
     * given file above the specified CCF threshold.
     *
     * @throws RuntimeException unless the file can be opened for
     * reading and contains properly formatted records.
     */
    public static MissenseTable load(String fileName, CellFraction threshold) {
        List<MissenseRecord> records = MissenseParser.parse(fileName);
        JamLogger.info("MissenseTable: Loaded [%d] records.", records.size());
        
        return load(records, threshold);
    }

    /**
     * Populates a table from a collection of missense mutation
     * records.
     *
     * @param records the records to be indexed in the table.
     *
     * @return a table containing all missense mutation records in
     * the given collection.
     */
    public static MissenseTable load(Collection<MissenseRecord> records) {
        return new MissenseTable(records, CellFraction.ZERO);
    }

    /**
     * Populates a table from a collection of missense mutation
     * records.
     *
     * @param records the records to be indexed in the table.
     *
     * @param threshold the minimum cancer cell fraction required to
     * be included in the table.
     *
     * @return a table containing all missense mutation records in
     * the given collection with cell fractions above the specified
     * threshold.
     */
    public static MissenseTable load(Collection<MissenseRecord> records, CellFraction threshold) {
        return new MissenseTable(records, threshold);
    }

    /**
     * Identifies tumor-gene pairs contained in this mutation table.
     *
     * @param barcode the tumor barcode of interest.
     *
     * @param symbol the gene of interest.
     *
     * @return {@code true} iff this table contains mutations for the
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
     * Counts the total number of missense mutations in a given tumor.
     *
     * @param barcode the tumor barcode of interest.
     *
     * @return the total number of missense mutations in the specified
     * tumor.
     */
    public int count(TumorBarcode barcode) {
        int total = 0;

        for (HugoSymbol symbol : table.viewInnerKeys(barcode))
            total += count(barcode, symbol);

        return total;
    }

    /**
     * Counts the number of missense mutations for a given tumor
     * and gene.
     *
     * @param barcode the tumor barcode of interest.
     *
     * @param symbol the gene of interest.
     *
     * @return the number of missense mutations for the specified
     * tumor and gene.
     */
    public int count(TumorBarcode barcode, HugoSymbol symbol) {
        return lookup(barcode, symbol).size();
    }

    /**
     * Groups the mutations by barcode and symbol.
     *
     * @return a list containing all unique missense groups in this
     * table.
     */
    public List<MissenseGroup> group() {
        List<MissenseGroup> groups =
            new ArrayList<MissenseGroup>();

        for (TumorBarcode barcode : viewBarcodes()) {
            for (HugoSymbol symbol : viewSymbols(barcode)) {
                try {
                    groups.add(MissenseGroup.create(lookup(barcode, symbol)));
                }
                catch (RuntimeException ex) {
                    JamLogger.warn(ex);
                }
            }
        }

        return groups;
    }

    /**
     * Groups the mutations for a single barcode by symbol.
     *
     * @param barcode the tumor sample of interest.
     *
     * @return a list containing all unique missense groups for the
     * specified barcode.
     */
    public List<MissenseGroup> group(TumorBarcode barcode) {
        List<MissenseGroup> groups =
            new ArrayList<MissenseGroup>();

        for (HugoSymbol symbol : viewSymbols(barcode)) {
            try {
                groups.add(MissenseGroup.create(lookup(barcode, symbol)));
            }
            catch (RuntimeException ex) {
                JamLogger.warn(ex);
            }
        }

        return groups;
    }

    /**
     * Returns all missense mutations for a given tumor and gene.
     *
     * @param barcode the tumor barcode of interest.
     *
     * @param symbol the HUGO symbol of interest.
     *
     * @return an immutable list containing all missense mutations for
     * the specified tumor and gene (or an empty list if there are no
     * matching mutations).
     */
    public List<MissenseRecord> lookup(TumorBarcode barcode, HugoSymbol symbol) {
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
