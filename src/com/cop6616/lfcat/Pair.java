package com.cop6616.lfcat;

import org.deuce.Atomic;

public class Pair
{

    public AVLTree left = null;
    public AVLTree right = null;
    public Integer key = 0;

    Pair(Integer _key, AVLTree _left, AVLTree _right)
    {
        key = _key;
        left = _left;
        right = _right;
    }

}