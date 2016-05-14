/*******************************************************************************
 * Copyright (c) 2011, 2015 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.alg.layered.p3order.counting;

/**
 * Sorted list of integers storing values from 0 up to the maxNumber passed on creation. Adding, removing and indexOf
 * (and addAndIndexOf) is in O(log maxNumber).
 * <p/>
 * Implemented as a binary tree where each leaf stores the number of integers at the leaf index and each node stores the
 * number of values in the left branch of the node.
 *
 * @author alan
 *
 */
public class BinaryPrefixTree {
    private int[] binarySums;
    private int[] numsPerIndex;
    private int size = 0;

    /**
     * Construct tree given maximum number of elements.
     *
     * @param maxNum
     *            maximum number elements.
     */
    public BinaryPrefixTree(final int maxNum) {
        binarySums = new int[maxNum + 1];
        numsPerIndex = new int[maxNum];
    }

    /**
     * Increment given index.
     *
     * @param index
     *            The index to increment.
     */
    public void add(final int index) {
        size++;
        numsPerIndex[index]++;
        int i = index + 1;
        while (i < binarySums.length) {
            binarySums[i]++;
            i += i & -i;
        }
    }

    /**
     * Sum all entries before given index, i.e. index - 1.
     *
     * @param index
     *            Not included end index.
     * @return sum.
     */
    public int rank(final int index) {
        int i = index;
        int sum = 0;
        while (i > 0) {
            sum += binarySums[i];
            i -= i & -i;
        }
        return sum;
    }

    /**
     * Return size of tree.
     *
     * @return size
     */
    public int size() {
        return size;
    }

    /**
     * Remove all entries for one index.
     *
     * @param index
     *            the index
     */
    public void removeAll(final int index) {
        int numEntries = numsPerIndex[index];
        if (numEntries == 0) {
            return;
        }
        numsPerIndex[index] = 0;
        size -= numEntries;
        int i = index + 1;
        while (i < binarySums.length) {
            binarySums[i] -= numEntries;
            i += i & -i;
        }
    }

    /**
     * Sum all between the given indices (excluding boundaries).
     * 
     * @param from
     *            Sum from this index
     * @param to
     *            to this index
     * @return sum
     */
    public int sumBetween(final int from, final int to) {
        return rank(to) - rank(from + 1);
    }

    /**
     * 
     */
    public void clear() {
        // TODO Auto-generated method stub

    }
}
