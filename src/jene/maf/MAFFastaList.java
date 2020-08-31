
package jene.maf;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Provides an immutable list of {@code MAFFastaRecord}s.
 */
public final class MAFFastaList extends AbstractList<MAFFastaRecord> {
    private final List<MAFFastaRecord> elements;

    private MAFFastaList(List<MAFFastaRecord> records, boolean copy) {
        if (copy)
            this.elements = new ArrayList<MAFFastaRecord>(records);
        else
            this.elements = records;
    }

    /**
     * The single empty list.
     */
    public static final MAFFastaList EMPTY = new MAFFastaList(List.of(), false);

    /**
     * Creates a new immutable {@code MAFFastaRecord} list.
     *
     * @param records the records that compose the list.
     *
     * @return a new immutable {@code MAFFastaRecord} list with
     * records arranged in the order returned by the iterator of
     * the input collection.
     */
    public static MAFFastaList create(Collection<MAFFastaRecord> records) {
        return new MAFFastaList(new ArrayList<MAFFastaRecord>(records), false);
    }

    /**
     * Sorts the records in this list into their natural order and
     * returns them in a new list; this list is unchanged.
     *
     * @return a new list containing the records of this list in
     * their natural order.
     */
    public MAFFastaList sort() {
        List<MAFFastaRecord> sorted = new ArrayList<MAFFastaRecord>(elements);
        sorted.sort(MAFFastaRecord.BARCODE_COMPARATOR);

        return new MAFFastaList(sorted, false);
    }

    @Override public MAFFastaRecord get(int index) {
        return elements.get(index);
    }

    @Override public int size() {
        return elements.size();
    }
}
