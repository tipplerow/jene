
package jene.ensembl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import jam.lang.JamException;
import jam.util.MapUtil;

import jene.hugo.HugoSymbol;
import jene.fasta.FastaPeptideReader;
import jene.fasta.FastaPeptideRecord;

/**
 * Manages the human proteome data from Ensembl.
 */
public final class EnsemblProteinDb {
    // Protein and transcript keys map uniquely to peptide structures...
    private final Map<EnsemblProteinID, EnsemblProteinRecord> proteinRecordMap;
    private final Map<EnsemblTranscriptID, EnsemblProteinRecord> transcriptRecordMap;

    // ...while gene identifiers may map to multiple peptide structures.
    private final Multimap<HugoSymbol, EnsemblProteinRecord> hugoRecordMap;
    private final Multimap<EnsemblGeneID, EnsemblProteinRecord> geneRecordMap;

    // The GRCh38 reference Ensembl database defines a mapping from gene to HUGO symbol...
    private final Map<EnsemblGeneID, HugoSymbol> geneHugoMap;

    private static EnsemblProteinDb reference = null;

    private EnsemblProteinDb() {
        this.proteinRecordMap = new HashMap<EnsemblProteinID, EnsemblProteinRecord>();
        this.transcriptRecordMap = new HashMap<EnsemblTranscriptID, EnsemblProteinRecord>();

        this.hugoRecordMap = HashMultimap.create();
        this.geneRecordMap = HashMultimap.create();

        this.geneHugoMap = new HashMap<EnsemblGeneID, HugoSymbol>();
    }

    /**
     * Creates a database of Ensembl records from FASTA files.
     *
     * <p>The first file is the <em>primary</em> file: 
     * files are <em>secondary</em> files: they may contain duplicate
     * protein or transcript identifiers, but those records will be
     * ignored.
     *
     * @param primaryFile the primary FASTA file:  all protein and
     * transcript identifiers in this file must be unique.
     *
     * @param secondaryFiles optional secondary FASTA files:  they may
     * contain duplicate protein or transcript identifiers, which will
     * be ignored.
     *
     * @return the database of Ensembl records.
     *
     * @throws RuntimeException if any I/O errors occur.
     */
    public static EnsemblProteinDb load(String primaryFile, String... secondaryFiles) {
        EnsemblProteinDb database = new EnsemblProteinDb();
        database.loadPrimary(primaryFile);

        for (String secondaryFile : secondaryFiles)
            database.loadSecondary(secondaryFile);

        return database;
    }

    private void loadPrimary(String fastaFile) {
        try (FastaPeptideReader reader = FastaPeptideReader.open(fastaFile)) {
            loadPrimary(reader);
        }
    }

    private void loadSecondary(String fastaFile) {
        try (FastaPeptideReader reader = FastaPeptideReader.open(fastaFile)) {
            loadSecondary(reader);
        }
    }

    private void loadPrimary(Iterable<FastaPeptideRecord> records) {
        for (FastaPeptideRecord record : records)
            addPrimary(record);
    }

    private void loadSecondary(Iterable<FastaPeptideRecord> records) {
        for (FastaPeptideRecord record : records)
            addSecondary(record);
    }

    private void addPrimary(FastaPeptideRecord fastaRecord) {
        EnsemblProteinRecord ensemblRecord = EnsemblProteinRecord.parse(fastaRecord);

        mapProtein(ensemblRecord);
        mapTranscript(ensemblRecord);
        mapGene(ensemblRecord);
        mapHugo(ensemblRecord);
    }

    private void mapProtein(EnsemblProteinRecord record) {
        EnsemblProteinID proteinID = record.getEnsemblProteinID();

        if (proteinRecordMap.put(proteinID, record) != null)
            throw JamException.runtime("Duplicate protein ID: [%s]", proteinID);
    }

    private void mapTranscript(EnsemblProteinRecord record) {
        EnsemblTranscriptID transcriptID = record.getEnsemblTranscriptID();

        if (transcriptRecordMap.put(transcriptID, record) != null)
            throw JamException.runtime("Duplicate transcript ID: [%s]", transcriptID);
    }

    private void mapGene(EnsemblProteinRecord record) {
        //
        // There will be multiple records for a single gene...
        //
        geneRecordMap.put(record.getEnsemblGeneID(), record);
    }

