
package jene.hugo;

import jam.lang.KeyedObject;

/**
 * Represents HUGO Gene Nomenclature Committee (HGNC) identifiers.
 */
public final class HugoSymbol extends KeyedObject<String> {
    private HugoSymbol(String key) {
        super(key);
    }

    /**
     * The canonical column name for HUGO symbols in the header line
     * of data files to be analyzed by the {@code jene} library.
     */
    public static final String COLUMN_NAME = "Hugo_Symbol";

    /**
     * Returns the HUGO symbol object for a given key string.
     *
     * @param key the HUGO symbol key.
     *
     * @return the HUGO symbol object for the given key string.
     */
    public static HugoSymbol instance(String key) {
        return new HugoSymbol(key);
    }
}
