
package jene.hla;

import java.util.List;
import java.util.Set;

import jam.util.RegexUtil;

import org.junit.*;
import static org.junit.Assert.*;

public class GenotypeTest {
    private static final Allele A1 = Allele.instance("A0201");
    private static final Allele A2 = Allele.instance("A3010");

    private static final Allele B1 = Allele.instance("B0702");
    private static final Allele B2 = Allele.instance("B3501");

    private static final Allele C1 = Allele.instance("C0103");
    private static final Allele C2 = Allele.instance("C0702");

    private static final Genotype homoA2 = Genotype.instance(A2, A2, B1, B2, C1, C2);
    private static final Genotype homoB1 = Genotype.instance(A1, A2, B1, B1, C1, C2);
    private static final Genotype hetero = Genotype.instance(A1, A2, B1, B2, C1, C2);

    @Test public void testCompare() {
        assertTrue(homoA2.compareTo(homoA2) == 0);
        assertTrue(homoA2.compareTo(homoB1)  > 0);
        assertTrue(homoA2.compareTo(hetero)  > 0);

        assertTrue(homoB1.compareTo(homoA2)  < 0);
        assertTrue(homoB1.compareTo(homoB1) == 0);
        assertTrue(homoB1.compareTo(hetero)  < 0);

        assertTrue(hetero.compareTo(homoA2)  < 0);
        assertTrue(hetero.compareTo(homoB1)  > 0);
        assertTrue(hetero.compareTo(hetero) == 0);

        Genotype g1 = Genotype.instance(C1, A1);
        Genotype g2 = Genotype.instance(A2, B1);

        // g1 is "less than" g2 because the alleles are sorted...
        assertTrue(g1.compareTo(g2) < 0);

        // All else equal, the longer genotype is "greater"...
        Genotype g3 = Genotype.instance(A1, A2, B1);
        Genotype g4 = Genotype.instance(A1, A2, B1, B2);

        assertTrue(g3.compareTo(g4) < 0);
    }

    @Test public void testCountUnique() {
        assertEquals(5, homoA2.countUniqueAlleles());
        assertEquals(5, homoB1.countUniqueAlleles());
        assertEquals(6, hetero.countUniqueAlleles());

        assertEquals(1, homoA2.countUniqueAlleles(Locus.A));
        assertEquals(2, homoA2.countUniqueAlleles(Locus.B));
        assertEquals(2, homoA2.countUniqueAlleles(Locus.C));

        assertEquals(2, homoB1.countUniqueAlleles(Locus.A));
        assertEquals(1, homoB1.countUniqueAlleles(Locus.B));
        assertEquals(2, homoB1.countUniqueAlleles(Locus.C));

        assertEquals(2, hetero.countUniqueAlleles(Locus.A));
        assertEquals(2, hetero.countUniqueAlleles(Locus.B));
        assertEquals(2, hetero.countUniqueAlleles(Locus.C));
    }

    @Test public void testDelete() {
        assertEquals(Genotype.instance(A1, A2, B1, C1, C2), hetero.delete(B2));
        assertTrue(hetero.contains(B2));

        assertEquals(Genotype.instance(A2, A2, B1, C2), homoA2.delete(B2, C1));
        assertTrue(homoA2.contains(B2));
        assertTrue(homoA2.contains(C1));

        assertEquals(Genotype.instance(B1, B2, C1, C2), homoA2.delete(A2));
        assertTrue(homoA2.contains(A2));
    }

    @Test public void testEnumerateLOH3() {
        Genotype hetero = Genotype.instance(A1, B1, C1);

        Set<Genotype> LOH1 = hetero.enumerateLOH(1);
        Set<Genotype> LOH2 = hetero.enumerateLOH(2);

        assertEquals(3, LOH1.size());
        assertEquals(3, LOH2.size());
        
        assertTrue(LOH1.contains(Genotype.instance(A1, B1)));
        assertTrue(LOH1.contains(Genotype.instance(A1, C1)));
        assertTrue(LOH1.contains(Genotype.instance(B1, C1)));
        
        assertTrue(LOH2.contains(Genotype.instance(A1)));
        assertTrue(LOH2.contains(Genotype.instance(B1)));
        assertTrue(LOH2.contains(Genotype.instance(C1)));
    }

