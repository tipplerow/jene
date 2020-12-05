
package jene.nucleic;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import jam.io.LineReader;
import jam.lang.JamException;

import jene.fasta.FastaReader;

/**
 * Defines a fixed linear sequence of nucleotides.
 */
public abstract class NucleicAcid implements Iterable<Nucleotide> {
    private final List<Nucleotide> nucleotides;

    /**
     * Creates a new (empty) nucleic acid: the concrete subclass must
     * assign the nucleotides.
     */
    protected NucleicAcid() {
        this.nucleotides = new ArrayList<Nucleotide>();
    }

    /**
     * Creates a new (empty) nucleic acid with a known capacity: the
     * concrete subclass must assign the nucleotides.
     *
     * @param capacity the expected number of nucleotides.
     */
    protected NucleicAcid(int capacity) {
        this.nucleotides = new ArrayList<Nucleotide>(capacity);
    }

    /**
     * Adds a nucleotide to this nucleic acid.
     *
     * @param nucleotide the nucleotide to add.
     *
     * @throws IllegalArgumentException unless the nucleotide is
     * permitted in this nucleic acid.
     */
    protected void add(Nucleotide nucleotide) {
        validate(nucleotide);
        nucleotides.add(nucleotide);
    }            

    /**
     * Adds a collection of nucleotides to this nucleic acid.
     *
     * @param nucleotides the nucleotides to add.
     *
     * @throws IllegalArgumentException unless all nucleotides are
     * permitted in this nucleic acid.
     */
    protected void add(Collection<Nucleotide> nucleotides) {
        for (Nucleotide nucleotide : nucleotides)
            add(nucleotide);
    }            

    /**
     * Assigns the nucleotide sequence for this nucleic acid from a
     * string representation containing single-character nucleotide
     * codes.
     *
     * @param s a sequence of single-character nucleotide codes.
     *
     * @throws IllegalArgumentException unless the input string is a
     * valid nucleic acid representation.
     *
     * @throws IllegalStateException if any nucleotides have already
     * been assigned.
     */
    protected void parseString(String s) {
        if (!nucleotides.isEmpty())
            throw new IllegalStateException("Nucleotides have already been assigned.");

        addString(s);
    }

    private void addString(String s) {
        for (int k = 0; k < s.length(); ++k)
            add(Nucleotide.valueOf(s.charAt(k)));
    }

    /**
     * Assigns the nucleotide sequence for this nucleic acid by
     * reading the sequence from a FASTA file.
     *
     * <p>The first line of the FASTA file must be a header line
     * (which is ignored).  All remaining lines must contain the
     * nucleotide sequence.
     *
     * @param file the FASTA file to parse.
     *
     * @throws RuntimeException unless the FASTA file contains a valid
     * nucleic acid representation.
     *
     * @throws IllegalStateException if any nucleotides have already
     * been assigned.
     */
    protected void parseFASTA(File file) {
        if (!nucleotides.isEmpty())
            throw new IllegalStateException("Nucleotides have already been assigned.");

        LineReader reader = LineReader.open(file);

        try {
            String header = reader.next();

            if (!FastaReader.isHeaderLine(header))
                throw JamException.runtime("Missing header line.");

            for (String line : reader)
                if (FastaReader.isHeaderLine(line))
                    throw JamException.runtime("Unexpected header line.");
                else
                    addString(line);
        }
        finally {
            reader.close();
        }
    }

    /**
     * Tests whether this nucleic acid may contain a given nucleotide.
     *
     * @param nucleotide the nucleotide to test.
     *
     * @throws IllegalArgumentException unless the specified
     * nucleotide may reside in this nucleic acid.
     */
    protected abstract void validate(Nucleotide nucleotide);

    /**
     * Returns the nucleotide at a specified location.
     *
     * @param index the index of the desired location.
     *
     * @return the nucleotide at the specified location.
     *
     * @throws IndexOutOfBoundsException unless the index is valid.
     */
    public Nucleotide at(int index) {
        return nucleotides.get(index);
    }

    /**
     * Returns the number of nucleotides in this nucleic acid.
     *
     * @return the number of nucleotides in this nucleic acid.
     */
    public int length() {
        return nucleotides.size();
    }

    /**
     * Returns a read-only view of the nucleotides in this nucleic acid.
     *
     * @return a read-only view of the nucleotides in this nucleic acid.
     */
    public List<Nucleotide> viewNucleotides() {
        return Collections.unmodifiableList(nucleotides);
    }

    /**
     * Formats the nucleotides in this nucleic acid into a string of
     * single-character codes.
     *
     * @return the nucleotides in this nucleic acid as string of
     * single-character codes.
     */
    public String formatString() {
        StringBuilder builder = new StringBuilder();

        for (Nucleotide nucleotide : nucleotides)
            builder.append(nucleotide.name());

        return builder.toString();
    }

    @Override public Iterator<Nucleotide> iterator() {
        return viewNucleotides().iterator();
    }

    @Override public String toString() {
        return String.format("%s(%s)", getClass().getSimpleName(), formatString());
    }
}
