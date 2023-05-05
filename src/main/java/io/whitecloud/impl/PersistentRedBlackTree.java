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

        Node<E> newNode = new Node<>(el);

        if (root == null) {
            newNode.setBlack(true);
            return new PersistentRedBlackTree<>(newNode);
        }

        Node<E> newRoot = Node.clone(root);
        LinkedList<Node<E>> parents = new LinkedList<>();
        traverse(newRoot, el, node -> node, (node, direction) -> {
                parents.add(node);
                node.cloneChild(direction);
            }
        );

        Node<E> parent = parents.getLast();
        Node.Direction dir = el.compareTo(parent.getValue()) < 0 ? Node.Direction.LEFT : Node.Direction.RIGHT;
        parent.setChild(newNode, dir);

        Node<E> root = insert(parents, newNode, newRoot);
        return new PersistentRedBlackTree<>(root);
    }

    @Override
    public PersistentRedBlackTree<E> delete(E el) {
        if (!contains(el)) {
            return null;
        }

        Node<E> newRoot = Node.clone(root);
        LinkedList<Node<E>> parents = new LinkedList<>();

        Node<E> root = traverse(newRoot, el, node -> remove(parents, node, newRoot),
            (node, dir) -> {
                parents.add(node);
                node.cloneChild(dir);
            }
        );

        return new PersistentRedBlackTree<>(root);
    }

    @Override
    public boolean contains(E el) {
        if (root == null) {
            return false;
        }
        return traverse(root, el, node -> node, (__, ___) -> {}) != null;
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

            return current.getValue();
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
