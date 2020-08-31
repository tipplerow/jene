
package jene.chr;

import java.io.File;

import jene.ensembl.EnsemblLocator;
import jene.nucleic.DNA;

/**
 * Enumerates all human chromosomes.
 */
public enum Chromosome {
    Chr1("1",248956,125000),
    Chr2("2",242194,93300),
    Chr3("3",198296,91000),
    Chr4("4",190215,50400),
    Chr5("5",181538,48400),
    Chr6("6",170806,61000),
    Chr7("7",159346,59900),
    Chr8("8",145139,45600),
    Chr9("9",138395,49000),
    Chr10("10",133797,40200),
    Chr11("11",135087,53700),
    Chr12("12",133275,35800),
    Chr13("13",114364,17900),
    Chr14("14",107044,17600),
    Chr15("15",101991,19000),
    Chr16("16",90338,36600),
    Chr17("17",83257,24000),
    Chr18("18",80373,17200),
    Chr19("19",58618,26500),
    Chr20("20",64444,27500),
    Chr21("21",46710,13200),
    Chr22("22",50818,14700),
    ChrX("X",156041,60600),
    ChrY("Y",57227,12500);

    private final String code;
    private final Length length;
    private final Length centromere;

    // Created on demand...
    private DNA dna = null;

    private Chromosome(String code, int length_kb, int centromere_kb) {
        this.code = code;
        this.length = Length.kilo(length_kb);
        this.centromere = Length.kilo(centromere_kb);
    }

    /**
     * Returns the file containing the Ensembl nucleotide sequence for
     * this chromosome.
     *
     * @return the file containing the Ensembl nucleotide sequence for
     * this chromosome.
     */
    public File ensemblFile() {
        return EnsemblLocator.resolveChromosomeFile(code);
    }

    /**
     * Returns the short string code for this chromosome.
     *
     * @return the short string code for this chromosome.
     */
    public String code() {
        return code;
    }

    /**
     * Returns the reference DNA sequence for this chromosome.
     *
     * @return the reference DNA sequence for this chromosome.
     */
    public DNA dna() {
        if (dna == null)
            dna = DNA.load(ensemblFile());

        return dna;
    }

    /**
     * Returns the length of this chromosome.
     *
     * @return the length of this chromosome.
     */
    public Length length() {
        return length;
    }

    /**
     * Returns the location of the centromere on this chromosome.
     *
     * @return the location of the centromere on this chromosome.
     */
    public Length centromere() {
        return centromere;
    }
}
