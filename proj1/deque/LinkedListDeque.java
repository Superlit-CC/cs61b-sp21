package deque;

/**
 * @author superlit
 * @create 2023/1/16 16:06
 */
public class LinkedListDeque<T> implements Deque<T> {
    private class Node {
        T value;
        Node front, back;

        public Node (T value, Node front, Node back) {
            this.value = value;
            this.front = front;
            this.back = back;
        }
    }

    private Node first, last;
    private int size;

    public LinkedListDeque() {
        first = new Node(null, null, null);
        last = new Node(null, null, null);
        first.back = last;
        last.front = first;
        size = 0;
    }

    @Override
    public void addFirst(T item) {
        Node next = first.back;
        Node node = new Node(item, first, next);
        first.back = node;
        next.front = node;
        size ++ ;
    }

    @Override
    public void addLast(T item) {
        Node next = last.front;
        Node node = new Node(item, next, last);
        last.front = node;
        next.back = node;
        size ++ ;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        Node temp = first.back;
        for (int i = 0; i < size; i ++ ) {
            System.out.print(temp.value + " ");
            temp = temp.back;
        }
        System.out.println();
    }

    @Override
    public T removeFirst() {
        if (size == 0) {
            return null;
        }
        T res = first.back.value;
        first.back = first.back.back;
        first.back.front = first;
        size -- ;
        return res;
    }

    @Override
    public T removeLast() {
        if (size == 0) {
            return null;
        }
        T res = last.front.value;
        last.front = last.front.front;
        last.front.back = last;
        size -- ;
        return res;
    }

    @Override
    public T get(int index) {
        if (index < 0 || index + 1 > size) {
            return null;
        }
        Node temp = first.back;
        for (int i = 0; i < index; i ++ ) {
            temp = temp.back;
        }
        return temp.value;
    }

    /** Same as get, but uses recursion. */
    public T getRecursive(int index) {
        if (index < 0 || index + 1 > size) {
            return null;
        }
        return recursive(index, first.back);
    }

    private T recursive(int index, Node node) {
        if (index == 0) return node.value;
        return recursive(index - 1, node.back);
    }
}
