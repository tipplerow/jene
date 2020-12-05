
package jene.peptide;

import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import jam.lang.ObjectFactory;
import jam.util.ListUtil;

/**
 * Represents an immutable set of peptides.
 */
public final class Peptidome extends AbstractSet<Peptide> {
    private final Set<Peptide> peptides;

    private Peptidome(Set<Peptide> peptides) {
        this.peptides = Collections.unmodifiableSet(peptides);
    }

    /**
     * The single peptidome containing no peptides.
     */
    public static final Peptidome EMPTY = new Peptidome(Collections.emptySet());

    /**
     * Creates a new peptidome with a fixed set of peptides.
     *
     * @param peptides the members of the peptidome.
     *
     * @return the new peptidome.
     */
    public static Peptidome create(Collection<? extends Peptide> peptides) {
        return new Peptidome(new LinkedHashSet<Peptide>(peptides));
    }

    /**
     * Creates a new peptidome with a specified size.
     *
     * @param factory the creator of individual peptides.
     *
     * @param size the number of peptides to create.
     *
     * @return the new peptidome.
     */
    public static Peptidome create(ObjectFactory<? extends Peptide> factory, int size) {
        Set<Peptide> peptides = new LinkedHashSet<Peptide>(size);

        while (peptides.size() < size)
            peptides.add(factory.newInstance());

        return new Peptidome(peptides);
    }

    /**
     * Reads peptides from a flat file containing one peptide per line
     * (and no header line).
     *
     * @param fileName the name of the flat file to read.
     *
     * @return a peptidome containing all peptides listed in the
     * specified file.
     *
     * @throws RuntimeException if any I/O errors occur.
     */
    public static Peptidome load(String fileName) {
        return Peptidome.create(Peptide.load(fileName));
    }

    /**
     * Creates a peptidome composed of peptides with native residues
     * chosen randomly with equal probability.
     *
     * @param length the desired number of residues.
     *
     * @param size the desired number of peptides.
     *
     * @return a peptidome containing {@code size} new peptides, each
     * with exactly {@code length} randomly chosen native residues.
     */
    public static Peptidome random(int length, int size) {
        Set<Peptide> peptides = new HashSet<Peptide>(size);

        while (peptides.size() < size)
            peptides.add(Peptide.newNative(length));

        return new Peptidome(peptides);
    }

    /**
     * Creates the union of several peptidomes.
     *
     * @param peptidomes the peptidomes to join.
     *
     * @return a new peptidome containing all peptides contained in
     * any of the specified peptidomes.
     */
    public static Peptidome union(Peptidome... peptidomes) {
        Set<Peptide> peptides = new LinkedHashSet<Peptide>();

        for (Peptidome peptidome : peptidomes)
            peptides.addAll(peptidome);

        return new Peptidome(peptides);
    }

    /**
     * Randomly mutates the peptides in this peptidome and returns the
     * mutated peptides in a new peptidome; this peptidome is unchanged.
     *
     * <p>In one iteration, a parent peptide is selected at random and
     * then mutated; the process repeats {@code mutationCount} times.
     * Some parents may be selected more than once, some not at all.
     *
     * @param mutationCount the desired number of mutated peptides to
     * generate.
     *
     * @return a new peptidome containing the mutated peptides.
     */
    public Peptidome mutate(int mutationCount) {
        //
        // Dump the parents into an ArrayList for efficient random
        // selection...
        //
        List<Peptide> parents = new ArrayList<Peptide>(peptides);
        Set<Peptide>  mutants = new HashSet<Peptide>(mutationCount);

        int maxIter = 10 * mutationCount;

        for (int iter = 0; iter < maxIter; ++ iter) {
            mutants.add(ListUtil.select(parents).mutate());

            if (mutants.size() == mutationCount)
                return new Peptidome(mutants);
        }

        throw new IllegalStateException("Exceeded maximum iteration count.");
    }

    @Override public boolean contains(Object obj) {
        return peptides.contains(obj);
    }

    @Override public Iterator<Peptide> iterator() {
        return peptides.iterator();
    }

    @Override public int size() {
        return peptides.size();
    }
}
