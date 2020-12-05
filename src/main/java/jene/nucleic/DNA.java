
package jene.nucleic;

import java.io.File;

/**
 * Represents the nucleotide sequence of a DNA molecule.
 */
public final class DNA extends NucleicAcid {
    private DNA() {
        super();
    }

    private DNA(int capacity) {
        super(capacity);
    }

    /**
     * Creates a DNA molecule from a string of single-character
     * nucleotide codes.
     *
     * @param s a sequence of single-character nucleotide codes.
     *
     * @return the DNA molecule encoded in the input string.
     *
     * @throws IllegalArgumentException unless the input string
     * is a valid DNA representation.
     */
    public static DNA parse(String s) {
        DNA dna = new DNA(s.length());
        dna.parseString(s);
        return dna;
    }

    /**
     * Creates a DNA molecule by reading the nucleotide sequence from
     * a FASTA file.
     *
     * <p>The first line of the FASTA file must be a header line
     * (which is ignored).  All remaining lines must contain the
     * nucleotide sequence.
     *
     * @param fileName the name of the FASTA file to parse.
     *
     * @return the DNA molecule encoded in the input file.
     *
     * @throws RuntimeException unless the FASTA file contains a valid
     * DNA representation.
     */
    public static DNA load(String fileName) {
        return load(new File(fileName));
    }

    /**
     * Creates a DNA molecule by reading the nucleotide sequence from
     * a FASTA file.
     *
     * <p>The first line of the FASTA file must be a header line
     * (which is ignored).  All remaining lines must contain the
     * nucleotide sequence.
     *
     * @param file the FASTA file to parse.
     *
     * @return the DNA molecule encoded in the input file.
     *
     * @throws RuntimeException unless the FASTA file contains a valid
     * DNA representation.
     */
    public static DNA load(File file) {
        DNA dna = new DNA();
        dna.parseFASTA(file);
        return dna;
    }

    /**
     * Returns the transcription product of this DNA molecule.
     *
     * @return the transcription product of this DNA molecule.
     */
    public RNA transcribe() {
        return RNA.transcribe(this);
    }

    @Override protected void validate(Nucleotide nucleotide) {
        if (!nucleotide.inDNA())
            throw new IllegalArgumentException("Invalid nucleotide: " + nucleotide);
    }
}
