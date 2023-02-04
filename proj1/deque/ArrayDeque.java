package deque;

import java.util.Arrays;
import java.util.Iterator;

/**
 * @author superlit
 * @create 2023/1/16 19:21
 */
public class ArrayDeque<T> implements Deque<T>, Iterable<T> {
    T[] items;
    private int size, head, tail;
    private int wizPos;
    private static int RFACTOR = 2;

    public ArrayDeque() {
        items = (T[]) new Object[8];
        size = 0;
        head = 0;
        tail = items.length - 1;
    }

    private void resize(int capability) {
        T[] a = (T[]) new Object[capability];
        for (int i = 0; i < size; i += 1) {
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
        size += 1;
    }

    @Override
    public void addLast(T item) {
        if (size == items.length) {
            resize(size * RFACTOR);
        }
        items[tail] = item;
        tail = (tail - 1 + items.length) % items.length;
        size += 1;
    }

    @Override
    public int size() {
        return size;
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
        size -= 1;
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
        size -= 1;
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

    @Override
    public Iterator<T> iterator() {
        return new ArrayDequeIterator();
    }

    private class ArrayDequeIterator implements Iterator<T> {
        private int wizPos;

        public ArrayDequeIterator() {
            wizPos = 0;
        }

        public boolean hasNext() {
            return wizPos < size;
        }

        public T next() {
            T returnItem = items[wizPos];
            wizPos += 1;
            return returnItem;
        }
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null) {
            return false;
        }
        if (other.getClass() != this.getClass()) {
            return false;
        }
        ArrayDeque<T> o = (ArrayDeque<T>) other;
        if (o.size() != this.size()) {
            return false;
        }
        Iterator<T> iterator1 = iterator();
        Iterator<T> iterator2 = o.iterator();
        while (iterator2.hasNext() && iterator1.hasNext()) {
            if (!iterator1.next().equals(iterator2.next())) {
                return false;
            }
        }
        return true;
    }
}
