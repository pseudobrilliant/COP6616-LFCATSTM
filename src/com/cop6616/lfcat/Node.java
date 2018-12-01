package com.cop6616.lfcat;

import org.deuce.Atomic;
import org.omg.PortableInterceptor.INACTIVE;

import java.lang.reflect.Array;
import java.util.ArrayDeque;

public class Node
{
    //For casting purposes all nodes should be provided a type in the constructor
    public NodeType type = NodeType.BASE;

    //All nodes should be able to have a parent as part of the tree structure.
    public Node parent = null;

    private int key = 0;

    private Node left = null;

    private Node right = null;

    public AVLTree data = null;

    //Types of supported nodes, used for casting
    enum NodeType
    {
        ROUTE,
        BASE
    }

    Node()
    {
        type = NodeType.BASE;

        data = new AVLTree();
    }

    Node(Node node)
    {
        type = node.type;

        if(node.type == NodeType.BASE)
        {
            data = node.data;
            parent = node.parent;
        }
        else
        {
            left = node.left;
            right = node.right;
            key = node.key;
        }
    }

    //Used to populate node with avl tree data
    Node(AVLTree tree)
    {
        type = NodeType.BASE;
        data = tree;
    }

    //Used to create a route node
    Node(int _key, Node _left, Node _right)
    {
        type = NodeType.ROUTE;
        key = _key;
        left = _left;
        right = _right;
    }

    /***
     * Atomically searches for the base node to atomically insert x into
     * @param x
     * @return
     */
    @Atomic
    boolean insert(Integer x)
    {
        if(type == NodeType.BASE)
        {
            return data.insert(x);
        }
        else
        {
            if(x <= key && left != null)
            {
                return left.insert(x);
            }
            else
            if(x > key && right != null)
            {
                return right.insert(x);
            }
        }

        return false;
    }

    /***
     * Searches for the node range with value x to replace with route node r (not used concurrently)
     * @param x
     * @param r
     * @return
     */
    void replace(Integer x, Node r)
    {
        if(type == NodeType.BASE)
        {
            if(this.parent.left == this)
            {
                this.parent.left = r;
            }
            else
            if(this.parent.right == this)
            {
                this.parent.right = r;
            }
        }
        else
        {
            if(x <= key && left != null)
            {
                this.left.replace(x,r);
            }
            else
            if(x > key && right != null)
            {
                this.right.replace(x,r);
            }
        }
    }

    /***
     * Searches for the node range with value x to split the avl tree data contained (not used concurrently)
     * @param x
     * @param r
     * @return
     */
    Pair split (Integer x)
    {
        if(type == NodeType.BASE)
        {
           return data.split();
        }
        else
        {
            if(x <= key && left != null)
            {
                return left.split(x);
            }
            else
            if(x > key && right != null)
            {
                return right.split(x);
            }
        }

        return null;
    }

    /***
     * Atomically searches for the base node to atomically remove x from
     * @param x
     * @return
     */
    @Atomic
    void remove(Integer x)
    {
        if(type == NodeType.BASE)
        {
            data.delete(x);
        }
        else
        {
            if(x <= key && left != null)
            {
                left.remove(x);
            }
            else
            if(x > key && right != null)
            {
                right.remove(x);
            }
        }
    }

    /***
     * Atomically searches for the base node that should contain value x
     * @param x
     * @return
     */
    @Atomic
    boolean contains(Integer x)
    {
        if(type == NodeType.BASE)
        {
            return data.contains(x);
        }
        else
        {
            if(x <= key && left != null)
            {
                return left.contains(x);
            }
            else
            if(x > key && right != null)
            {
                return right.contains(x);
            }
        }

        return false;
    }

    /***
     * Atomically retrieves the size of the full tree
     * @param x
     * @return
     */
    @Atomic
    int size()
    {
        int size = 0;

        if(type == NodeType.BASE)
        {
            return data.size;
        }
        else
        {
            if(left != null)
            {
                size += left.size();
            }
            else
            if(right != null)
            {
                size += right.size();
            }
        }

        return size;
    }

    /***
     * Atomically searches for the base nodes in the given range
     * @param lo
     * @param hi
     * @return
     */
    @Atomic
    AVLTree range(Integer lo, Integer hi)
    {
        AVLTree tree = new AVLTree();
        ArrayDeque<Node> q = new ArrayDeque<>();
        Node n = findLow(lo,q);
        tree.join(n.data);
        n.findRest(q,hi,tree);

        return tree;
    }

    /***
     * Atomically searches for the base node matching to the lowest value in the given range
     * @param x
     * @return
     */
    @Atomic
    Node findLow(Integer x, ArrayDeque<Node> q)
    {
        q.push(this);

        if(type == NodeType.BASE)
        {
            return this;
        }
        else
        {
            if(x <= key && left != null)
            {
                return left.findLow(x, q);
            }
            else
            if(x > key && right != null)
            {
                return right.findLow(x, q);
            }
        }

        return null;
    }

    /***
     * Atomically searches for the rest of the base nodes in the range by popping the stack and continuing the search
     * @param x
     * @return
     */
    @Atomic
    Node findRest(ArrayDeque<Node> q, Integer hi, AVLTree tree)
    {
        Node n = q.pop();

        while(q.size() > 0)
        {
           n = q.pop();
            if(n.right != null && n.key < hi)
            {
                Node nb = n.right.findNextBase(q, hi);
                tree.join(nb.data);
            }
        }

        return n;
    }

    @Atomic
    Node findNextBase(ArrayDeque<Node> q, Integer hi)
    {
        if(left != null)
        {
            q.push(this);
            return left.findNextBase(q,hi);
        }

        return this;
    }
}
