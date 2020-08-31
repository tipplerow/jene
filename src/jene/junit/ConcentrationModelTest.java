
package jene.junit;

import java.util.List;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import jene.chem.Concentration;
import jene.hugo.HugoPeptideTable;
import jene.hugo.HugoSymbol;
import jene.peptide.Peptide;
import jene.peptide.PeptideConcentrationProfile;
import jene.rna.Expression;
import jene.rna.ExpressionProfile;
import jene.rna.ConcentrationModel;
import jene.rna.ConcentrationModelType;

import org.junit.*;
import static org.junit.Assert.*;

public class ConcentrationModelTest {
    private static final ConcentrationModel MODEL =
        ConcentrationModelType.LINEAR.defaultModel();

    private static final HugoSymbol gene1 = HugoSymbol.instance("Gene1");
    private static final HugoSymbol gene2 = HugoSymbol.instance("Gene2");

    private static final Peptide pep1 = Peptide.instance("ALL");
    private static final Peptide pep2 = Peptide.instance("VAV");
    private static final Peptide pep3 = Peptide.instance("ILE");

    private static final Expression expr1 = Expression.valueOf(10.0);
    private static final Expression expr2 = Expression.valueOf(20.0);

    @Test public void testBuild() {
        HugoPeptideTable peptideTable = buildPeptideTable();
        ExpressionProfile exprProfile = buildExpressionProfile();

        PeptideConcentrationProfile concProfile =
            MODEL.buildProfile(peptideTable, exprProfile);

        assertEquals(Concentration.valueOf(10.0), concProfile.get(pep1));
        assertEquals(Concentration.valueOf(30.0), concProfile.get(pep2));
        assertEquals(Concentration.valueOf(20.0), concProfile.get(pep3));
    }

    private HugoPeptideTable buildPeptideTable() {
        Multimap<HugoSymbol, Peptide> peptides = HashMultimap.create();

        peptides.put(gene1, pep1);
        peptides.put(gene1, pep2);
        peptides.put(gene2, pep2);
        peptides.put(gene2, pep3);

        return HugoPeptideTable.create(peptides);
    }

    private ExpressionProfile buildExpressionProfile() {
        return ExpressionProfile.create(List.of(gene1, gene2), List.of(expr1, expr2));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jene.junit.ConcentrationModelTest");
    }
}
