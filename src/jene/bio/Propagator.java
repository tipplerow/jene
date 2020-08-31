
package jene.bio;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import jam.lang.Ordinal;

/**
 * Represents a biologial entity that can propagate itself, e.g., a
 * cell or virus.
 */
public abstract class Propagator extends Ordinal {
    private final int generation;
    private final Propagator parent;
    private final Propagator founder;

    /**
     * Creates all propagators.
     *
     * @param index the ordinal index of the propagator.
     *
     * @param parent the parent of the new propagator; {@code null}
     * for a founding propagator.
     */
    protected Propagator(long index, Propagator parent) {
        super(index);

        this.parent = parent;
        this.founder = deriveFounder(parent);
        this.generation = deriveGeneration(parent);
    }

    private Propagator deriveFounder(Propagator parent) {
        if (parent != null)
            return parent.founder;
        else
            return this;
    }

    private static int deriveGeneration(Propagator parent) {
        if (parent != null)
            return parent.generation + 1;
        else
            return 0;
    }

    /**
     * Returns the generation index for this propagator, with index
     * {@code 0} denoting the founding propagator.
     *
     * @return the generation index for this propagator.
     */
    public final int getGeneration() {
        return generation;
    }

    /**
     * Returns the founder of this propagator.
     *
     * <p>Subclasses may override this method and cast the return
     * value to the appropriate class.
     *
     * @return the founder of this propagator.
     */
    public Propagator getFounder() {
        return founder;
    }

    /**
     * Returns the parent of this propagator ({@code null} if this is
     * a founding propagator).
     *
     * <p>Subclasses may override this method and cast the return
     * value to the appropriate class.
     *
     * @return the parent of this propagator ({@code null} if this is
     * a founding propagator).
     */
    public Propagator getParent() {
        return parent;
    }

    /**
     * Identifies founders of new lineages.
     *
     * @return {@code true} iff this is propagator founded a new
     * lineage.
     */
    public final boolean isFounder() {
        return parent == null;
    }

    /**
     * Traces the lineage of this propagator.
     *
     * @return a list containing all parents of this propagator (and
     * this propagator itself), ordered by generation starting with
     * the founder.
     */
    public List<? extends Propagator> traceLineage() {
        return traceLineage(0);
    }

    /**
     * Traces the lineage of this propagator back to a specific
     * generation.
     *
     * @param firstGeneration the earliest generation to include in
     * the lineage.
     *
     * @return a list containing all parents of this propagator (and
     * this propagator itself), ordered by generation starting with
     * the propagator at the given generation.
     *
     * @throws IllegalArgumentException if the first generation is
     * negative.
     */
    public List<? extends Propagator> traceLineage(int firstGeneration) {
        if (firstGeneration < 0)
            throw new IllegalArgumentException("First generation must be non-negative.");

        Propagator propagator = this;
        LinkedList<Propagator> lineage = new LinkedList<Propagator>();

        while (propagator != null && propagator.getGeneration() >= firstGeneration) {
            //
            // Push the current cycle onto the front of the
            // list...
            //
            lineage.addFirst(propagator);
            propagator = propagator.getParent();
        }

        // Return an ArrayList which will provide better performance
        // in most situations...
        return new ArrayList<Propagator>(lineage);
    }
}
