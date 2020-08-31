
package jene.rna;

import java.util.List;

import jam.app.JamLogger;

import jene.tcga.TumorBarcode;

public final class ExpressionExtractor {
    private final ExpressionManager manager;
    private final List<TumorBarcode> barcodes;
    private final TumorExpressionMatrix matrix;

    private ExpressionExtractor(ExpressionManager manager,
                                List<TumorBarcode> barcodes,
                                TumorExpressionMatrix matrix) {
        this.matrix = matrix;
        this.manager = manager;
        this.barcodes = barcodes;
    }

    /**
     * Extracts individual tumor profiles from an RNA expression
     * matrix.
     *
     * @param matrixFile file containing the complete expression
     * matrix.
     *
     * @param barcodeFile file containing the tumor barcodes to
     * extract.
     *
     * @param extractDir destination for the individual profiles.
     */
    public static void extract(String matrixFile, String barcodeFile, String extractDir) {
        ExpressionManager manager = ExpressionManager.create(extractDir);
        List<TumorBarcode> barcodes = TumorBarcode.load(barcodeFile);
        TumorExpressionMatrix matrix = TumorExpressionMatrix.load(matrixFile);

        ExpressionExtractor extractor = new ExpressionExtractor(manager, barcodes, matrix);
        extractor.extract();
    }

    private void extract() {
        for (TumorBarcode barcode : barcodes) {
            ExpressionProfile profile = matrix.get(barcode);

            if (profile != null)
                manager.store(barcode, profile);
            else
                JamLogger.info("Missing expression data: [%s].", barcode.getKey());
        }
    }

    private static void usage() {
        System.err.println("Usage: jene.rna.ExpressionManager MATRIX_FILE BARCODE_FILE EXTRACT_DIR");
        System.exit(1);
    }

    public static void main(String[] args) {
        if (args.length != 3)
            usage();

        String matrixFile  = args[0];
        String barcodeFile = args[1];
        String extractDir  = args[2];

        extract(matrixFile, barcodeFile, extractDir);
    }
}
