
package jene.hla;

/**
 * Enumerates the source of the peptide fragments bound to MHC
 * molecules.
 */
public enum PeptideSource {
    /**
     * Peptides originate from non-synonymous somatic mutations.
     */
    NEO, 

    /**
     * Peptides originate from proteins encoded in the germline
     * genome.
     */
    SELF;
}
