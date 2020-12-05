
package jene.peptide;

import java.util.Arrays;
import java.util.Collection;

import com.google.common.collect.Multiset;
import com.google.common.collect.TreeMultiset;

/**
 * Generates and manipulates isomers of peptides.
 */
public final class PeptideIsomer {
    /**
     * Generates a unique key to identify peptide isomers.
     *
     * @param peptide the peptide to classify.
     *
     * @return the isomer key for this peptide.
     */
    public static String isomerKey(Peptide peptide) {
        char[] codes = new char[peptide.length()];

        for (int k = 0; k < codes.length; ++k)
            codes[k] = peptide.get(k).code1();

        Arrays.sort(codes);
        return String.valueOf(codes);
    }

    /**
     * Enumerates all distinct isomers for peptides with a fixed
     * length and counts the number of occurrences of each isomer.
     *
     * @param length the desired peptide length.
     *
     * @return a multiset counting the number of occurrences of each
     * distinct isomer for peptides with the specified length.
     *
     * @throws IllegalArgumentException unless the length is positive
     * but not greater than the enumeration limit.
     */
    public static Multiset<String> mapIsomers(int length) {
        return mapIsomers(PeptideEnumerator.enumerate(length));
    }

    /**
     * Enumerates all distinct isomers for a collection of peptides.
     *
     * @param peptides a collection of peptides to examine.
     *
     * @return a multiset counting the number of occurrences of each
     * distinct isomer in the peptide collection.
     */
    public static Multiset<String> mapIsomers(Collection<Peptide> peptides) {
        Multiset<String> counts = TreeMultiset.create();

        for (Peptide peptide : peptides)
            counts.add(isomerKey(peptide));

        return counts;
    }
}
