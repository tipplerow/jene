
package jene.maf;

import jam.app.JamProperties;

import jene.ensembl.EnsemblTranscriptID;
import jene.hugo.HugoSymbol;
import jene.peptide.ProteinChange;
import jene.tcga.CellFraction;
import jene.tcga.TumorBarcode;

/**
 * Manages system properties for the {@code maf} package.
 */
public final class MAFProperties {
    /**
     * Name of the input MAF file to process.
     */
    public static final String MAF_FILE_PROPERTY = "jene.maf.mafFileInput";

    /**
     * Name of the output FASTA file to write.
     */
    public static final String FASTA_FILE_PROPERTY = "jene.maf.fastaFileOutput";

    /**
     * Name of the MAF file column containing the tumor barcode.
     */
    public static final String TUMOR_BARCODE_COLUMN_PROPERTY = "jene.maf.tumorBarcodeColumn";

    /**
     * Name of the MAF file column containing the HUGO symbol.
     */
    public static final String HUGO_SYMBOL_COLUMN_PROPERTY = "jene.maf.hugoSymbolColumn";

    /**
     * Name of the MAF file column containing the Ensembl transcript key.
     */
    public static final String TRANSCRIPT_COLUMN_PROPERTY = "jene.maf.transcriptColumn";

    /**
     * Name of the MAF file column containing the variant classification.
     */
    public static final String CLASSIFICATION_COLUMN_PROPERTY = "jene.maf.classificationColumn";

    /**
     * Name of the MAF file column containing the variant type.
     */
    public static final String VARIANT_TYPE_COLUMN_PROPERTY = "jene.maf.variantTypeColumn";

    /**
     * Name of the MAF file column containing the protein change.
     */
    public static final String PROTEIN_CHANGE_COLUMN_PROPERTY = "jene.maf.proteinChangeColumn";

    /**
     * Name of the MAF file column containing the cancer cell fraction.
     */
    public static final String CELL_FRACTION_COLUMN_PROPERTY = "jene.maf.cellFractionColumn";

    /**
     * Minimum cancer cell fraction required to process a mutation.
     */
    public static final String CCF_THRESHOLD_PROPERTY = "jene.maf.ccfThreshold";

    /**
     * Returns the name of the input MAF file to process.
     *
     * @return the name of the input MAF file to process.
     */
    public static String resolveMAFFile() {
        return JamProperties.getRequired(MAF_FILE_PROPERTY);
    }

    /**
     * Returns the name of the output FASTA file to write.
     *
     * @return the name of the output FASTA file to write.
     */
    public static String resolveFastaFile() {
        return JamProperties.getRequired(FASTA_FILE_PROPERTY);
    }

    /**
     * Returns the name of the MAF file column containing the tumor
     * barcode.
     *
     * @return the name of the MAF file column containing the tumor
     * barcode.
     */
    public static String resolveTumorBarcodeColumnName() {
        return JamProperties.getOptional(TUMOR_BARCODE_COLUMN_PROPERTY, TumorBarcode.COLUMN_NAME);
    }

    /**
     * Returns the name of the MAF file column containing the HUGO
     * symbol.
     *
     * @return the name of the MAF file column containing the HUGO
     * symbol.
     */
    public static String resolveHugoSymbolColumnName() {
        return JamProperties.getOptional(HUGO_SYMBOL_COLUMN_PROPERTY, HugoSymbol.COLUMN_NAME);
    }

    /**
     * Returns the name of the MAF file column containing the Ensembl
     * transcript.
     *
     * @return the name of the MAF file column containing the Ensembl
     * transcript.
     */
    public static String resolveTranscriptColumnName() {
        return JamProperties.getOptional(TRANSCRIPT_COLUMN_PROPERTY, EnsemblTranscriptID.COLUMN_NAME);
    }

    /**
     * Returns the name of the MAF file column containing the variant
     * classification code.
     *
     * @return the name of the MAF file column containing the variant
     * classification code.
     */
    public static String resolveClassificationColumnName() {
        return JamProperties.getOptional(CLASSIFICATION_COLUMN_PROPERTY, VariantClassification.COLUMN_NAME);
    }

    /**
     * Returns the name of the MAF file column containing the variant
     * type code.
     *
     * @return the name of the MAF file column containing the variant
     * type code.
     */
    public static String resolveVariantTypeColumnName() {
        return JamProperties.getOptional(VARIANT_TYPE_COLUMN_PROPERTY, VariantType.COLUMN_NAME);
    }

    /**
     * Returns the name of the MAF file column containing the protein
     * change.
     *
     * @return the name of the MAF file column containing the protein
     * change.
     */
    public static String resolveProteinChangeColumnName() {
        return JamProperties.getOptional(PROTEIN_CHANGE_COLUMN_PROPERTY, ProteinChange.COLUMN_NAME);
    }

    /**
     * Returns the name of the MAF file column containing the cancer
     * cell fraction.
     *
     * @return the name of the MAF file column containing the cancer
     * cell fraction.
     */
    public static String resolveCellFractionColumnName() {
        return JamProperties.getOptional(CELL_FRACTION_COLUMN_PROPERTY, CellFraction.COLUMN_NAME);
    }

    /**
     * Returns the name of the MAF file column containing the cancer
     * cell fraction.
     *
     * @return the name of the MAF file column containing the cancer
     * cell fraction.
     */
    public static CellFraction resolveCCFThreshold() {
        return CellFraction.valueOf(JamProperties.getOptionalDouble(CCF_THRESHOLD_PROPERTY, 0.0));
    }
}
