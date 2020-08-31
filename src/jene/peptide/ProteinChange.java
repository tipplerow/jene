
package jene.peptide;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jam.lang.JamException;
import jam.math.IntRange;

/**
 * Represents a single missense mutation in a peptide.
 */
public final class ProteinChange {
    private final int position;
    private final Residue native_;
    private final Residue mutated;

    /**
     * The canonical column name for protein changes in the header
     * line of data files to be analyzed by the {@code jene} library.
     */
    public static final String COLUMN_NAME = "Protein_Change";

    /**
     * Creates a new single missense mutation.
     *
     * @param position the position in the protein at which the
     * residue change occurs (starting at position 1, not zero).
     *
     * @param native_ the original (native) residue.
     *
     * @param mutated the final (mutated) residue.
     */
    public ProteinChange(int position, Residue native_, Residue mutated) {
        this.position = position;
        this.native_  = native_;
        this.mutated  = mutated;

        validate();
    }

    private void validate() {
        if (position < 1)
            throw new IllegalArgumentException("Position must be positive.");

        if (!native_.isNative())
            throw new IllegalArgumentException("Original residue must be naturally occurring.");

        if (!mutated.isNative())
            throw new IllegalArgumentException("Final residue must be naturally occurring.");
    }

    /**
     * Applies this mutation to a list of residues (representing the
     * original native peptide structure).
     *
     * @param residues the original native peptide structure.
     *
     * @throws RuntimeException unless this mutation lies within the
     * residue list and the residue at the mutation location matches
     * the native residue in this mutation.
     */
    public void apply(List<Residue> residues) {
        int residueIndex = getResidueIndex();

        if (residueIndex >= residues.size())
            throw JamException.runtime("Mutation position lies outside the peptide.");

        if (!residues.get(residueIndex).equals(getNative()))
            throw JamException.runtime("Mismatch in the native residue [%s].", toString());

        residues.set(residueIndex, getMutated());
    }

    /**
     * Applies a collection of mutations to a list of residues
     * (representing the original native peptide structure).
     *
     * @param mutations the mutations to apply.
     *
     * @param residues the original native peptide structure.
     *
     * @throws RuntimeException unless all native residues in the
     * mutations match those in the native peptide, all mutations
     * occur at different locations in the native peptide, and all
     * mutations occur at valid locations within the peptide.
     */
    public static void apply(Collection<ProteinChange> mutations, List<Residue> residues) {
        //
        // Ensure that all mutations occur at different locations by
        // keeping a record of those locations...
        //
        Set<Integer> residueIndexes = new HashSet<Integer>();

        for (ProteinChange mutation : mutations) {
            int residueIndex = mutation.getResidueIndex();

            if (residueIndexes.contains(residueIndex))
                throw JamException.runtime("Duplicate mutation location: [%d].", residueIndex);

            mutation.apply(residues);
            residueIndexes.add(residueIndex);
        }
    }

    /**
     * Parses a protein change string in standard format.
     *
     * <p>The standard format for a protein change from residue {@code
     * X} to residue {@code Y} at position {@code k} is {@code XkY},
     * where {@code X} and {@code Y} are the single-character residue
     * codes.  The string may also start with {@code p.}.
     *
     * @param s a protein-change string in standard format.
     *
     * @return the protein change encoded in the given string.
     *
     * @throws RuntimeException unless the string is properly formatted.
     */
    public static ProteinChange parse(String s) {
        if (s.startsWith("p."))
            s = s.substring(2);

        char nativeChar = s.charAt(0);
        char mutatedChar = s.charAt(s.length() - 1);
        String positionStr = s.substring(1, s.length() - 1);

        return new ProteinChange(Integer.parseInt(positionStr),
                                 Residue.valueOfCode1(nativeChar),
                                 Residue.valueOfCode1(mutatedChar));
    }

    /**
     * Encodes this protein change in the standard format.
     *
     * @return a string describing this protein change in standard
     * format.
     */
    public String format() {
        return Character.toString(native_.code1())
            + Integer.toString(position)
            + Character.toString(mutated.code1());
    }

    /**
     * Returns the original (native) residue.
     *
     * @return the original (native) residue.
     */
    public Residue getNative() {
        return native_;
    }

    /**
     * Returns the final (mutated) residue.
     *
     * @return the final (mutated) residue.
     */
    public Residue getMutated() {
        return mutated;
    }

    /** 
     * Returns the position the position in the protein at which the
     * residue change occurs (starting at position 1, not zero).
     *
     * @return the position the position in the protein at which the
     * residue change occurs.
     */
    public int getPosition() {
        return position;
    }

    /** 
     * Returns the zero-offset index of the residue changed by this
     * mutation.
     *
     * @return the zero-offset index of the residue changed by this
     * mutation.
     */
    public int getResidueIndex() {
        return position - 1;
    }

    /**
     * Identifies native peptides having a sequence consistent with
     * a collection of protein changes.
     *
     * @param peptide a peptide to examine.
     *
     * @param changes the protein changes to test.
     *
     * @return {@code true} iff the residue at the mutation position
     * in the specified peptide matches the native residue in this
     * protein change.
     */
    public static boolean isNative(Peptide peptide, Collection<ProteinChange> changes) {
        for (ProteinChange change : changes)
            if (!change.isNative(peptide))
                return false;

        return true;
    }

    /**
     * Identifies native peptides having a sequence consistent with
     * this protein change.
     *
     * @param peptide a peptide to examine.
     *
     * @return {@code true} iff the residue at the mutation position
     * in the specified peptide matches the native residue in this
     * protein change.
     */
    public boolean isNative(Peptide peptide) {
        int residueIndex = getResidueIndex();

        return residueIndex < peptide.length() && peptide.get(residueIndex).equals(native_);
    }

    /**
     * Finds all peptide fragments of a fixed length that contain
     * the mutation.
     *
     * @param fragmentLength the length of the peptide fragments
     * to identify.
     *
     * @param nativeLength the length of the native peptide where
     * the mutation occurred.
     *
     * @return a list containing the residue index ranges for all
     * peptide fragments having the specified length that contain
     * the mutation.
     */
    public List<IntRange> resolveFragments(int fragmentLength, int nativeLength) {
        List<IntRange> fragmentRanges =
            new ArrayList<IntRange>(fragmentLength);

        int mutationIndex = getResidueIndex();
        int lowerResidue  = Math.max(0, mutationIndex - fragmentLength + 1);
        int upperResidue  = lowerResidue + fragmentLength - 1;

        while (lowerResidue <= mutationIndex && upperResidue < nativeLength) {
            IntRange fragmentRange =
                IntRange.instance(lowerResidue, upperResidue);

            fragmentRanges.add(fragmentRange);

            ++lowerResidue;
            ++upperResidue;
        }

        return fragmentRanges;
    }
         
    @Override public boolean equals(Object obj) {
        return (obj instanceof ProteinChange) && equalsProteinChange((ProteinChange) obj);
    }

    private boolean equalsProteinChange(ProteinChange that) {
        return this.position == that.position
            && this.native_.equals(that.native_)
            && this.mutated.equals(that.mutated);
    }

    @Override public int hashCode() {
        return position + 37 * native_.hashCode() + 37 * 37 * mutated.hashCode();
    }

    @Override public String toString() {
        return "ProteinChange(" + format() + ")";
    }
}
