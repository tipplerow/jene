
package jene.peptide;

import java.io.File;
import java.util.List;

import jene.chem.Concentration;

import org.junit.*;
import static org.junit.Assert.*;

public class PeptideConcentrationProfileTest {
    @Test public void testBasic() {
        Peptide p1 = Peptide.instance("ALY");
        Peptide p2 = Peptide.instance("ILE");
        Peptide p3 = Peptide.instance("LIE");

        PeptideConcentrationBuilder builder =
            PeptideConcentrationBuilder.create();

        builder.addAll(List.of(p1, p2), Concentration.valueOf(1.0));
        builder.add(p2, Concentration.valueOf(1.345));

        PeptideConcentrationProfile profile = builder.build();

        //assertEquals(2, profile.size());
        assertTrue(profile.contains(p1));
        assertTrue(profile.contains(p2));
        assertFalse(profile.contains(p3));

        assertEquals(Concentration.valueOf(1.0), profile.get(p1));
        assertEquals(Concentration.valueOf(2.345), profile.get(p2));
        assertEquals(Concentration.valueOf(0.0), profile.get(p3));

        File file = new File("data/test/_conc_profile.csv");

        file.deleteOnExit();
        profile.store(file);

        PeptideConcentrationProfile profile2 = PeptideConcentrationProfile.load(file);

        assertEquals(2, profile2.size());
        assertEquals(Concentration.valueOf(1.0), profile.get(p1));
        assertEquals(Concentration.valueOf(2.345), profile.get(p2));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jene.peptide.PeptideConcentrationProfileTest");
    }
}
