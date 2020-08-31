
package jene.chem;

import java.util.Arrays;
import java.util.Collection;

import jam.lang.DomainDouble;
import jam.math.DoubleRange;

/**
 * Represents the concentration of a chemical species.
 */
public final class Concentration extends DomainDouble {
    /**
     * Valid range for concentration values.
     */
    public static final DoubleRange RANGE = DoubleRange.NON_NEGATIVE;

    /**
     * A globally sharable instance representing zero concentration.
     */
    public static final Concentration ZERO = valueOf(0.0);

    /**
     * Creates a new concentration.
     *
     * @param value the concentration value.
     *
     * @throws IllegalArgumentException if the concentration is
     * negative.
     */
    public Concentration(double value) {
        super(value, RANGE);
    }

    /**
     * Simulates the decay of a chemical species at a rate
     * proportional to its concentration.
     *
     * @param halfLife the half-life for the decay process.
     *
     * @param duration the duration of the decay process (in the same
     * units as the half-life).
     *
     * @return a new {@code Concentration} object containing the
     * decayed concentration value.
     */
    public Concentration decay(HalfLife halfLife, double duration) {
        return valueOf(halfLife.decay(doubleValue(), duration));
    }

    /**
     * Subtracts another concentration from this and returns the result
     * in a new {@code Concentration} object; this object is unchanged.
     *
     * @param that the concentration to subtract.
     *
     * @return the concentration difference.
     *
     * @throws IllegalArgumentException if the resulting concentration
     * would be negative.
     */
    public Concentration minus(Concentration that) {
        return valueOf(this.doubleValue() - that.doubleValue());
    }

    /**
     * Creates a new concentration by parsing a string containing the
     * concentration value.
     *
     * @param s a string representation of the concentration value.
     *
     * @return a new concentration with the value represented by the
     * input string.
     */
    public static Concentration parse(String s) {
        return valueOf(Double.parseDouble(s));
    }

    /**
     * Adds another concentration to this and returns the result in a
     * new {@code Concentration} object; this object is unchanged.
     *
     * @param that the concentration to add.
     *
     * @return the concentration sum.
     */
    public Concentration plus(Concentration that) {
        return valueOf(this.doubleValue() + that.doubleValue());
    }

    /**
     * Computes the (dimensionless) ratio of two concentrations.
     *
     * @param numer the concentration numerator.
     *
     * @param denom the concentration denominator.
     *
     * @return the (dimensionless) ratio of the concentrations.
     */
    public static double ratio(Concentration numer, Concentration denom) {
        return numer.doubleValue() / denom.doubleValue();
    }

    /**
     * Multiplies this concentration by a dimensionless scalar value
     * and returns the result in a new {@code Concentration} object;
     * this object is unchanged.
     *
     * @param scalar the dimensionless scalar factor.
     *
     * @return the rescaled concentration.
     *
     * @throws IllegalArgumentException if the resulting concentration
     * would be negative.
     */
    public Concentration times(double scalar) {
        return valueOf(scalar * doubleValue());
    }

    /**
     * Computes the total concentration of a sequence of species.
     *
     * @param concentrations the concentrations to process.
     *
     * @return the total concentration of the species provided.
     */
    public static Concentration total(Concentration... concentrations) {
	return total(Arrays.asList(concentrations));
    }

    /**
     * Computes the total concentration of a collection of species.
     *
     * @param concentrations the concentrations to process.
     *
     * @return the total concentration of the species provided.
     */
    public static Concentration total(Collection<Concentration> concentrations) {
        double total = 0.0;

        for (Concentration conc : concentrations)
            total += conc.doubleValue();

        return Concentration.valueOf(total);
    }

    /**
     * Validates a concentration value.
     *
     * @param value the concentration to validate.
     *
     * @throws IllegalArgumentException if the concentration is
     * negative.
     */
    public static void validate(double value) {
        RANGE.validate(value);
    }

    /**
     * Marks a {@code double} value as a {@code Concentration}.
     *
     * @param value the concentration value.
     *
     * @return a {@code Concentration} object having the specified
     * concentration value.
     *
     * @throws IllegalArgumentException if the concentration is
     * negative.
     */
    public static Concentration valueOf(double value) {
        return new Concentration(value);
    }
}
