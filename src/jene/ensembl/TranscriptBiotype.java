
package jene.ensembl;

/**
 * Enumerates the transcript biotypes in the Ensembl database.
 */
public enum TranscriptBiotype {
    IG_C_GENE,
    IG_D_GENE,
    IG_J_GENE,
    IG_V_GENE,
    TR_C_GENE,
    TR_D_GENE,
    TR_J_GENE,
    TR_V_GENE,
    NON_STOP_DECAY,
    NONSENSE_MEDIATED_DECAY,
    POLYMORPHIC_PSEUDOGENE,
    PROTEIN_CODING;

    private static final String LABEL_CODE = "transcript_biotype:";

    /**
     * Extracts the transcript biotype from an Ensembl record header
     * line.
     *
     * @param headerLine the header line from an Ensembl record.
     *
     * @return the transcript biotype contained in the given header
     * line.
     *
     * @throws RuntimeException unless the header line contains a
     * properly formatted transcript biotype.
     */
    public static TranscriptBiotype parseHeader(String headerLine) {
        return valueOf(EnsemblID.parseHeader(headerLine, LABEL_CODE).toUpperCase());
    }
}
