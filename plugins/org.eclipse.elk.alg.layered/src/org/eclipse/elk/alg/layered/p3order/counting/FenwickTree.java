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
public class FenwickTree {
    private int[] binarySums;
    private int[] numsPerIndex;
    private int size = 0;

    public FenwickTree(final int maxNum) {
        System.out.println(maxNum);
        binarySums = new int[maxNum + 1];
        numsPerIndex = new int[maxNum];
    }

    public void add(final int index) {
        size++;
        numsPerIndex[index]++;
        int i = index + 1;
        // for (; i < (int)tree.size(); i |= i + 1)
        while (i < binarySums.length) {
            binarySums[i]++;
            i += i & -i;
        }
    }

    public int sumBefore(final int index) {
        int i = index;
        int sum = 0;
        while (i > 0) {
            sum += binarySums[i];
            i -= i & -i;
        }
        System.out.println("Sum " + sum);
        return sum;
    }

    public int size() {
        System.out.println("Size " + size);
        return size;
    }

    public void removeAll(final int index) {
        int numEntries = numsPerIndex[index];
        numsPerIndex[index] = 0;
        size -= numEntries;
        int i = index + 1;
        while (i < binarySums.length) {
            binarySums[i] -= numEntries;
            i += i & -i;
        }
    }

}
