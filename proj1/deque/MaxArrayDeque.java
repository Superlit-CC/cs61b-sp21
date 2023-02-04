package deque;

import java.util.Comparator;

/**
 * @author superlit
 * @create 2023/1/19 16:46
 */
public class MaxArrayDeque<T> extends ArrayDeque<T> {
    private Comparator<T> cmp;

    /** creates a MaxArrayDeque with the given Comparator. */
    public MaxArrayDeque(Comparator<T> c) {
        super();
        this.cmp = c;
    }

    /** returns the maximum element in the deque as governed by the previously given Comparator.
     * If the MaxArrayDeque is empty, simply return null. */
    public T max() {
        return max(cmp);
    }

    /** returns the maximum element in the deque as governed by the parameter Comparator c.
     * If the MaxArrayDeque is empty, simply return null. */
    public T max(Comparator<T> c) {
        if (isEmpty()) {
            return null;
        }

        T res = get(0);
        for (int i = 1; i < size(); i ++ ) {
            T temp = get(i);
            if (c.compare(res, temp) < 0) {
                res = temp;
            }
        }
        return res;
    }
}
