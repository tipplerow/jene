
package jene.hla;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Pattern;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import jam.app.JamLogger;
import jam.io.TableReader;
import jam.lang.JamException;
import jam.util.RegexUtil;

import jene.tcga.PatientID;

/**
 * Reads genotypes from a file and stores them in memory indexed by
 * patient ID.
 *
 * <p><b>File format.</b> The file must contain a header line with
 * columns labeled {@code Patient_ID} and {@code Genotype}.  The
 * genotype code should list each unique allele separated by white
 * space.
 *
 * <p>Commas are the stanard column delimiter, but tabs and pipe
 * characters ({@code |}) are also permitted.
 */
public final class PatientGenotypeTable {
    private final Map<PatientID, Genotype> genotypes;
    private final Multimap<Allele, PatientID> alleleMap = HashMultimap.create();

    private PatientGenotypeTable(Map<PatientID, Genotype> genotypes, boolean copy) {
        if (copy)
            this.genotypes = new TreeMap<PatientID, Genotype>(genotypes);
        else
            this.genotypes = genotypes;

        fillAlleleMap();
    }

    private void fillAlleleMap() {
        for (Map.Entry<PatientID, Genotype> entry : genotypes.entrySet()) {
            PatientID patient  = entry.getKey();
            Genotype  genotype = entry.getValue();

            for (Allele allele : genotype.viewUniqueAlleles())
                alleleMap.put(allele, patient);
        }
    }

    /**
     * Delimiter that separates alleles within the genotype column in
     * a database file.
     */
    public static final Pattern ALELE_DELIM = RegexUtil.MULTI_WHITE_SPACE;

    /**
     * Name of the column containing patient IDs.
     */
    public static final String PATIENT_COLUMN_NAME = "Patient_ID";

    /**
     * Name of the column containing the genotypes.
     */
    public static final String GENOTYPE_COLUMN_NAME = "Genotype";

    /**
     * Creates a new table with fixed genotypes.
     *
     * @param genotypes the genotypes indexed by patient ID.
     */
    public PatientGenotypeTable(Map<PatientID, Genotype> genotypes) {
        this(genotypes, true);
    }

    /**
     * Loads patient genotypes from a file in standard format.
     *
     * @param file the genotype file to read.
     *
     * @return a table containing the patient genotypes mapped to
     * their unique identifier.
     *
     * @throws RuntimeException unless the file name contains a valid
     * genotype file in standard format.
     */
    public static PatientGenotypeTable load(File file) {
        return Loader.load(file);
    }

    /**
     * Loads patient genotypes from a file in standard format.
     *
     * @param fileName the name of the genotype file to read.
     *
     * @return a table containing the patient genotypes mapped to
     * their unique identifier.
     *
     * @throws RuntimeException unless the file name contains a valid
     * genotype file in standard format.
     */
    public static PatientGenotypeTable load(String fileName) {
        return load(new File(fileName));
    }

    /**
     * Identifies patients with genotypes in this database.
     *
     * @param patient a patient ID of interest.
     *
     * @return {@code true} iff this database contains a genotype for
     * the specified patient.
     */
    public boolean contains(PatientID patient) {
        return genotypes.containsKey(patient);
    }

    /**
     * Returns the genotype for a given patient.
     *
     * @param patient a patient ID of interest.
     *
     * @return the genotype for the specified patient, or {@code null}
     * if the patient is not in this database.
     */
    public Genotype lookup(PatientID patient) {
        return genotypes.get(patient);
    }

    /**
     * Finds all patients whose genotype contains a given allele.
     *
     * @param allele the allele to match.
     *
     * @return a read-only view of the patients whose genotype
     * contains the target allele.
     */
    public Collection<PatientID> match(Allele allele) {
        return Collections.unmodifiableCollection(alleleMap.get(allele));
    }

    /**
     * Returns the genotype for a given patient.
     *
     * @param patient a patient ID of interest.
     *
     * @return the genotype for the specified patient.
     *
     * @throws RuntimeException unless this database contains the
     * specified patient.
     */
    public Genotype require(PatientID patient) {
        Genotype genotype = lookup(patient);

        if (genotype != null)
            return genotype;
        else
            throw JamException.runtime("No genotype for patient [%s].", patient.getKey());
    }

    /**
     * Returns the number of genotypes in this database.
     *
     * @return the number of genotypes in this database.
     */
    public int size() {
        return genotypes.size();
    }

    /**
     * Returns a read-only view of the unique HLA alleles in this
     * database.
     *
     * @return an unmodifiable set containing all unique HLA alleles
     * from the genotypes in this database.
     */
    public Set<Allele> viewAlleles() {
        Set<Allele> alleles = new TreeSet<Allele>();

        for (Genotype genotype : genotypes.values())
            alleles.addAll(genotype);

        return alleles;
    }

    /**
     * Returns a read-only view of the patients in this database.
     *
     * @return an unmodifiable set containing all patient IDs
     * from this database.
     */
    public Set<PatientID> viewPatients() {
        return Collections.unmodifiableSet(genotypes.keySet());
    }

    private static class Loader {
        private final File file;

        private TableReader reader;
        private Map<PatientID, Genotype> genotypes;

        private int patientIndex;
        private int genotypeIndex;

        private Loader(File file) {
            this.file = file;
        }

        private static PatientGenotypeTable load(File file) {
            Loader loader = new Loader(file);
            return loader.load();
        }
        
        private PatientGenotypeTable load() {
            reader = TableReader.open(file);
            genotypes = new TreeMap<PatientID, Genotype>();

            try {
                patientIndex = reader.requireColumn(PATIENT_COLUMN_NAME);
                genotypeIndex = reader.requireColumn(GENOTYPE_COLUMN_NAME);

                for (List<String> columns : reader)
                    parseColumns(columns);
            }
            finally {
                reader.close();
            }

            JamLogger.info("PatientGenotypeTable: Loaded [%d] genoypes.", genotypes.size());
            return new PatientGenotypeTable(genotypes, false);
        }

        private void parseColumns(List<String> columns) {
            PatientID patient  = PatientID.instance(columns.get(patientIndex));
            Genotype  genotype = Genotype.parse(columns.get(genotypeIndex), ALELE_DELIM);

            if (genotypes.containsKey(patient))
                throw JamException.runtime("Duplicate patient: [%s]", patient.getKey());

            genotypes.put(patient, genotype);
        }
    }
}
