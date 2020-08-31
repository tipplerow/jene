
package jene.fasta;

import java.io.BufferedReader;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;

import jam.app.JamLogger;
import jam.io.IOUtil;
import jam.lang.JamException;
import jam.util.RegexUtil;

/**
 * Provides a base class for reading FASTA records sequentially from a
 * FASTA-formatted file.
 */
public abstract class FastaReader<V extends FastaRecord> implements AutoCloseable, Iterable<V>, Iterator<V> {
    private final File file;
    private final BufferedReader reader;

    // The next header line to be processed (null when there are no
    // records remaining)...
    private String nextHeaderLine;

    /**
     * Creates a new FASTA reader over a fixed FASTA file.
     *
     * @param file the FASTA file to process.
     */
    protected FastaReader(File file) {
        JamLogger.info("Reading [%s]...", file);

        this.file = file;
        this.reader = IOUtil.openReader(file);

        // Advance the next header pointer to the first non-comment
        // line in the file...
        this.nextHeaderLine = nextDataLine();
    }

    private String nextDataLine() {
        return IOUtil.nextDataLine(reader, COMMENT_PATTERN);
    }

    /**
     * The comment character for FASTA files.
     */
    public static final Pattern COMMENT_PATTERN = RegexUtil.SEMICOLON;

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
    public abstract V createRecord(String key, String comment, String data);

    /**
     * Identifies header lines in FASTA files.
     *
     * @param line a line from a FASTA file.
     *
     * @return {@code true} iff the specified line is a header line.
     */
    public static boolean isHeaderLine(String line) {
        return line.startsWith(FastaRecord.HEADER_MARKER);
    }

    /**
     * Reads all FASTA records from this file.
     *
     * @return all FASTA records contained in the file.
     *
     * @throws RuntimeException unless all records in the file are
     * properly formatted.
     */
    public List<V> read() {
        List<V> records = new ArrayList<V>();

        for (V record : this)
            records.add(record);

        return records;
    }

    /**
     * Closes this reader.
     */
    @Override public void close() {
        IOUtil.close(reader);
    }

    /**
     * Identifies the end of the FASTA file.
     *
     * @return {@code true} iff the FASTA file contains another FASTA
     * record.
     */
    @Override public boolean hasNext() {
        return nextHeaderLine != null;
    }

    /**
     * Returns an iterator over all FASTA records in this file.
     *
     * @return an iterator over all FASTA records in this file.
     */
    @Override public Iterator<V> iterator() {
        return this;
    }

    /**
     * Reads the next complete FASTA record from the file.
     *
     * @return the next complete FASTA record from the file.
     *
     * @throws RuntimeException unless the file contains at least one
     * more properly formatted FASTA record.
     */
    @Override public V next() {
        if (nextHeaderLine != null)
            return createRecord(nextHeaderLine, readData());
        else
            throw new NoSuchElementException();
    }

    private String readData() {
        StringBuilder builder = new StringBuilder();

        while (true) {
            String line = nextDataLine();

            if (line == null) {
                nextHeaderLine = null;
                break;
            }
            else if (isHeaderLine(line)) {
                nextHeaderLine = line;
                break;
            }
            else {
                builder.append(line);
            }
        }

        return builder.toString();
    }

    private V createRecord(String headerLine, String data) {
        if (!isHeaderLine(headerLine))
            throw JamException.runtime("Expected a record header line but found [%s].", headerLine);

        if (data.isEmpty())
            throw JamException.runtime("No data for record [%s].", headerLine);

        // Remove the header marker...
        headerLine = headerLine.substring(FastaRecord.HEADER_MARKER.length());

        // Split into key and comment, delimited by white space...
        String[] fields = FastaRecord.KEY_COMMENT_DELIM.split(headerLine, 2);

        String key = fields[0];
        String comment = (fields.length == 2) ? fields[1] : "";

        return createRecord(key, comment, data);
    }
}
