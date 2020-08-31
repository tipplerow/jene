
package jene.hla;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableSortedMultiset;
import com.google.common.collect.Multiset;

import org.apache.commons.math3.util.Combinations;

import jam.io.ObjectParser;
import jam.io.ObjectReader;
import jam.lang.JamException;
import jam.util.AbstractImmutableMultiset;
import jam.util.CollectionUtil;

/**
 * Represents the complete HLA genotype for a single MHC class.
 */
public final class Genotype extends AbstractImmutableMultiset<Allele> implements Comparable<Genotype> {
    private final Map<Locus, List<Allele>> locusMap =
        new EnumMap<Locus, List<Allele>>(Locus.class);

    private Genotype(ImmutableSortedMultiset<Allele> alleles) {
        super(alleles);
        fillLocusMap();
    }

    private void fillLocusMap() {
        for (Locus locus : Locus.values())
            locusMap.put(locus, new ArrayList<Allele>(2));

        for (Allele allele : this)
            mapAllele(allele);
    }

    private void mapAllele(Allele allele) {
        List<Allele> alleleList = locusMap.get(allele.getLocus());

        if (!alleleList.contains(allele))
            alleleList.add(allele);
    }

    /**
     * Creates a new fixed genotype.
     *
     * @param alleles the alleles that compose the genotype.
     *
     * @return the new genotype.
     */
    public static Genotype instance(Allele... alleles) {
        return new Genotype(ImmutableSortedMultiset.copyOf(alleles));
    }

    /**
     * Creates a new fixed genotype.
     *
     * @param alleles the alleles that compose the genotype.
     *
     * @return the new genotype.
     */
    public static Genotype instance(Collection<Allele> alleles) {
        return new Genotype(ImmutableSortedMultiset.copyOf(alleles));
    }

    /**
     * Reads genotypes from a flat file containing one genotype per
     * line (and no header line).
     *
     * @param fileName the name of the flat file to read.
     *
     * @param delim the delimiter separating the individual alleles.
     *
     * @return a list containing the genotypes in the flat file.
     *
     * @throws RuntimeException if any I/O errors occur.
     */
    public static List<Genotype> loadFlatFile(String fileName, Pattern delim) {
        return ObjectReader.load(fileName, new GenotypeParser(delim));
    }

    private static final class GenotypeParser implements ObjectParser<Genotype> {
        private final Pattern delim;

        private GenotypeParser(Pattern delim) {
            this.delim = delim;
        }

        @Override public Genotype parse(String s) {
            return Genotype.parse(s, delim);
        }
    }

    /**
     * Constructs a new fixed genotype by parsing a string
     * representation.
     *
     * @param s the string representation.
     *
     * @param delim the delimiter separating the individual alleles.
     *
     * @return the fixed genotype described by the input string.
     */
    public static Genotype parse(String s, Pattern delim) {
        return instance(Allele.parse(s, delim));
    }

    /**
     * Returns the first unique allele at the A locus.
     *
     * @return the first unique allele at the A locus.
     */
    public Allele A1() {
        return getLocus(Locus.A, 0);
    }

    /**
     * Returns the second unique allele at the A locus.
     *
     * @return the second unique allele at the A locus ({@code null}
     * if this genotype is homozygous at the A locus).
     */
    public Allele A2() {
        return getLocus(Locus.A, 1);
    }

    /**
     * Returns the first unique allele at the B locus.
     *
     * @return the first unique allele at the B locus.
     */
    public Allele B1() {
        return getLocus(Locus.B, 0);
    }

    /**
     * Returns the second unique allele at the B locus.
     *
     * @return the second unique allele at the B locus ({@code null}
     * if this genotype is homozygous at the B locus).
     */
    public Allele B2() {
        return getLocus(Locus.B, 1);
    }

    /**
     * Returns the first unique allele at the C locus.
     *
     * @return the first unique allele at the C locus.
     */
    public Allele C1() {
        return getLocus(Locus.C, 0);
    }

    /**
     * Returns the second unique allele at the C locus.
     *
     * @return the second unique allele at the C locus ({@code null}
     * if this genotype is homozygous at the C locus).
     */
    public Allele C2() {
        return getLocus(Locus.C, 1);
    }

    private Allele getLocus(Locus locus, int index) {
        List<Allele> alleleList = locusMap.get(locus);

        if (index < alleleList.size())
            return alleleList.get(index);
        else
            return null;
    }

    /**
     * Returns the number of unique alleles in this genotype.
     *
     * @return the number of unique alleles in this genotype.
     */
    public int countUniqueAlleles() {
        return elementSet().size();
    }

