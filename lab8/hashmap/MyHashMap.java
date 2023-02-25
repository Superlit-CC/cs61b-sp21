package hashmap;

import java.util.*;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author YOUR NAME HERE
 */
public class MyHashMap<K, V> implements Map61B<K, V> {
    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /* Instance Variables */
    private Collection<Node>[] buckets;
    // You should probably define some more!
    private int size;
    private double loadFactor;
    private Set<K> ks = new HashSet<>();

    /** Constructors */
    public MyHashMap() {
        buckets = createTable(16);
        loadFactor = 0.75;
        size = 0;
    }

    public MyHashMap(int initialSize) {
        buckets = createTable(initialSize);
        loadFactor = 0.75;
        size = 0;
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        buckets = createTable(initialSize);
        loadFactor = maxLoad;
        size = 0;
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     *  1. Insert items (`add` method)
     *  2. Remove items (`remove` method)
     *  3. Iterate through items (`iterator` method)
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * Override this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new LinkedList<>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     *
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        Collection<Node>[] table = new Collection[tableSize];
        for (int i = 0; i < tableSize; i ++ ) {
            table[i] = createBucket();
        }
        return table;
    }

    // TODO: Implement the methods of the Map61B Interface below
    // Your code won't compile until you do so!
    @Override
    public void clear() {
        buckets = createTable(16);
        ks = new HashSet<>();
        size = 0;
    }

    @Override
    public boolean containsKey(K key) {
        if (buckets == null) {
            return false;
        }
        return getNode(key) != null;
    }

    @Override
    public V get(K key) {
        Node node = getNode(key);
        if (node != null) {
            return getNode(key).value;
        }
        return null;
    }

    /** Returns the key-value to which the specified key is mapped,
     * or null if this map contains no mapping for the key.*/
    private Node getNode(K key) {
        int index = getIndex(key, buckets.length);
        if (buckets[index] == null) {
            return null;
        } else {
            for (Node node : buckets[index]) {
                if (node.key.equals(key)) {
                    return node;
                }
            }
        }
        return null;
    }

    /** Return the index of the k. */
    private int getIndex(K k, int num) {
        int hashCode = k.hashCode();
        return Math.floorMod(hashCode, num);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void put(K key, V value) {
        int index = getIndex(key, buckets.length);
        if (buckets[index] != null) {
            for (Node node : buckets[index]) {
                if (node.key.equals(key)) {
                    node.value = value;
                    return;
                }
            }
        }
        buckets[index].add(createNode(key, value));
        size ++ ;
        ks.add(key);
        if ((double) size / buckets.length > loadFactor) {
            resize(buckets.length * 2);
        }
    }

    /** resize the table. */
    private void resize(int x) {
        Collection<Node>[] newTable = createTable(x);
        for (int i = 0; i < buckets.length; i ++ ) {
            for (Node node : buckets[i]) {
                int index = getIndex(node.key, x);
                newTable[index].add(node);
            }
        }
        buckets = newTable;
    }

    @Override
    public Set<K> keySet() {
        return new HashSet<K>(ks);
    }

    @Override
    public V remove(K key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(K key, V value) {
        throw new UnsupportedOperationException();
    }

    /** returns an Iterator that iterates over the stored keys. */
    @Override
    public Iterator<K> iterator() {
        return ks.iterator();
    }
}
