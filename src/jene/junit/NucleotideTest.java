
package jene.junit;

import jene.nucleic.Nucleotide;

import org.junit.*;
import static org.junit.Assert.*;

public class NucleotideTest {
    private static final Nucleotide A = Nucleotide.A;
    private static final Nucleotide T = Nucleotide.T;
    private static final Nucleotide G = Nucleotide.G;
    private static final Nucleotide C = Nucleotide.C;
    private static final Nucleotide U = Nucleotide.U;

    @Test public void testPartner() {
        assertEquals(T, A.getPartner());
        assertEquals(A, T.getPartner());

        assertEquals(C, G.getPartner());
        assertEquals(G, C.getPartner());
    }

    @Test public void testTranscribe() {
        assertEquals(A, A.transcribe());
        assertEquals(U, T.transcribe());

        assertEquals(G, G.transcribe());
        assertEquals(C, C.transcribe());
    }

    @Test public void testInDNA() {
        assertTrue(A.inDNA());
        assertTrue(T.inDNA());
        assertTrue(G.inDNA());
        assertTrue(C.inDNA());

        assertFalse(U.inDNA());
    }


    @Test public void testInRNA() {
        assertTrue(A.inRNA());
        assertTrue(U.inRNA());
        assertTrue(G.inRNA());
        assertTrue(C.inRNA());

        assertFalse(T.inRNA());
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jene.junit.NucleotideTest");
    }
}
