package flik;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author superlit
 * @create 2023/2/2 20:09
 */
public class TestFlik {
    @Test
    public void testFlik() {
        for (int i = 0; i < 1000; i ++ ) {
            assertEquals(true, Flik.isSameNumber(i, i));
        }
    }
}
