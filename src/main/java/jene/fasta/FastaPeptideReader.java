
package jene.fasta;

import java.io.File;
import java.util.List;

import jene.peptide.Peptide;

/**
 * Reads FASTA records sequentially from a FASTA-formatted file.
 */
public final class FastaPeptideReader extends FastaReader<FastaPeptideRecord> {
    private FastaPeptideReader(File file) {
        super(file);
    }

    /**
     * Opens a FASTA peptide file for reading.
     *
     * @param file the path to the FASTA file.
     *
     * @return the opened reader.
     *
     * @throws RuntimeException if the file cannot be opened for
     * reading.
     */
    public static FastaPeptideReader open(File file) {
        return new FastaPeptideReader(file);
    }

    /**
     * Opens a FASTA peptide file for reading.
     *
     * @param fileName the name of the FASTA file.
     *
     * @return the opened reader.
     *
     * @throws RuntimeException if the file cannot be opened for
     * reading.
     */
    public static FastaPeptideReader open(String fileName) {
        return open(new File(fileName));
    }

    /**
     * Reads all FASTA peptide records from a given file.
     *
     * @param file the path to the FASTA file.
     *
     * @return all FASTA records contained in the given file.
     *
     * @throws RuntimeException if the file cannot be opened for
     * reading or if the file contains invalid records.
     */
    public static List<FastaPeptideRecord> read(File file) {
        try (FastaPeptideReader reader = open(file)) {
            return reader.read();
        }
    }

    /**
     * Reads all FASTA peptide records from a given file.
     *
     * @param fileName the name of the FASTA file.
     *
     * @return all FASTA records contained in the given file.
     *
     * @throws RuntimeException if the file cannot be opened for
     * reading or if the file contains invalid records.
     */
    public static List<FastaPeptideRecord> read(String fileName) {
        return read(new File(fileName));
    }

    /**
     * Creates the next FASTA record from the file.
     *
     * @param key the FASTA record key.
     *
     * @param comment the FASTA record comment text.
     *
     * @param data the concatenated data lines for the record entry
     * (containing the nucleotides or residues in the record).
     *
     * @return the FASTA record encoded in the header and data lines.
     */
    @Override public FastaPeptideRecord createRecord(String key, String comment, String data) {
        return new FastaPeptideRecord(key, comment, Peptide.instance(data));
    }
}
