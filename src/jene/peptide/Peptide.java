
package jene.peptide;

import java.io.File;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jam.app.JamLogger;
import jam.io.LineReader;
import jam.math.IntRange;
import jam.math.JamRandom;
import jam.report.LineBuilder;
import jam.util.ListUtil;

/**
 * Defines a fixed linear sequence of amino acids.
 */
public final class Peptide extends AbstractList<Residue> {
    private final int hashCode;
    private final List<Residue> residues;

    private static final Map<String, Peptide> instances = new HashMap<String, Peptide>();

    private Peptide(List<Residue> residues, boolean copy) {
        if (copy)
            this.residues = new ArrayList<Residue>(residues);
        else
            this.residues = residues;

        this.hashCode = residues.hashCode();
    }

    /**
     * Formats each peptide in a collection.
     *
     * @param peptides the peptides to format.
     *
     * @return the formatted peptide strings in the order returned by
     * the collection iterator.
     */
    public static List<String> formatString(Collection<Peptide> peptides) {
        List<String> strings = new ArrayList<String>(peptides.size());

        for (Peptide peptide : peptides)
            strings.add(peptide.formatString());

        return strings;
    }

    /**
     * Returns a peptide having the amino acid sequence encoded as a
     * sequence of single-character residue codes in a string.
     *
     * @param s a sequence of single-character residue codes.
     *
     * @return a peptide containing the residues specified in the
     * input string.
     *
     * @throws IllegalArgumentException unless the input string is a
     * valid peptide representation.
     */
    public static Peptide instance(String s) {
        Peptide peptide = instances.get(s);

        if (peptide == null) {
            peptide = parse(s);
            instances.put(s, peptide);
        }

        return peptide;
    }

    private static Peptide parse(String s) {
        List<Residue> residues = new ArrayList<Residue>(s.length());

        for (int k = 0; k < s.length(); ++k)
            residues.add(Residue.valueOfCode1(s.charAt(k)));

        return new Peptide(residues, false);
    }

    /**
     * Reads peptides from a flat file (no header, one peptide per line).
     *
     * @param fileName the name of the file to load.
     *
     * @return a list containing the peptides from the specified file.
     */
    public static List<Peptide> load(String fileName) {
        return load(new File(fileName));
    }

    /**
     * Reads peptides from a flat file (no header, one peptide per line).
     *
     * @param file the file to load.
     *
     * @return a list containing the peptides from the specified file.
     */
    public static List<Peptide> load(File file) {
        List<Peptide> peptides = new ArrayList<Peptide>();

        try (LineReader reader = LineReader.open(file)) {
            for (String line : reader)
                peptides.add(instance(line));
        }

        JamLogger.info("Loaded [%d] peptides...", peptides.size());
        return peptides;
    }

    /**
     * Generates randomly mutations in a collection of peptides.
     *
     * <p>In one iteration, a parent peptide is selected at random and
     * then mutated; the process repeats {@code mutationCount} times.
     * Some parents may be selected more than once, some not at all.
     *
     * @param parents the parent peptides to choose from.
     *
     * @param mutationCount the desired number of mutated peptides to
     * generate.
     *
     * @return a list of mutated peptides.
     */
    public static List<Peptide> mutate(Collection<Peptide> parents, int mutationCount) {
        //
        // Dump the parents into an ArrayList for efficient random
        // selection...
        //
        List<Peptide> parentList = new ArrayList<Peptide>(parents);
        List<Peptide> mutantList = new ArrayList<Peptide>(mutationCount);

        while (mutantList.size() < mutationCount)
            mutantList.add(ListUtil.select(parentList).mutate());

        return mutantList;
    }

    /**
     * Creates a new peptide with native residues chosen randomly with
     * equal probability.
     *
     * @param length the desired number of residues.
     *
     * @return a new peptide with exactly {@code length} native
     * residues chosen at random with equal probability.
     */
    public static Peptide newNative(int length) {
        List<Residue> nativeResidues = new ArrayList<Residue>(length);

        while (nativeResidues.size() < length)
            nativeResidues.add(Residue.selectNative(JamRandom.global()));

        return new Peptide(nativeResidues, false);
    }

    /**
     * Creates new peptides with native residues chosen randomly with
     * equal probability.
     *
     * @param length the desired number of residues.
     *
     * @param count the desired number of peptides.
     *
     * @return a list of {@code count} new peptides, each with exactly
     * {@code length} randomly chosen native residues.
     */
    public static List<Peptide> newNative(int length, int count) {
        List<Peptide> peptides = new ArrayList<Peptide>(count);

        while (peptides.size() < count)
            peptides.add(newNative(length));

        return peptides;
    }

    /**
     * Creates a new peptide from a sequence of residues.
     *
     * @param residues the sequence of residues to compose the peptide.
     *
     * @return a new peptide with the specified sequence.
     */
    public static Peptide of(Residue... residues) {
        return new Peptide(List.of(residues), false);
    }

    /**
     * Creates a new peptide from a sequence of residues.
     *
     * @param residues the list of residues to compose the peptide.
     *
     * @return a new peptide with the specified sequence.
     */
    public static Peptide of(List<Residue> residues) {
        return new Peptide(residues, true);
    }

    /**
     * Appends a sequence of residues to this peptide and returns a
     * new peptide with the full sequence; this peptide is unchanged.
     *
     * @param addlResidues the residues to append.
     *
     * @return the new peptide with the additional residues.
     */
    public Peptide append(Residue... addlResidues) {
        return append(List.of(addlResidues));
    }

