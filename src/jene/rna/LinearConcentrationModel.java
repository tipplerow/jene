
package jene.rna;

import jam.app.JamProperties;
import jam.lang.JamException;

import jene.chem.Concentration;

/**
 * Defines a linear protein concentration model where the protein
 * concentration is equal to the expression level:
 *
 * <pre>
 *     C = min(FPKM, Cmax),
 * </pre>
 *
 * where {@code FPKM} is the RNA transcript expression level and
 * {@code Cmax} is the maximum concentration.
 */
public final class LinearConcentrationModel extends ConcentrationModel {
    private static LinearConcentrationModel global = null;

    /**
     * Creates a new linear concentration model with fixed parameters.
     *
     * @param exprThreshold the minimum RNA expression level required
     * for positive peptide concentration.
     *
     * @param maxExpression the maximum RNA expression level passed to
     * the concentration model.
     */
    public LinearConcentrationModel(double exprThreshold, double maxExpression) {
        super(exprThreshold, maxExpression);
    }

    /**
     * The linear concentration model with default parameters.
     */
    public static LinearConcentrationModel DEFAULT =
        new LinearConcentrationModel(EXPR_THRESHOLD_DEFAULT, MAX_EXPRESSION_DEFAULT);

    /**
     * Returns the global linear concentration model defined by system
     * properties.
     *
     * @return the global linear concentration model defined by system
     * properties.
     */
    public static LinearConcentrationModel global() {
        if (global == null)
            global = new LinearConcentrationModel(resolveExprThreshold(), resolveMaxExpression());

        return global;
    }

    @Override public ConcentrationModelType getType() {
        return ConcentrationModelType.LINEAR;
    }

    @Override protected double translate(double expression) {
        //
        // The expression has already been filtered...
        //
        return expression;
    }
}
