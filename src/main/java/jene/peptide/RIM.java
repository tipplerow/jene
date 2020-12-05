
package jene.peptide;

import java.io.File;
import java.util.List;

import jam.app.JamEnv;
import jam.dist.RealDistribution;
import jam.io.FileUtil;
import jam.math.StatUtil;
import jam.matrix.MatrixUtil;
import jam.vector.VectorView;

/**
 * Represents pairwise interactions between native residues (a
 * <em>R</em>esidue <em>I</em>nteraction <em>M</em>atrix).
 */
public final class RIM {
    private final double[][] matrix;
    private final double[]   means;
    private final double[]   variances;;

    private RIM(double[][] matrix) {
        validateMatrix(matrix);

        this.matrix    = matrix;
        this.means     = MatrixUtil.rowMeans(matrix);
        this.variances = computeVariances(matrix);
    }

    private static void validateMatrix(double[][] matrix) {
        int N = Residue.countNative();

        if (MatrixUtil.nrow(matrix) != N)
            throw new IllegalArgumentException("Invalid matrix row dimension.");

        if (MatrixUtil.ncol(matrix) != N)
            throw new IllegalArgumentException("Invalid matrix column dimension.");

        if (!MatrixUtil.isSymmetric(matrix))
            throw new IllegalArgumentException("Non-symmetric matrix.");
    }

    private static double[] computeVariances(double[][] matrix) {
        int nrow = MatrixUtil.nrow(matrix);
        double[] result = new double[nrow];

        for (int row = 0; row < nrow; ++row)
            result[row] = StatUtil.variance(VectorView.wrap(matrix[row]));

        return result;
    }

    /**
     * The Miyazawa-Jernigan interaction matrix (Journal of Molecular Biology, 1996).
     */
    public static final RIM MiyazawaJernigan = createMJ();

    private static RIM createMJ() {
        try {
            return new RIM(RIMReader.readSparse(findMJData()));
        }
        catch (Exception ex) {
            throw new IllegalStateException("Failed to load Miyazawa-Jernigan interaction matrix.");
        }
    }

    private static File findMJData() {
        return new File(FileUtil.join(JamEnv.getRequired("JENE_HOME"), "data", "rim", "MJ_upper.csv"));
    }

    /**
     * Creates a residue-interaction matrix with elements sampled from
     * a probability distribution.
     *
     * @param distrib the distribution from which to sample interactions.
     *
     * @return the new random interaction matrix.
     */
    public static RIM random(RealDistribution distrib) {
        int N = Residue.countNative();
        double[][] elements = MatrixUtil.square(N, Double.NaN);

        for (int i = 0; i < N; ++i) {
            elements[i][i] = distrib.sample();

            for (int j = i + 1; j < N; ++j) {
                double xij = distrib.sample();

                elements[i][j] = xij;
                elements[j][i] = xij;
            }
        }

        return new RIM(elements);
    }

    /**
     * Computes the nearest-neighbor interaction energy between two
     * peptides of the same length, assuming that they are perfectly
     * aligned.
     *
     * @param pep1 the first peptide.
     *
     * @param pep2 the second peptide.
     *
     * @return the nearest-neighbor interaction energy defined above.
     *
     * @throws IllegalArgumentException unless the peptides have the
     * same length.
     */
    public double computeNearest(Peptide pep1, Peptide pep2) {
        if (pep1.length() != pep2.length())
            throw new IllegalArgumentException("Peptide lengths are unequal.");

        double result = 0.0;

        for (int k = 0; k < pep1.length(); k++)
            result += get(pep1.get(k), pep2.get(k));

        return result;
    }

    /**
     * Computes the nearest-neighbor interaction energy between a
     * binder and target peptide, given a mapping from binder to
     * target residues.
     *
     * @param binder the binder peptide.
     *
     * @param target the target peptide.
     *
     * @param TIPs the <em>target interaction points</em>, defined as
     * follows: residue {@code k} in the binder peptide interacts with
     * residue {@code TIPs.get(k)} of the target peptide.
     *
     * @return the nearest-neighbor interaction energy between the
     * binder and target.
     *
     * @throws IllegalArgumentException unless the binder peptide and
     * {@code TIPs} list have equal lengths and all interaction points
     * refer to valid locations in the target peptide.
     */
    public double computeNearest(Peptide binder, Peptide target, List<Integer> TIPs) {
        if (binder.length() != TIPs.size())
            throw new IllegalArgumentException("Invalid target interaction points.");

        double result = 0.0;

        for (int binderIndex = 0; binderIndex < binder.length(); ++binderIndex)
            result += get(binder.get(binderIndex), target.get(TIPs.get(binderIndex)));

        return result;
    }

    /**
     * Computes the average nearest-neighbor interaction energy for a
     * given binder peptide averaged over all possible target peptides
     * of the same length, assuming that amino acids are distributed
     * independently with equal probability.
     *
     * @param binder the binder peptide.
     *
     * @return the average nearest-neighbor interaction energy.
     */
    public double computeMeanNearest(Peptide binder) {
        double result = 0.0;

        for (Residue residue : binder)
            result += mean(residue);

        return result;
    }

    /**
     * Computes the ideal standard deviation in the nearest-neighbor
     * interaction energy for a given binder peptide when averaged
     * over all possible target peptides of the same length.
     *
     * @param binder the binder peptide.
     *
     * @return the ideal standard deviation in the nearest-neighbor
     * interaction energy.
     */
    public double computeStDevNearest(Peptide binder) {
        return Math.sqrt(computeVarianceNearest(binder));
    }

    /**
     * Computes the ideal variance in the nearest-neighbor interaction
     * energy for a given binder peptide when averaged over all target
     * peptides of the same length.
     *
     * @param binder the binder peptide.
     *
     * @return the ideal variance in the nearest-neighbor interaction
     * energy.
     */
    public double computeVarianceNearest(Peptide binder) {
        double result = 0.0;

        for (Residue residue : binder)
            result += variance(residue);

        return result;
    }

    /**
     * Returns the interaction strength between specific residues.
     *
     * @param res1 the first residue.
     *
     * @param res2 the second residue.
     *
     * @return the interaction strength between the specific residues.
     */
    public double get(Residue res1, Residue res2) {
	return matrix[indexOf(res1)][indexOf(res2)];
    }

    private static int indexOf(Residue res) {
        return res.ordinal();
    }

    /**
     * Returns the mean interaction for one residue taken over all
     * native residues.
     *
     * @param res the residue of interest.
     *
     * @return the mean interaction of the specified residue taken
     * over all native residues.
     */
    public double mean(Residue res) {
        return means[indexOf(res)];
    }

    /**
     * Returns the standard deviation of the interactions of one
     * residue with all native residues.
     *
     * @param res the residue of interest.
     *
     * @return the standard deviation of the interactions of the
     * specified residue with all native residues.
     */
    public double stdev(Residue res) {
        return Math.sqrt(variance(res));
    }

    /**
     * Returns the variance of the interactions of one residue with
     * all native residues.
     *
     * @param res the residue of interest.
     *
     * @return the variance of the interactions of the specified
     * residue with all native residues.
     */
    public double variance(Residue res) {
        return variances[indexOf(res)];
    }
}
