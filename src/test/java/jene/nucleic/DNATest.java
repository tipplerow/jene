
package jene.nucleic;

import jam.app.JamProperties;

import jene.chr.Chromosome;
import jene.ensembl.EnsemblLocator;

import org.junit.*;
import static org.junit.Assert.*;

public class DNATest {
    private static final Nucleotide A = Nucleotide.A;
    private static final Nucleotide T = Nucleotide.T;
    private static final Nucleotide G = Nucleotide.G;
    private static final Nucleotide C = Nucleotide.C;
    private static final Nucleotide N = Nucleotide.N;

    static {
        JamProperties.setProperty(EnsemblLocator.GENOME_DIR_PROPERTY, "data/test");
    }

    @Test public void testChr22() {
        Chromosome chr = Chromosome.Chr22;
        
        DNA dna = chr.dna();

        assertEquals(240, dna.length());
        assertEquals(N, dna.at(0));
        assertEquals(N, dna.at(1));
        assertEquals(N, dna.at(2));
        assertEquals(C, dna.at(237));
        assertEquals(C, dna.at(238));
        assertEquals(A, dna.at(239));

        assertTrue(dna.formatString().startsWith("NNNNNGGACTGTTGTGGGGTGGGGGGAGGGATAGTATTGGGAGATATACCTAATGCTAGA"));
        assertTrue(dna.formatString().endsWith("AATAATTAAATAGAACACCTAGAAAAAACTTTAAAATTTACTCAACTGAAAAGAAACCCA"));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jene.nucleic.DNATest");
    }
}
