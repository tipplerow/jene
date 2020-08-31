
package jene.tcga;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import jam.lang.JamException;

import jene.hla.Genotype;
import jene.hla.PatientGenotypeTable;

/**
 * Maps tumor barcodes to the genotype of the sampled patient.
 */
public final class TumorGenotypeTable {
    private final Map<TumorBarcode, Genotype> map;

    private TumorGenotypeTable(Map<TumorBarcode, Genotype> map) {
        this.map = map;
    }

    /**
     * Creates a tumor-genotype table by loading and merging a
     * tumor-patient table and a patient-genotype table.
     *
     * @param tumorPatientFile the file containing the mapping from
     * tumor samples to patients.
     *
     * @param patientGenotypeFile the file containing the patient
     * genotypes.
     *
     * @return a table mapping tumor barcodes to patient genotypes.
     *
     * @throws RuntimeException unless the input files contain valid
     * data tables.
     */
    public static TumorGenotypeTable load(File tumorPatientFile,
                                          File patientGenotypeFile) {
        TumorPatientTable tumorPatientTable =
            TumorPatientTable.load(tumorPatientFile);

        PatientGenotypeTable patientGenotypeTable =
            PatientGenotypeTable.load(patientGenotypeFile);

        return merge(tumorPatientTable, patientGenotypeTable);
    }

    /**
     * Creates a tumor-genotype table by loading and merging a
     * tumor-patient table and a patient-genotype table.
     *
     * @param tumorPatientFile the file containing the mapping from
     * tumor samples to patients.
     *
     * @param patientGenotypeFile the file containing the patient
     * genotypes.
     *
     * @return a table mapping tumor barcodes to patient genotypes.
     *
     * @throws RuntimeException unless the input files contain valid
     * data tables.
     */
    public static TumorGenotypeTable load(String tumorPatientFile,
                                          String patientGenotypeFile) {
        return load(new File(tumorPatientFile),
                    new File(patientGenotypeFile));
    }

    /**
     * Creates a tumor-genotype table by merging a tumor-patient table
     * and a patient-genotype table.
     *
     * @param tumorPatientTable the mapping from tumor samples to
     * patients.
     *
     * @param patientGenotypeTable the mapping from patients to
     * genotypes.
     *
     * @return a table mapping tumor barcodes to patient genotypes.
     */
    public static TumorGenotypeTable merge(TumorPatientTable tumorPatientTable,
                                           PatientGenotypeTable patientGenotypeTable) {
        Map<TumorBarcode, Genotype> merged =
            new HashMap<TumorBarcode, Genotype>(tumorPatientTable.size());

        for (TumorBarcode barcode : tumorPatientTable.viewBarcodes()) {
            PatientID patient  = tumorPatientTable.require(barcode);
            Genotype  genotype = patientGenotypeTable.lookup(patient);

            if (genotype != null)
                merged.put(barcode, genotype);
        }

        return new TumorGenotypeTable(merged);
    }

    /**
     * Identifies tumor samples in this table.
     *
     * @param barcode a tumor barcode of interest.
     *
     * @return {@code true} iff this table contains the specified
     * tumor sample.
     */
    public boolean contains(TumorBarcode barcode) {
        return map.containsKey(barcode);
    }

    /**
     * Returns the patient genotype for a given tumor.
     *
     * @param barcode a tumor barcode of interest.
     *
     * @return the genotype of the sampled patient ({@code null} if
     * the barcode is not in this table).
     */
    public Genotype lookup(TumorBarcode barcode) {
        return map.get(barcode);
    }

    /**
     * Returns the patient genotype for a given tumor.
     *
     * @param barcode a tumor barcode of interest.
     *
     * @return the genotype of the sampled patient.
     *
     * @throws RuntimeException unless the barcode is present.
     */
    public Genotype require(TumorBarcode barcode) {
        Genotype genotype = lookup(barcode);

        if (genotype != null)
            return genotype;
        else
            throw JamException.runtime("No genotype mapped to barcode [%s].", barcode.getKey());
    }

    /**
     * Returns the number of tumors in this table.
     *
     * @return the number of tumors in this table.
     */
    public int size() {
        return map.size();
    }

    /**
     * Returns a read-only view of the barcodes in this table.
     *
     * @return an unmodifiable set containing all barcodes in this
     * table.
     */
    public Set<TumorBarcode> viewBarcodes() {
        return Collections.unmodifiableSet(map.keySet());
    }
}
