package deque;

import org.junit.Test;
import java.util.Comparator;
import static org.junit.Assert.*;

/**
 * @author superlit
 * @create 2023/2/2 21:15
 */
public class MaxArrayDequeTest {
    private static class myIntegerComparator implements Comparator<Integer> {
        @Override
        public int compare(Integer o1, Integer o2) {
            return -o1.compareTo(o2);
        }
    }

    @Test
    public void testMultiCmp() {
        Comparator<Integer> myIntegerComparator = new myIntegerComparator();
        MaxArrayDeque<Integer> mad = new MaxArrayDeque<>(myIntegerComparator);
        assertEquals(null, mad.max());
        mad.addLast(-1);
        mad.addLast(1);
        mad.addLast(100);
        mad.addLast(77);
        mad.addLast(-99);
        assertEquals(Integer.valueOf(-99), mad.max());
        assertEquals(Integer.valueOf(100), mad.max(Integer::compareTo));
    }
}
