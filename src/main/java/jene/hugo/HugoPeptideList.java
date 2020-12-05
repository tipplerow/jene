
package jene.hugo;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jene.peptide.Peptide;

/**
 * Associates a HUGO symbol with an immutable list of peptides
 * (e.g., the fragments produced by proteasomal cleavage of the
 * protein encoded by the gene).
 */
public final class HugoPeptideList extends AbstractList<Peptide> {
    private final HugoSymbol symbol;
    private final List<Peptide> peptides;

    private HugoPeptideList(HugoSymbol symbol, List<Peptide> peptides, boolean copy) {
        this.symbol = symbol;

        if (copy)
            this.peptides = new ArrayList<Peptide>(peptides);
        else
            this.peptides = peptides;
    }

    /**
     * Associates a HUGO symbol with a list of peptides.
     *
     * @param symbol the HUGO symbol.
     *
     * @param peptides peptides to associate with the HUGO symbol.
     *
     * @return a new {@code HugoPeptideList}, which maintains a
     * reference to the input list; subsequent changes to the input
     * list will be reflected in the new {@code HugoPeptideList}.
     */
    public static HugoPeptideList wrap(HugoSymbol symbol, List<Peptide> peptides) {
        return new HugoPeptideList(symbol, peptides, false);
    }

    /**
     * Returns the HUGO symbol that is associated with the peptides in
     * this list.
     *
     * @return the HUGO symbol that is associated with the peptides in
     * this list.
     */
    public HugoSymbol getSymbol() {
        return symbol;
    }

    @Override public Peptide get(int index) {
        return peptides.get(index);
    }

    @Override public int size() {
        return peptides.size();
    }
}
