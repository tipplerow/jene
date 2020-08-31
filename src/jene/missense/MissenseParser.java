
package jene.missense;

import java.util.ArrayList;
import java.util.List;

import jam.app.JamLogger;
import jam.io.IOUtil;
import jam.io.TableReader;
import jam.lang.JamException;

import jene.ensembl.EnsemblTranscriptID;
import jene.hugo.HugoSymbol;
import jene.maf.MAFProperties;
import jene.peptide.ProteinChange;
import jene.tcga.CellFraction;
import jene.tcga.TumorBarcode;

/**
 * Reads MAF files and extracts the missense mutation records.
 */
public final class MissenseParser {
    private final String mafFile;
    private final List<MissenseRecord> records = new ArrayList<MissenseRecord>();

    private TableReader reader;

    private int tumorBarcodeIndex;
    private int hugoSymbolIndex;
    private int transcriptIndex;
    private int proteinChangeIndex;
    private int cellFractionIndex;

    private MissenseParser(String mafFile) {
        this.mafFile = mafFile;
    }

    /**
     * Reads a MAF file and extracts the missense mutations.
     *
     * @param mafFile the name of the MAF file to process.
     *
     * @return all missense mutation records in the specified MAF
     * file.
     *
     * @throws RuntimeException if any I/O or parsing errors occur.
     */
    public static List<MissenseRecord> parse(String mafFile) {
        MissenseParser parser = new MissenseParser(mafFile);
        return parser.parse();
    }

    private List<MissenseRecord> parse() {
        reader = TableReader.open(mafFile);

        try {
            tumorBarcodeIndex  = reader.requireColumn(MAFProperties.resolveTumorBarcodeColumnName());
            hugoSymbolIndex    = reader.requireColumn(MAFProperties.resolveHugoSymbolColumnName());
            proteinChangeIndex = reader.requireColumn(MAFProperties.resolveProteinChangeColumnName());

            // The transcript may be missing (as in the Liu et al. data)...
            transcriptIndex = reader.findColumn(MAFProperties.resolveTranscriptColumnName());

            // The cancer cell fraction may be missing (as in the TCGA data)...
            cellFractionIndex = reader.findColumn(MAFProperties.resolveCellFractionColumnName());

            while (reader.hasNext())
                processLine();
        }
        finally {
            IOUtil.close(reader);
        }

        return records;
    }

    private void processLine() {
        List<String> fields = reader.next();

        try {
            processLine(fields);
        }
        catch (Exception ex) {
            logException(fields, ex);
        }
    }

    private void processLine(List<String> fields) {
        HugoSymbol hugoSymbol = HugoSymbol.instance(fields.get(hugoSymbolIndex));
        TumorBarcode tumorBarcode = TumorBarcode.instance(fields.get(tumorBarcodeIndex));
        ProteinChange proteinChange = ProteinChange.parse(fields.get(proteinChangeIndex));

        CellFraction cellFraction = parseCellFraction(fields);
        EnsemblTranscriptID transcriptID = parseTranscriptID(fields);

        records.add(new MissenseRecord(tumorBarcode, transcriptID, hugoSymbol, proteinChange, cellFraction));
    }

    private CellFraction parseCellFraction(List<String> fields) {
        if (cellFractionIndex < 0)
            return CellFraction.UNIT;
        else
            return CellFraction.valueOf(fields.get(cellFractionIndex));
    }

    private EnsemblTranscriptID parseTranscriptID(List<String> fields) {
        if (transcriptIndex < 0)
            return null;
        else
            return EnsemblTranscriptID.instance(fields.get(transcriptIndex));
    }

    private void logException(List<String> fields, Exception ex1) {
        try {
            String tumorBarcode  = fields.get(tumorBarcodeIndex);
            String hugoSymbol    = fields.get(hugoSymbolIndex);
            String transcriptID  = fields.get(transcriptIndex);
            String proteinChange = fields.get(proteinChangeIndex);

            String message =
                String.format("Invalid annotation: [%s; %s; %s; %s]",
                              tumorBarcode, hugoSymbol, transcriptID, proteinChange);

            JamException.log(message, ex1);
            JamLogger.warn(message);
        }
        catch (Exception ex2) {
            JamLogger.warn("Exception logging failed: [%s; %s].", ex1, ex2);
        }
    }
}
