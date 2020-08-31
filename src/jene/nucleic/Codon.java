
package jene.nucleic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.EnumMultiset;
import com.google.common.collect.Multiset;

import jam.math.JamRandom;
import jam.util.MultisetUtil;

import jene.peptide.Residue;

/**
 * Nucleic acid triplets encoding amino acids.
 */
public enum Codon {
    GCA(Residue.Ala),
    GCC(Residue.Ala),
    GCG(Residue.Ala),
    GCU(Residue.Ala),

    AGA(Residue.Arg),
    AGG(Residue.Arg),
    CGA(Residue.Arg),
    CGC(Residue.Arg),
    CGG(Residue.Arg),
    CGU(Residue.Arg),

    GAC(Residue.Asp),
    GAU(Residue.Asp),

    AAC(Residue.Asn),
    AAU(Residue.Asn),

    UGC(Residue.Cys),
    UGU(Residue.Cys),

    GAA(Residue.Glu),
    GAG(Residue.Glu),

    CAA(Residue.Gln),
    CAG(Residue.Gln),

    GGA(Residue.Gly),
    GGC(Residue.Gly),
    GGG(Residue.Gly),
    GGU(Residue.Gly),

    CAC(Residue.His),
    CAU(Residue.His),

    AUA(Residue.Ile),
    AUC(Residue.Ile),
    AUU(Residue.Ile),

    UUA(Residue.Leu),
    UUG(Residue.Leu),
    CUA(Residue.Leu),
    CUC(Residue.Leu),
    CUG(Residue.Leu),
    CUU(Residue.Leu),

    AAA(Residue.Lys),
    AAG(Residue.Lys),

    AUG(Residue.Met),

    UUC(Residue.Phe),
    UUU(Residue.Phe),

    CCA(Residue.Pro),
    CCC(Residue.Pro),
    CCG(Residue.Pro),
    CCU(Residue.Pro),

    AGC(Residue.Ser),
    AGU(Residue.Ser),
    UCA(Residue.Ser),
    UCC(Residue.Ser),
    UCG(Residue.Ser),
    UCU(Residue.Ser),

    ACA(Residue.Thr),
    ACC(Residue.Thr),
    ACG(Residue.Thr),
    ACU(Residue.Thr),

    UGG(Residue.Trp),

    UAC(Residue.Tyr),
    UAU(Residue.Tyr),

    GUA(Residue.Val),
    GUC(Residue.Val),
    GUG(Residue.Val),
    GUU(Residue.Val),

    UAA(Residue.STOP),
    UAG(Residue.STOP),
    UGA(Residue.STOP);

    private final Residue residue;

    private static final List<Codon> nativeList = new ArrayList<Codon>();

    private static final Map<Residue, Set<Codon>> reverseMap =
        new EnumMap<Residue, Set<Codon>>(Residue.class);

    static {
        populateNativeList();
        populateReverseMap();
    }

    private static void populateNativeList() {
        for (Codon codon : values())
            if (codon.translate().isNative())
                nativeList.add(codon);
    }

    private static void populateReverseMap() {
        if (values().length != 64)
            throw new IllegalStateException("Invalid codon count.");

        for (Residue residue : Residue.values())
            reverseMap.put(residue, EnumSet.noneOf(Codon.class));

        for (Codon codon : Codon.values())
            reverseMap.get(codon.translate()).add(codon);
    }

    private Codon(Residue residue) {
        this.residue = residue;
        validateNucleotides();
    }

    private void validateNucleotides() {
        Nucleotide[] nucleotides = getNucleotides();

        if (nucleotides.length != LENGTH)
            throw new IllegalStateException("Invalid codon.");

        for (Nucleotide nucleotide : nucleotides)
            if (!nucleotide.inRNA())
                throw new IllegalStateException("Invalid nucleotide.");
    }

    /**
     * Number of nucleotides per codon.
     */
    public static final int LENGTH = 3;

