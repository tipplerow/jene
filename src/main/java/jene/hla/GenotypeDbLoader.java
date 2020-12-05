
package jene.hla;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jam.app.JamLogger;
import jam.io.TableReader;
import jam.lang.JamException;

import jene.tcga.TumorBarcode;

final class GenotypeDbLoader {
    private final File file;

    private TableReader reader;
    private Map<TumorBarcode, Genotype> genotypes;

    private int barcodeIndex;
    private int genotypeIndex;

    private GenotypeDbLoader(File file) {
        this.file = file;
    }

    static GenotypeDb load(File file) {
        GenotypeDbLoader loader = new GenotypeDbLoader(file);
        return loader.load();
    }

    private GenotypeDb load() {
        reader = TableReader.open(file);
        genotypes = new HashMap<TumorBarcode, Genotype>();

        try {
            barcodeIndex = reader.requireColumn(GenotypeDb.BARCODE_COLUMN_NAME);
            genotypeIndex = reader.requireColumn(GenotypeDb.GENOTYPE_COLUMN_NAME);

            for (List<String> columns : reader)
                parseColumns(columns);
        }
        finally {
            reader.close();
        }

        JamLogger.info("GenotypeDbLoader: Loaded [%d] genoypes.", genotypes.size());
        return new GenotypeDb(genotypes);
    }

    private void parseColumns(List<String> columns) {
        TumorBarcode barcode  = TumorBarcode.instance(columns.get(barcodeIndex));
        Genotype     genotype = Genotype.parse(columns.get(genotypeIndex), GenotypeDb.ALLELE_ALELE_DELIM);

        if (genotypes.containsKey(barcode))
            throw JamException.runtime("Duplicate tumor barcode: [%s]", barcode.getKey());

        genotypes.put(barcode, genotype);
    }
}
