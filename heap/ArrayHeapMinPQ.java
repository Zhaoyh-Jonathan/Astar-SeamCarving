package heap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class ArrayHeapMinPQ<T> implements ExtrinsicMinPQ<T> {
    private ArrayList<PriorityNode> PQ;
    private int num;  // number of items on priority queue
    private Map<T, PriorityNode> itemMap;  // hashtable

    // node in the queue
    private class PriorityNode implements Comparable<PriorityNode> {
        private T item;
        private double priorityValue;
        private int index;

        PriorityNode(T item, double priorityValue, int index) {
            this.item = item;
            this.priorityValue = priorityValue;
            this.index = index;
        }

        T getItem() {
            return item;
        }

        double getPriorityValue() {
            return priorityValue;
        }

        void setPriorityValue(double priorityValue) {
            this.priorityValue = priorityValue;
        }

        int getIndex() {
            return index;
        }

        void setIndex(int index) {
            this.index = index;
        }

        @Override
        public int compareTo(PriorityNode other) {
            if (other == null) {
                return -1;
            }
            return Double.compare(this.getPriorityValue(), other.getPriorityValue());
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || o.getClass() != this.getClass()) {
                return false;
            } else {
                return (((PriorityNode) o).getItem().equals(this.getItem()));
            }
        }

        @Override
        public int hashCode() {
            return item.hashCode();
        }

    }

    public ArrayList<PriorityNode> getPQ() {
        return PQ;
    }

    public ArrayHeapMinPQ() {
        PQ = new ArrayList<>();
        PQ.add(null);
        itemMap = new HashMap<>();
        num = 0;
    }

    /*
    Here's a helper method and a method stub that may be useful. Feel free to change or remove
    them, if you wish.
     */

    /**
     * A helper method to create arrays of T, in case you're using an array to represent your heap.
     * You shouldn't need this if you're using an ArrayList instead.
     */
    @SuppressWarnings("unchecked")
    private T[] makeArray(int newCapacity) {
        return (T[]) new Object[newCapacity];
    }

    /**
     * A helper method for swapping the items at two indices of the array heap.
     */
    private void swap(int a, int b) {
        PriorityNode temp = PQ.get(a);
        int tempIndex = temp.getIndex();
        PQ.set(a, PQ.get(b));
        PQ.set(b, temp);
        // swap the indices
        PQ.get(b).setIndex(PQ.get(a).getIndex());
        PQ.get(a).setIndex(tempIndex);
    }

    /**
     * Adds an item with the given priority value.
     * Assumes that item is never null.
     * Runs in O(log N) time (except when resizing).
     * @throws IllegalArgumentException if item is already present in the PQ
     */
    @Override
    public void add(T item, double priority) {
        if (contains(item)) {
            throw new IllegalArgumentException();
        }
        num = num + 1;
        PriorityNode node = new PriorityNode(item, priority, num);
        PQ.add(node);
        swim(num);
        itemMap.put(item, node);
    }

    /**
     * Returns true if the PQ contains the given item; false otherwise.
     * Runs in O(log N) time.
     */
    @Override
    public boolean contains(T item) {
        return itemMap.containsKey(item);
    }

    /**
     * Returns the item with the smallest priority.
     * Runs in O(log N) time.
     * @throws NoSuchElementException if the PQ is empty
     */
    @Override
    public T getSmallest() {
        if (PQ.isEmpty()) {
            throw new NoSuchElementException();
        }

        return PQ.get(1).getItem();
    }

    /**
     * Removes and returns the item with the smallest priority.
     * Runs in O(log N) time (except when resizing).
     * @throws NoSuchElementException if the PQ is empty
     */
    @Override
    public T removeSmallest() {
        if (PQ.isEmpty()) {
            throw new NoSuchElementException();
        }
        swap(1, num);
        PriorityNode smallestNode = PQ.get(num);
        PQ.remove(num);
        num = num - 1;
        sink(1);
        itemMap.remove(smallestNode.getItem());
        return smallestNode.getItem();
    }

    /**
     * Changes the priority of the given item.
     * Runs in O(log N) time.
     * @throws NoSuchElementException if the item is not present in the PQ
     */
    @Override
    public void changePriority(T item, double priority) {
        if (!contains(item)) {
            throw new NoSuchElementException();
        }
        PriorityNode node = itemMap.get(item);
        int currIndex = node.getIndex();
        double originalPriority = PQ.get(currIndex).getPriorityValue();
        node.setPriorityValue(priority);
        if (originalPriority < priority) {
            sink(currIndex);
        } else {
            swim(currIndex);
        }
    }

    /**
     * Returns the number of items in the PQ.
     * Runs in O(log N) time.
     */
    @Override
    public int size() {
        return num;
    }


    // helper methods based on the slides
    private void sink(int k) {
        while (leftChild(k) <= num) {
            int j = leftChild(k);
            if (j < num) {
                int cmp = PQ.get(j).compareTo(PQ.get(j+1));
                if (cmp > 0) {
                    j++;
                }
                cmp = PQ.get(k).compareTo(PQ.get(j));
                if (cmp <= 0) {
                    break;
                }
                swap(k, j);
                k = j;
            } else {  // j == num
                int cmp = PQ.get(k).compareTo(PQ.get(j));
                if (cmp <= 0) {
                    break;
                }
                swap(k, j);
                k = j;
            }
        }
    }

    // Swap a node up the tree until its parent is smaller than itself.
    private void swim(int k) {
        if (k > 1) {
            int cmp = PQ.get(k).compareTo(PQ.get(parent(k)));
            if (cmp < 0) {
                swap(k, parent(k));
                swim(parent(k));
            }
        }
    }

    // return the index of the parent of the element which has an index k
    private int parent(int k) {
        return (k / 2);
    }

    private int leftChild(int k) {
        return (k * 2);
    }

    private int rightChild(int k) {
        return (k * 2 + 1);
    }
}
