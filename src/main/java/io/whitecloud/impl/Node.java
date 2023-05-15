package io.whitecloud.impl;

record Node<E>(E value, boolean isBlack, Node<E> left, Node<E> right) {
    enum Direction {
        LEFT, RIGHT;

        public Direction getOpposite() {
            return this == LEFT ? RIGHT : LEFT;
        }
    }
    public record NodeWithDirection<E>(Node<E> node, Direction direction) {}

    public Node<E> getChild(Direction direction) {
        return switch (direction) {
            case LEFT -> left;
            case RIGHT -> right;
        };
    }

    public Direction getDirectionOfChild(Node<E> child) {
        return this.right == child ? Direction.RIGHT : Direction.LEFT;
    }

    public static <E> NodeBuilder<E> builderFrom(Node<E> node) {
        return new NodeBuilder<>(node.value, node.isBlack, node.left, node.right);
    }

    public static class NodeBuilder<E> {
        private E value;
        private boolean isBlack;
        private Node<E> left;
        private Node<E> right;

        NodeBuilder(E value, boolean isBlack, Node<E> left, Node<E> right) {
            this.value = value;
            this.isBlack = isBlack;
            this.left = left;
            this.right = right;
        }

        public NodeBuilder<E> value(E value) {
            this.value = value;
            return this;
        }

        public NodeBuilder<E> isBlack(boolean isBlack) {
            this.isBlack = isBlack;
            return this;
        }

        public NodeBuilder<E> child(Node<E> node, Direction direction) {
            switch (direction) {
                case LEFT -> left = node;
                case RIGHT -> right = node;
            }
            return this;
        }

        public Node<E> build() {
            return new Node<>(value, isBlack, left, right);
        }
    }
}
