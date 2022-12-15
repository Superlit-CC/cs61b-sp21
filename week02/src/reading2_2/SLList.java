package reading2_2;

/**
 * @author superlit
 * @create 2022/12/14 17:53
 */
public class SLList {
    /** private: Outer will not use it.
     *  static: IntNode will never look out. */
    private static class IntNode {
        public int item;
        public IntNode next;

        public IntNode(int i, IntNode n) {
            item = i;
            next = n;
        }
    }
    private IntNode sentinel;
    private int size;

    public SLList() {
        sentinel = new IntNode(-1, null);
        size = 0;
    }

    public SLList(int x) {
        sentinel = new IntNode(-1, null);
        sentinel.next = new IntNode(x, null);
        size = 1;
    }

    /** Add x to the front of the list */
    public void addFirst(int x) {
        sentinel.next = new IntNode(x, sentinel.next);
        size ++ ;
    }

    /** Return the first IntNode's value. */
    public int getFirst() {
        return sentinel.next.item;
    }

    /** Add an IntNode to the end. */
    public void addLast(int x) {
        size ++ ;

        IntNode temp = sentinel;
        // Move to the last IntNode.
        while (temp.next != null) {
            temp = temp.next;
        }
        temp.next = new IntNode(x, null);
    }

    /** Return the size, start from the node.
     * Language of the Gods. */
//    private static int size(IntNode node) {
//        if (node.next == null) {
//            return 1;
//        }
//        return size(node.next) + 1;
//    }

    /** Return the size of SLList. */
    public int size() {
        return size;
    }

    // implement iteratively
//    public int size() {
//        int num = 0;
//        IntNode temp = first;
//        while (temp != null) {
//            num ++ ;
//            temp = temp.next;
//        }
//        return num;
//    }

    public static void main(String[] args) {
        SLList L = new SLList();
        L.addLast(33);
        L.addFirst(15);
        L.addFirst(44);
        System.out.println(L.getFirst());
        System.out.println(L.size());
    }
}
