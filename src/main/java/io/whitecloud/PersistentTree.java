package io.whitecloud;

import java.util.Iterator;

public interface PersistentTree<E extends Comparable<E>, V extends PersistentTree<E, V>> {
    /**
     * Add new element to the persistent tree.
     * If element already existed in the tree - will be returned null.
     * If not - will be returned modified version of the tree, while this tree will remain the same.
     * @param el element to add
     * @return tree with added element or null
     */
    V add(E el);
    /**
     * Delete an element from the persistent tree.
     * If element doesn't exist in the tree - will be returned null.
     * If not - will be returned modified version of the tree, while this tree will remain the same.
     * @param el element to delete
     * @return tree with deleted element or null
     */
    V delete(E el);
    /**
     * Check if element is in the tree.
     * Doesn't modify the tree.
     * @param el element to check
     * @return if element is in the tree or not
     */
    boolean contains(E el);

    enum Order {InOrder, PreOrder, PostOrder}
    /**
     * An iterator over the tree, that allows to specify traversal order.
     * Iterator doesn't support remove operation or any other changes of tree.
     * @param order traversal order
     * @return iterator over the tree
     */
    Iterator<E> iterator(Order order);
}
