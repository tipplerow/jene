
package jene.fasta;

import java.io.File;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.List;

import jam.io.IOUtil;

/**
 * Reads and writes FASTA files.
 */
public final class FastaPeptideFile {
    /**
     * Reads FASTA records from a file.
     *
     * @param file the file to read.
     *
     * @return a list of records read from the file.
     *
     * @throws RuntimeException if the file cannot be opened for
     * reading or if it contains improperly formatted peptides.
     */
    public static List<FastaPeptideRecord> read(File file) {
        return FastaPeptideReader.read(file);
    }

    /**
     * Reads FASTA records from a file.
     *
     * @param fileName the name of the file to read.
     *
     * @return a list of records read from the file.
     *
     * @throws RuntimeException if any errors occur.
     */
    public static List<FastaPeptideRecord> read(String fileName) {
        return read(new File(fileName));
    }

    /**
     * Writes FASTA records to a file.
     *
     * @param file the output file.
     *
     * @param records the records to write.
     *
     * @throws RuntimeException if any I/O errors occur.
     */
    public static void write(File file, Collection<FastaPeptideRecord> records) {
	PrintWriter writer = IOUtil.openWriter(file);

        for (FastaPeptideRecord record : records)
            writer.println(record.format());

        writer.close();
    }

    /**
     * Writes FASTA records to a file.
     *
     * @param fileName the name of the output file.
     *
     * @param records the records to write.
     *
     * @throws RuntimeException if any I/O errors occur.
     */
    public static void write(String fileName, Collection<FastaPeptideRecord> records) {
        write(new File(fileName), records);
    }
}
