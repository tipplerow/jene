
package jene.ensembl;

import java.io.File;
import java.io.FileFilter;

import jam.app.JamEnv;
import jam.app.JamProperties;
import jam.lang.JamException;

/**
 * Resolves path names to Ensembl data files.
 */
public final class EnsemblLocator {
    /**
     * Environment variable that defines the absolute path name for
     * the directory that contains Ensembl chromosome FASTA files.
     * If the system property {@code jene.ensembl.genomeDir} is also
     * defined, it will override the environment variable.
     */
    public static final String GENOME_DIR_ENV = "JENE_ENSEMBL_GENOME_DIR";

    /**
     * System property that defines the absolute path name for the
     * directory that contains Ensembl chromosome FASTA files.  If
     * not defined, environment variable {@code GENOME_DIR_ENV} will
     * be used by default.
     */
    public static final String GENOME_DIR_PROPERTY = "jene.ensembl.genomeDir";

    /**
     * Environment variable that defines the name of the Ensembl proteome
     * file.  If the system property {@code jene.ensembl.primaryFile} is
     * also defined, it will override the environment variable.
     */
    public static final String PROTEOME_FILE_ENV = "JENE_ENSEMBL_PROTEOME_FILE";

    /**
     * System property that defines the absolute path name for the
     * Ensembl proteome file.  If the property is not defined, the
     * environment variable {@code PROTEOME_FILE_ENV} will be used.
     */
    public static final String PROTEOME_FILE_PROPERTY = "jene.ensembl.proteomeFile";

    /**
     * Environment variable that defines the name of the secondary
     * Ensembl proteome file (an older version which can serve as a
     * backstop when mapping to third-party data.  If the system
     * property {@code jene.ensembl.secondaryProteome} is also
     * defined, it will override the environment variable.
     */
    public static final String SECONDARY_PROTEOME_ENV = "JENE_ENSEMBL_SECONDARY_PROTEOME";

    /**
     * System property that defines the name of the secondary Ensembl
     * proteome file.  If the property is not defined, the environment
     * variable {@code SECONDARY_PROTEOME_ENV} will be used.
     */
    public static final String SECONDARY_PROTEOME_PROPERTY = "jene.ensembl.secondaryProteome";

    /**
     * Returns the file that contains the nucleotide sequence for a
     * specific human chromosome.
     *
     * @param code the code (number or letter) of the chromosome to
     * retrieve.
     *
     * @return the file that contains the nucleotide sequence for the
     * specified human chromosome.
     *
     * @throws RuntimeException unless a FASTA file for the specified
     * chromosome exists in the genome directory.
     */
    public static File resolveChromosomeFile(String code) {
        File[] files = resolveGenomeDir().listFiles(chromosomeFileFilter(code));

        if (files.length == 1)
            return files[0];
        else
            throw JamException.runtime("Could not find nucleotide sequence for chromosome [%s].", code);
    }

    private static FileFilter chromosomeFileFilter(String code) {
        return new FileFilter() {
            @Override public boolean accept(File file) {
                String baseName = file.getName();
                String speciesFragment = "Homo_sapiens.GRCh";
                String chromosomeFragment = ".dna.chromosome." + code + ".fa";

                return baseName.startsWith(speciesFragment)
                    && baseName.contains(chromosomeFragment);
            }
        };
    }

    /**
     * Returns the directory that contains the Ensembl chromosome
     * FASTA files.
     *
     * @return the directory that contains the Ensembl chromosome
     * FASTA files.
     */
    public static File resolveGenomeDir() {
        return new File(resolveGenomeDirName());
    }

    /**
     * Returns the name of the directory that contains the Ensembl
     * chromosome FASTA files.
     *
     * @return the name of the directory that contains the Ensembl
     * chromosome FASTA files.
     */
    public static String resolveGenomeDirName() {
        if (JamProperties.isSet(GENOME_DIR_PROPERTY))
            return JamProperties.getRequired(GENOME_DIR_PROPERTY);
        else
            return JamEnv.getRequired(GENOME_DIR_ENV);
    }

    /**
     * Returns the file that contains the primary Ensembl proteome.
     *
     * @return the file that contains the primary Ensembl proteome.
     */
    public static File resolvePrimaryProteomeFile() {
        return new File(resolvePrimaryProteomeFileName());
    }

    /**
     * Returns the name of the file that contains the primary Ensembl
     * proteome.
     *
     * @return the name of the file that contains the primary Ensembl
     * proteome.
     */
    public static String resolvePrimaryProteomeFileName() {
        if (JamProperties.isSet(PROTEOME_FILE_PROPERTY))
            return JamProperties.getRequired(PROTEOME_FILE_PROPERTY);
        else
            return JamEnv.getRequired(PROTEOME_FILE_ENV);
    }

    /**
     * Returns the file that contains the secondary Ensembl proteome.
     *
     * @return the file that contains the secondary Ensembl proteome
     * ({@code null} if none has been specified).
     */
    public static File resolveSecondaryProteomeFile() {
        String fileName = resolveSecondaryProteomeFileName();

        if (fileName != null)
            return new File(fileName);
        else
            return null;
    }

    /**
     * Returns the name of the file that contains the secondary
     * Ensembl proteome.
     *
     * @return the name of the file that contains the secondary
     * Ensembl proteome ({@code null} if none has been specified).
     */
    public static String resolveSecondaryProteomeFileName() {
        if (JamProperties.isSet(SECONDARY_PROTEOME_PROPERTY))
            return JamProperties.getRequired(SECONDARY_PROTEOME_PROPERTY);
        else if (JamEnv.isSet(SECONDARY_PROTEOME_ENV))
            return JamEnv.getRequired(SECONDARY_PROTEOME_ENV);
        else
            return null;
    }
}
