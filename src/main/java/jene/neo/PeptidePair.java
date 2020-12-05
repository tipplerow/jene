
package jene.neo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jene.peptide.Peptide;

/**
 * Associates a neo-peptide with the self-peptide from which it
 * originated.
 */
public final class PeptidePair {
    private final NeoPeptide neo;
    private final SelfPeptide self;

    private PeptidePair(SelfPeptide self, NeoPeptide neo) {
        this.neo = neo;
        this.self = self;
    }

    /**
     * Returns a peptide pair with fixed components.
     *
     * @param self the self-peptide derived from the germline genome.
     *
     * @param neo the neo-peptide derived from the self-peptide by
     * somatic mutation.
     *
     * @return the peptide pair with the specified components.
     */
    public static PeptidePair instance(SelfPeptide self, NeoPeptide neo) {
        return new PeptidePair(self, neo);
    }

    /**
     * Extracts the neo-peptides from a collection of pairs.
     *
     * @param pairs the peptide pairs to process.
     *
     * @return the neo-peptides contained in the specified pairs (in
     * the order returned by the collection iterator).
     */
    public static List<Peptide> neo(Collection<PeptidePair> pairs) {
        List<Peptide> peptides =
            new ArrayList<Peptide>(pairs.size());

        for (PeptidePair pair : pairs)
            peptides.add(pair.neo());

        return peptides;
    }

    /**
     * Extracts the self-peptides from a collection of pairs.
     *
     * @param pairs the peptide pairs to process.
     *
     * @return the self-peptides contained in the specified pairs (in
     * the order returned by the collection iterator).
     */
    public static List<Peptide> self(Collection<PeptidePair> pairs) {
        List<Peptide> peptides =
            new ArrayList<Peptide>(pairs.size());

        for (PeptidePair pair : pairs)
            peptides.add(pair.self());

        return peptides;
    }

    /**
     * Extracts the peptides from a collection of pairs.
     *
     * @param pairs the peptide pairs to process.
     *
     * @return the neo-peptides and self-peptides contained in the
     * specified pairs (in no particular order).
     */
    public static Collection<Peptide> peptides(Collection<PeptidePair> pairs) {
        Collection<Peptide> peptides =
            new ArrayList<Peptide>(2 * pairs.size());

        for (PeptidePair pair : pairs) {
            peptides.add(pair.self());
            peptides.add(pair.neo());
        }

        return peptides;
    }

    /**
     * Returns the neo-peptide derived from the self-peptide by
     * somatic mutation.
     *
     * @return the neo-peptide derived from the self-peptide by
     * somatic mutation.
     */
    public Peptide neo() {
        return neo.getPeptide();
    }

    /**
     * Returns the self-peptide derived from the germline genome.
     *
     * @return the self-peptide derived from the germline genome.
     */
    public Peptide self() {
        return self.getPeptide();
    }

    @Override public String toString() {
        return String.format("PeptidePair(%s => %s)", self().formatString(), neo().formatString());
    }
}
