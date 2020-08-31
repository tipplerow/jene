
package jene.chr;

/**
 * Quantifies the length of a chromosome segment.
 */
public final class Length {
    private final int units;
    private final Basis basis;

    private Length(int units, Basis basis) {
        this.units = units;
        this.basis = basis;
    }

    /**
     * Returns the length corresponding to a given number of
     * individual base pairs.
     *
     * @param units the number of individual base pairs.
     *
     * @return the length corresponding to the specified number of
     * individual base pairs.
     */
    public static Length bases(int units) {
        return new Length(units, Basis.BASES);
    }

    /**
     * Returns the length corresponding to a given number of
     * kilobases.
     *
     * @param units the number of kilobases.
     *
     * @return the length corresponding to the specified number of
     * kilobases.
     */
    public static Length kilo(int units) {
        return new Length(units, Basis.KILO);
    }

    /**
     * Returns the length corresponding to a given number of
     * megabases.
     *
     * @param units the number of megabases.
     *
     * @return the length corresponding to the specified number of
     * megabases.
     */
    public static Length mega(int units) {
        return new Length(units, Basis.MEGA);
    }

    /**
     * Returns the length in units of individual base pairs.
     *
     * @return the length in units of individual base pairs.
     */
    public int bases() {
        return units * basis.unitLength();
    }

    /**
     * Returns the length in units of kilobases (rounded if
     * necessary).
     *
     * @return the length in units of kilobases (rounded if
     * necessary).
     */
    public int kilo() {
        return (int) Math.round(1.0E-03 * bases());
    }

    /**
     * Returns the length in units of megabases (rounded if
     * necessary).
     *
     * @return the length in units of megabases (rounded if
     * necessary).
     */
    public int mega() {
        return (int) Math.round(1.0E-06 * bases());
    }
}
