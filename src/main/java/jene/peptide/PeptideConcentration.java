
package jene.peptide;

import jene.chem.Concentration;

/**
 * Maps peptides to cellular concentrations.
 */
public final class PeptideConcentration {
    private final Peptide peptide;
    private final Concentration concentration;

    /**
     * Creates a new peptide concentration record.
     *
     * @param peptide the peptide being measured.
     *
     * @param concentration the concentration of the peptide.
     */
    public PeptideConcentration(Peptide peptide, Concentration concentration) {
        this.peptide = peptide;
        this.concentration = concentration;
    }


    /**
     * Adds a positive peptide concentration to this record and
     * returns the result in a new record; this record is unchanged.
     *
     * @param concentration the additional peptide concentration.
     *
     * @return a new record containing the updated concentration.
     */
    public PeptideConcentration plus(Concentration concentration) {
        if (concentration.isPositive())
            return new PeptideConcentration(peptide, this.concentration.plus(concentration));
        else
            return this;
    }

    /**
     * Returns the peptide in this record.
     *
     * @return the peptide in this record.
     */
    public Peptide getPeptide() {
        return peptide;
    }

    /**
     * Returns the concentration in this record.
     *
     * @return the concentration in this record.
     */
    public Concentration getConcentration() {
        return concentration;
    }

    @Override public String toString() {
        return "PeptideConcentration(" + peptide.formatString() + " => " + concentration.doubleValue() + ")";
    }
}
