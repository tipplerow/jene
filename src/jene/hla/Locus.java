
package jene.hla;

import jene.hugo.HugoSymbol;

/**
 * Enumerates the HLA allele locus.
 */
public enum Locus {
    A(MHCClass.I, HugoSymbol.instance("HLA-A")),
    B(MHCClass.I, HugoSymbol.instance("HLA-B")),
    C(MHCClass.I, HugoSymbol.instance("HLA-C"));

    private final MHCClass mhcClass;
    private final HugoSymbol hugoSymbol;

    private Locus(MHCClass mhcClass, HugoSymbol hugoSymbol) {
        this.mhcClass = mhcClass;
        this.hugoSymbol = hugoSymbol;
    }

    /**
     * Returns the HUGO symbol of the encoding gene.
     *
     * @return the HUGO symbol of the encoding gene.
     */
    public HugoSymbol getHugoSymbol() {
        return hugoSymbol;
    }

    /**
     * Returns the MHC restriction class for this locus.
     *
     * @return the MHC restriction class for this locus.
     */
    public MHCClass getMHCClass() {
        return mhcClass;
    }
}
