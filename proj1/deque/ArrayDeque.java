package deque;

import java.util.Arrays;

/**
 * @author superlit
 * @create 2023/1/16 19:21
 */
public class ArrayDeque<T> implements Deque<T> {
    T[] items;
    int size, head, tail;
    private static int RFACTOR = 2;

    public ArrayDeque() {
        items = (T[]) new Object[8];
        size = 0;
        head = 0;
        tail = items.length - 1;
    }

    private void resize(int capability) {
        T[] a = (T[]) new Object[capability];
        for (int i = 0; i < size; i ++ ) {
            tail = (tail + 1) % items.length;
            a[i] = items[tail];
        }
        items = a;
        head = size;
        tail = a.length - 1;
    }

    @Override
    public void addFirst(T item) {
        if (size == items.length) {
            resize(size * RFACTOR);
        }
        items[head] = item;
        head = (head + 1) % items.length;
        size ++ ;
    }

    @Override
    public void addLast(T item) {
        if (size == items.length) {
            resize(size * RFACTOR);
        }
        items[tail] = item;
        tail = (tail - 1 + items.length) % items.length;
        size ++ ;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        int index = head;
        for (int i = 0; i < size(); i ++ ) {
            System.out.print(items[index]);
            index = (index - 1 + items.length) % items.length;
        }
        System.out.println();
    }

    @Override
    public T removeFirst() {
        if (size == 0) {
            return null;
        }
        if ((size < items.length / 4) && (size >= 16)) {
            resize(items.length / 4);
        }
        head = (head - 1 + items.length) % items.length;
        T res = items[head];
        items[head] = null;
        size -- ;
        return res;
    }

    @Override
    public T removeLast() {
        if (size == 0) {
            return null;
        }
        if ((size < items.length / 4) && (size >= 16)) {
            resize(items.length / 4);
        }
        tail = (tail + 1) % items.length;
        T res = items[tail];
        items[tail] = null;
        size -- ;
        return res;
    }

    @Override
    public T get(int index) {
        if (index < 0 || index + 1 > size) {
            return null;
        }
        index = (head - index - 1 + items.length) % items.length;
        return items[index];
    }
}
