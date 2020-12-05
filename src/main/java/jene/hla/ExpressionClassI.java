
package jene.hla;

import java.util.EnumMap;
import java.util.Map;

import jam.math.DoubleUtil;

import jene.rna.Expression;
import jene.rna.ExpressionProfile;
import jene.rna.TumorExpressionMatrix;
import jene.tcga.TumorBarcode;

/**
 * Encapsulates RNA expression levels for the HLA class I genes.
 */
public final class ExpressionClassI {
    private final Expression total;
    private final Map<Locus, Expression> expr;

    private ExpressionClassI(Map<Locus, Expression> expr) {
        this.expr = expr;
        this.total = computeTotal();
    }

    private Expression computeTotal() {
        return get(Locus.A).plus(get(Locus.B)).plus(get(Locus.C));
    }

    /**
     * Creates a new expression record for fixed RNA levels.
     *
     * @param exprA the RNA expression of the HLA-A gene.
     *
     * @param exprB the RNA expression of the HLA-B gene.
     *
     * @param exprC the RNA expression of the HLA-C gene.
     *
     * @return a new expression record with the specfied RNA levels.
     */
    public static ExpressionClassI create(Expression exprA, Expression exprB, Expression exprC) {
        Map<Locus, Expression> expr =
            new EnumMap<Locus, Expression>(Locus.class);

        expr.put(Locus.A, exprA);
        expr.put(Locus.B, exprB);
        expr.put(Locus.C, exprC);

        return new ExpressionClassI(expr);
    }

    /**
     * Creates a new expression record from an RNA expression profile.
     *
     * @param profile an RNA expression profile.
     *
     * @return a new expression record with RNA levels extracted from
     * the input profile.
     */
    public static ExpressionClassI create(ExpressionProfile profile) {
        return create(profile.get(Locus.A.getHugoSymbol()),
                      profile.get(Locus.B.getHugoSymbol()),
                      profile.get(Locus.C.getHugoSymbol()));
    }

    /**
     * Creates a new expression record for a tumor sample.
     *
     * @param barcode the barcode of interest.
     *
     * @param matrix the RNA expression matrix.

     * @return a new expression record with the RNA levels for the
     * specified tumor sample.
     */
    public static ExpressionClassI create(TumorBarcode barcode, TumorExpressionMatrix matrix) {
        return create(matrix.get(barcode, Locus.A.getHugoSymbol()),
                      matrix.get(barcode, Locus.B.getHugoSymbol()),
                      matrix.get(barcode, Locus.C.getHugoSymbol()));
    }

    /**
     * Returns the raw RNA expression for a given HLA locus.
     *
     * @param locus the locus of interest.
     *
     * @return the raw RNA expression for the specified locus.
     */
    public Expression get(Locus locus) {
        return expr.get(locus);
    }

    /**
     * Computes a diversity index for the HLA expression in a given
     * genotype.
     *
     * <p>The diversity index is a measure of how HLA expression is
     * distributed among the alleles in a genotype. It takes values
     * in the range {@code [0, 1]}, where 0 indicates that all HLA
     * expression is concentrated in a single allele and 1 indicates
     * that expression is equal for all alleles.
     *
     * @param genotype the genotype of interest.
     *
     * @return the diversity index for the HLA expression of the
     * specified genotype.
     */
    public double diversity(Genotype genotype) {
        int N = genotype.countUniqueAlleles();

        return (1.0 - herfindahl(genotype)) / (1.0 - 1.0 / N);
    }

    /**
     * Computes the Herfindahl index of the HLA expression for a
     * given genotype.
     *
     * <p>The Herfindahl index is a measure of how concentrated the
     * HLA expression is among the alleles in a genotype.  It takes
     * values in the range {@code [1/N, 1]}, where {@code N} is the
     * number of unique alleles, with {@code 1/N} corresponding to
     * equal expression among all alleles and {@code 1} indicating
     * that all expression is concentrated in a single allele.
     *
     * @param genotype the genotype of interest.
     *
     * @return the Herfindahl index of the HLA expression for the
     * specified genotype.
     */
    public double herfindahl(Genotype genotype) {
        double result = 0.0;

        for (Allele allele : genotype.viewUniqueAlleles())
            result += DoubleUtil.square(normalize(allele, genotype));

        return result;
    }

    /**
     * Returns the fraction of HLA expression that can be attributed
     * to a single allele (the fractional population of that allele
     * inferred from RNA expression and homozygosity).
     *
     * @param allele the allele of interest.
     *
     * @param genotype the genotype to which the allele belongs.
     *
     * @return the fraction of HLA expression that can be attributed
     * to the specified allele (its fractional population among the
     * alleles present in the genotype).
     *
     * @throws IllegalArgumentException unless the genotype contains
     * the allele.
     */
    public double normalize(Allele allele, Genotype genotype) {
        if (!genotype.contains(allele))
            throw new IllegalArgumentException("Allele/genotype mismatch.");

        Locus locus = allele.getLocus();
        int   count = genotype.countUniqueAlleles(locus);

        return expr.get(locus).doubleValue() / total.doubleValue() / count;
    }

    /**
     * Returns the total expression across the three HLA alleles.
     *
     * @return the total expression across the three HLA alleles.
     */
    public Expression total() {
        return total;
    }
}
