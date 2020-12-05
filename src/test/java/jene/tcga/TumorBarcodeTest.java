
package jene.tcga;

import java.util.List;

import org.junit.*;
import static org.junit.Assert.*;

public class TumorBarcodeTest {
    private static final TumorBarcode barcode1 = TumorBarcode.instance("Barcode1");
    private static final TumorBarcode barcode2 = TumorBarcode.instance("Barcode2");
    private static final TumorBarcode barcode3 = TumorBarcode.instance("Barcode3");

    private static final String BARCODE_FILE = "data/test/barcodes.txt";

    @Test public void testLoad() {
        List<TumorBarcode> barcodes = TumorBarcode.load(BARCODE_FILE);
        assertEquals(List.of(barcode1, barcode2, barcode3), barcodes);
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jene.tcga.TumorBarcodeTest");
    }
}
