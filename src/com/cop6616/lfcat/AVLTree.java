package com.cop6616.lfcat;

import org.deuce.Atomic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/***
 * Simplified AVL Tree taken from https://rosettacode.org/wiki/AVL_tree#Java
 */
public class AVLTree
{
    private TreeNode root = null;
    public int size = 0;

    AVLTree()
    {
    }

    @Atomic
    public boolean insert(int key)
    {
        if (root == null) {
            root = new TreeNode(key, null);
            size ++;
            return true;
        }

        TreeNode n = root;
        while (true) {
            if (n.key == key)
                return false;

            TreeNode parent = n;

            boolean goLeft = n.key > key;
            n = goLeft ? n.left : n.right;

            if (n == null) {
                if (goLeft) {
                    parent.left = new TreeNode(key, parent);
                } else {
                    parent.right = new TreeNode(key, parent);
                }
                rebalance(parent);
                break;
            }
        }

        size++;
        return true;
    }

    @Atomic
    public boolean contains(int _key)
    {
        if(root == null)
        {
            return false;
        }

        return root.contains(_key);
    }

    @Atomic
    private void delete(TreeNode TreeNode) {
        if (TreeNode.left == null && TreeNode.right == null) {
            if (TreeNode.parent == null) {
                root = null;
            } else {
                TreeNode parent = TreeNode.parent;
                if (parent.left == TreeNode) {
                    parent.left = null;
                } else {
                    parent.right = null;
                }
                rebalance(parent);
            }
            size --;
            return;
        }

        if (TreeNode.left != null) {
            TreeNode child = TreeNode.left;
            while (child.right != null) child = child.right;
            TreeNode.key = child.key;
            delete(child);
        } else {
            TreeNode child = TreeNode.right;
            while (child.left != null) child = child.left;
            TreeNode.key = child.key;
            delete(child);
        }
    }

    @Atomic
    public void delete(int delKey) {
        if (root == null)
            return;

        TreeNode child = root;
        while (child != null) {
            TreeNode TreeNode = child;
            child = delKey >= TreeNode.key ? TreeNode.right : TreeNode.left;
            if (delKey == TreeNode.key) {
                delete(TreeNode);
                return;
            }
        }
    }

    @Atomic
    public void join(AVLTree tree)
    {
        ArrayList<Integer> treel = new ArrayList<>();
        tree.root.getList(treel);

        for(int i = 0; i < treel.size(); i++)
        {
            this.insert(treel.get(i));
        }
    }

    @Atomic
    private void rebalance(TreeNode n) {
        setBalance(n);

        if (n.balance == -2) {
            if (height(n.left.left) >= height(n.left.right))
                n = rotateRight(n);
            else
                n = rotateLeftThenRight(n);

        } else if (n.balance == 2) {
            if (height(n.right.right) >= height(n.right.left))
                n = rotateLeft(n);
            else
                n = rotateRightThenLeft(n);
        }

        if (n.parent != null) {
            rebalance(n.parent);
        } else {
            root = n;
        }
    }

    @Atomic
    private TreeNode rotateLeft(TreeNode a) {

        TreeNode b = a.right;
        b.parent = a.parent;

        a.right = b.left;

        if (a.right != null)
            a.right.parent = a;

        b.left = a;
        a.parent = b;

        if (b.parent != null) {
            if (b.parent.right == a) {
                b.parent.right = b;
            } else {
                b.parent.left = b;
            }
        }

        setBalance(a, b);

        return b;
    }

    @Atomic
    private TreeNode rotateRight(TreeNode a) {

        TreeNode b = a.left;
        b.parent = a.parent;

        a.left = b.right;

        if (a.left != null)
            a.left.parent = a;

        b.right = a;
        a.parent = b;

        if (b.parent != null) {
            if (b.parent.right == a) {
                b.parent.right = b;
            } else {
                b.parent.left = b;
            }
        }

        setBalance(a, b);

        return b;
    }

    @Atomic
    private TreeNode rotateLeftThenRight(TreeNode n) {
        n.left = rotateLeft(n.left);
        return rotateRight(n);
    }

    @Atomic
    private TreeNode rotateRightThenLeft(TreeNode n) {
        n.right = rotateRight(n.right);
        return rotateLeft(n);
    }

    @Atomic
    private int height(TreeNode n) {
        if (n == null)
            return -1;
        return n.height;
    }

    @Atomic
    private void setBalance(TreeNode... TreeNodes) {
        for (TreeNode n : TreeNodes) {
            reheight(n);

            if(n == null)
            {
                return;
            }

            n.balance = height(n.right) - height(n.left);
        }
    }

    @Atomic
    public void printBalance() {
        printBalance(root);
    }

    @Atomic
    private void printBalance(TreeNode n) {
        if (n != null) {
            printBalance(n.left);
            System.out.printf("%s ", n.balance);
            printBalance(n.right);
        }
    }

    @Atomic
    private void reheight(TreeNode TreeNode) {
        if (TreeNode != null) {
            TreeNode.height = 1 + Math.max(height(TreeNode.left), height(TreeNode.right));
        }
    }

    class NaturalComparator implements Comparator<Integer>
    {
        @Atomic
        public int compare(Integer a, Integer b)
        {
            return a.compareTo(b);
        }
    }

    @Atomic
    public Pair split()
    {
        ArrayList<Integer> al = new ArrayList<>();
        root.getList(al);

        NaturalComparator comparator = new NaturalComparator();

        Collections.sort(al, comparator);

        AVLTree left = new AVLTree();
        AVLTree right = new AVLTree();

        int mid = al.get(al.size() / 2);

        for(int i = 0; i < al.size(); i++)
        {
            if(i <= al.size() / 2)
            {
                left.insert(al.get(i));
            }
            else
            {
                right.insert(al.get(i));
            }
        }

        Pair pair = new Pair(mid, left, right);

        return pair;
    }

}