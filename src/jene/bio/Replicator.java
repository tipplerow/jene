
package jene.bio;

import java.util.Arrays;
import java.util.List;

import jam.lang.OrdinalIndex;

/**
 * Represents a biological entity that lives forever and clones itself
 * (produces one copy of itself) at each time step.
 */
public class Replicator extends Propagator {
    private static OrdinalIndex ordinalIndex = OrdinalIndex.create();

    private Replicator(Replicator parent) {
        super(ordinalIndex.next(), parent);
    }

    /**
     * Creates a founding replicator.
     *
     * @return a new founding replicator.
     */
    public static Replicator create() {
        return new Replicator(null);
    }

    /**
     * Creates a (daughter) copy of this replicator.
     *
     * @return a (daughter) copy of this replicator.
     */
    public Replicator replicate() {
        return new Replicator(this);
    }

    @Override public Replicator getFounder() {
        return (Replicator) super.getFounder();
    }

    @Override public Replicator getParent() {
        return (Replicator) super.getParent();
    }

    @SuppressWarnings("unchecked") 
    @Override public List<Replicator> traceLineage() {
        return (List<Replicator>) super.traceLineage();
    }

    @SuppressWarnings("unchecked") 
    @Override public List<Replicator> traceLineage(int firstGeneration) {
        return (List<Replicator>) super.traceLineage(firstGeneration);
    }
}
