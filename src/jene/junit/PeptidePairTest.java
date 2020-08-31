
package jene.junit;

import java.util.List;

import jam.junit.JamTestBase;

import jene.neo.NeoPeptide;
import jene.neo.PeptidePair;
import jene.neo.PeptideType;
import jene.neo.SelfPeptide;
import jene.peptide.Peptide;

import org.junit.*;
import static org.junit.Assert.*;

public class PeptidePairTest extends JamTestBase {
    @Test public void testInstance() {
        Peptide p1 = Peptide.instance("QVSRDQVLD");
        Peptide p2 = Peptide.instance("QVSRDQVLE");

        SelfPeptide self = SelfPeptide.instance(p1);
        NeoPeptide  neo  = NeoPeptide.instance(p2);
        PeptidePair pair = PeptidePair.instance(self, neo);

        assertEquals(p1, pair.self());
        assertEquals(p2, pair.neo());

        assertEquals(PeptideType.NEO, neo.getType());
        assertEquals(PeptideType.SELF, self.getType());
    }

    @Test public void testPeptides() {
        Peptide p1 = Peptide.instance("QVSRDQVLD");
        Peptide p2 = Peptide.instance("QVSRDQVLE");
        Peptide p3 = Peptide.instance("THALPCCRA");
        Peptide p4 = Peptide.instance("TQALPCCRA");

        PeptidePair pair1 =
            PeptidePair.instance(SelfPeptide.instance(p1),
                                 NeoPeptide.instance(p2));

        PeptidePair pair2 =
            PeptidePair.instance(SelfPeptide.instance(p3),
                                 NeoPeptide.instance(p4));

        assertCollection(List.of(p2, p4), PeptidePair.neo(List.of(pair1, pair2)));
        assertCollection(List.of(p1, p3), PeptidePair.self(List.of(pair1, pair2)));

        assertCollection(List.of(p1, p2, p3, p4), PeptidePair.peptides(List.of(pair1, pair2)));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jene.junit.PeptidePairTest");
    }
}
