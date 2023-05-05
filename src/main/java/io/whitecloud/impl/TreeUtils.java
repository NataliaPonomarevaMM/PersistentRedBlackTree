package io.whitecloud.impl;

import java.util.LinkedList;
import java.util.function.BiConsumer;
import java.util.function.Function;

class TreeUtils {
    public static <E extends Comparable<E>> Node<E> traverse(Node<E> root, E el,
                                                             Function<Node<E>, Node<E>> onExact,
                                                             BiConsumer<Node<E>, Node.Direction> onEach)
    {
        Node<E> current = root;
        while (current != null) {
            int comparisonResult = el.compareTo(current.getValue());
            if (comparisonResult == 0) {
                return onExact.apply(current);
            }
            Node.Direction dir = comparisonResult < 0 ? Node.Direction.LEFT : Node.Direction.RIGHT;
            onEach.accept(current, dir);
            current = current.getChild(dir);
        }
        return null;
    }

    public static <E> Node<E> insert(LinkedList<Node<E>> parents, Node<E> node, Node<E> root) {
        if (parents.isEmpty()) {
            node.setBlack(true);
            return node;
        }
        else if (!isBlack(parents.getLast())) {
            return insertUncleRed(parents, node, root);
        }
        return root;
    }

    private static <E> Node<E> insertUncleRed(LinkedList<Node<E>> parents, Node<E> node, Node<E> root) {
        Node<E> grandParent = parents.get(parents.size() - 2);
        Node<E> parent = parents.getLast();
        Node.Direction uncleDirection = grandParent.getDirectionOfChild(parent).getOpposite();
        Node<E> uncle = grandParent.getChild(uncleDirection);

        if (isBlack(uncle)) {
            return insertUncleBlack(parents, node, root);
        }

        parent.setBlack(true);
        Node<E> clonedUncle = grandParent.cloneChild(uncleDirection);
        clonedUncle.setBlack(true);
        grandParent.setBlack(false);

        parents.removeLast();
        parents.removeLast();

        return insert(parents, grandParent, root);
    }

    private static <E> Node<E> insertUncleBlack(LinkedList<Node<E>> parents, Node<E> node, Node<E> root) {
        Node<E> grandParent = parents.get(parents.size() - 2);
        Node<E> parent = parents.getLast();

        Node.Direction nodeDirection = parent.getDirectionOfChild(node);
        Node.Direction parentDirection = grandParent.getDirectionOfChild(parent);

        if (nodeDirection != parentDirection) {
            parents.removeLast();
            Node<E> replacement = rotate(grandParent, parent, parentDirection);
            replacement.cloneChild(parentDirection);
            parents.addLast(replacement);
            parent = replacement;
        }

        parent.setBlack(true);
        grandParent.setBlack(false);

        parents.removeLast();
        parents.removeLast();
        Node<E> grandGrandParent = parents.isEmpty() ? null : parents.getLast();
        Node<E> rotated = rotate(grandGrandParent, grandParent, parentDirection.getOpposite());
        return grandGrandParent == null ? rotated : root;
    }

    public static <E> Node<E> remove(LinkedList<Node<E>> parents, Node<E> node, Node<E> root) {
        if (node.getChild(Node.Direction.LEFT) != null && node.getChild(Node.Direction.RIGHT) != null) {
            parents.addLast(node);
            node.cloneChild(Node.Direction.LEFT);

            Node<E> current = node.cloneChild(Node.Direction.RIGHT);
            while (current != null) {
                parents.addLast(current);
                current = current.cloneChild(Node.Direction.LEFT);
            }

            Node<E> currentParent = parents.getLast();
            node.setValue(currentParent.getValue());
            parents.removeLast();
            return removeOneChild(parents, currentParent, root);
        }
        return removeOneChild(parents, node, root);
    }

    private static <E> Node<E> removeOneChild(LinkedList<Node<E>> parents, Node<E> node, Node<E> root) {
        Node<E> child = node.cloneChild(node.getDirectionOfChild(null).getOpposite());

        if (parents.isEmpty()) {
            root = child;
        } else {
            Node<E> parent = parents.getLast();
            Node.Direction nodeDirection = parent.getDirectionOfChild(node);
            parent.setChild(child, nodeDirection);
        }

        if (isBlack(node)) {
            if (isBlack(child)) {
                return removeRedSibling(parents, child, root);
            }
            child.setBlack(true);
        }
        return root;
    }

    private static <E> Node<E> removeRedSibling(LinkedList<Node<E>> parents, Node<E> node, Node<E> root) {
        if (parents.isEmpty()) {
            return node;
        }

        Node<E> parent = parents.getLast();
        Node.Direction nodeDirection = parent.getDirectionOfChild(node);
        Node<E> sibling = parent.getChild(nodeDirection.getOpposite());

        if (!isBlack(sibling)) {
            Node<E> clonedSibling = parent.cloneChild(nodeDirection.getOpposite());
            parent.setBlack(false);
            clonedSibling.setBlack(true);

            parents.removeLast();
            Node<E> grandParent = parents.isEmpty() ? null : parents.getLast();
            Node<E> replacement = rotate(grandParent, parent, nodeDirection);
            if (grandParent == null) {
                root = replacement;
            }

            parents.add(replacement);
            parents.add(replacement.getChild(nodeDirection));
            node = replacement.getChild(nodeDirection).cloneChild(nodeDirection);
        }
        return removeBlackSibling(parents, node, root);
    }

    private static <E> Node<E> removeBlackSibling(LinkedList<Node<E>> parents, Node<E> node, Node<E> root) {
        Node<E> parent = parents.getLast();
        Node.Direction nodeDirection = parent.getDirectionOfChild(node);
        Node<E> sibling = parent.cloneChild(nodeDirection.getOpposite());

        if (sibling != null && sibling.isBlack() && isBlack(sibling.getChild(nodeDirection.getOpposite()))) {
            sibling.setBlack(false);

            if (isBlack(sibling.getChild(nodeDirection))) {
                if (parent.isBlack()) {
                    parents.removeLast();
                    return removeRedSibling(parents, parent, root);
                }

                parent.setBlack(true);
                return root;
            }

            sibling.cloneChild(nodeDirection);
            sibling.getChild(nodeDirection).setBlack(true);
            rotate(parent, sibling, nodeDirection.getOpposite());
        }

        parents.removeLast();
        boolean parentBlack = parent.isBlack();
        parent.setBlack(true);

        if (sibling != null) {
            sibling.setBlack(parentBlack);
            sibling.cloneChild(nodeDirection.getOpposite());
            if (sibling.getChild(nodeDirection.getOpposite()) != null) {
                sibling.getChild(nodeDirection.getOpposite()).setBlack(true);
            }
            Node<E> grandParent = parents.isEmpty() ? null : parents.getLast();
            Node<E> rotated = rotate(grandParent, parent, nodeDirection);

            return grandParent == null ? rotated : root;
        }
        return root;
    }

    private static <E> Node<E> rotate(Node<E> parent, Node<E> node, Node.Direction direction) {
        Node<E> child = node.getChild(direction.getOpposite());

        if (parent != null) {
            parent.setChild(child, parent.getDirectionOfChild(node));
        }

        node.setChild(child.getChild(direction), direction.getOpposite());
        child.setChild(node, direction);
        return child;
    }

    private static <E> boolean isBlack(Node<E> node) {
        return node == null || node.isBlack();
    }
}