    /**
     * Appends a list of residues to this peptide and returns a new
     * peptide with the full sequence; this peptide is unchanged.
     *
     * @param addlResidues the residues to append.
     *
     * @return the new peptide with the additional residues.
     */
    public Peptide append(List<Residue> addlResidues) {
        List<Residue> newResidues = new ArrayList<Residue>(residues);
        newResidues.addAll(addlResidues);

        return new Peptide(newResidues, false);
    }

    /**
     * Appends another peptide to this peptide and returns a new
     * peptide with the full sequence; this peptide is unchanged.
     *
     * @param peptide the peptide to append.
     *
     * @return the new peptide with the additional residues.
     */
    public Peptide append(Peptide peptide) {
        return append(peptide.residues);
    }

    /**
     * Formats the residues in this peptide for output to a CSV file.
     *
     * @return the residues in this peptide formatted for output to a
     * CSV file.
     */
    public String formatCSV() {
        LineBuilder builder = LineBuilder.csv();

        for (Residue residue : viewResidues())
            builder.append(residue.code1());

        return builder.toString();
    }

    /**
     * Formats the residues in this peptide into a string of
     * single-character codes.
     *
     * @return the residues in this peptide as string of
     * single-character codes.
     */
    public String formatString() {
        StringBuilder builder = new StringBuilder();

        for (Residue residue : viewResidues())
            builder.append(residue.code1());

        return builder.toString();
    }

    /**
     * Returns a subsegment of this peptide.
     *
     * <p>The residue indexes in the fragment are {@code 0, ..., range.size() - 1}
     * and correspond to indexes {@code range.lower(), ..., range.upper()} on this
     * peptide.
     *
     * @param range the zero-offset index range of residues in the
     * fragment.
     *
     * @return a read-only view of a subsegment of this peptide.
     *
     * @throws RuntimeException unless the specified fragment falls
     * entirely within this peptide.
     */
    public Peptide fragment(IntRange range) {
        return new Peptide(residues.subList(range.lower(), range.upper() + 1), false);
    }

    /**
     * Identifies native peptides.
     *
     * @return {@code true} iff every residue in this peptide is a
     * native residue.
     */
    public boolean isNative() {
        for (Residue residue : this)
            if (!residue.isNative())
                return false;

        return true;
    }

    /**
     * Returns the number of residues in this peptide.
     *
     * @return the number of residues in this peptide.
     */
    public int length() {
        return residues.size();
    }

    /**
     * Randomly mutates this peptide and returns the mutation as a new
     * instance; this peptide is unchanged.
     *
     * @return a new peptide with one residue chosen at random changed
     * to another residue chosen at random with equal probability.
     */
    public Peptide mutate() {
        int index = JamRandom.global().nextInt(length());

        List<Residue> newResidues = new ArrayList<Residue>(residues);
        newResidues.set(index, residues.get(index).mutate(JamRandom.global()));

        return new Peptide(newResidues, false);
    }

    /**
     * Applies a single-residue mutation to this peptide.
     *
     * @param mutation the mutation to apply.
     *
     * @return a new peptide with the specified mutation applied.
     *
     * @throws IllegalArgumentException unless the residue at the
     * mutation location in this peptide matches the one specified
     * as the original residue and the mutation position lies within
     * this peptide.
     */
    public Peptide mutate(ProteinChange mutation) {
        return mutate(List.of(mutation));
    }

    /**
     * Applies single-residue mutations to this peptide.
     *
     * @param mutations the mutations to apply.
     *
     * @return a new peptide with the specified mutations applied.
     *
     * @throws IllegalArgumentException unless all residues at the
     * mutation locations in this peptide match those specified as
     * the original residues and all mutation positions lie within
     * this peptide.
     */
    public Peptide mutate(Collection<ProteinChange> mutations) {
        List<Residue> newResidues = new ArrayList<Residue>(residues);
        ProteinChange.apply(mutations, newResidues);

        return new Peptide(newResidues, false);
    }

    /**
     * Extract all native N-mers from this peptide.
     *
     * @param N the length of peptide fragments to extract.
     *
     * @return a list containing all native N-mers from this peptide.
     */
    public List<Peptide> nativeFragments(int N) {
        if (length() < N)
            return Collections.emptyList();

        List<Peptide> fragments = new ArrayList<Peptide>(length() - N + 1);

        for (int start = 0; start <= length() - N; ++start) {
            Peptide fragment = fragment(IntRange.instance(start, start + N - 1));

            if (fragment.isNative())
                fragments.add(fragment);
        }

        return fragments;
    }

    /**
     * Returns a read-only view of the residues in this peptide.
     *
     * @return a read-only view of the residues in this peptide.
     */
    public List<Residue> viewResidues() {
        return Collections.unmodifiableList(residues);
    }

    @Override public boolean equals(Object obj) {
        //
        // Since many instances are references to the same physical
        // object in the "instances" cache, try reference equality
        // first...
        //
        return (this == obj) || ((obj instanceof Peptide) && equalsPeptide((Peptide) obj));
    }

    private boolean equalsPeptide(Peptide that) {
        return this.residues.equals(that.residues);
    }

    @Override public Residue get(int index) {
        return residues.get(index);
    }

    @Override public int hashCode() {
        return hashCode;
    }

    @Override public int size() {
        return residues.size();
    }

    @Override public String toString() {
        return "Peptide(" + formatString() + ")";
    }
}
