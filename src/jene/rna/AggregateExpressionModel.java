
package jene.rna;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jam.app.JamProperties;
import jam.io.TableReader;
import jam.lang.JamException;
import jam.util.MapUtil;

import jene.hugo.HugoSymbol;
import jene.tcga.TumorBarcode;

/**
 * Represents a gene expression model in which a single profile
 * applies to an entire cohort. The aggregate profile is typically
 * the median expression in another proxy cohort.
 */
public final class AggregateExpressionModel extends ExpressionModel {
    private final Map<HugoSymbol, Expression> profile;

    private static AggregateExpressionModel global = null;

    private AggregateExpressionModel(Map<HugoSymbol, Expression> profile) {
        this.profile = Collections.unmodifiableMap(profile);
    }

    /**
     * Name of the system property that specifies the data file
     * containing the global aggregate expression profile.
     */
    public static final String PROFILE_FILE_NAME_PROPERTY =
        "jene.rna.aggregateExpressionProfile";

    /**
     * Returns the global expression model defined by system
     * properties.
     *
     * @return the global expression model defined by system
     * properties.
     */
    public static AggregateExpressionModel global() {
        if (global == null)
            global = createGlobal();

        return global;
    }

    private static AggregateExpressionModel createGlobal() {
        return load(resolveProfileFileName());
    }

    private static String resolveProfileFileName() {
        return JamProperties.getRequired(PROFILE_FILE_NAME_PROPERTY);
    }

    /**
     * Loads an aggregate expression profile from a data file.
     *
     * @param file the file to load.
     *
     * @return the aggregate expression profile contained in the
     * specified data file.
     *
     * @throws RuntimeException unless the specified file contains a
     * valid expression profile.
     */
    public static AggregateExpressionModel load(File file) {
        Map<HugoSymbol, Expression> profile =
            new HashMap<HugoSymbol, Expression>();

        TableReader reader = TableReader.open(file);

        if (reader.columnKeys().size() != 2)
            throw JamException.runtime("Exactly two columns are required in aggregate profile [%s].", file);

        for (List<String> columns : reader) {
            HugoSymbol symbol = HugoSymbol.instance(columns.get(0));
            Expression level  = Expression.valueOf(columns.get(1));

            MapUtil.putUnique(profile, symbol, level);
        }

        return new AggregateExpressionModel(profile);
    }

    /**
     * Loads an aggregate expression profile from a data file.
     *
     * @param fileName the name of the file to load.
     *
     * @return the aggregate expression profile contained in the
     * specified data file.
     *
     * @throws RuntimeException unless the specified file contains a
     * valid expression profile.
     */
    public static AggregateExpressionModel load(String fileName) {
        return load(new File(fileName));
    }

    @Override public Expression lookup(TumorBarcode barcode, HugoSymbol symbol) {
        //
        // All tumors have the same expression for a given gene...
        //
        return profile.get(symbol);
    }
}
