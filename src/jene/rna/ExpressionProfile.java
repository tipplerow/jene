
package jene.rna;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import jam.app.JamLogger;
import jam.io.TableReader;
import jam.io.TableWriter;
import jam.lang.JamException;
import jam.util.MapUtil;

import jene.hugo.HugoSymbol;

/**
 * An immutable collection of RNA expression indexed by HUGO symbol.
 */
public final class ExpressionProfile {
    private final Map<HugoSymbol, Expression> profile;

    private static final DecimalFormat EXPRESSION_FORMAT = new DecimalFormat("#0.0###");

    private ExpressionProfile(Map<HugoSymbol, Expression> profile, boolean copy) {
        if (copy)
            this.profile = new HashMap<HugoSymbol, Expression>(profile);
        else
            this.profile = Collections.unmodifiableMap(profile);
    }

    /**
     * Creates a new expression profile from an expression map.
     *
     * @param profile a mapping from HUGO symbol to expression level.
     *
     * @return the new expression profile.
     */
    public static ExpressionProfile create(Map<HugoSymbol, Expression> profile) {
        return new ExpressionProfile(profile, true);
    }

    /**
     * Creates a new expression profile from an expression mapping.
     *
     * @param symbols the genes to include in the profile.
     *
     * @param levels the RNA expression corresponding to each gene in
     * the symbol list: {@code levels.get(k)} must be the expression
     * level for {@code symbols.get(k)}.
     *
     * @return the new expression profile.
     *
     * @throws RuntimeException unless the lists have equal length.
     */
    public static ExpressionProfile create(List<HugoSymbol> symbols, List<Expression> levels) {
        return new ExpressionProfile(MapUtil.zipHash(symbols, levels), false);
    }

    /**
     * Loads an expression profile from a data file.
     *
     * @param fileName the name of the file to load.
     *
     * @return the expression profile contained in the specified file.
     *
     * @throws RuntimeException unless the specified file contains a
     * valid expression profile.
     */
    public static ExpressionProfile load(String fileName) {
        return load(new File(fileName));
    }

    /**
     * Loads an expression profile from a data file.
     *
     * @param file the file to load.
     *
     * @return the expression profile contained in the specified file.
     *
     * @throws RuntimeException unless the specified file contains a
     * valid expression profile.
     */
    public static ExpressionProfile load(File file) {
        JamLogger.info("Loading expression profile [%s]...", file.getName());

        try (TableReader reader = TableReader.open(file)) {
            return load(reader);
        }
    }

    private static ExpressionProfile load(TableReader reader) {
        if (reader.columnKeys().size() != 2)
            throw JamException.runtime("Exactly two columns are required for an expression profile.");

        Map<HugoSymbol, Expression> profile =
            new HashMap<HugoSymbol, Expression>();

        for (List<String> columns : reader) {
            HugoSymbol symbol = HugoSymbol.instance(columns.get(0));
            Expression level  = Expression.valueOf(columns.get(1));

            MapUtil.putUnique(profile, symbol, level);
        }

        return new ExpressionProfile(profile, false);
    }

    /**
     * Identifies genes contained in this profile.
     *
     * @param symbol the HUGO symbol of the target gene.
     *
     * @return {@code true} iff this profile contains the target gene.
     */
    public boolean contains(HugoSymbol symbol) {
        return profile.containsKey(symbol);
    }

    /**
     * Returns the RNA expression for a given gene.
     *
     * @param symbol the HUGO symbol of the target gene.
     *
     * @return the RNA expression of the target gene, if the gene is
     * present in this profile, or {@code Expression.ZERO} otherwise
     * (never {@code null}).
     */
    public Expression get(HugoSymbol symbol) {
        Expression expression = profile.get(symbol);

        if (expression != null)
            return expression;
        else
            return Expression.ZERO;
    }

    /**
     * Stores this expression profile in a data file.
     *
     * @param fileName the name of the file to write.
     *
     * @throws RuntimeException if the file cannot be opened for
     * writing.
     */
    public void store(String fileName) {
        store(new File(fileName));
    }

    /**
     * Stores this expression profile in a data file.
     *
     * @param file the file to write.
     *
     * @throws RuntimeException if the file cannot be opened for
     * writing.
     */
    public void store(File file) {
        JamLogger.info("Storing expression profile [%s]...", file.getName());

        try (TableWriter writer = TableWriter.open(file)) {
            store(writer);
        }
    }

    private void store(TableWriter writer) {
        writeHeader(writer);
        writeExpression(writer);
    }

    private static void writeHeader(TableWriter writer) {
        writer.println("Hugo_Symbol", "Expression");
    }

    private void writeExpression(TableWriter writer) {
        //
        // Much nicer to see the genes in alphabetical order...
        //
        Set<HugoSymbol> symbols = new TreeSet<HugoSymbol>(profile.keySet());

        for (HugoSymbol symbol : symbols)
            writeExpression(writer, symbol);
    }

    private void writeExpression(TableWriter writer, HugoSymbol symbol) {
        Expression expression = profile.get(symbol);

        if (expression.isPositive())
            writer.println(symbol.getKey(), expression.format(EXPRESSION_FORMAT));
    }

    /**
     * Returns a read-only view of the gene expression data in this
     * profile.
     *
     * @return a read-only view of the gene expression data in this
     * profile.
     */
    public Set<Map.Entry<HugoSymbol, Expression>> viewEntries() {
        return Collections.unmodifiableSet(profile.entrySet());
    }

    /**
     * Returns a read-only view of the genes in this profile.
     *
     * @return a read-only view of the genes in this profile.
     */
    public Set<HugoSymbol> viewSymbols() {
        return Collections.unmodifiableSet(profile.keySet());
    }
}
