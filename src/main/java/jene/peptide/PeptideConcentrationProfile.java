
package jene.peptide;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jam.app.JamLogger;
import jam.io.TableReader;
import jam.io.TableWriter;
import jam.lang.JamException;

import jene.chem.Concentration;

/**
 * Maps peptides to cellular concentrations.
 */
public final class PeptideConcentrationProfile {
    private final Map<Peptide, Concentration> map;

    private static final DecimalFormat CONCENTRATION_FORMAT = new DecimalFormat("#0.0#####E0");

    private PeptideConcentrationProfile(Map<Peptide, Concentration> map) {
        this.map = map;
    }

    /**
     * The single empty concentration profile.
     */
    public static PeptideConcentrationProfile EMPTY =
        new PeptideConcentrationProfile(Collections.emptyMap());

    /**
     * Creates a new concentration profile from a collection of
     * concentration records.
     *
     * <p>The concentration records in the input collection may
     * contain duplicate peptides: all duplicates for a peptide
     * will be grouped together and the total concentration will
     * by mapped to that peptide.
     *
     * @param concentrations the peptide concentrations to include
     * in the profile.
     *
     * @return a new concentration profile.
     */
    public static PeptideConcentrationProfile create(Collection<PeptideConcentration> concentrations) {
        PeptideConcentrationBuilder builder =
            PeptideConcentrationBuilder.create();

        for (PeptideConcentration concRecord : concentrations)
            builder.add(concRecord);

        return builder.build();
    }

    /**
     * Creates a new concentration profile from an existing
     * concentration map.
     *
     * @param map a mapping of peptides to concentration.
     *
     * @return a new concentration profile.
     */
    public static PeptideConcentrationProfile create(Map<Peptide, Concentration> map) {
        return new PeptideConcentrationProfile(new HashMap<Peptide, Concentration>(map));
    }

    /**
     * Loads a peptide concentration profile from a data file.
     *
     * @param file the data file to load.
     *
     * @return the peptide concentration profile stored in the
     * specified data file.
     */
    public static PeptideConcentrationProfile load(File file) {
        JamLogger.info("Loading peptide concentration profile [%s]...", file.getName());

        try (TableReader reader = TableReader.open(file)) {
            return load(reader);
        }
    }

    private static PeptideConcentrationProfile load(TableReader reader) {
        if (reader.ncol() != 2)
            throw JamException.runtime("Exactly two columns are required for a concentration profile.");

        PeptideConcentrationBuilder builder =
            PeptideConcentrationBuilder.create();

        for (List<String> fields : reader) {
            Peptide peptide = Peptide.instance(fields.get(0));
            Concentration concentration = Concentration.parse(fields.get(1));

            builder.add(peptide, concentration);
        }

        return builder.build();
    }

    /**
     * Loads a peptide concentration profile from a data file.
     *
     * @param fileName the name of the data file to load.
     *
     * @return the peptide concentration profile stored in the
     * specified data file.
     */
    public static PeptideConcentrationProfile load(String fileName) {
        return load(new File(fileName));
    }

    /**
     * Identifies peptides in this profile.
     *
     * @param peptide the peptide of interest.
     *
     * @return {@code true} iff this profile contains the specified
     * peptide.
     */
    public boolean contains(Peptide peptide) {
        return map.containsKey(peptide);
    }

    /**
     * Returns the concentration of a given peptide.
     *
     * @param peptide the peptide of interest.
     *
     * @return the concentration of the specified peptide
     * ({@code Concentration.ZERO}, not {@code null}, if
     * this profile does not contain the given peptide.
     */
    public Concentration get(Peptide peptide) {
        Concentration conc = map.get(peptide);

        if (conc != null)
            return conc;
        else
            return Concentration.ZERO;
    }

    /**
     * Returns the number of peptides in this concentration profile.
     *
     * @return the number of peptides in this concentration profile.
     */
    public int size() {
        return map.size();
    }

    /**
     * Stores this peptide concentration profile in a data file.
     *
     * @param file the data file to write (previous contents will be
     * erased).
     */
    public void store(File file) {
        JamLogger.info("Storing peptide concentration profile [%s]...", file.getName());

        try (TableWriter writer = TableWriter.open(file)) {
            store(writer);
        }
    }

    private void store(TableWriter writer) {
        writeHeader(writer);
        writeConcentration(writer);
    }

    private void writeHeader(TableWriter writer) {
        writer.println("Hugo_Symbol", "Concentration");
    }

    private void writeConcentration(TableWriter writer) {
        for (Map.Entry<Peptide, Concentration> entry : map.entrySet())
            writeConcentration(writer, entry.getKey(), entry.getValue());
    }

    private void writeConcentration(TableWriter writer, Peptide pep, Concentration conc) {
        String pepStr = pep.formatString();
        String concStr = conc.format(CONCENTRATION_FORMAT);

        writer.println(pepStr, concStr);
    }

    /**
     * Stores this peptide concentration profile in a data file.
     *
     * @param fileName the name of the data file to write (previous
     * contents will be erased).
     */
    public void store(String fileName) {
        store(new File(fileName));
    }

    /**
     * Returns a read-only view of the concentration mappings in this
     * profile.
     *
     * @return all peptide-concentration mappings in this profile in
     * an unmodifiable set.
     */
    public Set<Map.Entry<Peptide, Concentration>> viewEntries() {
        return Collections.unmodifiableSet(map.entrySet());
    }

    /**
     * Returns a read-only view of the peptides in this profile.
     *
     * @return all peptides in this profile in an unmodifiable set.
     */
    public Set<Peptide> viewPeptides() {
        return Collections.unmodifiableSet(map.keySet());
    }
}
