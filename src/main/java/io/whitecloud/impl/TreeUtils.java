package io.whitecloud.impl;

import java.util.LinkedList;
import java.util.function.Consumer;
import java.util.function.Function;

class TreeUtils {
    public static <E extends Comparable<E>> Node<E> traverse(Node<E> root, E el,
                                                             Function<Node<E>, Node<E>> onExact,
                                                             Consumer<Node<E>> onEach)
    {
        Node<E> current = root;
        while (current != null) {
            int comparisonResult = el.compareTo(current.value());
            if (comparisonResult == 0) {
                return onExact.apply(current);
            }
            Node.Direction dir = comparisonResult < 0 ? Node.Direction.LEFT : Node.Direction.RIGHT;
            onEach.accept(current);
            current = current.getChild(dir);
        }
        return null;
    }

    public static <E> Node<E> insert(LinkedList<Node<E>> parents, Node<E> node, Node.Direction nodeDirection) {
        if (parents.isEmpty()) {
            return Node.builderFrom(node)
                .isBlack(true)
                .build();
        }
        else if (!isBlack(parents.getLast())) {
            return insertUncleRed(parents, node, nodeDirection);
        }
        while (!parents.isEmpty()) {
            Node<E> parent = parents.removeLast();
            Node<E> newParent = Node.builderFrom(parent)
                .child(node, nodeDirection)
                .build();
            nodeDirection = getNodeDirection(parents, parent);
            node = newParent;
        }
        return node;
    }

    private static <E> Node<E> insertUncleRed(LinkedList<Node<E>> parents, Node<E> node, Node.Direction nodeDirection) {
        Node<E> grandParent = parents.get(parents.size() - 2);
        Node<E> parent = parents.getLast();
        Node.Direction uncleDirection = grandParent.getDirectionOfChild(parent).getOpposite();
        Node<E> uncle = grandParent.getChild(uncleDirection);

        if (isBlack(uncle)) {
            return insertUncleBlack(parents, node, nodeDirection);
        }

        parents.removeLast();
        parents.removeLast();

        Node<E> newParent = Node.builderFrom(parent)
            .child(node, nodeDirection)
            .isBlack(true)
            .build();
        Node<E> newUncle = Node.builderFrom(uncle)
            .isBlack(true)
            .build();
        Node<E> newGrandParent = Node.builderFrom(grandParent)
            .child(newUncle, uncleDirection)
            .child(newParent, uncleDirection.getOpposite())
            .isBlack(false)
            .build();

        Node.Direction direction = getNodeDirection(parents, grandParent);
        return insert(parents, newGrandParent, direction);
    }

    private static <E> Node<E> insertUncleBlack(LinkedList<Node<E>> parents, Node<E> node, Node.Direction nodeDirection) {
        Node<E> grandParent = parents.get(parents.size() - 2);
        Node<E> parent = parents.getLast();
        Node.Direction parentDirection = grandParent.getDirectionOfChild(parent);

        Node.NodeBuilder<E> parentBuilder = Node.builderFrom(parent)
            .child(node, nodeDirection);

        if (nodeDirection != parentDirection) {
            parents.removeLast();
            Node<E> replacement = rotate(parentBuilder.build(), parentDirection);
            parents.addLast(replacement);
            parentBuilder = Node.builderFrom(replacement);
        }

        Node<E> newParent = parentBuilder
            .isBlack(true)
            .build();
        Node<E> newGrandParent = Node.builderFrom(grandParent)
            .child(newParent, grandParent.getDirectionOfChild(parent))
            .isBlack(false)
            .build();

        parents.removeLast();
        parents.removeLast();

        Node<E> rotated = rotate(newGrandParent, parentDirection.getOpposite());
        Node.Direction direction = getNodeDirection(parents, grandParent);
        return insert(parents, rotated, direction);
    }

    public static <E> Node<E> remove(LinkedList<Node.NodeWithDirection<E>> parents, Node<E> node) {
        if (node.getChild(Node.Direction.LEFT) != null && node.getChild(Node.Direction.RIGHT) != null) {
            Node<E> current = node.getChild(Node.Direction.RIGHT);
            E lastValue = null;
            while (current != null) {
                lastValue = current.value();
                current = current.getChild(Node.Direction.LEFT);
            }

            Node<E> newNode = Node.builderFrom(node)
                .value(lastValue)
                .build();
            var direction = parents.isEmpty() ? null : parents.getLast().node().getDirectionOfChild(node);
            parents.add(new Node.NodeWithDirection<>(newNode, direction));

            current = node.getChild(Node.Direction.RIGHT);
            while (current != null) {
                parents.add(createNodeWithDirection(parents, current));
                current = current.getChild(Node.Direction.LEFT);
            }
            Node<E> currentParent = parents.removeLast().node();
            return removeOneChild(parents, currentParent);
        }
        return removeOneChild(parents, node);
    }

    private static <E> Node<E> removeOneChild(LinkedList<Node.NodeWithDirection<E>> parents, Node<E> node) {
        Node<E> child = node.getChild(node.getDirectionOfChild(null).getOpposite());
        Node<E> newChild = child == null ? null : Node.builderFrom(child)
            .isBlack(true)
            .build();

        if (parents.isEmpty()) {
            return newChild;
        }

        Node.Direction nodeDirection = parents.getLast().node().getDirectionOfChild(node);
        if (isBlack(node)) {
            if (isBlack(child)) {
                return removeRedSibling(parents, newChild, nodeDirection);
            }
        }
        return updateParentsUpToRoot(parents, newChild, nodeDirection);
    }

