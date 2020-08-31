
package jene.tcga;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jam.app.JamLogger;
import jam.app.JamProperties;
import jam.io.TableReader;
import jam.lang.JamException;

/**
 * Maps patients to their cancer types.
 *
 * <p><b>File format.</b> The data file must contain a header line
 * (which is ignored) and every other line must contain the patient
 * key and cancer type separated by a comma, tab, or pipe character.
 */
public final class PatientCancerTypeTable {
    private final Map<PatientID, CancerType> map;

    private static PatientCancerTypeTable global = null;

    private PatientCancerTypeTable() {
        this.map = new HashMap<PatientID, CancerType>();
    }

    /**
     * Name of the system property that contains the full path name of
     * the file containing the global data table.
     */
    public static final String TABLE_FILE_PROPERTY = "jene.tcga.patientCancerTypeTable";

    /**
     * Returns the global data table.
     *
     * @return the global data table.
     *
     * @throws RuntimeException unless the system property with the
     * name given {@code TABLE_FILE_PROPERTY} contains the name of
     * a file with a valid table.
     */
    public static PatientCancerTypeTable global() {
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
    public static PatientCancerTypeTable load(File file) {
        PatientCancerTypeTable table = new PatientCancerTypeTable();

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
    public static PatientCancerTypeTable load(String fileName) {
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

        JamLogger.info("PatientCancerTypeTable: Loaded [%d] records.", size());
    }

    private void parseColumns(List<String> columns) {
        assert columns.size() == 2;

        PatientID  patientID  = PatientID.instance(columns.get(0));
        CancerType cancerType = CancerType.valueOf(columns.get(1));

        if (map.containsKey(patientID))
            throw JamException.runtime("Duplicate key: [%s]", patientID.getKey());

        map.put(patientID, cancerType);
    }

    /**
     * Identifies patients in this table.
     *
     * @param patientID a patient key of interest.
     *
     * @return {@code true} iff this table contains the specified
     * patient.
     */
    public boolean contains(PatientID patientID) {
        return map.containsKey(patientID);
    }

    /**
     * Returns the cancer type for a given patient.
     *
     * @param patientID a patient key of interest.
     *
     * @return the cancer type for the specified patient
     * ({@code null} if the patient is not in this table).
     */
    public CancerType lookup(PatientID patientID) {
        return map.get(patientID);
    }

    /**
     * Returns the cancer type for a given patient.
     *
     * @param patientID a patient key of interest.
     *
     * @return the cancer type for the specified patient
     * ({@code null} if the patient is not in this table).
     *
     * @throws RuntimeException unless the patient is present.
     */
    public CancerType require(PatientID patientID) {
        CancerType cancerType = lookup(patientID);

        if (cancerType != null)
            return cancerType;
        else
            throw JamException.runtime("No cancer type for patient [%s].", patientID.getKey());
    }

    /**
     * Returns the number of patients in this table.
     *
     * @return the number of patients in this table.
     */
    public int size() {
        return map.size();
    }

    /**
     * Returns a read-only view of the patients in this table.
     *
     * @return an unmodifiable set containing all patients in this
     * table.
     */
    public Set<PatientID> viewPatients() {
        return Collections.unmodifiableSet(map.keySet());
    }
}
