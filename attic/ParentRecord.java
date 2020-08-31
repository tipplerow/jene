
package jam.bio;

import jam.util.RegexUtil;

/**
 * Encapsulates a parent-child lineage relationship.
 */
public final class ParentRecord {
    private final long childIndex;
    private final long parentIndex;

    private ParentRecord(long childIndex, long parentIndex) {
        this.childIndex  = childIndex;
        this.parentIndex = parentIndex;
    }

    /**
     * Creates a new record for a given propagator.
     *
     * @param propagator the propagator to examine.
     *
     * @return the parent-child record for the given propagator.
     */
    public static ParentRecord create(Propagator propagator) {
        long childIndex  = propagator.getIndex();
        long parentIndex = propagator.isFounder() ? childIndex : propagator.getParent().getIndex();

        return new ParentRecord(childIndex, parentIndex);
    }

    /**
     * Returns the header line for parent lineage files.
     *
     * @return the header line for parent lineage files.
     */
    public static String header() {
        return "childIndex,parentIndex";
    }

    /**
     * Creates a new record by parsing a line from a parent lineage
     * file.
     *
     * @param s the line to parse.
     *
     * @return the record defined by the input string.
     *
     * @throws IllegalArgumentException unless the input string is a
     * valid representation of a record.
     */
    public static ParentRecord parse(String s) {
        String[] fields = RegexUtil.COMMA.split(s);

        if (fields.length != 2)
            throw new IllegalArgumentException("Invalid record: [" + s + "].");

        long childIndex  = Long.parseLong(fields[0]);
        long parentIndex = Long.parseLong(fields[1]);

        return new ParentRecord(childIndex, parentIndex);
    }

    /**
     * Formats this record for writing to a parent lineage file.
     *
     * @return the canonical string representation for this record.
     */
    public String format() {
        return String.format("%d,%d", getChildIndex(), getParentIndex());
    }

    /**
     * Returns the index of the child component.
     *
     * @return the index of the child component.
     */
    public long getChildIndex() {
        return childIndex;
    }

    /**
     * Returns the index of the parent component.
     *
     * @return the index of the parent component.
     */
    public long getParentIndex() {
        return parentIndex;
    }
}
