
package jene.peptide;

import java.util.Set;
import java.util.TreeSet;

import jam.app.JamLogger;

public final class NativeGenerator {
    private final int peptideCount;
    private final int peptideLength;
    private final Set<String> nativePeptides;

    private NativeGenerator(int peptideCount, int peptideLength) {
        this.peptideCount = peptideCount;
        this.peptideLength = peptideLength;
        this.nativePeptides = new TreeSet<String>();
    }

    private void run() {
        generatePeptides();
        writePeptides();
    }

    private void generatePeptides() {
        while (nativePeptides.size() < peptideCount)
            nativePeptides.add(Peptide.newNative(peptideLength).formatString());
    }

    private void writePeptides() {
        for (String peptide : nativePeptides)
            System.out.println(peptide);
    }

    private static void usage() {
        System.err.println("Usage: java jene.peptide.NativeGenerator PEPTIDE_COUNT PEPTIDE_LENGTH");
        System.exit(1);
    }

    public static void main(String[] args) {
        if (args.length != 2)
            usage();

        int peptideCount = Integer.parseInt(args[0]);
        int peptideLength = Integer.parseInt(args[1]);

        NativeGenerator generator = new NativeGenerator(peptideCount, peptideLength);
        generator.run();
    }
}
