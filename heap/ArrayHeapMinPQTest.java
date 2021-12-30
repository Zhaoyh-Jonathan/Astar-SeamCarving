package heap;

import org.junit.Test;

import static org.junit.Assert.*;

public class ArrayHeapMinPQTest {
    /* Be sure to write randomized tests that can handle millions of items. To
     * test for runtime, compare the runtime of NaiveMinPQ vs ArrayHeapMinPQ on
     * a large input of millions of items. */
    @Test
    public void testSimple() {
        ArrayHeapMinPQ<String> myHeap = new ArrayHeapMinPQ<>();
        myHeap.add("Coconut", 13);
        myHeap.add("Nutmeg", 20);
        myHeap.add("Salt", 3);
        myHeap.add("BlackPepper", 5);

        assertTrue(myHeap.contains("Salt"));
        assertFalse(myHeap.contains("Salsa"));

        assertEquals(4, myHeap.size());

        assertEquals("Salt", myHeap.getSmallest());


        myHeap.changePriority("Salt", 99);
        assertEquals("BlackPepper", myHeap.getSmallest());
        assertEquals("BlackPepper", myHeap.removeSmallest());
    }
}