    private void mapHugo(EnsemblProteinRecord record) {
        HugoSymbol hugo = record.getHugoSymbol();
        EnsemblGeneID gene = record.getEnsemblGeneID();

        if (hugo != null) {
            hugoRecordMap.put(hugo, record);
            MapUtil.putUnique(geneHugoMap, gene, hugo);
        }
    }

    private void addSecondary(FastaPeptideRecord fastaRecord) {
        EnsemblProteinRecord ensemblRecord = EnsemblProteinRecord.parse(fastaRecord);

        if (isUniqueProtein(ensemblRecord) && isUniqueTranscript(ensemblRecord))
            addPrimary(fastaRecord);
    }

    private boolean isUniqueProtein(EnsemblProteinRecord ensemblRecord) {
        return !proteinRecordMap.containsKey(ensemblRecord.getEnsemblProteinID());
    }

    private boolean isUniqueTranscript(EnsemblProteinRecord ensemblRecord) {
        return !transcriptRecordMap.containsKey(ensemblRecord.getEnsemblTranscriptID());
    }

    /**
     * Returns the reference human proteome.
     *
     * @return the reference human proteome.
     */
    public static synchronized EnsemblProteinDb reference() {
        if (reference == null)
            reference = loadReference();

        return reference;
    }

    private static EnsemblProteinDb loadReference() {
        String primaryFile = EnsemblLocator.resolvePrimaryProteomeFileName();
        String secondaryFile = EnsemblLocator.resolveSecondaryProteomeFileName();

        if (secondaryFile != null)
            return load(primaryFile, secondaryFile);
        else
            return load(primaryFile);
    }

    /**
     * Identifies genes in this map.
     *
     * @param gene a gene of interest.
     *
     * @return {@code true} iff this map contains the specified gene.
     */
    public boolean contains(EnsemblGeneID gene) {
        return geneRecordMap.containsKey(gene);
    }

    /**
     * Identifies proteins in this map.
     *
     * @param protein a protein of interest.
     *
     * @return {@code true} iff this map contains the specified protein.
     */
    public boolean contains(EnsemblProteinID protein) {
        return proteinRecordMap.containsKey(protein);
    }

    /**
     * Identifies transcripts in this map.
     *
     * @param transcript a transcript of interest.
     *
     * @return {@code true} iff this map contains the specified transcript.
     */
    public boolean contains(EnsemblTranscriptID transcript) {
        return transcriptRecordMap.containsKey(transcript);
    }

    /**
     * Identifies HUGO symbols in this map.
     *
     * @param hugo a HUGO symbol of interest.
     *
     * @return {@code true} iff this map contains the specified HUGO
     * symbol.
     */
    public boolean contains(HugoSymbol hugo) {
        return hugoRecordMap.containsKey(hugo);
    }

    /**
     * Counts the number of peptides mapped to a given gene.
     *
     * @param gene a gene of interest.
     *
     * @return the number of peptides mapped to the given gene.
     */
    public int count(EnsemblGeneID gene) {
        return geneRecordMap.get(gene).size();
    }

    /**
     * Counts the number of peptides mapped to a given HUGO symbol.
     *
     * @param hugo a HUGO symbol of interest.
     *
     * @return the number of peptides mapped to the given HUGO symbol.
     */
    public int count(HugoSymbol hugo) {
        return hugoRecordMap.get(hugo).size();
    }

    /**
     * Returns a read-only view of the records mapped to a given
     * gene.
     *
     * @param gene the gene of interest.
     *
     * @return a read-only collection containing the records mapped
     * to the specified gene (an empty collection if the gene is not
     * mapped).
     */
    public Collection<EnsemblProteinRecord> get(EnsemblGeneID gene) {
        return Collections.unmodifiableCollection(geneRecordMap.get(gene));
    }

    /**
     * Returns a read-only view of the records mapped to a collection
     * of genes.
     *
     * @param genes the genes of interest.
     *
     * @return a read-only collection containing the records mapped to
     * the specified genes (an empty collection if none of the genes
     * are mapped).
     */
    public Collection<EnsemblProteinRecord> get(Collection<EnsemblGeneID> genes) {
        List<EnsemblProteinRecord> records = new ArrayList<EnsemblProteinRecord>();

        for (EnsemblGeneID gene : genes)
            records.addAll(get(gene));

        return records;
    }

