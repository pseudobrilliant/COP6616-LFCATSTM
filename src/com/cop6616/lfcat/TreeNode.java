package com.cop6616.lfcat;

import org.deuce.Atomic;

import java.util.ArrayList;

class TreeNode {

    int key = 0;
    int balance = 0;
    int height = 0;
    TreeNode left = null;
    TreeNode right = null;
    TreeNode parent = null;

    TreeNode(int _key, TreeNode _parent)
    {
        this.key = _key;
        this.parent = _parent;
    }

    @Atomic
    boolean contains(int x)
    {
        if(x == this.key)
        {
            return true;
        }

        if(x < this.key && this.left != null)
        {
            return left.contains(x);
        }

        if(x > this.key && this.right != null)
        {
            return right.contains(x);
        }

        return false;
    }

    @Atomic
    void getList(ArrayList<Integer> al)
    {
        if(left != null)
        {
            left.getList(al);
        }

        if(right != null)
        {
            right.getList(al);
        }

        al.add(key);
    }

}
