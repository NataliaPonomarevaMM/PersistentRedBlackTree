package io.whitecloud.impl;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Stack;

import static io.whitecloud.impl.TreeUtils.*;

import io.whitecloud.PersistentTree;

public class PersistentRedBlackTree<E extends Comparable<E>> implements PersistentTree<E, PersistentRedBlackTree<E>> {
    private final Node<E> root;

    public PersistentRedBlackTree() {
        this.root = null;
    }

    private PersistentRedBlackTree(Node<E> root) {
        this.root = root;
    }

    @Override
    public PersistentRedBlackTree<E> add(E el) {
        if (contains(el)) {
            return null;
        }

        if (root == null) {
            Node<E> newNode = new Node<>(el, true, null, null);
            return new PersistentRedBlackTree<>(newNode);
        }

        Node<E> newNode = new Node<>(el, false, null, null);
        LinkedList<Node<E>> parents = new LinkedList<>();
        traverse(root, el, node -> node, parents::add);

        Node<E> parent = parents.getLast();
        Node.Direction dir = el.compareTo(parent.value()) < 0 ? Node.Direction.LEFT : Node.Direction.RIGHT;

        Node<E> newRoot = insert(parents, newNode, dir);
        return new PersistentRedBlackTree<>(newRoot);
    }

    @Override
    public PersistentRedBlackTree<E> delete(E el) {
        if (!contains(el)) {
            return null;
        }

        LinkedList<Node.NodeWithDirection<E>> parents = new LinkedList<>();
        Node<E> newRoot = traverse(root, el, node -> remove(parents, node),
            node -> parents.add(createNodeWithDirection(parents, node))
        );

        return new PersistentRedBlackTree<>(newRoot);
    }

    @Override
    public boolean contains(E el) {
        if (root == null) {
            return false;
        }
        return traverse(root, el, node -> node, __ -> {}) != null;
    }

    @Override
    public Iterator<E> iterator(Order order) {
        return new OrderIterator(order);
    }

    private class OrderIterator implements Iterator<E> {
        private final Stack<Node<E>> stack = new Stack<>();
        private final Stack<Boolean> visitedRight = new Stack<>();
        private final Order order;

        OrderIterator(Order order) {
            this.order = order;
            switch (order) {
                case InOrder, PostOrder -> moveLeft(root);
                case PreOrder -> {
                    if (root != null) {
                        stack.push(root);
                    }
                }
            }
        }

        public boolean hasNext() {
            return !stack.isEmpty();
        }

        public E next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            Node<E> current = stack.pop();

            switch (order) {
                case PreOrder -> {
                    stack.push(current.getChild(Node.Direction.RIGHT));
                    stack.push(current.getChild(Node.Direction.LEFT));
                }
                case InOrder -> moveLeft(current.getChild(Node.Direction.RIGHT));
                case PostOrder -> {
                    while (current.getChild(Node.Direction.RIGHT) != null && !visitedRight.peek()) {
                        visitedRight.pop();
                        visitedRight.push(true);
                        stack.push(current);
                        moveLeft(current.getChild(Node.Direction.RIGHT));
                        current = stack.pop();
                    }
                    visitedRight.pop();
                }
            }

            while (!stack.isEmpty() && stack.peek() == null) {
                stack.pop();
            }

            return current.value();
        }

        private void moveLeft(Node<E> current) {
            while (current != null) {
                stack.push(current);
                visitedRight.push(false);
                current = current.getChild(Node.Direction.LEFT);
            }
        }
    }
}
