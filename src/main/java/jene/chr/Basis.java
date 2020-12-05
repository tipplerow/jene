
package jene.chr;

/**
 * Enumerates bases used to describe chromosome lengths.
 */
public enum Basis {
    /**
     * Lengths are expressed as the number of individual base pairs.
     */
    BASES(1),

    /**
     * Lengths are expressed as the number of kilo-base pairs.
     */
    KILO(1000),

    /**
     * Lengths are expressed as the number of mega-base pairs.
     */
    MEGA(1000000);

    private final int unitLength;

    private Basis(int unitLength) {
        this.unitLength = unitLength;
    }

    /**
     * Returns the number of base pairs in a single basis unit.
     *
     * @return the number of base pairs in a single basis unit.
     */
    public int unitLength() {
        return unitLength;
    }
}
