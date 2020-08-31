
package jene.peptide;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jam.math.JamRandom;

/**
 * Enumerates the naturally-occuring amino acids and special markers
 * for deletions in mutated sequences and unknown or missing residues.
 */
public enum Residue {
    Asp('D', "Aspartic acid", 5.45, true, Family.ACIDIC),
    Glu('E', "Glutamic acid", 6.75, true, Family.ACIDIC),
    Arg('R', "Arginine",      5.53, true, Family.BASIC),
    Lys('K', "Lysine",        5.84, true, Family.BASIC),
    His('H', "Histidine",     2.27, true, Family.BASIC),
    Asn('N', "Asparagine",    4.06, true, Family.UNCHARGED_POLAR),
    Gln('Q', "Glutamine",     3.93, true, Family.UNCHARGED_POLAR),
    Ser('S', "Serine",        6.56, true, Family.UNCHARGED_POLAR),
    Thr('T', "Threonine",     5.34, true, Family.UNCHARGED_POLAR),
    Tyr('Y', "Tyrosine",      2.92, true, Family.UNCHARGED_POLAR),
    Ala('A', "Alanine",       8.25, true, Family.NONPOLAR),
    Gly('G', "Glycine",       7.07, true, Family.NONPOLAR),
    Val('V', "Valine",        6.87, true, Family.NONPOLAR),
    Leu('L', "Leucine",       9.66, true, Family.NONPOLAR),
    Ile('I', "Isoleucine",    5.96, true, Family.NONPOLAR),
    Pro('P', "Proline",       4.70, true, Family.NONPOLAR),
    Phe('F', "Phenylalanine", 3.86, true, Family.NONPOLAR),
    Met('M', "Methionine",    2.42, true, Family.NONPOLAR),
    Trp('W', "Tryptophan",    1.08, true, Family.NONPOLAR),
    Cys('C', "Cysteine",      1.37, true, Family.NONPOLAR),
    UNK('X', "UNKNOWN",       0.0,  false, null),
    DEL('-', "DELETION",      0.0,  false, null),
    STOP('.', "STOP",         0.0,  false, null);

    /**
     * Additional character symbols that may be used (e.g., in FASTA
     * files) to represent unknown amino acids.
     */
    public static char[] UNKNOWN_ALIAS = new char[] { '*', 'U' };

    /**
     * Side-chain polarity flag.
     */
    public enum Polarity { POLAR, NONPOLAR };

    /**
     * Side-chain family groups.
     */
    public enum Family { 
        ACIDIC(Polarity.POLAR), 
        BASIC(Polarity.POLAR), 
        UNCHARGED_POLAR(Polarity.POLAR), 
        NONPOLAR(Polarity.NONPOLAR);

        private final Polarity polarity;
	private Set<Residue> members = null; // Created on demand...

        private Family(Polarity polarity) {
            this.polarity = polarity;
        }

        /**
         * Returns the polarity of this family.
         *
         * @return the polarity of this family.
         */
        public Polarity polarity() {
            return polarity;
        }

        /**
         * Identifies polar families.
         *
         * @return {@code true} iff this family is polar.
         */
        public boolean isPolar() {
            return polarity.equals(Polarity.POLAR);
        }

        /**
         * Identifies nonpolar families.
         *
         * @return {@code true} iff this family is nonpolar.
         */
        public boolean isNonPolar() {
            return !isPolar();
        }

	/**
	 * Identifies residues that are members of this family.
	 *
	 * @return an unmodifiable set containing all members of this
	 * family.
	 */
	public Set<Residue> getMembers() {
	    if (members == null)
		members = findMembers();

	    return members;
	}

	private Set<Residue> findMembers() {
	    Set<Residue> result = EnumSet.noneOf(Residue.class);

	    for (Residue residue : Residue.values())
		if (this.equals(residue.family()))
		    result.add(residue);

	    return Collections.unmodifiableSet(result);
	}

	/**
	 * Returns the matrix of weights required to aggregate (coarse
	 * grain) residue-residue interactions into family-family
	 * interactions.  Residues are weighted equally within their
	 * family.
	 *
	 * @return a matrix {@code J} such that {@code J[R, F]} is the
	 * weight of residue {@code R} in family {@code F}.
	 */
        /*
	public static EnumDoubleMatrix<Residue, Family> getBucketWeights() {
	    EnumDoubleMatrix<Residue, Family> weights = 
		new EnumDoubleMatrix<Residue, Family>(Residue.class, Family.class);

	    for (Family family : Family.values()) {
		Set<Residue> members = family.getMembers();

		for (Residue residue : members)
		    weights.set(residue, family, 1.0 / ((double) members.size()));
	    }

	    return weights;
	}
        */
    };

    private final char    code1;
    private final Family  family;
    private final String  fullName;
    private final double  protFreq;
    private final boolean isNative;

    private static final Map<Character, Residue> map1 = new HashMap<Character, Residue>();
    private static final List<Residue> nativeList = new ArrayList<Residue>();

    static {
        populateMap1();
        populateNative();
    }

    private Residue(char code1, String fullName, double protFreq, boolean isNative, Family family) {
        this.code1    = code1;
        this.family   = family;
        this.fullName = fullName;
        this.protFreq = protFreq;
        this.isNative = isNative;
    }

    private static void populateMap1() {
        for (Residue residue : values())
            map1.put(residue.code1(), residue);

        for (char code : UNKNOWN_ALIAS)
            map1.put(code, Residue.UNK);
    }

