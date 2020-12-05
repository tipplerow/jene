
package jene.peptide;

import java.util.List;

import com.google.common.collect.Multiset;

import org.junit.*;
import static org.junit.Assert.*;

public class PeptideEnumeratorTest {
    @Test public void testEnumerate() {
        assertEquals(     20, PeptideEnumerator.enumerationSize(1));
        assertEquals(    400, PeptideEnumerator.enumerationSize(2));
        assertEquals(   8000, PeptideEnumerator.enumerationSize(3));
        assertEquals( 160000, PeptideEnumerator.enumerationSize(4));
        assertEquals(3200000, PeptideEnumerator.enumerationSize(5));

        for (int length = 1; length <= 4; ++length) {
            List<Peptide> peptides = PeptideEnumerator.enumerate(length);
            assertEquals((int) Math.pow(20, length), peptides.size());
        }
    }

    @Test public void testEnumerateUnordered() {
        assertEquals( 210, PeptideEnumerator.enumerateUnordered(2).size());
        assertEquals(1540, PeptideEnumerator.enumerateUnordered(3).size());
        assertEquals(8855, PeptideEnumerator.enumerateUnordered(4).size());
    }

    @Test public void testUnordered() {
        Peptide pep = Peptide.of(Residue.His,
                                 Residue.Cys,
                                 Residue.Gln,
                                 Residue.Gln,
                                 Residue.Cys,
                                 Residue.Gln,
                                 Residue.Ala,
                                 Residue.Lys);

        Multiset<Residue> counts = PeptideEnumerator.unordered(pep);

        assertEquals(counts.size(), pep.length());
        assertEquals(1, counts.count(Residue.Ala));
        assertEquals(1, counts.count(Residue.His));
        assertEquals(1, counts.count(Residue.Lys));
        assertEquals(2, counts.count(Residue.Cys));
        assertEquals(3, counts.count(Residue.Gln));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jene.peptide.PeptideEnumeratorTest");
    }
}
