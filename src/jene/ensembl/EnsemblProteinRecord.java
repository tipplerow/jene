
package jene.ensembl;

import jene.fasta.FastaPeptideRecord;
import jene.hugo.HugoSymbol;
import jene.peptide.Peptide;

/**
 * Encapsulates all information required to uniquely identify and
 * describe a protein structure in the Ensembl database.
 */
public final class EnsemblProteinRecord {
    private final Peptide peptide;
    private final HugoSymbol hugoSymbol;
    private final EnsemblGeneID geneID;
    private final EnsemblProteinID proteinID;
    private final EnsemblTranscriptID transcriptID;
    private final TranscriptBiotype transcriptBiotype;

    private EnsemblProteinRecord(Peptide peptide,
                                 HugoSymbol hugoSymbol,
                                 EnsemblGeneID geneID,
                                 EnsemblProteinID proteinID,
                                 EnsemblTranscriptID transcriptID,
                                 TranscriptBiotype transcriptBiotype) {
        this.peptide = peptide;
        this.hugoSymbol = hugoSymbol;
        this.geneID = geneID;
        this.proteinID = proteinID;
        this.transcriptID = transcriptID;
        this.transcriptBiotype = transcriptBiotype;
    }

    /**
     * Creates a new protein record by parsing a FASTA record found
     * in an Ensembl database file.
     *
     * @param fastaRecord the FASTA record that encodes the protein
     * structure and metadata.
     *
     * @return the protein record encoded in the given FASTA record.
     */
    public static EnsemblProteinRecord parse(FastaPeptideRecord fastaRecord) {
        String fastaKey = fastaRecord.getKey();
        String headerLine = fastaRecord.getComment();

        HugoSymbol hugoSymbol = EnsemblGeneSymbol.parseHeader(headerLine);
        EnsemblGeneID geneID = EnsemblGeneID.parseHeader(headerLine);
        EnsemblProteinID proteinID = EnsemblProteinID.parseKey(fastaKey);
        EnsemblTranscriptID transcriptID = EnsemblTranscriptID.parseHeader(headerLine);
        TranscriptBiotype transcriptBiotype = TranscriptBiotype.parseHeader(headerLine);

        return new EnsemblProteinRecord(fastaRecord.getPeptide(), hugoSymbol, geneID,
                                        proteinID, transcriptID, transcriptBiotype);
    }

    /**
     * Returns the HUGO symbol for the parent gene.
     *
     * @return the HUGO symbol for the parent gene.
     */
    public HugoSymbol getHugoSymbol() {
        return hugoSymbol;
    }

    /**
     * Returns the Ensembl identifier for the parent gene.
     *
     * @return the Ensembl identifier for the parent gene.
     */
    public EnsemblGeneID getEnsemblGeneID() {
        return geneID;
    }

    /**
     * Returns the Ensembl identifier for the protein.
     *
     * @return the Ensembl identifier for the protein.
     */
    public EnsemblProteinID getEnsemblProteinID() {
        return proteinID;
    }

    /**
     * Returns the Ensembl identifier for the RNA transcript.
     *
     * @return the Ensembl identifier for the RNA transcript.
     */
    public EnsemblTranscriptID getEnsemblTranscriptID() {
        return transcriptID;
    }

    /**
     * Returns the protein structure in this record.
     *
     * @return the protein structure in this record.
     */
    public Peptide getPeptide() {
        return peptide;
    }

    /**
     * Returns the transcript biotype for this record.
     *
     * @return the transcript biotype for this record.
     */
    public TranscriptBiotype getTranscriptBiotype() {
        return transcriptBiotype;
    }

    /**
     * Identifies protein coding records.
     *
     * @return {@code true} iff this record corresponds to a
     * protein-coding gene and transcript.
     */
    public boolean isProteinCoding() {
        return transcriptBiotype.equals(TranscriptBiotype.PROTEIN_CODING);
    }
}
