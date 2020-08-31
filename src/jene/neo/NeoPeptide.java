
package jene.neo;

import jene.peptide.Peptide;

/**
 * Represents peptides derived from the germline genome.
 */
public final class NeoPeptide extends PeptideBase {
    private NeoPeptide(Peptide peptide) {
        super(peptide);
    }

    /**
     * Creates a new neo-peptide.
     *
     * @param peptide the physical peptide.
     *
     * @return the new neo-peptide.
     */
    public static NeoPeptide instance(Peptide peptide) {
        return new NeoPeptide(peptide);
    }

    /**
     * Creates a new neo-peptide.
     *
     * @param sequence the amino acid sequence (as single-character
     * residue codes).
     *
     * @return the new neo-peptide.
     */
    public static NeoPeptide instance(String sequence) {
        return new NeoPeptide(Peptide.instance(sequence));
    }

    @Override public PeptideType getType() {
        return PeptideType.NEO;
    }
}
