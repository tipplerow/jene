
package jene.peptide;

import java.util.ArrayList;
import java.util.List;

import jam.math.UnitIndexRange;

import org.junit.*;
import static org.junit.Assert.*;

public class ProteinChangeTest {
    private static final Residue E = Residue.Glu;
    private static final Residue L = Residue.Leu;
    private static final Residue P = Residue.Pro;
    private static final Residue V = Residue.Val;

    @Test public void testApply1() {
        List<Residue> residues = makeResidueList();
        assertEquals(List.of(E, L, P, V), residues);

        ProteinChange pc = new ProteinChange(2, L, V);
        pc.apply(residues);

        assertEquals(List.of(E, V, P, V), residues);
    }

    private List<Residue> makeResidueList() {
        List<Residue> residues = new ArrayList<Residue>();

        residues.add(E);
        residues.add(L);
        residues.add(P);
        residues.add(V);

        return residues;
    }

    @Test(expected = RuntimeException.class)
    public void testApplyInvalidLocation() {
        List<Residue> residues = makeResidueList();
        assertEquals(List.of(E, L, P, V), residues);

        ProteinChange pc = new ProteinChange(11, L, V);
        pc.apply(residues);
    }

    @Test(expected = RuntimeException.class)
    public void testApplyInvalidResidue() {
        List<Residue> residues = makeResidueList();
        assertEquals(List.of(E, L, P, V), residues);

        ProteinChange pc = new ProteinChange(2, V, E);
        pc.apply(residues);
    }

    @Test public void testApply2() {
        List<Residue> residues = makeResidueList();
        assertEquals(List.of(E, L, P, V), residues);

        ProteinChange pc1 = new ProteinChange(4, V, E);
        ProteinChange pc2 = new ProteinChange(2, L, V);

        ProteinChange.apply(List.of(pc1, pc2), residues);
        assertEquals(List.of(E, V, P, E), residues);
    }

    @Test(expected = RuntimeException.class)
    public void testApplyDuplicateLocation() {
        List<Residue> residues = makeResidueList();
        assertEquals(List.of(E, L, P, V), residues);

        ProteinChange pc1 = new ProteinChange(3, P, E);
        ProteinChange pc2 = new ProteinChange(3, P, V);

        ProteinChange.apply(List.of(pc1, pc2), residues);
    }

    @Test public void testFormat() {
        ProteinChange pc = new ProteinChange(123, L, V);
        assertEquals("L123V", pc.format());
    }

    @Test public void testParse() {
        ProteinChange pc = ProteinChange.parse("p.L123V");

        assertEquals(L, pc.getNative());
        assertEquals(V, pc.getMutated());
        assertEquals(123, pc.getPosition().getUnitIndex());
    }

    @Test public void testPositionComparator() {
        ProteinChange pc1 = ProteinChange.parse("p.L123V");
        ProteinChange pc2 = ProteinChange.parse("p.Q123C");
        ProteinChange pc3 = ProteinChange.parse("p.L321V");

        assertTrue(ProteinChange.POSITION_COMPARATOR.compare(pc1, pc1) == 0);
        assertTrue(ProteinChange.POSITION_COMPARATOR.compare(pc1, pc2) == 0);
        assertTrue(ProteinChange.POSITION_COMPARATOR.compare(pc1, pc3)  < 0);

        assertTrue(ProteinChange.POSITION_COMPARATOR.compare(pc2, pc1) == 0);
        assertTrue(ProteinChange.POSITION_COMPARATOR.compare(pc2, pc2) == 0);
        assertTrue(ProteinChange.POSITION_COMPARATOR.compare(pc2, pc3)  < 0);

        assertTrue(ProteinChange.POSITION_COMPARATOR.compare(pc3, pc1)  > 0);
        assertTrue(ProteinChange.POSITION_COMPARATOR.compare(pc3, pc2)  > 0);
        assertTrue(ProteinChange.POSITION_COMPARATOR.compare(pc3, pc3) == 0);
    }

    @Test public void testResolveFragments() {
        ProteinChange pc = ProteinChange.parse("p.L123V");
        List<UnitIndexRange> fragments = pc.resolveFragments(9, 200);

        assertEquals(List.of(UnitIndexRange.instance(115, 123),
                             UnitIndexRange.instance(116, 124),
                             UnitIndexRange.instance(117, 125),
                             UnitIndexRange.instance(118, 126),
                             UnitIndexRange.instance(119, 127),
                             UnitIndexRange.instance(120, 128),
                             UnitIndexRange.instance(121, 129),
                             UnitIndexRange.instance(122, 130),
                             UnitIndexRange.instance(123, 131)),
                     fragments);

        for (UnitIndexRange range : fragments)
            assertEquals(9, range.size());

        pc = ProteinChange.parse("p.L3V");
        fragments = pc.resolveFragments(9, 200);

        assertEquals(List.of(UnitIndexRange.instance(1, 9),
                             UnitIndexRange.instance(2, 10),
                             UnitIndexRange.instance(3, 11)),
                     fragments);

        pc = ProteinChange.parse("p.L199V");
        fragments = pc.resolveFragments(9, 200);

        assertEquals(List.of(UnitIndexRange.instance(191, 199),
                             UnitIndexRange.instance(192, 200)),
                     fragments);
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jene.peptide.ProteinChangeTest");
    }
}
