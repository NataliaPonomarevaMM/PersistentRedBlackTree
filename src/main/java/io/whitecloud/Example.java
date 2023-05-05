package io.whitecloud;

import io.whitecloud.impl.PersistentRedBlackTree;

public class Example {
    public static void main(String[] args) {
        PersistentRedBlackTree<Integer> emptyTree = new PersistentRedBlackTree<>();
        PersistentRedBlackTree<Integer> oneTree = emptyTree.add(1);
        PersistentRedBlackTree<Integer> oneTreeDeleted = oneTree.delete(1);
        System.out.println(oneTreeDeleted.contains(1));
    }
}
