
package jene.peptide;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import jene.chem.Concentration;

/**
 * Maps peptides to cellular concentrations.
 */
public final class PeptideConcentrationBuilder {
    private final Map<Peptide, Concentration> map =
        new HashMap<Peptide, Concentration>();

    private PeptideConcentrationBuilder() {
    }

    /**
     * Creates a new (empty) profile builder.
     *
     * @return a new (empty) profile profile.
     */
    public static PeptideConcentrationBuilder create() {
        return new PeptideConcentrationBuilder();
    }

    /**
     * Adds a positive peptide concentration to this builder.
     *
     * <p>If the peptide is already present, the specified
     * concentration is added to the existing concentration.
     *
     * <p>Concentrations that are equal to zero (within the standard
     * floating-point tolerance) will <em>not</em> be added, so that
     * the profile will only contain peptides with a net positive
     * concentration.
     *
     * @param peptide the peptide to add.
     *
     * @param concentration the concentration of the peptide.
     */
    public void add(Peptide peptide, Concentration concentration) {
        if (!concentration.isPositive())
            return;

        Concentration existing = map.get(peptide);

        if (existing == null)
            map.put(peptide, concentration);
        else
            map.put(peptide, existing.plus(concentration));
    }

    /**
     * Adds a positive peptide concentration to this builder.
     *
     * <p>If the peptide is already present, the specified
     * concentration is added to the existing concentration.
     *
     * <p>Concentrations that are equal to zero (within the standard
     * floating-point tolerance) will <em>not</em> be added, so that
     * the profile will only contain peptides with a net positive
     * concentration.
     *
     * @param record the concentration record to add.
     */
    public void add(PeptideConcentration record) {
        add(record.getPeptide(), record.getConcentration());
    }

    /**
     * Adds peptides to this profile.
     *
     * <p>If any peptide is already present in this profile, the
     * specified concentration is added to its existing concentration.
     *
     * @param peptides the peptides to add.
     *
     * @param concentration the uniform concentration of each peptide.
     */
    public void addAll(Collection<Peptide> peptides, Concentration concentration) {
        for (Peptide peptide : peptides)
            add(peptide, concentration);
    }

    /**
     * Creates a new (read-only) peptide concentration profile
     * reflecting the current state of this builder.
     *
     * @return a new (read-only) peptide concentration profile
     * containing the concentrations currenly in this builder.
     */
    public PeptideConcentrationProfile build() {
        return PeptideConcentrationProfile.create(map);
    }
}
