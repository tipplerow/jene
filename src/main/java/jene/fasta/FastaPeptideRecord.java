
package jene.fasta;

import jene.peptide.Peptide;

/**
 * Encapsulates a FASTA peptide record.
 */
public final class FastaPeptideRecord extends FastaRecord {
    private final Peptide peptide;

    /**
     * Creates a new FASTA peptide record.
     *
     * @param key the record key.
     *
     * @param comment thd record comment.
     *
     * @param peptide the peptide sequence.
     */
    public FastaPeptideRecord(String key, String comment, Peptide peptide) {
        super(key, comment);
        this.peptide = peptide;
    }

    /**
     * Formats this record for output to a FASTA file.
     *
     * @return this record formatted as a single string.
     */
    @Override public String format() {
        StringBuilder builder = new StringBuilder();

        builder.append(HEADER_MARKER);
        builder.append(getKey());
        builder.append(" ");
        builder.append(getComment());

        for (int index = 0; index < peptide.length(); ++index) {
            if (index % LINE_LENGTH == 0)
                builder.append(System.lineSeparator());

            builder.append(peptide.get(index).code1());
        }

        return builder.toString();
    }

    /**
     * Returns the peptide sequence in this record.
     *
     * @return the peptide sequence in this record.
     */
    public Peptide getPeptide() {
        return peptide;
    }
}
