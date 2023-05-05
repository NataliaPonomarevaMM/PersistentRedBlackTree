[![Tests](https://github.com/NataliaPonomarevaMM/PersistentRedBlackTree/actions/workflows/main.yml/badge.svg)](https://github.com/NataliaPonomarevaMM/PersistentRedBlackTree/actions/workflows/main.yml)


# PersistentRedBlackTree

This is an implementation of a red black tree with persistence.
This means, that every operation on the tree creates new version of tree. 
This allows user to have all versions of the tree and moreover to make operations on any version of the tree.

A red-black tree is a self-balancing binary search tree with the following rules:
1. Each node is either red or black.
2. The root is black and all leaves are black.
4. A red node must not have red children.
5. Every simple path from a given node to any of its descendant leaves contains the same number of black nodes.

Each operation on a persistent red black tree needs O(log n) time and memory.

## Example:

```java
PersistentRedBlackTree<Integer> emptyTree = new PersistentRedBlackTree<>();
PersistentRedBlackTree<Integer> oneTree = emptyTree.add(1);
PersistentRedBlackTree<Integer> oneTreeDeleted = oneTree.delete(1);
System.out.println(oneTreeDeleted.contains(1));
```