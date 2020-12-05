
package jene.hla;

import java.util.List;
import java.util.regex.Pattern;

import jam.util.RegexUtil;

import org.junit.*;
import static org.junit.Assert.*;

public class AlleleTest {
    private static final Allele A1 = Allele.instance("A0201");
    private static final Allele A2 = Allele.instance("A3010");

    private static final Allele B1 = Allele.instance("B0702");
    private static final Allele B2 = Allele.instance("B3501");

    private static final Allele C1 = Allele.instance("C0103");
    private static final Allele C2 = Allele.instance("C0702");

    @Test public void testCompare() {
        assertTrue(B1.compareTo(A1)  > 0);
        assertTrue(B1.compareTo(A2)  > 0);
        assertTrue(B1.compareTo(B1) == 0);
        assertTrue(B1.compareTo(B2)  < 0);
        assertTrue(B1.compareTo(C1)  < 0);
        assertTrue(B1.compareTo(C2)  < 0);
    }

    @Test public void testEquals() {
        assertTrue(Allele.instance("HLA-A*02:01").equals(Allele.instance("A0201")));
        assertFalse(Allele.instance("HLA-A*02:01").equals(Allele.instance("A0102")));
    }

    @Test public void testHashCode() {
        assertEquals( 201, A1.hashCode());
        assertEquals(3010, A2.hashCode());

        assertEquals(10702, B1.hashCode());
        assertEquals(13501, B2.hashCode());

        assertEquals(20103, C1.hashCode());
        assertEquals(20702, C2.hashCode());
    }

    @Test public void testInstance() {
        assertEquals(A1, Allele.instance("HLA-A*02:01"));
        assertEquals(A1, Allele.instance("HLA-A-02:01"));
        assertEquals(A1, Allele.instance("HLA-A-02-01"));
    }

    @Test public void testLoad() {
        List<Allele> alleles = Allele.load("data/test/alleles.txt");

        assertEquals(List.of(Allele.instance("A0101"), Allele.instance("A0201")), alleles);
    }

    @Test public void testParse() {
        assertEquals(List.of(C2, A1, B2), Allele.parse("C0702 A0201 B3501", RegexUtil.MULTI_WHITE_SPACE));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jene.hla.AlleleTest");
    }
}
