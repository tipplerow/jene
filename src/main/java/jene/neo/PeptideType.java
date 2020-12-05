
package jene.neo;

/**
 * Distinguishes neo-peptides and self-peptides.
 */
public enum PeptideType {
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
