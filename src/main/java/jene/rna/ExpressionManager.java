
package jene.rna;

import java.io.File;

import jam.lang.JamException;

import jene.tcga.TumorBarcode;

/**
 * Manages persistent RNA expression profile data.
 */
public final class ExpressionManager {
    private final String dirName;

    private ExpressionManager(String dirName) {
        this.dirName = dirName;
    }

    private static final String BASE_SUFFIX = "_expression_profile.csv.gz";

    /**
     * Creates a new expression data manager.
     *
     * @param dirName the directory containing individual tumor
     * expression profiles.
     *
     * @return a new expression data manager for the specified
     * directory.
     */
    public static ExpressionManager create(String dirName) {
        return new ExpressionManager(dirName);
    }

    /**
     * Returns the full path name of the expression file for a given
     * tumor sample.
     *
     * @param barcode the barcode of the tumor sample.
     *
     * @return the full path name of the expression file for a given
     * tumor sample.
     */
    public File expressionFile(TumorBarcode barcode) {
        return new File(dirName, baseName(barcode));
    }

    private static String baseName(TumorBarcode barcode) {
        return barcode.getKey() + BASE_SUFFIX;
    }

    /**
     * Determines whether the expression profile for a given tumor
     * sample exists.
     *
     * @param barcode the barcode of the tumor sample.
     *
     * @return {@code true} iff the expression profile for the
     * specified tumor sample exists in the data directory.
     */
    public boolean exists(TumorBarcode barcode) {
        return expressionFile(barcode).exists();
    }

    /**
     * Loads the expression profile for a given tumor sample.
     *
     * @param barcode the barcode of the desired tumor sample.
     *
     * @return the expression profile for the specified tumor sample
     * ({@code null} if the profile does not exist).
     */
    public ExpressionProfile load(TumorBarcode barcode) {
        File file = expressionFile(barcode);

        if (file.canRead())
            return ExpressionProfile.load(file);
        else
            return null;
    }

    /**
     * Loads the expression profile for a given tumor sample.
     *
     * @param barcode the barcode of the desired tumor sample.
     *
     * @return the expression profile for the specified tumor sample.
     *
     * @throws RuntimeException if the profile does not exist.
     */
    public ExpressionProfile require(TumorBarcode barcode) {
        ExpressionProfile profile = load(barcode);

        if (profile == null)
            throw JamException.runtime("No expression profile for barcode [%s].", barcode.getKey());

        return profile;
    }

    /**
     * Stores the expression profile for a given tumor sample.
     *
     * @param barcode the barcode for the tumor sample.
     *
     * @param profile the expression profile for the tumor sample.
     */
    public void store(TumorBarcode barcode, ExpressionProfile profile) {
        profile.store(expressionFile(barcode));
    }
}
