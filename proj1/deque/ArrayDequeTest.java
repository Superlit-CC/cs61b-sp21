package deque;

import org.junit.Test;
import edu.princeton.cs.algs4.StdRandom;
import static org.junit.Assert.*;

/**
 * @author superlit
 * @create 2023/1/16 20:49
 */
public class ArrayDequeTest {
    @Test
    public void randomizedTest() {
        LinkedListDeque<Integer> L = new LinkedListDeque<>();
        ArrayDeque<Integer> L2 = new ArrayDeque<>();

        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 7);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                L2.addLast(randVal);
            } else if (operationNumber == 1) {
                // addFirst
                int randVal = StdRandom.uniform(0, 100);
                L.addFirst(randVal);
                L2.addFirst(randVal);
            } else if (operationNumber == 2) {
                // size
                assertEquals(L.size(), L2.size());
            } else if (operationNumber == 3) {
                // removeLast
                if (L.size() == 0) continue;
                assertEquals(L.removeLast(), L2.removeLast());
            } else if (operationNumber == 4) {
                // removeFirst
                if (L.size() == 0) continue;
                assertEquals(L.removeFirst(), L2.removeFirst());
            } else if (operationNumber == 5) {
                // isEmpty
                assertEquals(L.isEmpty(), L2.isEmpty());
            } else if (operationNumber == 6) {
                // get
                int randVal = StdRandom.uniform(0, 100);
                assertEquals(L.get(randVal), L2.get(randVal));
            }
        }
    }

    @Test
    public void addRemoveTest() {
        int N = 50000;
        ArrayDeque<Integer> L = new ArrayDeque<>();
        for (int i = 0; i < N; i ++ ) {
            L.addFirst(i);
            L.addLast(i);
        }
        for (int i = 0; i < N - 1; i ++ ) {
            L.removeFirst();
            L.removeLast();
        }
    }

    @Test
    public void multipleParamTest() {
        ArrayDeque<String>  lld1 = new ArrayDeque<String>();
        ArrayDeque<Double>  lld2 = new ArrayDeque<Double>();
        ArrayDeque<Boolean> lld3 = new ArrayDeque<Boolean>();

        lld1.addFirst("string");
        lld2.addFirst(3.14159);
        lld3.addFirst(true);

        String s = lld1.removeFirst();
        double d = lld2.removeFirst();
        boolean b = lld3.removeFirst();
    }

    @Test
    public void equalsTest() {
        Deque<Integer> d1 = new ArrayDeque<>();
        Deque<Integer> d2 = new LinkedListDeque<>();
        Deque<String> d3 = new LinkedListDeque<>();
        for (int i = 0; i < 100; i ++ ) {
            d1.addFirst(i);
            d2.addFirst(i);
            d3.addFirst("HHHH");
        }
        assertEquals(true, d2.equals(d1));
        assertEquals(true, d1.equals(d2));
        assertEquals(false, d1.equals(d3));
        assertEquals(false, d2.equals(d3));
    }
}
