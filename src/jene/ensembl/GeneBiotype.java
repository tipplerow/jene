
package jene.ensembl;

/**
 * Enumerates the gene biotypes in the Ensembl database.
 */
public enum GeneBiotype {
    IG_C_GENE,
    IG_D_GENE,
    IG_J_GENE,
    IG_V_GENE,
    TR_C_GENE,
    TR_D_GENE,
    TR_J_GENE,
    TR_V_GENE,
    POLYMORPHIC_PSEUDOGENE,
    PROTEIN_CODING;

    private static final String LABEL_CODE = "gene_biotype:";

    /**
     * Extracts the gene biotype from an Ensembl record header line.
     *
     * @param headerLine the header line from an Ensembl record.
     *
     * @return the gene biotype contained in the given header line.
     *
     * @throws RuntimeException unless the header line contains a
     * properly formatted gene biotype.
     */
    public static GeneBiotype parseHeader(String headerLine) {
        return valueOf(EnsemblID.parseHeader(headerLine, LABEL_CODE).toUpperCase());
    }
}
