
package jene.fasta;

import java.util.regex.Pattern;

import jam.util.RegexUtil;

/**
 * Provides a base class for FASTA records.
 */
public abstract class FastaRecord {
    private final String key;
    private final String comment;

    /**
     * Creates a new FASTA record.
     *
     * @param key the record key.
     *
     * @param comment thd record comment.
     */
    protected FastaRecord(String key, String comment) {
        this.key = key;
	this.comment = comment;
    }

    /**
     * Special character identifying header lines in FASTA files.
     */
    public static final String HEADER_MARKER = ">";

    /**
     * Delimiter separating the header key from header comment text.
     */
    public static final Pattern KEY_COMMENT_DELIM = RegexUtil.MULTI_WHITE_SPACE;

    /**
     * Maximum line length for FASTA files.
     */
    public static final int LINE_LENGTH = 70;

    /**
     * Formats this record for output to a FASTA file.
     *
     * @return this record formatted as a single string.
     */
    public abstract String format();

    /**
     * Returns the key for this record.
     *
     * @return the key for this record.
     */
    public String getKey() {
        return key;
    }

    /**
     * Returns the comment for this record.
     *
     * @return the comment for this record.
     */
    public String getComment() {
        return comment;
    }

    @Override public String toString() {
	return format();
    }
}
