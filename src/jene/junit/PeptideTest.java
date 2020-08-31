
package jene.junit;

import java.util.List;

import jam.math.IntRange;

import jene.peptide.Peptide;
import jene.peptide.ProteinChange;
import jene.peptide.Residue;

import org.junit.*;
import static org.junit.Assert.*;

public class PeptideTest {
    @Test public void testAppend() {
        Peptide p1 = Peptide.of(Residue.Ala);
        Peptide p2 = p1.append(Residue.Cys);
        Peptide p3 = p2.append(Residue.Leu);

        assertEquals(1, p1.length());
        assertEquals(2, p2.length());
        assertEquals(3, p3.length());

        assertEquals(Residue.Ala, p1.get(0));

        assertEquals(Residue.Ala, p2.get(0));
        assertEquals(Residue.Cys, p2.get(1));

        assertEquals(Residue.Ala, p3.get(0));
        assertEquals(Residue.Cys, p3.get(1));
        assertEquals(Residue.Leu, p3.get(2));
    }

    @Test public void testBasic() {
        Peptide pep = Peptide.of(Residue.Ala, Residue.Cys, Residue.Leu);

        assertEquals(3, pep.length());

        assertEquals(Residue.Ala, pep.get(0));
        assertEquals(Residue.Cys, pep.get(1));
        assertEquals(Residue.Leu, pep.get(2));
    }

    @Test public void testEquals() {
        Peptide p1 = Peptide.of(Residue.His, Residue.Gln);
        Peptide p2 = Peptide.of(Residue.His, Residue.Gln);
        Peptide p3 = Peptide.of(Residue.Gln, Residue.His);
        Peptide p4 = Peptide.of(Residue.His, Residue.Gln, Residue.Gln);

        assertTrue(p1.equals(p1));
        assertTrue(p1.equals(p2));
        assertFalse(p1.equals(p3));
        assertFalse(p1.equals(p4));
    }

    @Test public void testFragment() {
        Peptide full = Peptide.of(Residue.Ala, Residue.Cys, Residue.Leu, Residue.Phe, Residue.Arg);
        Peptide frag = full.fragment(IntRange.instance(2, 3));

        assertEquals(2, frag.length());
        assertEquals(Residue.Leu, frag.get(0));
        assertEquals(Residue.Phe, frag.get(1));
    }

    @Test(expected = RuntimeException.class)
    public void testFragmentInvalid() {
        Peptide full = Peptide.of(Residue.Ala, Residue.Cys, Residue.Leu, Residue.Phe, Residue.Arg);
        Peptide frag = full.fragment(IntRange.instance(2, 8));
    }

    @Test public void testHashCode() {
        Peptide p1 = Peptide.of(Residue.His, Residue.Gln);
        Peptide p2 = Peptide.of(Residue.His, Residue.Gln);
        Peptide p3 = Peptide.of(Residue.Gln, Residue.His);
        Peptide p4 = Peptide.of(Residue.His, Residue.Gln, Residue.Gln);

        assertTrue(p1.hashCode() == p1.hashCode());
        assertTrue(p1.hashCode() == p2.hashCode());
        assertTrue(p1.hashCode() != p3.hashCode());
        assertTrue(p1.hashCode() != p4.hashCode());

        Peptide p5 = Peptide.instance("QVSRDQVLD");
        Peptide p6 = Peptide.instance("QVSRDQVLD");
        Peptide p7 = Peptide.instance("QVSREQYLE");
        Peptide p8 = Peptide.instance("QVSREQYLE");

        assertTrue(p5.hashCode() == p6.hashCode());
        assertTrue(p7.hashCode() == p8.hashCode());
        assertTrue(p5.hashCode() != p7.hashCode());
    }

    @Test public void testInstance() {
        Peptide pep = Peptide.instance("ACHK");

        assertEquals(4, pep.length());
        assertEquals(Residue.Ala, pep.get(0));
        assertEquals(Residue.Cys, pep.get(1));
        assertEquals(Residue.His, pep.get(2));
        assertEquals(Residue.Lys, pep.get(3));

        Peptide p1 = Peptide.instance("ALVP");
        Peptide p2 = Peptide.instance("ALVP");

        // "p2" should be the same physical object...
        assertTrue(p1 == p2);
    }

    @Test public void testLoad() {
        List<Peptide> peptides = Peptide.load("data/test/peptide_flat.txt");

        assertEquals(10, peptides.size());
        assertEquals(Peptide.instance("QVSRDQVLD"), peptides.get(0));
        assertEquals(Peptide.instance("QVSREQYLE"), peptides.get(9));
    }

    @Test public void testMutate() {
        ProteinChange change = ProteinChange.parse("K3A");

        Peptide native_ = Peptide.instance("MPKLNSTF");
        Peptide mutated = native_.mutate(change);
        
        assertEquals(Peptide.instance("MPALNSTF"), mutated);
    }

    @Test(expected = RuntimeException.class)
    public void testMutateInvalid1() {
        ProteinChange change = ProteinChange.parse("K0A");

        Peptide native_ = Peptide.instance("MPKLNSTF");
        Peptide mutated = native_.mutate(change);
    }

    @Test(expected = RuntimeException.class)
    public void testMutateInvalid2() {
        ProteinChange change = ProteinChange.parse("K100A");

        Peptide native_ = Peptide.instance("MPKLNSTF");
        Peptide mutated = native_.mutate(change);
    }

    @Test(expected = RuntimeException.class)
    public void testMutateInvalid3() {
        ProteinChange change = ProteinChange.parse("M3A");

        Peptide native_ = Peptide.instance("MPKLNSTF");
        Peptide mutated = native_.mutate(change);
    }

    @Test public void testNativeFragments() {
        Peptide parent = Peptide.instance("MPKLNSTFVTEFLFEG");

        assertEquals(List.of(), parent.nativeFragments(100));
        assertEquals(List.of(parent), parent.nativeFragments(parent.length()));
        assertEquals(List.of(Peptide.instance("MPKLNSTFV"),
                             Peptide.instance("PKLNSTFVT"),
                             Peptide.instance("KLNSTFVTE"),
                             Peptide.instance("LNSTFVTEF"),
                             Peptide.instance("NSTFVTEFL"),
                             Peptide.instance("STFVTEFLF"),
                             Peptide.instance("TFVTEFLFE"),
                             Peptide.instance("FVTEFLFEG")),
                     parent.nativeFragments(9));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jene.junit.PeptideTest");
    }
}
