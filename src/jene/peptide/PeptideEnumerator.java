
package jene.peptide;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.EnumMultiset;
import com.google.common.collect.Multiset;

/**
 * Enumerates all possible peptides of a given length.
 */
public final class PeptideEnumerator {
    /**
     * The maximum peptide length that can be explicitly enumerated
     * without exceeding the maximum size of a {@code Collection}.
     */
    public static final int ENUMERATION_LIMIT = 7;

    /**
     * Enumerates every native peptide of a given length.
     *
     * @param length the desired peptide length.
     *
     * @return a list containing all possible native peptides
     * (composed of native residues) with the specified length.
     *
     * @throws IllegalArgumentException unless the length is positive
     * but not greater than the enumeration limit.
     */
    public static List<Peptide> enumerate(int length) {
        if (length < 1)
            throw new IllegalArgumentException("Length must be positive.");

        if (length == 1)
            return enumerate1();

        if (length <= ENUMERATION_LIMIT)
            return addGeneration(enumerate(length - 1));

        throw new IllegalArgumentException("Length must not exceed the enumeration limit.");
    }

    private static List<Peptide> enumerate1() {
        List<Peptide> resultList =
            new ArrayList<Peptide>(Residue.countNative());

        for (Residue residue : Residue.listNative())
            resultList.add(Peptide.of(residue));

        return resultList;
    }

    private static List<Peptide> addGeneration(List<Peptide> parentList) {
        List<Peptide> resultList =
            new ArrayList<Peptide>(Residue.countNative() * parentList.size());

        for (Peptide parent : parentList)
            for (Residue residue : Residue.listNative())
                resultList.add(parent.append(residue));

        return resultList;
    }

    /**
     * Enumerates all native peptides of a given length having a
     * distinct unordered representation (residue count).
     *
     * @param length the desired peptide length.
     *
     * @return a list containing all native peptides (composed of
     * native residues) with the specified length having a distinct
     * unordered representation (residue count).
     *
     * @throws IllegalArgumentException unless the length is positive
     * but not greater than the enumeration limit.
     */
    public static List<Peptide> enumerateUnordered(int length) {
        List<Peptide> allPeptides = enumerate(length);
        ArrayList<Peptide> unorderedPeptides = new ArrayList<Peptide>();
        Set<Multiset<Residue>> unorderedKeys = new HashSet<Multiset<Residue>>();

        for (Peptide peptide : allPeptides) {
            Multiset<Residue> unorderedKey = unordered(peptide);

            if (!unorderedKeys.contains(unorderedKey)) {
                unorderedKeys.add(unorderedKey);
                unorderedPeptides.add(peptide);
            }
        }

        unorderedPeptides.trimToSize();
        return unorderedPeptides;
    }

    /**
     * Returns the number of unique native peptides with a fixed
     * length.
     *
     * @param length the desired peptide length.
     *
     * @return the number of unique native peptides with the specified
     * length.
     *
     * @throws IllegalArgumentException unless the length is positive
     * but not greater than the enumeration limit.
     */
    public static int enumerationSize(int length) {
        if (length < 1)
            throw new IllegalArgumentException("Length must be positive.");
        else if (length > ENUMERATION_LIMIT)
            throw new IllegalArgumentException("Length must not exceed the enumeration limit.");

        return (int) Math.pow(Residue.countNative(), length);
    }

    /**
     * Returns an unordered view of a peptide: a multiset counting the
     * number of times each residue occurs.
     *
     * @param peptide a peptide to examine.
     *
     * @return an unordered view of the peptide.
     */
    public static Multiset<Residue> unordered(Peptide peptide) {
        Multiset<Residue> counts = EnumMultiset.create(Residue.class);

        for (Residue residue : peptide.viewResidues())
            counts.add(residue);

        return counts;
    }
}