    /**
     * Returns the number of unique alleles at a given HLA locus.
     *
     * @param locus the HLA locus of interest.
     *
     * @return the number of unique alleles at the specified locus.
     */
    public int countUniqueAlleles(Locus locus) {
        return locusMap.get(locus).size();
    }

    /**
     * Removes one or more alleles from this genotype to simulate a
     * loss-of-heterozygosity event.
     *
     * <p>If this genotype contains more than one copy of an allele in
     * the argument list, all copies of that allele will be removed.
     *
     * @param alleles the alleles to remove.
     *
     * @return a new genotype with the specified alleles removed.
     *
     * @throws RuntimeException unless this genotype contains every
     * allele in the argument list.
     */
    public Genotype delete(Allele... alleles) {
        return delete(List.of(alleles));
    }

    /**
     * Removes one or more alleles from this genotype to simulate a
     * loss-of-heterozygosity event.
     *
     * <p>If this genotype contains more than one copy of an allele in
     * the argument collection, all copies of that allele will be removed.
     *
     * @param alleles the alleles to remove.
     *
     * @return a new genotype with the specified alleles removed.
     *
     * @throws RuntimeException unless this genotype contains every
     * allele in the argument collection.
     */
    public Genotype delete(Collection<Allele> alleles) {
        Multiset<Allele> keep = HashMultiset.create(this);

        for (Allele allele : alleles)
            if (keep.contains(allele))
                keep.setCount(allele, 0);
            else
                throw JamException.runtime("Genotype does not contain allele [%s].", allele);

        return instance(keep);
    }

    /**
     * Enumerates all genotypes that may be formed from loss of
     * heterozygosity.
     *
     * @param count the number of unique alleles lost.
     *
     * @return a set containing all unique genotypes that may be
     * formed by losing the specified number of alleles.
     */
    public Set<Genotype> enumerateLOH(int count) {
        if (count < 1)
            throw new IllegalArgumentException("LOH count must be positive.");

        List<Allele> uniqueAlleles = new ArrayList<Allele>(viewUniqueAlleles());

        if (count > uniqueAlleles.size())
            throw new IllegalArgumentException("LOU count must not exceed the number of unique alleles.");

        Set<Genotype> lohGenotypes = new TreeSet<Genotype>();
        Combinations  combinations = new Combinations(uniqueAlleles.size(), count);

        for (int[] indexes : combinations) {
            Set<Allele> lohAlleles = copyUniqueAlleles();

            for (int index : indexes)
                lohAlleles.remove(uniqueAlleles.get(index));

            lohGenotypes.add(instance(lohAlleles));
        }

        return lohGenotypes;
    }

    /**
     * Identifies heterozygous genotypes.
     *
     * @return {@code true} iff this genotype contains exactly one
     * copy of each allele.
     */
    public boolean isHeterozygous() {
        return !isHomozygous();
    }

    /**
     * Identifies homozygous genotypes.
     *
     * @return {@code true} iff this genotype contains two or more
     * copies of any allele.
     */
    public boolean isHomozygous() {
        for (Multiset.Entry<Allele> entry : entrySet())
            if (entry.getCount() > 1)
                return true;

        return false;
    }

    /**
     * Identifies homozygosity at a given HLA locus.
     *
     * @param locus the locus of interest.
     *
     * @return {@code true} iff this genotype contains fewer than two
     * unique alleles at the specified locus.
     */
    public boolean isHomozygous(Locus locus) {
        return locusMap.get(locus).size() < 2;
    }

    /**
     * Returns the unique alleles in this genotype in a new set.
     *
     * @return the unique alleles in this genotype in a new set.
     */
    public Set<Allele> copyUniqueAlleles() {
        return new TreeSet<Allele>(elementSet());
    }

    /**
     * Returns a read-only view of the <em>unique</em> alleles for a
     * given locus.
     *
     * @param locus the HLA locus of interest.
     *
     * @return a read-only view of the unique alleles for the
     * specified locus.
     */
    public List<Allele> viewUniqueAlleles(Locus locus) {
        return Collections.unmodifiableList(locusMap.get(locus));
    }

    /**
     * Returns a read-only view of the unique alleles in this genotype.
     *
     * @return a read-only view of the unique alleles in this genotype.
     */
    public Set<Allele> viewUniqueAlleles() {
        return elementSet();
    }

    @Override public int compareTo(Genotype that) {
        return CollectionUtil.compareIterationOrder(this, that);
    }
}
