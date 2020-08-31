
package jene.nucleic;

/**
 * Enumerates the nucleotides contained in DNA and RNA.
 */
public enum Nucleotide {
    /**
     * Adenine.
     */
    A("Adenine", Family.PURINE, true, true) {
        @Override public Nucleotide getPartner() {
            return T;
        }

        @Override public Nucleotide transcribe() {
            return this;
        }
    },

    /**
     * Thymine.
     */
    T("Thymine", Family.PYRIMIDINE, true, false) {
        @Override public Nucleotide getPartner() {
            return A;
        }

        @Override public Nucleotide transcribe() {
            return U;
        }
    },

    /**
     * Guanine.
     */
    G("Guanine", Family.PURINE, true, true) {
        @Override public Nucleotide getPartner() {
            return C;
        }

        @Override public Nucleotide transcribe() {
            return this;
        }
    },

    /**
     * Cytosine.
     */
    C("Cytosine", Family.PYRIMIDINE, true, true) {
        @Override public Nucleotide getPartner() {
            return G;
        }

        @Override public Nucleotide transcribe() {
            return this;
        }
    },

    /**
     * Uracil.
     */
    U("Uracil", Family.PYRIMIDINE, false, true) {
        @Override public Nucleotide getPartner() {
            return A;
        }

        @Override public Nucleotide transcribe() {
            throw new UnsupportedOperationException("Cannot transcribe uracil.");
        }
    },

    /**
     * Any of the possible nucleotides.
     */
    N("Any", null, true, false) {
        @Override public Nucleotide getPartner() {
            return this;
        }

        @Override public Nucleotide transcribe() {
            return this;
        }
    };

    /**
     * Ring family.
     */
    public enum Family { PYRIMIDINE, PURINE };

    private final String baseName;
    private final Family ringFamily;
    private final boolean inDNA;
    private final boolean inRNA;

    private Nucleotide(String baseName, Family ringFamily, boolean inDNA, boolean inRNA) {
        this.baseName   = baseName;
        this.ringFamily = ringFamily;

        this.inDNA = inDNA;
        this.inRNA = inRNA;
    }

    /**
     * Retrives nucleotides by their single-character code.
     *
     * @param code the code to search for.
     *
     * @return the nucleotide with the specified single-character code.
     *
     * @throws IllegalArgumentException unless the code maps to a nucleotide.
     */
    public static Nucleotide valueOf(Character code) {
        return valueOf(code.toString());
    }

    /**
     * Returns the base name of this amino acid.
     *
     * @return the base name of this amino acid.
     */
    public String baseName() {
        return baseName;
    }

    /**
     * Returns the ring family of this amino acid.
     *
     * @return the ring family of this amino acid.
     */
    public Family ringFamily() {
        return ringFamily;
    }

    /**
     * Identifies nucleotides occurring in DNA.
     *
     * @return {@code true} iff this nucleotide occurs naturally in DNA.
     */
    public boolean inDNA() {
        return inDNA;
    }

    /**
     * Identifies nucleotides occurring in RNA.
     *
     * @return {@code true} iff this nucleotide occurs naturally in RNA.
     */
    public boolean inRNA() {
        return inRNA;
    }

    /**
     * Returns the base-pair partner of this nucleotide.
     *
     * @return the base-pair partner of this nucleotide.
     */
    public abstract Nucleotide getPartner();

    /**
     * Returns the RNA transcription product of this nucleotide.
     *
     * @return the RNA transcription product of this nucleotide.
     *
     * @throws UnsupportedOperationException when called for uracil,
     * as it should not appear in DNA sequences.
     */
    public abstract Nucleotide transcribe();
}
