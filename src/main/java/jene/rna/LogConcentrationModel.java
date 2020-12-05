
package jene.rna;

import jam.app.JamProperties;
import jam.lang.JamException;

import jene.chem.Concentration;

/**
 * Defines a log-transformed protein concentration model:
 * The protein concentration is equal to:
 *
 * <pre>
 *     C = min[log(1 + FPKM / alpha), Cmax],
 * </pre>
 *
 * where {@code FPKM} is the RNA transcript expression level,
 * {@code alpha} is a positive scale parameter (in FPKM units),
 * {@code Cmax} is the maximum (log-transformed) concentration.
 */
public final class LogConcentrationModel extends ConcentrationModel {
    private final double alphaFactor;

    private static LogConcentrationModel global = null;

    /**
     * Creates a new log-transformed concentration model with fixed
     * parameters.
     *
     * @param exprThreshold the minimum RNA expression level required
     * for positive peptide concentration.
     *
     * @param maxExpression the maximum RNA expression level passed to
     * the concentration model.
     *
     * @param alphaFactor the alpha scaling parameter.
     */
    public LogConcentrationModel(double exprThreshold,
                                 double maxExpression,
                                 double alphaFactor) {
        super(exprThreshold, maxExpression);
        this.alphaFactor = alphaFactor;
    }

    /**
     * Name of the system property that specifies the alpha scaling
     * factor.
     */
    public static final String ALPHA_PROPERTY = "jene.rna.log1P.alpha";

    /**
     * Default value for the alpha scaling factor.
     */
    public static final double ALPHA_DEFAULT = 1.0;

    /**
     * The log-transformed concentration model with default parameters.
     */
    public static final LogConcentrationModel DEFAULT =
        new LogConcentrationModel(EXPR_THRESHOLD_DEFAULT, MAX_EXPRESSION_DEFAULT, ALPHA_DEFAULT);

    /**
     * Returns the global log-transformed concentration model defined
     * by system properties.
     *
     * @return the global log-transformed concentration model defined
     * by system properties.
     */
    public static LogConcentrationModel global() {
        if (global == null)
            global = new LogConcentrationModel(resolveExprThreshold(),
                                               resolveMaxExpression(),
                                               resolveAlphaFactor());

        return global;
    }

    private static double resolveAlphaFactor() {
        return JamProperties.getOptionalDouble(ALPHA_PROPERTY, ALPHA_DEFAULT);
    }

    @Override public ConcentrationModelType getType() {
        return ConcentrationModelType.LOG;
    }

    @Override protected double translate(double expression) {
        //
        // The expression has already been filtered... 
        //
        return Math.log(1.0 + expression / alphaFactor);
    }
}
