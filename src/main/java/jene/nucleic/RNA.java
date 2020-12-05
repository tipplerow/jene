
package jene.nucleic;

/**
 * Represents the nucleotide sequence of a RNA molecule.
 */
public final class RNA extends NucleicAcid {
    private RNA() {
        super();
    }

    private RNA(int capacity) {
        super(capacity);
    }

    /**
     * Creates the RNA transcript for a DNA molecule.
     *
     * @param dna the DNA molecule to transcribe.
     *
     * @return the RNA transcript for the specified DNA molecule.
     */
    public static RNA transcribe(DNA dna) {
        RNA rna = new RNA(dna.length());

        for (Nucleotide nucleotide : dna)
            rna.add(nucleotide.transcribe());

        return rna;
    }

    @Override protected void validate(Nucleotide nucleotide) {
        if (!nucleotide.inRNA())
            throw new IllegalArgumentException("Invalid nucleotide: " + nucleotide);
    }
}
