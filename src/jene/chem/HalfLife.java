
package jene.chem;

import jam.lang.DomainDouble;
import jam.math.DoubleRange;
import jam.math.DoubleUtil;

/**
 * Represents the half-life of a first-order decay process: the time
 * by when the quantity has decayed to half its original value.
 */
public final class HalfLife extends DomainDouble {
    private final double rate;

    /**
     * Valid range for concentration values.
     */
    public static final DoubleRange RANGE = DoubleRange.POSITIVE;

    /**
     * Creates a new half-life.
     *
     * @param halfLife the time by which the quantity has decayed to half
     * its original value.
     *
     * @throws IllegalArgumentException unless the half-life is positive.
     */
    public HalfLife(double halfLife) {
        super(halfLife, RANGE);
        rate = computeRate(halfLife);
    }

    /**
     * Computes the single-period decay rate corresponding to a given
     * half-life.
     *
     * @param halfLife the half-life of the decay process.
     *
     * @return the single-period decay rate corresponding to the given
     * half-life.
     *
     * @throws IllegalArgumentException unless the half-life is positive.
     */
    public static double computeRate(double halfLife) {
        RANGE.validate(halfLife);
        return 1.0 - Math.pow(0.5, 1.0 / halfLife);
    }

    /**
     * Simulates the decay of a quantity over a fixed duration, when
     * the decay process has this half-life.
     *
     * @param quantity the original quantity.
     *
     * @param duration the duration of the decay process.
     *
     * @return the quantity remaining after the decay process has
     * operated for the specified duration.
     */
    public double decay(double quantity, double duration) {
        return quantity * Math.pow(1.0 - rate, duration);
    }

    /**
     * Returns the single-period decay rate corresponding to this
     * half-life.
     *
     * @return the single-period decay rate corresponding to this
     * half-life.
     */
    public double getRate() {
        return rate;
    }

    /**
     * Creates a new half-life by parsing a string containing the
     * half-life value.
     *
     * @param s a string representation of the half-life value.
     *
     * @return a new half-life with the value represented by the
     * input string.
     */
    public static HalfLife parse(String s) {
        return valueOf(Double.parseDouble(s));
    }

    /**
     * Validates a half-life value.
     *
     * @param halfLife the time by which the quantity has decayed to half
     * its original value.
     *
     * @throws IllegalArgumentException unless the half-life is positive.
     */
    public static void validate(double halfLife) {
        RANGE.validate(halfLife);
    }

    /**
     * Marks a {@code double} value as a {@code HalfLife}.
     *
     * @param halfLife the time by which the quantity has decayed to half
     * its original value.
     *
     * @return a {@code HalfLife} object having the specified half-life.
     *
     * @throws IllegalArgumentException unless the half-life is positive.
     */
    public static HalfLife valueOf(double halfLife) {
        return new HalfLife(halfLife);
    }
}
