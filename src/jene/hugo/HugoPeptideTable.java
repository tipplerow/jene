
package jene.hugo;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import jam.app.JamLogger;
import jam.io.IOUtil;
import jam.io.TableReader;
import jam.io.TableWriter;

import jene.peptide.Peptide;

/**
 * Maps HUGO symbols to peptides derived from the corresponding
 * proteins (e.g., by proteasomal cleavage).
 */
public final class HugoPeptideTable {
    private final Set<Peptide> peptides = new HashSet<Peptide>();
    private final Multimap<HugoSymbol, Peptide> hugoMap = HashMultimap.create();

    private static final int SYMBOL_INDEX = 0;
    private static final int PEPTIDE_INDEX = 1;

    private HugoPeptideTable() {
    }

    /**
     * The single empty peptide table.
     */
    public static final HugoPeptideTable EMPTY = new HugoPeptideTable();

    /**
     * Creates a new table from a gene-peptide mapping.
     *
     * @param map a mapping from genes to their peptides.
     *
     * @return a new table containing the specifiedq gene-peptide
     * mapping.
     */
    public static HugoPeptideTable create(Multimap<HugoSymbol, Peptide> map) {
        HugoPeptideTable table = new HugoPeptideTable();

        for (Map.Entry<HugoSymbol, Peptide> entry : map.entries())
            table.addPeptide(entry.getKey(), entry.getValue());

        return table;
    }

    private void addPeptide(HugoSymbol symbol, Peptide peptide) {
        peptides.add(peptide);
        hugoMap.put(symbol, peptide);
    }

    /**
     * Loads a table from an input file.
     *
     * <p>The file must contain two columns separated by a comma, tab,
     * or pipe delimiter. The first line must be header line (although
     * the column keys are ignored); all subsequent lines must contain
     * a Hugo symbol in the first column and a peptide in the second.
     *
     * @param file the file to load.
     *
     * @return a new table containing the gene-peptide mappings
     * contained in the input file.
     *
     * @throws RuntimeException unless the input file can be opened
     * for reading and contains valid gene-peptide mappings.
     */
    public static HugoPeptideTable load(File file) {
        TableReader      reader = TableReader.open(file);
        HugoPeptideTable table  = new HugoPeptideTable();

        table.load(reader);
        return table;
    }

    /**
     * Loads a table from an input file.
     *
     * <p>The file must contain two columns separated by a comma, tab,
     * or pipe delimiter. The first line must be header line (although
     * the column keys are ignored); all subsequent lines must contain
     * a Hugo symbol in the first column and a peptide in the second.
     *
     * @param fileName the name of the file to load.
     *
     * @return a new table containing the gene-peptide mappings
     * contained in the input file.
     *
     * @throws RuntimeException unless the input file can be opened
     * for reading and contains valid gene-peptide mappings.
     */
    public static HugoPeptideTable load(String fileName) {
        return load(new File(fileName));
    }

    private void load(TableReader reader) {
        try {
            for (List<String> fields : reader)
                processFields(fields);

            JamLogger.info("HugoPeptideTable: Loaded [%d] records.", size());
        }
        finally {
            IOUtil.close(reader);
        }
    }

    private void processFields(List<String> fields) {
        HugoSymbol symbol  = HugoSymbol.instance(fields.get(SYMBOL_INDEX));
        Peptide    peptide = Peptide.instance(fields.get(PEPTIDE_INDEX));

        addPeptide(symbol, peptide);
    }

    /**
     * Identifies HUGO symbols contained in this table.
     *
     * @param symbol a HUGO symbol of interest.
     *
     * @return {@code true} iff this table contains the specified
     * symbol.
     */
    public boolean contains(HugoSymbol symbol) {
        return hugoMap.containsKey(symbol);
    }

    /**
     * Identifies peptides contained in this table.
     *
     * @param peptide a peptide of interest.
     *
     * @return {@code true} iff the specified peptide is mapped to one
     * or more HUGO symbols in this table.
     */
    public boolean contains(Peptide peptide) {
        return peptides.contains(peptide);
    }

    /**
     * Returns the peptides mapped to a given HUGO symbol.
     *
     * @param symbol the HUGO symbol of interest.
     *
     * @return the peptides mapped to the specified HUGO symbol (an
     * empty collection if the symbol is not present).
     */
    public Collection<Peptide> get(HugoSymbol symbol) {
        return Collections.unmodifiableCollection(hugoMap.get(symbol));
    }

    /**
     * Returns the number of gene-peptide mappings in this table.
     *
     * @return the number of gene-peptide mappings in this table.
     */
    public int size() {
        return hugoMap.size();
    }

    /**
     * Writes this gene-peptide map to a data file.
     *
     * @param file the file to write.
     */
    public void store(File file) {
        JamLogger.info("HugoPeptideTable: Writing file [%s]...", file.getName());

        try (TableWriter writer = TableWriter.open(file)) {
            store(writer);
        }
    }

    private void store(TableWriter writer) {
        writeHeader(writer);
        writePeptides(writer);
    }

    private static void writeHeader(TableWriter writer) {
        writer.println("Hugo_Symbol", "Peptide");
    }

    private void writePeptides(TableWriter writer) {
        //
        // Nice to have everything in alphabetical order...
        //
        TreeSet<HugoSymbol> symbols = new TreeSet<HugoSymbol>(hugoMap.keySet());

        for (HugoSymbol symbol : symbols)
            writePeptides(writer, symbol);
    }

    private void writePeptides(TableWriter writer, HugoSymbol symbol) {
        TreeSet<String> peptideStrings = new TreeSet<String>();

        for (Peptide peptide : get(symbol))
            peptideStrings.add(peptide.formatString());

        for (String peptideString : peptideStrings)
            writer.println(symbol.getKey(), peptideString);
    }

    /**
     * Writes this gene-peptide map to a data file.
     *
     * @param fileName the name of the file to write.
     */
    public void store(String fileName) {
        store(new File(fileName));
    }

    /**
     * Returns a read-only view of the unique peptides in this table.
     *
     * @return a read-only view of the unique peptides in this table.
     */
    public Set<Peptide> viewPeptides() {
        return Collections.unmodifiableSet(peptides);
    }

    /**
     * Returns a read-only view of the HUGO symbols in this table.
     *
     * @return a read-only view of the HUGO symbols in this table.
     */
    public Set<HugoSymbol> viewSymbols() {
        return Collections.unmodifiableSet(hugoMap.keySet());
    }
}
