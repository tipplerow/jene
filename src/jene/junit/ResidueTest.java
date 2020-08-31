
package jene.junit;

import java.util.List;

import jene.peptide.Residue;

import org.junit.*;
import static org.junit.Assert.*;

public class ResidueTest {
    @Test public void testCode1() {
        assertEquals(Residue.Val, Residue.valueOf("Val"));
        assertEquals(Residue.Val, Residue.valueOfCode1('V'));
        assertEquals(Residue.Val, Residue.valueOfCode1("V"));

        assertTrue(Residue.isValidCode1('A'));
        assertTrue(Residue.isValidCode1("A"));
        assertFalse(Residue.isValidCode1('?'));
        assertFalse(Residue.isValidCode1(""));
        assertFalse(Residue.isValidCode1("AV"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValueOfInvalidCode1() {
        Residue.valueOfCode1('Z');
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValueOfInvalidName() {
        Residue.valueOf("X");
    }

    @Test public void testListNative() {
        List<Residue> nativeList = Residue.listNative();
        assertEquals(20, nativeList.size());

        for (Residue residue : nativeList)
            assertTrue(residue.isNative());
    }

    @Test public void testFamilyMembers() {
	runFamilySizeTest( 2, Residue.Family.ACIDIC);
	runFamilySizeTest( 3, Residue.Family.BASIC);
	runFamilySizeTest( 5, Residue.Family.UNCHARGED_POLAR);
	runFamilySizeTest(10, Residue.Family.NONPOLAR);
    }

    private void runFamilySizeTest(int expectedSize, Residue.Family family) {
	assertEquals(expectedSize, family.getMembers().size());
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jene.junit.ResidueTest");
    }
}
