package List61B;

import AList.AList;

import java.util.ArrayList;
import java.util.List;

/**
 * @author superlit
 * @create 2023/1/13 21:10
 */
public class IsADemo {
    public static void main(String[] args) {
        List61B<String> someList = new SLList<String>();
        someList.addFirst("elk");
        someList.addLast("dwell");
        someList.addLast("on");
        someList.addLast("existential");
        someList.addLast("crises");
        someList.print();
    }
}
