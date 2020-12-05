
package jene.ensembl;

import org.junit.*;
import static org.junit.Assert.*;

public class EnsemblGeneIDTest {
    private static final String HEADER_LINE =
        "ENSP00000495858.1 pep chromosome:GRCh38:7:140833972:140924742:-1 gene:ENSG00000157764.13"
        + " transcript:ENST00000469930.2 gene_biotype:protein_coding transcript_biotype:protein_coding"
        + " gene_symbol:BRAF description:B-Raf proto-oncogene, serine/threonine kinase [Source:HGNC Symbol;Acc:HGNC:1097]";

    @Test public void testParseHeader() {
        EnsemblGeneID geneID = EnsemblGeneID.parseHeader(HEADER_LINE);
        assertEquals("ENSG00000157764", geneID.getKey());
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jene.ensembl.EnsemblGeneIDTest");
    }
}
