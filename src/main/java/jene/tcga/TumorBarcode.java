
package jene.tcga;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jam.app.JamLogger;
import jam.io.LineReader;
import jam.lang.KeyedObject;

/**
 * Represents a unique tumor sample identifier in the TCGA database.
 */
public final class TumorBarcode extends KeyedObject<String> {
    private TumorBarcode(String key) {
        super(key);
    }

    /**
     * The canonical column name for tumor identifiers in the header
     * line of data files to be analyzed by the {@code jene} library.
     */
    public static final String COLUMN_NAME = "Tumor_Barcode";

    /**
     * String length of the patient key encoded at the beginning of
     * TCGA barcodes.
     */
    public static final int PATIENT_KEY_LENGTH = 12;

    /**
     * Returns the tumor barcode for a given key string.
     *
     * @param key the key string.
     *
     * @return the tumor barcode for the given key string.
     */
    public static TumorBarcode instance(String key) {
        return new TumorBarcode(key);
    }

    /**
     * Reads tumor barcodes from a flat file (no header, one barcode
     * per line).
     *
     * @param fileName the name of the file to load.
     *
     * @return a list containing the barcodes from the specified file.
     */
    public static List<TumorBarcode> load(String fileName) {
        return load(new File(fileName));
    }

    /**
     * Reads tumor barcodes from a flat file (no header, one barcode
     * per line).
     *
     * @param file the file to load.
     *
     * @return a list containing the barcodes from the specified file.
     */
    public static List<TumorBarcode> load(File file) {
        List<TumorBarcode> barcodes = new ArrayList<TumorBarcode>();

        try (LineReader reader = LineReader.open(file)) {
            for (String line : reader)
                barcodes.add(instance(line));
        }

        JamLogger.info("Loaded [%d] barcodes...", barcodes.size());
        return barcodes;
    }

    /**
     * Returns the ID for the patient from which this tumor was
     * sampled.
     *
     * @return the ID for the patient from which this tumor was
     * sampled.
     */
    public PatientID patientID() {
        return PatientID.instance(getKey().substring(0, PATIENT_KEY_LENGTH));
    }
}
