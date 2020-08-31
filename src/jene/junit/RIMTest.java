
package jene.junit;

import jam.junit.NumericTestBase;

import jene.peptide.Peptide;
import jene.peptide.Residue;
import jene.peptide.RIM;

import org.junit.*;
import static org.junit.Assert.*;

public class RIMTest extends NumericTestBase {
    private static final Residue A = Residue.Ala;
    private static final Residue C = Residue.Cys;
    private static final Residue D = Residue.Asp;
    private static final Residue E = Residue.Glu;
    private static final Residue F = Residue.Phe;
    private static final Residue G = Residue.Gly;
    private static final Residue H = Residue.His;
    private static final Residue I = Residue.Ile;
    private static final Residue K = Residue.Lys;
    private static final Residue N = Residue.Asn;
    private static final Residue P = Residue.Pro;
    private static final Residue V = Residue.Val;

    private static final RIM MJ = RIM.MiyazawaJernigan;

    @Test public void testMJ() {
        assertDouble(-5.44, MJ.get(C, C));

        assertDouble(-6.29, MJ.get(V, F));
        assertDouble(-6.29, MJ.get(F, V));

        assertDouble(-1.53, MJ.get(P, N));
        assertDouble(-1.53, MJ.get(N, P));
    }

    @Test public void testMeans() {
        assertDouble(-2.443, MJ.mean(P));
    }

    @Test public void testStDev() {
        assertEquals(1.057624, MJ.stdev(P), 1.0E-6);
    }

    @Test public void testNearest() {
        Peptide p1 = Peptide.of(A, C);
        Peptide p2 = Peptide.of(K, I);

        assertDouble(MJ.get(A, K) + MJ.get(C, I), MJ.computeNearest(p1, p2));
        assertDouble(MJ.get(A, K) + MJ.get(C, I), MJ.computeNearest(p2, p1));

        Peptide p3 = Peptide.of(E, F, G);
        Peptide p4 = Peptide.of(H, G, E);
        
        assertDouble(MJ.get(E, H) + MJ.get(F, G) + MJ.get(G, E), MJ.computeNearest(p3, p4));
        assertDouble(MJ.get(E, H) + MJ.get(F, G) + MJ.get(G, E), MJ.computeNearest(p4, p3));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jene.junit.RIMTest");
    }
}