    /**
     * Finds all codons coding for a specific residue.
     *
     * @param residue the residue to search for.
     *
     * @return an unmodifiable set containing the codons that code for
     * the specified residue.
     */
    public static Set<Codon> codesFor(Residue residue) {
        return Collections.unmodifiableSet(reverseMap.get(residue));
    }

    /**
     * Counts the number of codons that code for (translate to) each
     * residue.
     *
     * @param codons the codons to examine.
     *
     * @return a multiset containing the number of codons that code
     * for each residue.
     */
    public static Multiset<Residue> countTranslators(List<Codon> codons) {
        Multiset<Residue> counter = EnumMultiset.create(Residue.class);

        for (Codon codon : codons)
            counter.add(codon.translate());

        return counter;
    }

    /**
     * Computes the fraction of codons that code for each residue.
     *
     * @return a mapping from residues to the fraction of codons that
     * code for them.
     */
    public static EnumMap<Residue, Double> computeFrequency() {
        return computeFrequency(Arrays.asList(Codon.values()));
    }

    /**
     * Computes the fraction of native codons that code for each
     * native residue.
     *
     * @return a mapping from native residues to the fraction of
     * native codons that code for them.
     */
    public static EnumMap<Residue, Double> computeNativeFrequency() {
        return computeFrequency(listNative());
    }

    private static EnumMap<Residue, Double> computeFrequency(List<Codon> codons) {
        Multiset<Residue> counts = countTranslators(codons);
        EnumMap<Residue, Double> frequencies = new EnumMap<Residue, Double>(Residue.class);

        for (Residue key : counts.elementSet())
            frequencies.put(key, MultisetUtil.frequency(counts, key));

        return frequencies;
    }

    /**
     * Finds the codon composed of three specific nucleotides.
     *
     * @param n1 the first nucleotide.
     * @param n2 the second nucleotide.
     * @param n3 the third nucleotide.
     *
     * @return the codon composed of the specified nucleotides.
     *
     * @throws IllegalArgumentException if the nucleotides do not map
     * to a codon (e.g., if a thymine is given instead of a uracil).
     */
    public static Codon instance(Nucleotide n1, Nucleotide n2, Nucleotide n3) {
        return valueOf(nameOf(n1, n2, n3));
    }

    /**
     * Composes the name of a codon from its nucleotides.
     *
     * @param n1 the first nucleotide.
     * @param n2 the second nucleotide.
     * @param n3 the third nucleotide.
     *
     * @return the name of the codon composed of the specified nuclotides.
     */
    public static String nameOf(Nucleotide n1, Nucleotide n2, Nucleotide n3) {
        return n1.name() + n2.name() + n3.name();
    }

    /**
     * Lists the codons coding for native residues.
     *
     * @return an unmodifiable list containing the native codons.
     */
    public static List<Codon> listNative() {
        return Collections.unmodifiableList(nativeList);
    }

    /**
     * Selects a native codon at random, with all native codons
     * equally likely.
     *
     * @param random the random number source.
     *
     * @return a randomly selected native codon.
     */
    public static Codon selectNative(JamRandom random) {
        return nativeList.get(random.nextInt(nativeList.size()));
    }

    /**
     * Returns the nucleotide at a specified position in this codon.
     *
     * @param position the zero-based position of interest.
     *
     * @return the nucleotide at the specified position.
     *
     * @throws IllegalArgumentException unless the position is in the
     * closed range {@code [0, 2]}.
     */
    public Nucleotide getNucleotide(int position) {
        if (position < 0 || position > 2)
            throw new IllegalArgumentException("Invalid nucleotide position.");

        return Nucleotide.valueOf(name().charAt(position));
    }

    /**
     * Returns the three nucleotides in this codon.
     *
     * @return the three nucleotides in this codon.
     */
    public Nucleotide[] getNucleotides() {
        Nucleotide[] nucleotides = new Nucleotide[LENGTH];

        for (int position = 0; position < LENGTH; position++)
            nucleotides[position] = getNucleotide(position);

        return nucleotides;
    }

    /**
     * Translates this codon into an amino acid (or stop marker).
     *
     * @return the amino acid coded by this codon.
     */
    public Residue translate() {
        return residue;
    }
}