    private static <E> Node<E> removeRedSibling(LinkedList<Node.NodeWithDirection<E>> parents, Node<E> node, Node.Direction nodeDirection) {
        Node.NodeWithDirection<E> parentWithDir = parents.getLast();
        Node<E> parent = parentWithDir.node();
        Node<E> sibling = parent.getChild(nodeDirection.getOpposite());

        if (!isBlack(sibling)) {
            Node<E> newSibling = Node.builderFrom(sibling)
                .isBlack(true)
                .build();
            Node<E> newParent = Node.builderFrom(parent)
                .child(node, nodeDirection)
                .child(newSibling, nodeDirection.getOpposite())
                .isBlack(false)
                .build();

            Node<E> replacement = rotate(newParent, nodeDirection);

            parents.removeLast();
            parents.add(new Node.NodeWithDirection<>(replacement, parentWithDir.direction()));
            parents.add(new Node.NodeWithDirection<>(replacement.getChild(nodeDirection), nodeDirection));
            node = replacement.getChild(nodeDirection).getChild(nodeDirection);
        }
        return removeBlackSibling(parents, node, nodeDirection);
    }

    private static <E> Node<E> removeBlackSibling(LinkedList<Node.NodeWithDirection<E>> parents, Node<E> node, Node.Direction nodeDirection) {
        Node.NodeWithDirection<E> parentWithDir = parents.getLast();
        Node<E> parent = parentWithDir.node();
        Node<E> sibling = parent.getChild(nodeDirection.getOpposite());
        Node.NodeBuilder<E> newParentBuilder = Node.builderFrom(parent)
            .child(node, nodeDirection)
            .isBlack(true);

        if (sibling != null && isBlack(sibling.getChild(nodeDirection.getOpposite()))) {
            if (isBlack(sibling.getChild(nodeDirection))) {
                return removeBlackSiblingWithBlackChildren(parents, node, nodeDirection);
            }
            Node<E> newSiblingChild = Node.builderFrom(sibling.getChild(nodeDirection))
                .isBlack(true)
                .build();
            sibling = Node.builderFrom(sibling)
                .isBlack(false)
                .child(newSiblingChild, nodeDirection)
                .build();
            sibling = rotate(sibling, nodeDirection.getOpposite());
            newParentBuilder = newParentBuilder
                .child(sibling, nodeDirection.getOpposite());
        }

        parents.removeLast();

        if (sibling != null) {
            Node<E> siblingChild = sibling.getChild(nodeDirection.getOpposite());
            if (siblingChild != null) {
                siblingChild = Node.builderFrom(siblingChild)
                    .isBlack(true)
                    .build();
            }
            Node<E> newSibling = Node.builderFrom(sibling)
                .child(siblingChild, nodeDirection.getOpposite())
                .isBlack(parent.isBlack())
                .build();
            Node<E> newParent = newParentBuilder
                .child(newSibling, nodeDirection.getOpposite())
                .build();
            Node<E> rotated = rotate(newParent, nodeDirection);

            if (parents.isEmpty()) {
                return rotated;
            }
            return updateParentsUpToRoot(parents, rotated, parentWithDir.direction());
        }
        return updateParentsUpToRoot(parents, newParentBuilder.build(), parentWithDir.direction());
    }

    private static <E> Node<E> removeBlackSiblingWithBlackChildren(LinkedList<Node.NodeWithDirection<E>> parents, Node<E> node, Node.Direction nodeDirection) {
        Node.NodeWithDirection<E> parentWithDir = parents.removeLast();
        Node<E> parent = parentWithDir.node();
        Node<E> sibling = parent.getChild(nodeDirection.getOpposite());
        Node<E> newSibling = Node.builderFrom(sibling)
            .isBlack(false)
            .build();
        Node.NodeBuilder<E> newParentBuilder = Node.builderFrom(parent)
            .child(node, nodeDirection)
            .child(newSibling, nodeDirection.getOpposite());

        if (parent.isBlack()) {
            if (parents.isEmpty()) {
                return newParentBuilder.build();
            }
            return removeRedSibling(parents, newParentBuilder.build(), parentWithDir.direction());
        }

        Node<E> newParent = newParentBuilder
            .isBlack(true)
            .build();
        return updateParentsUpToRoot(parents, newParent, parentWithDir.direction());
    }

    private static <E> Node<E> rotate(Node<E> node, Node.Direction direction) {
        Node<E> child = node.getChild(direction.getOpposite());
        Node<E> newNode = Node.builderFrom(node)
            .child(child.getChild(direction), direction.getOpposite())
            .build();
        return Node.builderFrom(child)
            .child(newNode, direction)
            .build();
    }

    private static <E> boolean isBlack(Node<E> node) {
        return node == null || node.isBlack();
    }

    private static <E> Node.Direction getNodeDirection(LinkedList<Node<E>> parents, Node<E> node) {
        if (parents.isEmpty()) {
            return null;
        }

        Node<E> parent = parents.getLast();
        return parent.getDirectionOfChild(node);
    }

    public static <E> Node.NodeWithDirection<E> createNodeWithDirection(LinkedList<Node.NodeWithDirection<E>> parents, Node<E> node) {
        Node.Direction direction = null;
        if (!parents.isEmpty()) {
            Node.NodeWithDirection<E> parent = parents.getLast();
            direction = parent.node().getDirectionOfChild(node);
        }
        return new Node.NodeWithDirection<>(node, direction);
    }

    private static <E> Node<E> updateParentsUpToRoot(LinkedList<Node.NodeWithDirection<E>> parents, Node<E> node, Node.Direction nodeDirection) {
        while (!parents.isEmpty()) {
            Node.NodeWithDirection<E> parentWithDir = parents.removeLast();
            node = Node.builderFrom(parentWithDir.node())
                .child(node, nodeDirection)
                .build();
            nodeDirection = parentWithDir.direction();
        }
        return node;
    }
}
