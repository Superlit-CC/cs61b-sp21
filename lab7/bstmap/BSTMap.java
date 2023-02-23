package bstmap;

import java.util.Iterator;
import java.util.Set;

/**
 * @author superlit
 * @create 2023/2/22 18:12
 */
public class BSTMap<K extends Comparable, V> implements Map61B<K, V> {
    private BSTNode root;
    private int size = 0;

    /** Represents one node having left and right links to store key and value. */
    private class BSTNode {
        K key;
        V value;
        BSTNode left, right;

        BSTNode(K key, V value, BSTNode left, BSTNode right) {
            this.key = key;
            this.value = value;
            this.left = left;
            this.right = right;
        }

        @Override
        public String toString() {
            return  "(" + key + ", " + value + ")";
        }

        BSTNode get(K k) {
            if (k != null && k.equals(key)) {
                return this;
            }
            if (k.compareTo(key) > 0) {
                if (right == null) {
                    return null;
                } else {
                    return right.get(k);
                }
            } else {
                if (left == null) {
                    return null;
                } else {
                    return left.get(k);
                }
            }
        }

        boolean put(K k, V v) {
            if (k != null && k.equals(key)) {
                this.value = v;
                return false;
            }
            if (k.compareTo(key) > 0) {
                if (right == null) {
                    this.right = new BSTNode(k, v, null, null);
                    return true;
                } else {
                    return right.put(k, v);
                }
            } else {
                if (left == null) {
                    this.left = new BSTNode(k, v, null, null);
                    return true;
                } else {
                    return left.put(k, v);
                }
            }
        }
    }

    @Override
    public void clear() {
        size = 0;
        root = null;
    }

    @Override
    public boolean containsKey(K key) {
        if (root == null) {
            return false;
        }
        return root.get(key) != null;
    }

    @Override
    public V get(K key) {
        if (root == null) {
            return null;
        }
        BSTNode lookup = root.get(key);
        if (lookup == null) {
            return null;
        }
        return lookup.value;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void put(K key, V value) {
        if (root != null) {
            if (root.put(key, value)) {
                size += 1;
            }
        } else {
            root = new BSTNode(key, value, null, null);
            size += 1;
        }
    }

    @Override
    public Set<K> keySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(K key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(K key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<K> iterator() {
        throw new UnsupportedOperationException();
    }

    /** prints out BSTMap in order of increasing Key. */
    public void printInOrder() {
        printInOrder(root);
    }

    private void printInOrder(BSTNode n) {
        if (n.left != null) {
            printInOrder(n.left);
        }
        System.out.println(n);
        if (n.right != null) {
            printInOrder(n.right);
        }
    }
}