    private static void populateNative() {
        for (Residue residue : values())
            if (residue.isNative())
                nativeList.add(residue);
    }

    /**
     * Identifies valid single-character amino acid codes.
     *
     * @param code1 a single-character code.
     *
     * @return {@code true} iff the specified code identifies an amino
     * acid.
     */
    public static boolean isValidCode1(char code1) {
        return map1.containsKey(code1);
    }

    /**
     * Identifies valid single-character amino acid codes.
     *
     * @param code1 a single-character code.
     *
     * @return {@code true} iff the specified code identifies an amino
     * acid.
     */
    public static boolean isValidCode1(String code1) {
        return code1.length() == 1 && map1.containsKey(code1.charAt(0));
    }

    /**
     * Retrieves an amino acid by its single-character code.
     *
     * @param code1 a single-character code.
     *
     * @return the amino acid with the specified single-character
     * code, or {@code null} if the code is invalid.
     */
    public static Residue lookupCode1(char code1) {
        return map1.get(code1);
    }

    /**
     * Validates single-character amino acid codes.
     *
     * @param code1 a single-character code.
     *
     * @throws IllegalArgumentException unless the code is valid.
     */
    public static void validateCode1(char code1) {
        if (!isValidCode1(code1))
            throw new IllegalArgumentException(String.format("Invalid amino acid code [%c].", code1));
    }

    /**
     * Validates single-character amino acid codes.
     *
     * @param code1 a single-character code.
     *
     * @throws IllegalArgumentException unless the string is a valid
     * single-character code.
     */
    public static void validateCode1(String code1) {
        if (!isValidCode1(code1))
            throw new IllegalArgumentException(String.format("Invalid amino acid code [%s].", code1));
    }

    /**
     * Retrieves an amino acid by its single-character code.
     *
     * @param code1 a single-character code.
     *
     * @return the amino acid with the specified single-character
     * code.
     *
     * @throws IllegalArgumentException unless the character code is
     * valid.
     */
    public static Residue valueOfCode1(char code1) {
        validateCode1(code1);
        return lookupCode1(code1);
    }

    /**
     * Retrieves an amino acid by its single-character code.
     *
     * @param code1 a single-character code.
     *
     * @return the amino acid with the specified single-character
     * code.
     *
     * @throws IllegalArgumentException unless the string is a valid
     * single-character code.
     */
    public static Residue valueOfCode1(String code1) {
        validateCode1(code1);
        return lookupCode1(code1.charAt(0));
    }

    /**
     * Returns the number of native residues.
     *
     * @return the number of native residues.
     */
    public static int countNative() {
        return nativeList.size();
    }

    /**
     * Lists the native residues.
     *
     * @return an unmodifiable list containing the native residues.
     */
    public static List<Residue> listNative() {
        return Collections.unmodifiableList(nativeList);
    }

    /**
     * Returns a different native residue selected at random, with all
     * other native residues chosen with equal probability.
     *
     * @param random the random number source.
     *
     * @return a different native residue selected at random.
     */
    public Residue mutate(JamRandom random) {
        Residue result;

        do {
            result = selectNative(random);
        } while (result.equals(this));

        return result;
    }

    /**
     * Selects a native residue at random, with all native residues
     * equally likely.
     *
     * @param random the random number source.
     *
     * @return a randomly selected native residue.
     */
    public static Residue selectNative(JamRandom random) {
        return nativeList.get(random.nextInt(nativeList.size()));
    }

    /**
     * Returns the single-character code for this amino acid.
     *
     * <p>Note that the single-character code is of type {@code char}
     * and the three-character abbreviation is of type {@code String},
     * so the compiler will automatically enforce the distinction.
     *
     * @return the single-character code for this amino acid.
     */
    public char code1() {
        return code1;
    }

    /**
     * Returns the three-character abbreviation for this amino acid.
     *
     * <p>Note that the single-character code is of type {@code char}
     * and the three-character abbreviation is of type {@code String},
     * so the compiler will automatically enforce the distinction.
     *
     * @return the three-character abbreviation for this amino acid.
     */
    public String code3() {
        return name();
    }

    /**
     * Identifies native amino acids.
     *
     * @return {@code true} iff this is a native amino acid (not a
     * special marker).
     */
    public boolean isNative() {
        return isNative;
    }

    /**
     * Returns the side-chain family of this amino acid.
     *
     * @return the side-chain family of this amino acid.
     */
    public Family family() {
        return family;
    }

    /**
     * Returns the full name of this amino acid.
     *
     * @return the full name of this amino acid.
     */
    public String fullName() {
        return fullName;
    }

    /**
     * Identifies polar amino acids.
     *
     * <p>Note that the special deletion marker is neither polar nor
     * nonpolar.
     *
     * @return {@code true} iff this amino acid is polar.
     */
    public boolean isPolar() {
        return family != null && family.isPolar();
    }

    /**
     * Identifies nonpolar amino acids.
     *
     * <p>Note that the special deletion marker is neither polar nor
     * nonpolar.
     *
     * @return {@code true} iff this amino acid is nonpolar.
     */
    public boolean isNonPolar() {
        return family != null && family.isNonPolar();
    }

    /**
     * Returns the frequency with which this residue occurs in the
     * human proteome.
     *
     * <p>The frequencies were downloaded from
     * <pre>https://web.expasy.org/protscale/pscale/A.A.Swiss-Prot.html</pre>
     * on 19 December 2018.
     *
     * @return the frequency with which this residue occurs in the
     * human proteome.
     */
    public double proteomeFrequency() {
        return protFreq;
    }
}
