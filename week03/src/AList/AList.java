package AList;

import List61B.List61B;

import java.util.Arrays;

/**
 * Array based list.
 * @author superlit
 * @create 2023/1/13 15:47
 */
public class AList<T> implements List61B<T> {
    private T[] items;
    private int size;
    private static int RFACTOR = 2;

    /** create an AList. */
    public AList() {
        items = (T[]) new Object[100];
        size = 0;
    }

    /** resize the array to the target capacity. */
    private void resize(int capacity) {
        T[] a = (T[]) new Object[capacity];
        System.arraycopy(items, 0, a, 0, size);
        items = a;
    }

    @Override
    public void addFirst(T x) {
        insert(x, 0);
    }

    /** insert x to the back of the list. */
    @Override
    public void addLast(T x) {
        if (size == items.length) {
            resize(size * RFACTOR);
        }
        items[size] = x;
        size ++ ;
    }

    @Override
    public T getFirst() {
        return get(0);
    }

    /** return the item from the back of the list. */
    @Override
    public T getLast() {
        return items[size - 1];
    }

    /** return the ith item in the list. */
    @Override
    public T get(int i) {
        return items[i];
    }

    @Override
    public void insert(T x, int position) {
        T[] newItems = (T[]) new Object[items.length + 1];

        System.arraycopy(items, 0, newItems, 0, position);
        newItems[position] = x;

        System.arraycopy(items, position, newItems, position + 1, items.length - position);
        items = newItems;
    }

    /** return the size of the list. */
    @Override
    public int size() {
        return size;
    }

    /** remove the last item of the list and return it. */
    @Override
    public T removeLast() {
        T x = getLast();
        items[size - 1] = null;  // save storage
        size -- ;
        return x;
    }
}
