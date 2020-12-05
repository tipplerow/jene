
package jene.maf;

import java.util.HashMap;
import java.util.Map;

import jam.lang.JamException;

/**
 * Enumerates the variant classifications that are annotated in MAF
 * files.
 */
public enum VariantClassification {
    FLANK_3PRIME("3'Flank"),
    FLANK_5PRIME("5'Flank"),
    FRAME_SHIFT_DEL("Frame_Shift_Del"),
    FRAME_SHIFT_INS("Frame_Shift_Ins"),
    IN_FRAME_DEL("In_Frame_Del"),
    IN_FRAME_INS("In_Frame_Ins"),
    INTRON("Intron"),
    MISSENSE("Missense_Mutation"),
    NONSENSE("Nonsense_Mutation"),
    NONSTOP("Nonstop_Mutation"),
    RNA("RNA"),
    SILENT("Silent"),
    SPLICE("Splice_Site"),
    TRANS_START("Translation_Start_Site"),
    UTR_3PRIME("3'UTR"),
    UTR_5PRIME("5'UTR");

    private final String code;

    private static final Map<String, VariantClassification> codeMap =
        new HashMap<String, VariantClassification>();

    static {
        populateCodeMap();
    }

    private VariantClassification(String code) {
        this.code = code;
    }

    private static void populateCodeMap() {
        for (VariantClassification vc : values())
            codeMap.put(vc.code(), vc);
    }

    /**
     * The canonical column name for variant classifications in the
     * header line of MAF files.
     */
    public static final String COLUMN_NAME = "Variant_Classification";

    /**
     * Returns the code used to identify this classification in MAF
     * files.
     *
     * @return the code used to identify this classification in MAF
     * files.
     */
    public String code() {
        return code;
    }

    /**
     * Identifies valid classification codes.
     *
     * @param code the classification code to validate.
     *
     * @return {@code true} iff the specified code refers to a defined
     * variant classification.
     */
    public static boolean isValidCode(String code) {
        return codeMap.containsKey(code);
    }

    /**
     * Retrieves a variant classification by its MAF file code.
     *
     * @param code a classification code.
     *
     * @return the variant classification with the specified MAF file
     * code, or {@code null} if the code is invalid.
     */
    public static VariantClassification lookupCode(String code) {
        return codeMap.get(code);
    }

    /**
     * Retrieves a variant classification by its MAF file code.
     *
     * @param code a classification code.
     *
     * @return the variant classification with the specified MAF file
     * code.
     *
     * @throws RuntimeException unless the specified code is valid.
     */
    public static VariantClassification requireCode(String code) {
        VariantClassification vc = lookupCode(code);

        if (vc == null)
            throw JamException.runtime("Invalid classification code: [%s].", code);

        return vc;
    }
}
