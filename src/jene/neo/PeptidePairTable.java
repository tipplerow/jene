
package jene.neo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jam.app.JamLogger;
import jam.io.LineReader;

import jene.tcga.TumorGeneRecordTable;

/**
 * Indexes peptide pair records by tumor barcode and HUGO symbol.
 */
public final class PeptidePairTable extends TumorGeneRecordTable<PeptidePairRecord> {
    private PeptidePairTable(Collection<PeptidePairRecord> records) {
        super(records);
    }

    /**
     * Populates a table from a collection of records.
     *
     * @param records the records to be indexed in the table.
     *
     * @return a table containing all records in the given collection.
     */
    public static PeptidePairTable create(Collection<PeptidePairRecord> records) {
        return new PeptidePairTable(records);
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
        return create(records);
    }
}
