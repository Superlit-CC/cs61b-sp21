package deque;

import java.util.Iterator;

/**
 * @author superlit
 * @create 2023/1/16 16:10
 */
public interface Deque<T> {
    /** Adds an item of type T to the front of the deque. You can assume that item is never null. */
    public void addFirst(T item);
    /** Adds an item of type T to the back of the deque. You can assume that item is never null. */
    public void addLast(T item);
    /** Returns true if deque is empty, false otherwise. */
    public default boolean isEmpty() {
        if (size() == 0) {
            return true;
        }
        return false;
    }
    /** Returns the number of items in the deque. */
    public int size();
    /** Prints the items in the deque from first to last, separated by a space.
     * Once all the items have been printed, print out a new line. */
    public void printDeque();
    /** Removes and returns the item at the front of the deque.
     * If no such item exists, returns null. */
    public T removeFirst();
    /** Removes and returns the item at the back of the deque.
     * If no such item exists, returns null. */
    public T removeLast();
    /** Gets the item at the given index, where 0 is the front, 1 is the next item,
     * and so forth. If no such item exists, returns null. Must not alter the deque! */
    public T get(int index);

    /** The Deque objects we’ll make are iterable (i.e. Iterable<T>)
     * so we must provide this method to return an iterator.
     */
    public Iterator<T> iterator();

    /** Returns whether or not the parameter o is equal to the Deque.
     * o is considered equal if it is a Deque and
     * if it contains the same contents (as goverened by the generic T’s equals method) in the same order.
     * (ADDED 2/12: You’ll need to use the instance of keywords for this.)
     */
    public boolean equals(Object o);
}
