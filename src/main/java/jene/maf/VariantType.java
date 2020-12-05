
package jene.maf;

/**
 * Enumerates the mutation types annotated in MAF files.
 */
public enum VariantType {
    DEL, INS, ONP, SNP, TNP;

    /**
     * The canonical column name for variant types in the header line
     * of MAF files.
     */
    public static final String COLUMN_NAME = "Variant_Type";
}
