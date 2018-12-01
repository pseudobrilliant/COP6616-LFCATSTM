package com.cop6616.lfcat;

import org.deuce.Atomic;

/***
 * Simplified LFCAT that is more binary search tree than LFCAT. Has no dyynamic contention adaptation.
 */
public class LFCAT {

    private Node root = new Node();

    public static int maxSize =100;

    /***
     * Atomically inserts value x into the structure
     * @param x
     */
    @Atomic
    public void insert(Integer x)
    {
        root.insert(x);
    }

    /***
     * Atomically removes value x from the structure
     * @param x
     */
    @Atomic
    public void remove(Integer x)
    {
        root.remove(x);
    }

    /***
     * Atomically retrieves the given range
     * @param lo low value of the range
     * @param hi high value of the range
     */
    @Atomic
    public void range(Integer lo, Integer hi)
    {
        root.range(lo, hi);
    }

    /***
     * Atomically searches for value x in the tree
     * @param x
     */
    @Atomic
    public void contains(Integer x)
    {
        root.contains(x);
    }

    /***
     * Splits nodes to create tree, used only for pregenation, not atomic.
     * @param x
     */
    public void split(Integer x) {

        Pair pair = root.split(x);

        Node left = new Node(pair.left);
        Node right = new Node(pair.right);
        int key = pair.key;

        Node r = new Node(key, left, right);
        left.parent = r;
        right.parent = r;

        if(root.type == Node.NodeType.BASE)
        {
            root = r;
        }
        else
        {
            root.replace(x, r);
        }
    }
}