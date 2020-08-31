
package jene.maf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import jam.app.JamLogger;
import jam.util.ListUtil;
import jam.util.PairKeyTable;

import jene.fasta.FastaPeptideReader;
import jene.hugo.HugoSymbol;
import jene.tcga.TumorBarcode;

/**
 * Indexes a collection of {@code MAFFastaRecord}s by tumor barcode
 * and HUGO symbol.
 */
public final class MAFFastaTable {
    private final PairKeyTable<TumorBarcode, HugoSymbol, MAFFastaRecord> table = PairKeyTable.hash();

    private MAFFastaTable(Collection<MAFFastaRecord> records) {
        this.table.fill(records, x -> x.getTumorBarcode(), x -> x.getHugoSymbol());
    }

    /**
     * Populates a table by reading all records from a given file.
     *
     * @param fileName the path to the {@code MAFFastaRecord} file.
     *
     * @return a table containing all records in the given file.
     *
     * @throws RuntimeException unless the file can be opened for
     * reading and contains properly formatted records.
     */
    public static MAFFastaTable load(String fileName) {
        List<MAFFastaRecord> records =
            ListUtil.apply(FastaPeptideReader.read(fileName), x -> MAFFastaRecord.parse(x));

        JamLogger.info("MAFFastaTable: Loaded [%d] records.", records.size());
        return load(records);
    }

    /**
     * Populates a table from a collection of {@code MAFFastaRecord}
     * records.
     *
     * @param records the records to be indexed in the table.
     *
     * @return a table containing all records in the collection.
     */
    public static MAFFastaTable load(Collection<MAFFastaRecord> records) {
        return new MAFFastaTable(records);
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
     * Returns the {@code MAFFastaRecord} for a given tumor and gene.
     *
     * @param barcode the tumor barcode of interest.
     *
     * @param symbol the HUGO symbol of interest.
     *
     * @return the {@code MAFFastaRecord} for the specified tumor and
     * gene (or {@code null} if none exists).
     */
    public MAFFastaRecord lookup(TumorBarcode barcode, HugoSymbol symbol) {
        return table.get(barcode, symbol);
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
