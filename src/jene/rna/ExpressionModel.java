
package jene.rna;

import jam.app.JamProperties;
import jam.lang.JamException;

import jene.hugo.HugoSymbol;
import jene.tcga.TumorBarcode;

/**
 * Defines an interface to RNA expression data for a patient cohort.
 */
public abstract class ExpressionModel {
    private static ExpressionModel global = null;

    /**
     * Name of the system property that specifies the type of the
     * global RNA expression model.
     */
    public static final String MODEL_TYPE_PROPERTY =
        "jene.rna.expressionModelType";

    /**
     * Returns the global expression model defined by system
     * properties.
     *
     * @return the global expression model defined by system
     * properties.
     */
    public static ExpressionModel global() {
        if (global == null)
            global = createGlobal();

        return global;
    }

    private static ExpressionModel createGlobal() {
        ExpressionModelType type = resolveModelType();

        switch (type) {
        case AGGREGATE:
            return AggregateExpressionModel.global();

        case CANCER_TYPE:
            return CancerTypeExpressionModel.global();

        case INDIVIDUAL:
            return IndividualExpressionModel.global();

        default:
            throw JamException.runtime("Unsupported expression model type: [%s].", type);
        }
    }

    private static ExpressionModelType resolveModelType() {
        return JamProperties.getRequiredEnum(MODEL_TYPE_PROPERTY, ExpressionModelType.class);
    }

    /**
     * Returns the RNA expression level (FPKM) for a specified tumor
     * and gene.
     *
     * @param barcode the identifer for the tumor of interest.
     *
     * @param symbol the HUGO symbol for the gene of interest.
     *
     * @return the RNA expression level for the specified tumor and
     * gene ({@code null} if the model does not contain a matching
     * record).
     */
    public abstract Expression lookup(TumorBarcode barcode, HugoSymbol symbol);

    /**
     * Returns the RNA expression level (FPKM) for a specified tumor
     * and gene.
     *
     * @param barcode the identifer for the tumor of interest.
     *
     * @param symbol the HUGO symbol for the gene of interest.
     *
     * @return the RNA expression level (FPKM) for the specified tumor
     * and gene.
     *
     * @throws RuntimeException unless the model contains a matching
     * record.
     */
    public Expression require(TumorBarcode barcode, HugoSymbol symbol) {
        Expression result = lookup(barcode, symbol);

        if (result != null)
            return result;
        else
            throw JamException.runtime("Missing expression record: [%s, %s].",
                                       barcode.getKey(), symbol.getKey());
    }
}
