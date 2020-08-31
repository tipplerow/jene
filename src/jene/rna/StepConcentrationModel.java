
package jene.rna;

import jam.app.JamProperties;
import jam.lang.JamException;

import jene.chem.Concentration;

/**
 * Defines a step-function protein concentration model: the protein 
 *concentration, {@code C}, is a step function:
 *
 * <pre>
 *     C = 0, FPKM &lt; Fmin,
 *     C = 1, FPKM &ge; Fmin.
 * </pre>
 *
 * where {@code FPKM} is the RNA transcript expression level and
 * {@code Fmin} is a threshold expression level.
 */
public final class StepConcentrationModel extends ConcentrationModel {
    private static StepConcentrationModel global = null;

    /**
     * Creates a new step-function concentration model with a fixed
     * threshold.
     *
     * @param exprThreshold the minimum RNA expression level required
     * for positive peptide concentration.
     */
    public StepConcentrationModel(double exprThreshold) {
        super(exprThreshold, 1.0);
    }

    /**
     * The step-function concentration model with default parameters.
     */
    public static final StepConcentrationModel DEFAULT =
        new StepConcentrationModel(EXPR_THRESHOLD_DEFAULT);

    /**
     * Returns the global step-function concentration model defined by
     * system properties.
     *
     * @return the global step-function concentration model defined by
     * system properties.
     */
    public static StepConcentrationModel global() {
        if (global == null)
            global = new StepConcentrationModel(resolveExprThreshold());

        return global;
    }

    @Override public ConcentrationModelType getType() {
        return ConcentrationModelType.STEP;
    }

    @Override protected double translate(double expression) {
        //
        // Expressions below the threshold have already been removed...
        //
        return 1.0;
    }
}
