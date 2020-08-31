
package jene.tcga;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;

import jam.app.JamLogger;
import jam.app.JamProperties;
import jam.io.TableReader;
import jam.lang.JamException;
import jam.util.MapUtil;

/**
 * Maps tumor barcodes to the sampled patient in the cohort.
 *
 * <p>Tumor barcodes must be unique, but mulitple tumors may map
 * to the same patient.
 *
 * <p><b>File format.</b> The data file must contain a header line
 * (which is ignored) and every other line must contain the barcode
 * and patient key separated by a comma, tab, or pipe character.
 */
public final class TumorPatientTable {
    private final Map<TumorBarcode, PatientID> tumorMap;
    private final Multimap<PatientID, TumorBarcode> patientMap;

    private static TumorPatientTable global = null;

    private TumorPatientTable() {
        this.tumorMap = new HashMap<TumorBarcode, PatientID>();
        this.patientMap = TreeMultimap.create();
    }

    /**
     * Name of the system property that contains the full path name of
     * the file containing the global data table..
     */
    public static final String TABLE_FILE_PROPERTY = "jene.tcga.tumorPatientTable";

    /**
     * Returns the global data table.
     *
     * @return the global data table.
     *
     * @throws RuntimeException unless the system property with the
     * name given by {@code TABLE_FILE_PROPERTY} contains the name of
     * a file with a valid table.
     */
    public static TumorPatientTable global() {
        if (global == null)
            global = load(resolveFileName());

        return global;
    }

    private static String resolveFileName() {
        return JamProperties.getRequired(TABLE_FILE_PROPERTY);
    }

    /**
     * Loads a table from a data file.
     *
     * @param file the file to load.
     *
     * @return a table with the mappings specified in the input file.
     *
     * @throws RuntimeException unless the input file contains a valid
     * data table.
     */
    public static TumorPatientTable load(File file) {
        TumorPatientTable table = new TumorPatientTable();

        TableReader reader = openReader(file);
        table.load(reader);

        return table;
    }

    /**
     * Loads a table from a data file.
     *
     * @param fileName the name of the file to load.
     *
     * @return a table with the mappings specified in the input file.
     *
     * @throws RuntimeException unless the input file contains a valid
     * data table.
     */
    public static TumorPatientTable load(String fileName) {
        return load(new File(fileName));
    }

    private static TableReader openReader(File file) {
        TableReader reader = TableReader.open(file);

        if (reader.columnKeys().size() != 2)
            throw JamException.runtime("Invalid header in file: [%s].", file);

        return reader;
    }

    private void load(TableReader reader) {
        try {
            for (List<String> columns : reader)
                parseColumns(columns);
        }
        finally {
            reader.close();
        }

        JamLogger.info("TumorPatientTable: Loaded [%d] records.", size());
    }

    private void parseColumns(List<String> columns) {
        assert columns.size() == 2;

        TumorBarcode barcode   = TumorBarcode.instance(columns.get(0));
        PatientID    patientID = PatientID.instance(columns.get(1));

        patientMap.put(patientID, barcode);
        MapUtil.putUnique(tumorMap, barcode, patientID);
    }

    /**
     * Identifies patients in this table.
     *
     * @param patient the key for the patient of interest.
     *
     * @return {@code true} iff this table contains the specified
     * patient.
     */
    public boolean contains(PatientID patient) {
        return patientMap.containsKey(patient);
    }

    /**
     * Identifies tumors in this table.
     *
     * @param barcode a tumor barcode of interest.
     *
     * @return {@code true} iff this table contains the specified
     * tumor.
     */
    public boolean contains(TumorBarcode barcode) {
        return tumorMap.containsKey(barcode);
    }

    /**
     * Returns the tumors sampled from a given patient.
     *
     * @param patient the key for the patient of interest.
     *
     * @return the tumors sampled from the specified patient (or an
     * empty collection if this table does not contain the patient).
     */
    public Collection<TumorBarcode> lookup(PatientID patient) {
        return patientMap.get(patient);
    }

    /**
     * Returns the sampled patient for a given tumor.
     *
     * @param barcode a tumor barcode of interest.
     *
     * @return the sampled patient for the specified barcode 
     * ({@code null} if the barcode is not in this table).
     */
    public PatientID lookup(TumorBarcode barcode) {
        return tumorMap.get(barcode);
    }

    /**
     * Returns the sampled patient for a given tumor.
     *
     * @param barcode a tumor barcode of interest.
     *
     * @return the sampled patient for the specified barcode 
     * ({@code null} if the barcode is not in this table).
     *
     * @throws RuntimeException unless the barcode is present.
     */
    public PatientID require(TumorBarcode barcode) {
        PatientID patientID = lookup(barcode);

        if (patientID != null)
            return patientID;
        else
            throw JamException.runtime("No patient mapped to barcode [%s].", barcode.getKey());
    }

    /**
     * Returns the number of tumors in this table.
     *
     * @return the number of tumors in this table.
     */
    public int size() {
        return tumorMap.size();
    }

    /**
     * Returns a read-only view of the patients in this table.
     *
     * @return an unmodifiable set containing all patients in this
     * table.
     */
    public Set<PatientID> viewPatients() {
        return Collections.unmodifiableSet(patientMap.keySet());
    }

    /**
     * Returns a read-only view of the barcodes in this table.
     *
     * @return an unmodifiable set containing all barcodes in this
     * table.
     */
    public Set<TumorBarcode> viewBarcodes() {
        return Collections.unmodifiableSet(tumorMap.keySet());
    }
}