    @Test public void testEnumerateLOH4() {
        Genotype hetero = Genotype.instance(A1, A2, B1, C1);

        Set<Genotype> LOH1 = hetero.enumerateLOH(1);
        Set<Genotype> LOH2 = hetero.enumerateLOH(2);
        Set<Genotype> LOH3 = hetero.enumerateLOH(3);

        assertEquals(4, LOH1.size());
        assertEquals(6, LOH2.size());
        assertEquals(4, LOH3.size());
        
        assertTrue(LOH1.contains(Genotype.instance(A1, A2, B1)));
        assertTrue(LOH1.contains(Genotype.instance(A1, A2, C1)));
        assertTrue(LOH1.contains(Genotype.instance(A1, B1, C1)));
        assertTrue(LOH1.contains(Genotype.instance(A2, B1, C1)));
        
        assertTrue(LOH2.contains(Genotype.instance(A1, A2)));
        assertTrue(LOH2.contains(Genotype.instance(A1, B1)));
        assertTrue(LOH2.contains(Genotype.instance(A1, C1)));
        assertTrue(LOH2.contains(Genotype.instance(A2, B1)));
        assertTrue(LOH2.contains(Genotype.instance(A2, C1)));
        assertTrue(LOH2.contains(Genotype.instance(B1, C1)));
        
        assertTrue(LOH3.contains(Genotype.instance(A1)));
        assertTrue(LOH3.contains(Genotype.instance(A2)));
        assertTrue(LOH3.contains(Genotype.instance(B1)));
        assertTrue(LOH3.contains(Genotype.instance(C1)));
    }

    @Test public void testEquals() {
        assertEquals(hetero, Genotype.instance(C2, C1, A1, A2, B2, B1));
    }

    @Test public void testHashCode() {
        assertEquals(hetero.hashCode(), Genotype.instance(C2, C1, A1, A2, B2, B1).hashCode());
    }

    @Test public void testHeteroHomo() {
        assertTrue(homoA2.isHomozygous());
        assertTrue(homoB1.isHomozygous());
        assertTrue(hetero.isHeterozygous());

        assertFalse(homoA2.isHeterozygous());
        assertFalse(homoB1.isHeterozygous());
        assertFalse(hetero.isHomozygous());

        assertTrue(homoA2.isHomozygous(Locus.A));
        assertFalse(homoA2.isHomozygous(Locus.B));
        assertFalse(homoA2.isHomozygous(Locus.C));

        assertTrue(homoB1.isHomozygous(Locus.B));
        assertFalse(homoB1.isHomozygous(Locus.A));
        assertFalse(homoB1.isHomozygous(Locus.C));

        assertFalse(hetero.isHomozygous(Locus.A));
        assertFalse(hetero.isHomozygous(Locus.B));
        assertFalse(hetero.isHomozygous(Locus.C));
    }

    @Test public void testLocus() {
        assertNull(homoA2.A2());
        assertNull(homoB1.B2());

        assertEquals(A2, homoA2.A1());
        assertEquals(B1, homoA2.B1());
        assertEquals(B2, homoA2.B2());
        assertEquals(C1, homoA2.C1());
        assertEquals(C2, homoA2.C2());
        
        assertEquals(A1, homoB1.A1());
        assertEquals(A2, homoB1.A2());
        assertEquals(B1, homoB1.B1());
        assertEquals(C1, homoB1.C1());
        assertEquals(C2, homoB1.C2());
        
        assertEquals(A1, hetero.A1());
        assertEquals(A2, hetero.A2());
        assertEquals(B1, hetero.B1());
        assertEquals(B2, hetero.B2());
        assertEquals(C1, hetero.C1());
        assertEquals(C2, hetero.C2());
        
        assertEquals(List.of(A2),     homoA2.viewUniqueAlleles(Locus.A));
        assertEquals(List.of(B1, B2), homoA2.viewUniqueAlleles(Locus.B));
        assertEquals(List.of(C1, C2), homoA2.viewUniqueAlleles(Locus.C));

        assertEquals(List.of(A1, A2), homoB1.viewUniqueAlleles(Locus.A));
        assertEquals(List.of(B1),     homoB1.viewUniqueAlleles(Locus.B));
        assertEquals(List.of(C1, C2), homoB1.viewUniqueAlleles(Locus.C));

        assertEquals(List.of(A1, A2), hetero.viewUniqueAlleles(Locus.A));
        assertEquals(List.of(B1, B2), hetero.viewUniqueAlleles(Locus.B));
        assertEquals(List.of(C1, C2), hetero.viewUniqueAlleles(Locus.C));
    }

    @Test public void testParse() {
        assertEquals(Genotype.instance(C2, A1, B2), Genotype.parse("C0702 A0201 B3501", RegexUtil.MULTI_WHITE_SPACE));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jene.hla.GenotypeTest");
    }
}
