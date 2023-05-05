package io.whitecloud.impl;

class Node<E> {
    enum Direction {
        LEFT, RIGHT;

        public Direction getOpposite() {
            return this == LEFT ? RIGHT : LEFT;
        }
    }

    private E value;
    private boolean isBlack;
    private Node<E> left = null, right = null;

    public Node(E value) {
        this.value = value;
        this.isBlack = false;
    }

    public Node(E value, boolean isBlack, Node<E> left, Node<E> right) {
        this.value = value;
        this.isBlack = isBlack;
        this.left = left;
        this.right = right;
    }

    public E getValue() {
        return this.value;
    }

    public void setValue(E value) {
        this.value = value;
    }

    public boolean isBlack() {
        return this.isBlack;
    }

    public void setBlack(boolean isBlack) {
        this.isBlack = isBlack;
    }

    public Node<E> getChild(Direction direction) {
        return switch (direction) {
            case LEFT -> left;
            case RIGHT -> right;
        };
    }

    public void setChild(Node<E> node, Direction direction) {
        switch (direction) {
            case LEFT -> left = node;
            case RIGHT -> right = node;
        }
    }

    public Direction getDirectionOfChild(Node<E> child) {
        return this.right == child ? Direction.RIGHT : Direction.LEFT;
    }

    public Node<E> cloneChild(Direction direction) {
        return switch (direction) {
            case LEFT -> left = clone(left);
            case RIGHT -> right = clone(right);
        };
    }

    public static <E> Node<E> clone(Node<E> node) {
        if (node == null) {
            return null;
        }
        return new Node<>(node.value, node.isBlack, node.left, node.right);
    }
}
