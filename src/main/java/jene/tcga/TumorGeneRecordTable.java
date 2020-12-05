
package jene.tcga;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import jam.util.PairKeyTable;

import jene.hugo.HugoSymbol;
import jene.tcga.TumorBarcode;

/**
 * Indexes tumor-gene records by tumor barcode and HUGO symbol,
 * allowing multiple records for a tumor-gene pair.
 */
public abstract class TumorGeneRecordTable<V extends TumorGeneRecord> {
    // All records indexed by barcode (outer) and symbol (inner)...
    private final PairKeyTable<TumorBarcode, HugoSymbol, List<V>> table = PairKeyTable.hash();

    // Total number of records in the table...
    private int count = 0;

    /**
     * Creates and populates a new table.
     *
     * @param records the records to add to the table.
     */
    protected TumorGeneRecordTable(Collection<V> records) {
        fillMap(records);
    }

    private void fillMap(Collection<V> records) {
        for (V record : records)
            addRecord(record);
    }

    private void addRecord(V record) {
        HugoSymbol symbol = record.getHugoSymbol();
        TumorBarcode barcode = record.getTumorBarcode();

        ++count;
        recordList(barcode, symbol).add(record);
    }

    private List<V> recordList(TumorBarcode barcode, HugoSymbol symbol) {
        List<V> recordList = table.get(barcode, symbol);

        if (recordList == null) {
            recordList = new ArrayList<V>();
            table.put(barcode, symbol, recordList);
        }

        return recordList;
    }

    /**
     * Identifies tumor-gene pairs contained in this table.
     *
     * @param barcode the tumor barcode of interest.
     *
     * @param symbol the gene of interest.
     *
     * @return {@code true} iff this table contains one or more
     * records for the specified tumor-gene pair.
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
     * Counts the total number of records for a given tumor.
     *
     * @param barcode the tumor barcode of interest.
     *
     * @return the total number of records for the specified tumor.
     */
    public int count(TumorBarcode barcode) {
        int total = 0;

        for (HugoSymbol symbol : table.viewInnerKeys(barcode))
            total += count(barcode, symbol);

        return total;
    }

    /**
     * Counts the number of records for a given tumor and gene.
     *
     * @param barcode the tumor barcode of interest.
     *
     * @param symbol the gene of interest.
     *
     * @return the number of records for the specified tumor and gene.
     */
    public int count(TumorBarcode barcode, HugoSymbol symbol) {
        return lookup(barcode, symbol).size();
    }

    /**
     * Returns all records for a given tumor.
     *
     * @param barcode the tumor barcode of interest.
     *
     * @return an immutable list containing all records for the
     * specified tumor (an empty list if there are no matching
     * records).
     */
    public List<V> lookup(TumorBarcode barcode) {
        List<V> records =
            new ArrayList<V>();

        for (HugoSymbol symbol : viewSymbols(barcode))
            records.addAll(lookup(barcode, symbol));

        return records;
    }

    /**
     * Returns all records for a given tumor and gene.
     *
     * @param barcode the tumor barcode of interest.
     *
     * @param symbol the HUGO symbol of interest.
     *
     * @return an immutable list containing all records for the
     * specified tumor and gene (or an empty list if there are no
     * matching records).
     */
    public List<V> lookup(TumorBarcode barcode, HugoSymbol symbol) {
        List<V> recordList = table.get(barcode, symbol);

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
