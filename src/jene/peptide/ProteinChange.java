
package jene.peptide;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jam.lang.JamException;
import jam.math.UnitIndex;
import jam.math.UnitIndexRange;

/**
 * Represents a single missense mutation in a peptide.
 */
public final class ProteinChange {
    private final UnitIndex position;
    private final Residue   native_;
    private final Residue   mutated;

    /**
     * The canonical column name for protein changes in the header
     * line of data files to be analyzed by the {@code jene} library.
     */
    public static final String COLUMN_NAME = "Protein_Change";

    /**
     * Creates a new single missense mutation.
     *
     * @param position the unit-offset position in the protein at
     * which the residue change occurs (starting at position 1).
     *
     * @param native_ the original (native) residue.
     *
     * @param mutated the final (mutated) residue.
     */
    public ProteinChange(int position, Residue native_, Residue mutated) {
        this(UnitIndex.instance(position), native_, mutated);
    }

    /**
     * Creates a new single missense mutation.
     *
     * @param position the unit-offset position in the protein at
     * which the residue change occurs (starting at position 1).
     *
     * @param native_ the original (native) residue.
     *
     * @param mutated the final (mutated) residue.
     */
    public ProteinChange(UnitIndex position, Residue native_, Residue mutated) {
        this.position = position;
        this.native_  = native_;
        this.mutated  = mutated;

        validate();
    }

    private void validate() {
        if (!native_.isNative())
            throw new IllegalArgumentException("Original residue must be naturally occurring.");

        if (!mutated.isNative())
            throw new IllegalArgumentException("Final residue must be naturally occurring.");
    }

    /**
     * A comparator that orders protein changes by the positions of
     * their mutations.
     */
    public static final Comparator<ProteinChange> POSITION_COMPARATOR =
        new Comparator<ProteinChange>() {
            @Override public int compare(ProteinChange pc1, ProteinChange pc2) {
                return pc1.position.compareTo(pc2.position);
            }
        };

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
        Set<UnitIndex> positions = new HashSet<UnitIndex>();

        for (ProteinChange mutation : mutations) {
            UnitIndex position = mutation.getPosition();

            if (positions.contains(position))
                throw JamException.runtime("Duplicate mutation location: [%d].", position);

            mutation.apply(residues);
            positions.add(position);
        }
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
        if (position.get(residues).equals(native_))
            position.set(residues, mutated);
        else
            throw JamException.runtime("Mismatch in the native residue [%s].", toString());
    }

    /**
     * Encodes this protein change in the standard format.
     *
     * @return a string describing this protein change in standard
     * format.
     */
    public String format() {
        return Character.toString(native_.code1())
            + Integer.toString(position.getUnitIndex())
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
     * Returns the unit-offset position the position in the protein at
     * which the residue change occurs (starting at position 1).
     *
     * @return the unit-offset position the position in the protein at
     * which the residue change occurs.
     */
    public UnitIndex getPosition() {
        return position;
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
        return position.isIndexOf(peptide) && position.get(peptide).equals(native_);
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
     * @return a list containing the <em>unit-offset</em> index ranges
     * for all peptide fragments of the specified length that contain
     * the mutation.
     */
    public List<UnitIndexRange> resolveFragments(int fragmentLength, int nativeLength) {
        List<UnitIndexRange> fragmentRanges =
            new ArrayList<UnitIndexRange>(fragmentLength);

        int mutationPosition = position.getUnitIndex();

        int lowerPosition = Math.max(1, mutationPosition - fragmentLength + 1);
        int upperPosition = lowerPosition + fragmentLength - 1;

        while (lowerPosition <= mutationPosition && upperPosition <= nativeLength) {
            UnitIndexRange fragmentRange =
                UnitIndexRange.instance(lowerPosition, upperPosition);

            fragmentRanges.add(fragmentRange);

            ++lowerPosition;
            ++upperPosition;
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
        return position.getUnitIndex() + 37 * native_.hashCode() + 37 * 37 * mutated.hashCode();
    }

    @Override public String toString() {
        return "ProteinChange(" + format() + ")";
    }
}
