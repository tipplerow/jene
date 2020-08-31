
package jene.junit;

import java.util.ArrayList;
import java.util.List;

import jene.hugo.HugoSymbol;
import jene.hugo.HugoPeptideList;
import jene.peptide.Peptide;

import org.junit.*;
import static org.junit.Assert.*;

public class HugoPeptideListTest {
    private static final HugoSymbol hugo = HugoSymbol.instance("A1BG");

    private static final Peptide pep0 = Peptide.instance("AAPPPPVLM");
    private static final Peptide pep1 = Peptide.instance("ADSANYSCV");
    private static final Peptide pep2 = Peptide.instance("YWSLLTSLV");

    @Test(expected = UnsupportedOperationException.class)
    public void testAdd() {
        HugoPeptideList.wrap(hugo, List.of(pep0, pep1)).add(pep2);
    }

    @Test public void testBasic() {
        HugoPeptideList list = HugoPeptideList.wrap(hugo, List.of(pep0, pep1, pep2));

        assertEquals(3, list.size());

        assertEquals(hugo, list.getSymbol());
        assertEquals(pep0, list.get(0));
        assertEquals(pep1, list.get(1));
        assertEquals(pep2, list.get(2));

        assertEquals(List.of(pep0, pep1, pep2), list);
    }

    @Test public void testModifyUnderlying() {
        List<Peptide> peptides = new ArrayList<Peptide>();

        peptides.add(pep0);
        peptides.add(pep1);
        
        HugoPeptideList list = HugoPeptideList.wrap(hugo, peptides);

        assertEquals(2, list.size());
        assertEquals(List.of(pep0, pep1), list);

        peptides.add(pep2);

        assertEquals(3, list.size());
        assertEquals(List.of(pep0, pep1, pep2), list);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testRemove() {
        HugoPeptideList.wrap(hugo, List.of(pep0, pep1)).remove(pep0);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testSet() {
        HugoPeptideList.wrap(hugo, List.of(pep0, pep1, pep2)).set(1, pep0);
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jene.junit.HugoPeptideListTest");
    }
}
