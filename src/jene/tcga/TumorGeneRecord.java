
package jene.tcga;

import java.util.Comparator;

import jene.hugo.HugoSymbol;

/**
 * Provides a base class for data records indexed by tumor sample and
 * HUGO gene symbol.
 */
public abstract class TumorGeneRecord {
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
    protected TumorGeneRecord(TumorBarcode tumorBarcode,
                              HugoSymbol   hugoSymbol) {
        this.tumorBarcode = tumorBarcode;
        this.hugoSymbol   = hugoSymbol;
    }

    /**
     * A comparator that orders records by tumor barcode.
     */
    public static Comparator<TumorGeneRecord> BARCODE_COMPARATOR =
        new Comparator<TumorGeneRecord>() {
            @Override public int compare(TumorGeneRecord rec1, TumorGeneRecord rec2) {
                return rec1.tumorBarcode.compareTo(rec2.tumorBarcode);
            }
        };

    /**
     * A comparator that orders records by HUGO symbol.
     */
    public static Comparator<TumorGeneRecord> SYMBOL_COMPARATOR =
        new Comparator<TumorGeneRecord>() {
            @Override public int compare(TumorGeneRecord rec1, TumorGeneRecord rec2) {
                return rec1.hugoSymbol.compareTo(rec2.hugoSymbol);
            }
        };

    /**
     * A comparator that orders records by tumor barcode first, HUGO
     * symbol second.
     */
    public static Comparator<TumorGeneRecord> BARCODE_SYMBOL_COMPARATOR =
        new Comparator<TumorGeneRecord>() {
            @Override public int compare(TumorGeneRecord rec1, TumorGeneRecord rec2) {
                int barcodeCmp = BARCODE_COMPARATOR.compare(rec1, rec2);

                if (barcodeCmp != 0)
                    return barcodeCmp;
                else
                    return SYMBOL_COMPARATOR.compare(rec1, rec2);
            }
        };

    /**
     * Returns a comparator that orders records by tumor barcode.
     *
     * @param <V> the runtime type of the concrete subclass.
     *
     * @return a comparator that orders records by tumor barcode.
     */
    public static <V extends TumorGeneRecord> Comparator<V> barcodeComparator() {
        return new Comparator<V>() {
            @Override public int compare(V rec1, V rec2) {
                return BARCODE_COMPARATOR.compare(rec1, rec2);
            }
        };
    }

    /**
     * Returns a comparator that orders records by HUGO symbol.
     *
     * @param <V> the runtime type of the concrete subclass.
     *
     * @return a comparator that orders records by HUGO symbol.
     */
    public static <V extends TumorGeneRecord> Comparator<V> symbolComparator() {
        return new Comparator<V>() {
            @Override public int compare(V rec1, V rec2) {
                return SYMBOL_COMPARATOR.compare(rec1, rec2);
            }
        };
    }

    /**
     * Returns a comparator that orders records by tumor barcode
     * first, HUGO symbol second.
     *
     * @param <V> the runtime type of the concrete subclass.
     *
     * @return a comparator that orders records by tumor barcode
     * first, HUGO symbol second.
     */
    public static <V extends TumorGeneRecord> Comparator<V> barcodeSymbolComparator() {
        return new Comparator<V>() {
            @Override public int compare(V rec1, V rec2) {
                return BARCODE_SYMBOL_COMPARATOR.compare(rec1, rec2);
            }
        };
    }

    /**
     * Returns the unique identifier for the tumor sample.
     *
     * @return the unique identifier for the tumor sample.
     */
    public TumorBarcode getTumorBarcode() {
        return tumorBarcode;
    }

    /**
     * Returns the unique HUGO symbol for the gene.
     *
     * @return the unique HUGO symbol for the gene.
     */
    public HugoSymbol getHugoSymbol() {
        return hugoSymbol;
    }
}