    /**
     * Returns the record mapped to a given protein.
     *
     * @param protein the protein of interest.
     *
     * @return the record mapped to the specified protein (or
     * {@code null} if there is no mapping).
     */
    public EnsemblProteinRecord get(EnsemblProteinID protein) {
        return proteinRecordMap.get(protein);
    }

    /**
     * Returns the record mapped to a given transcript.
     *
     * @param transcript the transcript of interest.
     *
     * @return the record mapped to the specified transcript (or
     * {@code null} if there is no mapping).
     */
    public EnsemblProteinRecord get(EnsemblTranscriptID transcript) {
        return transcriptRecordMap.get(transcript);
    }

    /**
     * Returns a read-only view of the records mapped to a given
     * HUGO symbol.
     *
     * @param hugo the HUGO symbol of interest.
     *
     * @return a read-only collection containing the records mapped
     * to the specified HUGO symbol (an empty collection if the HUGO
     * symbol is not mapped).
     */
    public Collection<EnsemblProteinRecord> get(HugoSymbol hugo) {
        return Collections.unmodifiableCollection(hugoRecordMap.get(hugo));
    }

    /**
     * Returns the HUGO symbol mapped to a given gene.
     *
     * @param gene the gene of interest.
     *
     * @return the HUGO symbol mapped to the specified gene, or
     * {@code null} if there is no mapping.
     */
    public HugoSymbol getHugo(EnsemblGeneID gene) {
        return geneHugoMap.get(gene);
    }

    /**
     * Returns the HUGO symbol mapped to a given transcript.
     *
     * @param transcript the transcript of interest.
     *
     * @return the HUGO symbol mapped to the specified transcript, or
     * {@code null} if there is no mapping.
     */
    public HugoSymbol getHugo(EnsemblTranscriptID transcript) {
        EnsemblProteinRecord record = get(transcript);

        if (record != null)
            return getHugo(record.getEnsemblGeneID());
        else
            return null;
    }

    /**
     * Returns a read-only view of the genes in this map.
     *
     * @return a read-only set containing the genes in this map.
     */
    public Set<EnsemblGeneID> geneSet() {
        return Collections.unmodifiableSet(geneRecordMap.keySet());
    }

    /**
     * Returns a read-only view of the HUGO symbols in this map.
     *
     * @return a read-only set containing the HUGO symbols in this
     * map.
     */
    public Set<HugoSymbol> hugoSet() {
        return Collections.unmodifiableSet(hugoRecordMap.keySet());
    }

    /**
     * Returns a read-only view of the proteins in this map.
     *
     * @return a read-only set containing the proteins in this map.
     */
    public Set<EnsemblProteinID> proteinSet() {
        return Collections.unmodifiableSet(proteinRecordMap.keySet());
    }

    /**
     * Returns the record mapped to a given protein.
     *
     * @param protein the protein of interest.
     *
     * @return the record mapped to the specified protein.
     *
     * @throws RuntimeException unless the correponding protein
     * exists.
     */
    public EnsemblProteinRecord require(EnsemblProteinID protein) {
        EnsemblProteinRecord record = get(protein);

        if (record != null)
            return record;
        else
            throw JamException.runtime("Unmapped protein: [%s].", protein.getKey());
    }

    /**
     * Returns the record mapped to a given transcript.
     *
     * @param transcript the transcript of interest.
     *
     * @return the record mapped to the specified transcript.
     *
     * @throws RuntimeException unless the correponding protein
     * exists.
     */
    public EnsemblProteinRecord require(EnsemblTranscriptID transcript) {
        EnsemblProteinRecord record = get(transcript);

        if (record != null)
            return record;
        else
            throw JamException.runtime("Unmapped transcript: [%s].", transcript.getKey());
    }

    /**
     * Returns a read-only view of the transcripts in this map.
     *
     * @return a read-only set containing the transcripts in this map.
     */
    public Set<EnsemblTranscriptID> transcriptSet() {
        return Collections.unmodifiableSet(transcriptRecordMap.keySet());
    }

    /**
     * Returns the number of records in this database.
     *
     * @return the number of records in this database.
     */
    public int size() {
        return proteinRecordMap.size();
    }
}
