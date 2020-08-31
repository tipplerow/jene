
package jene.neo;

import jene.peptide.Peptide;

/**
 * Represents peptides derived from the germline genome.
 */
public final class SelfPeptide extends PeptideBase {
    private SelfPeptide(Peptide peptide) {
        super(peptide);
    }

    /**
     * Creates a new self-peptide.
     *
     * @param peptide the physical peptide.
     *
     * @return the new self-peptide.
     */
    public static SelfPeptide instance(Peptide peptide) {
        return new SelfPeptide(peptide);
    }

    /**
     * Creates a new self-peptide.
     *
     * @param sequence the amino acid sequence (as single-character
     * residue codes).
     *
     * @return the new self-peptide.
     */
    public static SelfPeptide instance(String sequence) {
        return new SelfPeptide(Peptide.instance(sequence));
    }

    @Override public PeptideType getType() {
        return PeptideType.SELF;
    }
}
