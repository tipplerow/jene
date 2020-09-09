
package jene.tcga;

import java.util.Comparator;

import jene.hugo.HugoSymbol;

/**
 * Provides a base class for data records indexed by tumor sample and
 * HUGO gene symbol.
 */
public abstract class TumorGeneRecordBase implements TumorGeneRecord {
    /**
     * The unique identifier for the tumor sample.
     */
    protected final TumorBarcode tumorBarcode;

    /**
     * The unique HUGO symbol for the gene.
     */
    protected final HugoSymbol hugoSymbol;

    /**
     * Creates a data record with fixed tumor and gene identifiers.
     *
     * @param tumorBarcode the unique identifier for the tumor sample.
     *
     * @param hugoSymbol the unique HUGO symbol for the gene.
     */
    protected TumorGeneRecordBase(TumorBarcode tumorBarcode,
                                  HugoSymbol   hugoSymbol) {
        this.tumorBarcode = tumorBarcode;
        this.hugoSymbol   = hugoSymbol;
    }

    /**
     * Returns the unique identifier for the tumor sample.
     *
     * @return the unique identifier for the tumor sample.
     */
    @Override public TumorBarcode getTumorBarcode() {
        return tumorBarcode;
    }

    /**
     * Returns the unique HUGO symbol for the gene.
     *
     * @return the unique HUGO symbol for the gene.
     */
    @Override public HugoSymbol getHugoSymbol() {
        return hugoSymbol;
    }
}
