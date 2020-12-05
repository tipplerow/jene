
package jene.tcga;

import jam.lang.DomainDouble;
import jam.math.DoubleRange;

import jene.chem.Concentration;

/**
 * Quantifies the fraction of tumor cells containing a specific
 * mutation.
 */
public final class CellFraction extends DomainDouble {
    /**
     * Valid range for cell fractions.
     */
    public static final DoubleRange RANGE = DoubleRange.FRACTIONAL;

    /**
     * A globally sharable instance representing zero cell fraction.
     */
    public static final CellFraction ZERO = valueOf(0.0);

    /**
     * A globally sharable instance representing unit cell fraction.
     */
    public static final CellFraction UNIT = valueOf(1.0);

    private CellFraction(double value) {
        super(value, RANGE);
    }

    /**
     * Name of the cell fraction column in stanardized data files.
     */
    public static final String COLUMN_NAME = "CCF";

    /**
     * Validates a cell fraction value.
     *
     * @param value the expression to validate.
     *
     * @throws IllegalArgumentException if the expression is negative.
     */
    public static void validate(double value) {
        RANGE.validate(value);
    }

    /**
     * Marks a {@code double} value as a {@code CellFraction}.
     *
     * @param value the expression value.
     *
     * @return a {@code CellFraction} object having the specified
     * expression value.
     *
     * @throws IllegalArgumentException if the expression is
     * negative.
     */
    public static CellFraction valueOf(double value) {
        return new CellFraction(value);
    }

    /**
     * Returns a cell fraction object corresponding to a string
     * representation.
     *
     * @param s a string representation of the expression value.
     *
     * @return a cell fraction object with the value represented
     * by the input string.
     */
    public static CellFraction valueOf(String s) {
        return valueOf(Double.parseDouble(s));
    }

    /**
     * Compares this cell fraction to a threshold value.
     *
     * @param threshold a threshold cell fraction.
     *
     * @return {@code true} iff this cell fraction exceeds the
     * threshold.
     */
    public boolean above(CellFraction threshold) {
        return this.doubleValue() > threshold.doubleValue();
    }

    /**
     * Compares this cell fraction to a threshold value.
     *
     * @param threshold a threshold cell fraction.
     *
     * @return {@code true} iff this cell fraction falls below the
     * threshold.
     */
    public boolean below(CellFraction threshold) {
        return this.doubleValue() < threshold.doubleValue();
    }

    /**
     * Multiples a protein concentration by this cell fraction.
     *
     * @param concentration a protein concentration.
     *
     * @return the protein concentration rescaled by this cell
     * fraction.
     */
    public Concentration times(Concentration concentration) {
        return Concentration.valueOf(concentration.doubleValue() * doubleValue());
    }
}
