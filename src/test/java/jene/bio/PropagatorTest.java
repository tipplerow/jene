
package jene.bio;

import java.util.ArrayList;
import java.util.List;

import org.junit.*;
import static org.junit.Assert.*;

public class PropagatorTest {
    private static final Replicator firstCell = Replicator.create();

    @Test public void testFirst() {
        assertEquals(0, firstCell.getIndex());
    }

    @Test public void testLineage() {
        List<Replicator> lineage = createLineage(10);
        Replicator lastCell = lineage.get(9);

        assertEquals(lineage, lastCell.traceLineage());

        assertEquals(lineage.subList(7, 10), lastCell.traceLineage(7));
        assertEquals(lineage.subList(2, 10), lastCell.traceLineage(2));

        assertEquals(0, lastCell.traceLineage(10).size());
        assertEquals(1, lastCell.traceLineage(9).size());
        assertEquals(2, lastCell.traceLineage(8).size());
        assertEquals(8, lastCell.traceLineage(2).size());
        assertEquals(9, lastCell.traceLineage(1).size());
    }

    private List<Replicator> createLineage(int size) {
        List<Replicator> lineage = new ArrayList<Replicator>(size);
        lineage.add(Replicator.create());

        for (int index = 1; index < size; ++index)
            lineage.add(lineage.get(index - 1).replicate());

        return lineage;
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jene.bio.PropagatorTest");
    }
}
