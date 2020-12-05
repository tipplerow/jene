
package jene.neo;

import jene.peptide.Peptide;

/**
 * Provides a base class for neo-peptides and self-peptides.
 */
public abstract class PeptideBase {
    private final Peptide peptide;

    /**
     * Creates a new peptide object.
     *
     * @param peptide the physical peptide.
     */
    protected PeptideBase(Peptide peptide) {
        this.peptide = peptide;
    }

    /**
     * Returns the enumerated peptide type.
     *
     * @return the enumerated peptide type.
     */
    public abstract PeptideType getType();

    /**
     * Returns the physical peptide.
     *
     * @return the physical peptide.
     */
    public Peptide getPeptide() {
        return peptide;
    }

    @Override public boolean equals(Object obj) {
        return (obj instanceof PeptideBase) && equalsPeptide((PeptideBase) obj);
    }

    private boolean equalsPeptide(PeptideBase that) {
        return this.peptide.equals(that.peptide) && this.getType().equals(that.getType());
    }

    @Override public String toString() {
        return String.format("%s(%s)", getClass().getSimpleName(), peptide.formatString());
    }
}
