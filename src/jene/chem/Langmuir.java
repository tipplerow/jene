
package jene.chem;

import jam.math.AbstractUnivariateFunction;
import jam.math.DoubleRange;
import jam.math.Probability;

/**
 * Implements the Langmuir adsorption isotherm: {@code conc / (1.0 + conc)},
 * where {@code conc} is the concentration of a chemical species.
 */
public final class Langmuir extends AbstractUnivariateFunction {
    private Langmuir() {}

    /**
     * The single function instance.
     */
    public static final Langmuir INSTANCE = new Langmuir();

    /**
     * Computes the Langmuir isotherm for a specific concentration.
     *
     * @param conc the chemical species concentration.
     *
     * @return the value of the Langmuir isotherm at the given
     * concentration.
     */
    public static double evaluate(Concentration conc) {
        return INSTANCE.evaluate(conc.doubleValue());
    }

    /**
     * Computes the Langmuir isotherm for a specific concentration and
     * returns the result as a probability of adsorption.
     *
     * @param conc the chemical species concentration.
     *
     * @return the value of the Langmuir isotherm at the given
     * concentration wrapped in a {@code Probability} object.
     */
    public static Probability probability(double conc) {
        return Probability.valueOf(INSTANCE.evaluate(conc));
    }

    /**
     * Computes the Langmuir isotherm for a specific concentration and
     * returns the result as a probability of adsorption.
     *
     * @param conc the chemical species concentration.
     *
     * @return the value of the Langmuir isotherm at the given
     * concentration wrapped in a {@code Probability} object.
     */
    public static Probability probability(Concentration conc) {
        return probability(conc.doubleValue());
    }

    @Override protected double evaluateInRange(double x) {
        return x / (1.0 + x);
    }

    @Override public DoubleRange range() {
        return DoubleRange.NON_NEGATIVE;
    }
}
