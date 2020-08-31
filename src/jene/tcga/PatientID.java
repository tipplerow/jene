
package jene.tcga;

import jam.lang.JamException;
import jam.lang.KeyedObject;

/**
 * Represents a unique patient identifier in the TCGA database.
 */
public final class PatientID extends KeyedObject<String> {
    private PatientID(String key) {
        super(key);
    }

    /**
     * The canonical column name for patient identifiers in the header
     * line of data files to be analyzed by the {@code jene} library.
     */
    public static final String COLUMN_NAME = "Patient_ID";

    /**
     * Returns the patient ID for a given key string.
     *
     * @param key the key string.
     *
     * @return the patient ID for the given key string.
     */
    public static PatientID instance(String key) {
        return new PatientID(key);
    }
}
