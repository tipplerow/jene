
package jene.hugo;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import jam.app.JamEnv;
import jam.app.JamProperties;
import jam.io.TableReader;

import jene.ensembl.EnsemblGeneID;

/**
 * Maintains mappings between HUGO symbols and Ensembl genes.
 */
public final class HugoMaster {
    private final Multimap<HugoSymbol, EnsemblGeneID> map;

    private static HugoMaster global = null;

    private HugoMaster() {
        this.map = HashMultimap.create();
    }

    /**
     * Name of the environment variable that defines the absolute path
     * name for the Hugo master symbol table.  If the system property
     * {@code jene.hugo.masterFile} is also defined, it will override
     * the environment variable.
     */
    public static final String MASTER_FILE_ENV = "JENE_HUGO_MASTER_FILE";

    /**
     * Name of the system property that defines the absolute path
     * name for the Hugo master symbol table. If not defined, the
     * environment variable {@code HUGO_MASTER_FILE} will be used 
     * by default.
     */
    public static final String MASTER_FILE_PROPERTY = "jene.hugo.masterFile";

    /**
     * Returns the global master table defined by system properties or
     * environment variables.
     *
     * @return the global master table defined by system properties or
     * environment variables.
     */
    public static HugoMaster global() {
        if (global == null)
            global = load(resolveMasterFile());

        return global;
    }

    private static String resolveMasterFile() {
        if (JamProperties.isSet(MASTER_FILE_PROPERTY))
            return JamProperties.getRequired(MASTER_FILE_PROPERTY);
        else
            return JamEnv.getRequired(MASTER_FILE_ENV);
    }

    /**
     * Loads the HUGO symbol mappings from a data file.
     *
     * @param masterFile the master file to load.
     *
     * @return the master table of HUGO symbol mappings.
     *
     * @throws RuntimeException if any I/O errors occur.
     */
    public static HugoMaster load(File masterFile) {
        HugoMaster  master = new HugoMaster();
        TableReader reader = TableReader.open(masterFile);

        try {
            master.load(reader);
        }
        finally {
            reader.close();
        }

        return master;
    }

    private void load(TableReader reader) {
        int hugoSymbolIndex = reader.requireColumn(HugoSymbol.COLUMN_NAME);
        int ensemblGeneIndex = reader.requireColumn(EnsemblGeneID.COLUMN_NAME);

        for (List<String> line : reader) {
            HugoSymbol hugoSymbol = HugoSymbol.instance(line.get(hugoSymbolIndex));
            EnsemblGeneID ensemblGeneID = EnsemblGeneID.instance(line.get(ensemblGeneIndex));

            map.put(hugoSymbol, ensemblGeneID);
        }
    }

    /**
     * Loads the HUGO symbol mappings from a data file.
     *
     * @param masterFileName the name of the master file to load.
     *
     * @return the master table of HUGO symbol mappings.
     *
     * @throws RuntimeException if any I/O errors occur.
     */
    public static HugoMaster load(String masterFileName) {
        return load(new File(masterFileName));
    }

    /**
     * Identifies HUGO symbols contained in this table.
     *
     * @param symbol a HUGO symbol of interest.
     *
     * @return {@code true} iff this table contains the specified HUGO
     * symbol.
     */
    public boolean contains(HugoSymbol symbol) {
        return map.containsKey(symbol);
    }

    /**
     * Returns the Ensembl genes corresponding to a given HUGO symbol.
     *
     * @param symbol a HUGO symbol of interest.
     *
     * @return the Ensembl genes corresponding to the specified symbol
     * (an empty collection if this table does not contain the symbol).
     */
    public Collection<EnsemblGeneID> get(HugoSymbol symbol) {
        return Collections.unmodifiableCollection(map.get(symbol));
    }
}
