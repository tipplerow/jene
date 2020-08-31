
package jene.rna;

import jam.app.JamProperties;
import jam.lang.JamException;

import jene.chem.Concentration;
import jene.hugo.HugoSymbol;
import jene.hugo.HugoPeptideTable;
import jene.peptide.Peptide;
import jene.peptide.PeptideConcentrationBuilder;
import jene.peptide.PeptideConcentrationProfile;

/**
 * Defines an interface to models that convert RNA expression to
 * protein concentration.
 */
public abstract class ConcentrationModel {
    private final double exprThreshold;
    private final double maxExpression;

    private static ConcentrationModel global = null;

    /**
     * Creates a new concentration model with given expression bounds.
     *
     * @param exprThreshold the minimum RNA expression level required
     * for positive peptide concentration.
     *
     * @param maxExpression the maximum RNA expression level passed to
     * the concentration model.
     */
    protected ConcentrationModel(double exprThreshold, double maxExpression) {
        this.exprThreshold = exprThreshold;
        this.maxExpression = maxExpression;
    }

    /**
     * System property that specifies the type of the global protein
     * concentration model.
     */
    public static final String CONC_MODEL_TYPE_PROPERTY = "jene.rna.concModelType";

    /**
     * System property that specified the minimum RNA expression
     * required for positive peptide concentration.
     */
    public static final String EXPR_THRESHOLD_PROPERTY = "jene.rna.exprThreshold";

    /**
     * Default value for the RNA expression threshold.
     */
    public static final double EXPR_THRESHOLD_DEFAULT = 0.13;

    /**
     * System property that specifies the maximum RNA expression
     * passed to the concentration model.
     */
    public static final String MAX_EXPRESSION_PROPERTY = "jene.rna.maxExpression";

    /**
     * Default value for the maximum RNA expression.
     */
    public static final double MAX_EXPRESSION_DEFAULT = 1.0E+05;

    /**
     * Returns the global concentration model defined through system
     * properties.
     *
     * @return the global concentration model defined through system
     * properties.
     */
    public static ConcentrationModel global() {
        if (global == null)
            global = createGlobal();

        return global;
    }

    private static ConcentrationModel createGlobal() {
        ConcentrationModelType modelType =
            JamProperties.getRequiredEnum(CONC_MODEL_TYPE_PROPERTY, ConcentrationModelType.class);

        return modelType.globalModel();
    }

    /**
     * Returns the minimum RNA expression required for positive
     * peptide concentration (as set through system properties).
     *
     * @return the minimum RNA expression required for positive
     * peptide concentration (as set through system properties).
     */
    public static double resolveExprThreshold() {
        return JamProperties.getOptionalDouble(EXPR_THRESHOLD_PROPERTY, EXPR_THRESHOLD_DEFAULT);
    }

    /**
     * Returns the maximum RNA expression passed to the concentration
     * model (as set through system properties).
     *
     * @return the maximum RNA expression passed to the concentration
     * model (as set through system properties).
     */
    public static double resolveMaxExpression() {
        return JamProperties.getOptionalDouble(MAX_EXPRESSION_PROPERTY, MAX_EXPRESSION_DEFAULT);
    }

    /**
     * Returns the enumerated type for this model.
     *
     * @return the enumerated type for this model.
     */
    public abstract ConcentrationModelType getType();

    /**
     * Builds a protein concentration profile from RNA expression
     * data.
     *
     * @param peptideTable a table containing peptides derived from
     * proteins (e.g., by proteasomal cleavage).
     *
     * @param expressionProfile RNA expression indexed by gene.
     *
     * @return the protein concentration profile derived from the
     * given expression profile.
     */
    public PeptideConcentrationProfile buildProfile(HugoPeptideTable peptideTable,
                                                    ExpressionProfile expressionProfile) {
        PeptideConcentrationBuilder builder =
            PeptideConcentrationBuilder.create();

        for (HugoSymbol symbol : peptideTable.viewSymbols()) {
            Expression expression = expressionProfile.get(symbol);
            Concentration concentration = translate(expression);

            if (concentration.isPositive())
                builder.addAll(peptideTable.get(symbol), concentration);
        }

        return builder.build();
    }

    /**
     * Returns the minimum RNA expression required for positive
     * peptide concentration.
     *
     * @return the minimum RNA expression required for positive
     * peptide concentration.
     */
    public double getExprThreshold() {
        return exprThreshold;
    }

    /**
     * Returns the maximum RNA expression passed to the concentration
     * model.
     *
     * @return the maximum RNA expression passed to the concentration
     * model.
     */
    public double getMaxExpression() {
        return maxExpression;
    }

    /**
     * Returns the protein concentration that corresponds to a given
     * RNA expression.
     *
     * @param expression the RNA expression level.
     *
     * @return the protein concentration that corresponds to the given
     * RNA expression level.
     */
    public Concentration translate(Expression expression) {
        double expr = expression.doubleValue();

        if (expr < exprThreshold)
            return Concentration.ZERO;
        else
            return Concentration.valueOf(translate(Math.min(expr, maxExpression)));
    }

    /**
     * Returns the protein concentration that corresponds to a given
     * RNA expression.
     *
     * @param expression the RNA expression level, guaranteed to be
     * above the threshold for positive peptide concentration and at
     * or below the maximum allowed value.
     *
     * @return the protein concentration that corresponds to the given
     * bounded RNA expression level.
     */
    protected abstract double translate(double expression);
}
