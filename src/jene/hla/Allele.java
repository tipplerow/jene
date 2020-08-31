
package jene.hla;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import jam.app.JamLogger;
import jam.io.LineReader;

/**
 * Represents a single HLA allele.
 */
public final class Allele implements Comparable<Allele> {
    private final Locus locus;
    private final int   superType;
    private final int   subType;

    private final String longKey;
    private final String shortKey;
    private final int    hashCode;

    private Allele(Locus locus, int superType, int subType) {
        validateType(superType);
        validateType(subType);

        this.locus     = locus;
        this.superType = superType;
        this.subType   = subType;

        this.longKey  = formatLongKey(locus, superType, subType);
        this.shortKey = formatShortKey(locus, superType, subType);
        this.hashCode = computeHashCode(locus, superType, subType);
    }

    private static void validateType(int type) {
        if (type < 1 || type > 199)
            throw new IllegalArgumentException("Invalid allele type.");
    }

    private static String formatLongKey(Locus locus, int superType, int subType) {
        return String.format("%s%s*%02d:%02d", PREFIX, locus, superType, subType);
    }

    private static String formatShortKey(Locus locus, int superType, int subType) {
        return String.format("%s%02d%02d", locus, superType, subType);
    }

    private static int computeHashCode(Locus locus, int superType, int subType) {
        return 10000 * locus.ordinal() + 100 * superType + subType;
    }

    /**
     * Standard prefix for HLA allele names.
     */
    public static final String PREFIX = "HLA-";

    /**
     * Returns the allele with a specified locus, supertype, and
     * subtype.
     *
     * @param locus the desired locus.
     *
     * @param superType the desired supertype.
     *
     * @param subType the desired subtype.
     *
     * @return the allele with the specified locus, supertype, and
     * subtype.
     *
     * @throws RuntimeException unless the supertype and subtype are
     * valid.
     */
    public static Allele instance(Locus locus, int superType, int subType) {
        //
        // Think about a private flyweight cache...
        //
        return new Allele(locus, superType, subType);
    }

    /**
     * Returns the allele encoded in a string.
     *
     * @param s a string that encodes a unique allele.
     *
     * @return the allele encoded by the given string.
     *
     * @throws RuntimeException unless the input string encodes a
     * valid allele.
     */
    public static Allele instance(String s) {
        return Parser.parse(s);
    }

    /**
     * Reads alleles from a flat file (no header, one allele per line).
     *
     * @param fileName the name of the file to load.
     *
     * @return a list containing the alleles from the specified file.
     */
    public static List<Allele> load(String fileName) {
        return load(new File(fileName));
    }

    /**
     * Reads alleles from a flat file (no header, one allele per line).
     *
     * @param file the file to load.
     *
     * @return a list containing the alleles from the specified file.
     */
    public static List<Allele> load(File file) {
        List<Allele> alleles = new ArrayList<Allele>();

        try (LineReader reader = LineReader.open(file)) {
            for (String line : reader)
                alleles.add(instance(line));
        }

        JamLogger.info("Loaded [%d] alleles...", alleles.size());
        return alleles;
    }

    /**
     * Returns the alleles encoded in a delimited string.
     *
     * @param s a string that encodes alleles separated by a
     * delimiting pattern.
     *
     * @param delim the allele-separating delimiter.
     *
     * @return a list containing the encoded alleles.
     *
     * @throws RuntimeException unless the input string encodes one or
     * more valid alleles.
     */
    public static List<Allele> parse(String s, Pattern delim) {
        String[] fields = delim.split(s.trim());
        List<Allele> alleles = new ArrayList<Allele>(fields.length);

        for (String field : fields)
            alleles.add(instance(field));

        return alleles;
    }

    /**
     * Returns the locus of this allele.
     *
     * @return the locus of this allele.
     */
    public Locus getLocus() {
        return locus;
    }

    /**
     * Returns the supertype of this allele.
     *
     * @return the supertype of this allele.
     */
    public int getSuperType() {
        return superType;
    }

    /**
     * Returns the this allele.
     *
     * @return the this allele.
     */
    public int getSubType() {
        return subType;
    }

    /**
     * Returns a long key for this allele (of the form {@code HLA-A*02:01}).
     *
     * @return a long key for this allele.
     */
    public String longKey() {
        return longKey;
    }

    /**
     * Returns a short key for this allele (of the form {@code A0201}).
     *
     * @return a short key for this allele.
     */
    public String shortKey() {
        return shortKey;
    }

    @Override public int compareTo(Allele that) {
        return Integer.compare(this.hashCode, that.hashCode);
    }

    @Override public boolean equals(Object obj) {
        return (obj instanceof Allele) && equalsAllele((Allele) obj);
    }

    private boolean equalsAllele(Allele that) {
        return this.locus.equals(that.locus)
            && this.superType == that.superType
            && this.subType   == that.subType;
    }

    @Override public int hashCode() {
        return hashCode;
    }

    @Override public String toString() {
        return longKey;
    }
}
