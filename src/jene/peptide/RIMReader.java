
package jene.peptide;

import java.io.File;

import jam.app.JamHome;
import jam.io.LineReader;
import jam.matrix.MatrixUtil;
import jam.util.RegexUtil;

final class RIMReader {
    private final File file;
    private final double[][] matrix;

    private RIMReader(File file) {
        this.file = file;
        this.matrix = MatrixUtil.square(Residue.countNative(), Double.NaN);
    }

    static double[][] readSparse(File file) {
        RIMReader reader = new RIMReader(file);
        return reader.readSparse();
    }

    private double[][] readSparse() {
        LineReader reader = LineReader.open(file);

        try {
            for (String line : reader)
                parseLine(line);
        }
        finally {
            reader.close();
        }

        return matrix;
    }

    private void parseLine(String line) {
        line = line.trim();

        if (line.isEmpty())
            return;

        String[] fields = RegexUtil.split(RegexUtil.COMMA, line, 3);

        Residue res1 = Residue.valueOf(fields[0]);
        Residue res2 = Residue.valueOf(fields[1]);
        double  bind = Double.parseDouble(fields[2]);

        int index1 = res1.ordinal();
        int index2 = res2.ordinal();

        matrix[index1][index2] = bind;
        matrix[index2][index1] = bind;
    }        
}
