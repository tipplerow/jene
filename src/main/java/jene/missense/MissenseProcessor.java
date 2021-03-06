
package jene.missense;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import jam.app.JamApp;
import jam.app.JamLogger;
import jam.lang.JamException;

import jene.ensembl.EnsemblProteinDb;
import jene.hugo.HugoMaster;
import jene.hugo.HugoSymbol;
import jene.maf.MAFFastaRecord;
import jene.tcga.CellFraction;
import jene.tcga.TumorBarcode;

/**
 * Processes MAF files and generates the protein structures generated
 * by missense mutations.
 */
public final class MissenseProcessor extends JamApp {
    private final String mafFile;
    private final String barcodeFile;
    private final String missenseDir;
    private final CellFraction ccfThreshold;

    private int processed = 0;
    private MissenseTable table;
    private MissenseManager manager;
    private List<TumorBarcode> barcodes;

    private final HugoMaster hugoMaster = HugoMaster.global();
    private final EnsemblProteinDb ensemblDb = EnsemblProteinDb.reference();

    private MissenseProcessor(String mafFile, String barcodeFile, String missenseDir, CellFraction ccfThreshold) {
        this.mafFile = mafFile;
        this.barcodeFile = barcodeFile;
        this.missenseDir = missenseDir;
        this.ccfThreshold = ccfThreshold;
    }

    /**
     * Processes a MAF file and generates the protein structures
     * generated by missense mutations.
     *
     * @param mafFile the name of the input MAF file (containing the
     * missense mutations).
     *
     * @param barcodeFile the name of the file containing tumor sample
     * barcodes to process.
     *
     * @param missenseDir the directory where the missense FASTA files
     * will be written.
     *
     * @param ccfThreshold the minimum cancer cell fraction required
     * to process a mutation.
     *
     * @throws RuntimeException if any errors occur.
     */
    public static void run(String mafFile, String barcodeFile, String missenseDir, CellFraction ccfThreshold) {
        System.setProperty(JamApp.REPORT_DIR_PROPERTY, missenseDir);

        MissenseProcessor processor = new MissenseProcessor(mafFile, barcodeFile, missenseDir, ccfThreshold);
        processor.run();
    }

    private void run() {
        table = MissenseTable.load(mafFile);
        manager = MissenseManager.create(missenseDir);
        barcodes = TumorBarcode.load(barcodeFile);

        writeRuntimeEnv("JAM_", "JENE_");
        writeRuntimeProperties("jam.", "jene.");

        processBarcodes();
        JamLogger.info("DONE!");
    }

    private void processBarcodes() {
        barcodes.parallelStream().forEach(barcode -> processBarcode(barcode));
    }

    private void processBarcode(TumorBarcode barcode) {
        ++processed;
        JamLogger.info("Processing barcode [%s] (%d of %d)...", barcode.getKey(), processed, barcodes.size());

        Set<HugoSymbol> hugoSymbols = table.viewSymbols(barcode);
        List<MAFFastaRecord> fastaRecords = new ArrayList<MAFFastaRecord>();

        for (HugoSymbol symbol : hugoSymbols) {
            MAFFastaRecord fastaRecord = processGene(barcode, symbol);

            if (fastaRecord != null)
                fastaRecords.add(fastaRecord);
        }

        if (!fastaRecords.isEmpty())
            manager.store(barcode, fastaRecords);
    }

    private MAFFastaRecord processGene(TumorBarcode barcode, HugoSymbol symbol) {
        MissenseEngine engine =
            new MissenseEngine(barcode, symbol, ccfThreshold, table, hugoMaster, ensemblDb);

        try {
            return engine.process();
        }
        catch (Exception ex) {
            String message =
                String.format("Error creating FASTA record [%s:%s]: %s",
                              barcode.getKey(), symbol.getKey(), ex.getMessage());
            
            JamLogger.warn(message);
            JamException.log(message);

            return null;
        }
    }

    private static void usage() {
        System.err.println("Usage: jam.missense.MissenseProcessor MAF_FILE BARCODE_FILE MISSENSE_DIR CCF_THRESHOLD");
        System.exit(1);
    }

    public static void main(String[] args) {
        if (args.length != 4)
            usage();

        String mafFile = args[0];
        String barcodeFile = args[1];
        String missenseDir = args[2];
        CellFraction ccfThreshold = CellFraction.valueOf(args[3]);

        run(mafFile, barcodeFile, missenseDir, ccfThreshold);
    }
}
