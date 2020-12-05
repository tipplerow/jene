
package jene.ensembl;

import jene.hugo.HugoSymbol;

/**
 * Represents the Ensembl gene symbol (HUGO) identifier.
 */
public final class EnsemblGeneSymbol extends EnsemblID {
    private static final String LABEL_CODE = "gene_symbol:";

    private EnsemblGeneSymbol(String key) {
        super(key, false);
    }

    /**
     * Extracts the HUGO symbol from an Ensembl record header line.
     *
     * @param headerLine the header line from an Ensembl record.
     *
     * @return the HUGO symbol contained in the given header line (or
     * {@code null} if the gene symbol is missing).
     */
    public static HugoSymbol parseHeader(String headerLine) {
        if (headerLine.contains(LABEL_CODE))
            return HugoSymbol.instance(parseHeader(headerLine, LABEL_CODE));
        else
            return null;
    }
}
