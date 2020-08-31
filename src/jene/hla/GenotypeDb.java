
package jene.hla;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import jam.app.JamProperties;
import jam.lang.JamException;
import jam.util.RegexUtil;

import jene.tcga.TumorBarcode;

/**
 * Reads genotypes from a file and stores them in memory indexed by
 * tumor barcode.
 *
 * <p><b>File format.</b> The file must contain a header line with
 * columns labeled {@code Tumor_Barcode} and {@code Genotype}. The
 * genotype code should list each allele separated by white space
 * (duplicates may be present for homozygous alleles).
 *
 * <p>Commas are the stanard column delimiter, but tabs and pipe
 * characters ({@code |}) are also permitted.
 */
public final class GenotypeDb {
    private final Map<TumorBarcode, Genotype> genotypes;

    private static GenotypeDb global = null;

    /**
     * Name of the system property that specifies the file containing
     * the global genotype data.
     */
    public static final String FILE_NAME_PROPERTY = "jam.hla.genotypeDbFile";

    /**
     * Delimiter that separates alleles within the genotype column in
     * a database file.
     */
    public static final Pattern ALLELE_ALELE_DELIM = RegexUtil.MULTI_WHITE_SPACE;

    /**
     * Name of the column containing tumor barcodes.
     */
    public static final String BARCODE_COLUMN_NAME = "Tumor_Barcode";

    /**
     * Name of the column containing the genotypes.
     */
    public static final String GENOTYPE_COLUMN_NAME = "Genotype";

    /**
     * Creates a new genotype database with fixed genotypes.
     *
     * @param genotypes the genotypes indexed by tumor barcode.
     */
    public GenotypeDb(Map<TumorBarcode, Genotype> genotypes) {
        this.genotypes = new HashMap<TumorBarcode, Genotype>(genotypes);
    }

    /**
     * Returns the global genotype database defined via system
     * properties.
     *
     * @return the global genotype database defined via system
     * properties.
     */
    public static GenotypeDb global() {
        if (global == null)
            global = createGlobal();

        return global;
    }

    private static GenotypeDb createGlobal() {
        return load(resolveFileName());
    }

    private static String resolveFileName() {
        return JamProperties.getRequired(FILE_NAME_PROPERTY);
    }

    /**
     * Loads tumor genotypes from a file in standard format.
     *
     * @param file the genotype file to read.
     *
     * @return a genotype database containing the tumor genotypes
     * mapped to their unique identifier.
     *
     * @throws RuntimeException unless the file name contains a valid
     * genotype file in standard format.
     */
    public static GenotypeDb load(File file) {
        return GenotypeDbLoader.load(file);
    }

    /**
     * Loads tumor genotypes from a file in standard format.
     *
     * @param fileName the name of the genotype file to read.
     *
     * @return a genotype database containing the tumor genotypes
     * mapped to their unique identifier.
     *
     * @throws RuntimeException unless the file name contains a valid
     * genotype file in standard format.
     */
    public static GenotypeDb load(String fileName) {
        return load(new File(fileName));
    }

    /**
     * Identifies tumors with genotypes in this database.
     *
     * @param barcode a tumor barcode of interest.
     *
     * @return {@code true} iff this database contains a genotype for
     * the specified tumor.
     */
    public boolean contains(TumorBarcode barcode) {
        return genotypes.containsKey(barcode);
    }

    /**
     * Returns the genotype for a given tumor.
     *
     * @param barcode a tumor barcode of interest.
     *
     * @return the genotype for the specified tumor, or {@code null}
     * if the tumor is not in this database.
     */
    public Genotype lookup(TumorBarcode barcode) {
        return genotypes.get(barcode);
    }

    /**
     * Returns the genotype for a given tumor.
     *
     * @param barcode a tumor barcode of interest.
     *
     * @return the genotype for the specified tumor.
     *
     * @throws RuntimeException unless this database contains the
     * specified tumor.
     */
    public Genotype require(TumorBarcode barcode) {
        Genotype genotype = lookup(barcode);

        if (genotype != null)
            return genotype;
        else
            throw JamException.runtime("No genotype for tumor [%s].", barcode.getKey());
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
     * Returns a read-only view of the tumors in this database.
     *
     * @return an unmodifiable set containing all tumor barcodes
     * from this database.
     */
    public Set<TumorBarcode> viewBarcodes() {
        return Collections.unmodifiableSet(genotypes.keySet());
    }
}
