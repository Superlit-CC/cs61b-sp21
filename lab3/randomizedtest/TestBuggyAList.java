package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
  // YOUR TESTS HERE
    @Test
    public void testThreeAddThreeRemove() {
        int n = 3;
        AListNoResizing<Integer> l1 = new AListNoResizing<>();
        BuggyAList<Integer> l2 = new BuggyAList<>();

        for (int i = 0; i < n; i ++ ) {
            l1.addLast(i);
            l2.addLast(i);
        }

        for (int i = 0; i < n; i ++ ) {
            int t1 = l1.removeLast();
            int t2 = l2.removeLast();
            assertEquals(t1, t2);
        }
    }

    @Test
    public void randomizedTest() {
        AListNoResizing<Integer> L = new AListNoResizing<>();
        BuggyAList<Integer> L2 = new BuggyAList<>();

        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                L2.addLast(randVal);
//                System.out.println("addLast(" + randVal + ")");
            } else if (operationNumber == 1) {
                // size
                int size = L.size();
                int size2 = L2.size();
//                System.out.println("size: " + size);
                assertEquals(size, size2);
            } else if (operationNumber == 2) {
                // getLast
                if (L.size() == 0 || L2.size() == 0) continue;
                int lastVal = L.getLast();
                int lastVal2 = L2.getLast();
//                System.out.println("getLast(" + lastVal + ")");
                assertEquals(lastVal, lastVal2);
            } else if (operationNumber == 3) {
                // removeLast
                if (L.size() == 0) continue;
                int lastVal = L.removeLast();
                int lastVal2 = L2.removeLast();
//                System.out.println("removeLast(" + lastVal + ")");
                assertEquals(lastVal, lastVal2);
            }
        }
    }
}
