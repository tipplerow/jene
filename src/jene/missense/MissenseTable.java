
package jene.missense;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jam.app.JamLogger;

import jene.hugo.HugoSymbol;
import jene.tcga.CellFraction;
import jene.tcga.TumorBarcode;
import jene.tcga.TumorGeneRecordTable;

/**
 * Indexes missesnse mutations by tumor barcode and HUGO symbol.
 */
public final class MissenseTable extends TumorGeneRecordTable<MissenseRecord> {
    private MissenseTable(Collection<MissenseRecord> records) {
        super(records);
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
    public static MissenseTable create(Collection<MissenseRecord> records) {
        return new MissenseTable(records);
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
        List<MissenseRecord> records = MissenseParser.parse(fileName);
        JamLogger.info("MissenseTable: Loaded [%d] records.", records.size());
        
        return create(records);
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
        
        records = MissenseRecord.filterCellFraction(records, threshold);
        JamLogger.info("MissenseTable: Retained [%d] records.", records.size());

        return create(records);
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
}
