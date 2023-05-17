package io.whitecloud;

import java.util.*;

import static io.whitecloud.TestUtils.EMPTY;
import static io.whitecloud.TestUtils.checkOrdersOfTree;
import static org.junit.jupiter.api.Assertions.*;

import io.whitecloud.impl.PersistentRedBlackTree;
import org.junit.jupiter.api.Test;

public class PersistentRedBlackTreeTest {
    @Test
    public void testEmpty() {
        assertFalse(EMPTY.contains(0));
        assertFalse(EMPTY.contains(1));
        assertFalse(EMPTY.contains(-1));
        assertFalse(EMPTY.contains(10000));
    }

    @Test
    public void testAdd() {
        PersistentRedBlackTree<Integer> one = EMPTY.add(1);
        assertNotNull(one);
        assertNotEquals(EMPTY, one);
        assertFalse(EMPTY.contains(1));
        assertFalse(EMPTY.contains(0));
        assertTrue(one.contains(1));
        assertFalse(one.contains(0));
    }

    @Test
    public void testAddExisted() {
        PersistentRedBlackTree<Integer> one = EMPTY.add(1);
        PersistentRedBlackTree<Integer> anotherOne = one.add(1);
        assertNotNull(one);
        assertNotEquals(EMPTY, one);
        assertNull(anotherOne);
    }

    @Test
    public void testDeleteOne() {
        PersistentRedBlackTree<Integer> one = EMPTY.add(1);
        PersistentRedBlackTree<Integer> deleted = one.delete(1);
        assertNotNull(one);
        assertNotNull(deleted);
        assertNotEquals(one, deleted);
        assertTrue(one.contains(1));
        assertFalse(one.contains(0));
        assertFalse(deleted.contains(1));
        assertFalse(deleted.contains(0));
    }

    @Test
    public void testDeleteNotExisted() {
        PersistentRedBlackTree<Integer> deleted = EMPTY.delete(1);
        assertNull(deleted);
    }

    @Test
    public void testModifyDifferentVersions() {
        var one = EMPTY.add(1);
        var two = EMPTY.add(2);
        var three = one.add(3);
        var threeDeleted = three.delete(1);
        assertNotEquals(one, two);
        assertNotEquals(one, three);
        assertNotEquals(one, threeDeleted);
        assertTrue(one.contains(1));
        assertFalse(one.contains(0));
        assertTrue(two.contains(2));
        assertFalse(two.contains(1));
        assertTrue(three.contains(1));
        assertTrue(three.contains(3));
        assertFalse(three.contains(2));
        assertFalse(threeDeleted.contains(1));
        assertTrue(threeDeleted.contains(3));
        assertFalse(threeDeleted.contains(2));
    }

    @Test
    public void testBigTree() {
        PersistentRedBlackTree<Integer> previous = EMPTY;
        for (int i = 1; i < 10000; i++) {
            var next = previous.add(i);
            assertNotNull(next);
            assertTrue(next.checkIsRedBlackTree());
            assertNotEquals(previous, next);
            assertTrue(next.contains(i));
            assertFalse(previous.contains(i));
            previous = next;
        }
        for (int i = 1; i < 10000; i++) {
            assertTrue(previous.contains(i));
        }
        for (int i = 1; i < 10000; i++) {
            var next = previous.delete(i);
            assertNotNull(next);
            assertTrue(next.checkIsRedBlackTree());
            assertNotEquals(previous, next);
            assertFalse(next.contains(i));
            assertTrue(previous.contains(i));
            previous = next;
        }
        for (int i = 1; i < 10000; i++) {
            assertFalse(previous.contains(i));
        }
    }

    @Test
    public void testCustomType() {
        var empty = new PersistentRedBlackTree<TestUtils.CustomInteger>();
        var one = new TestUtils.CustomInteger(1);
        var two = new TestUtils.CustomInteger(2);
        var three = new TestUtils.CustomInteger(3);
        var oneTree = empty.add(one);
        var twoTree = oneTree.add(two);
        var threeTree = oneTree.add(three);

        checkOrdersOfTree(oneTree, Map.of(PersistentTree.Order.PreOrder, List.of(one)));
        checkOrdersOfTree(twoTree, Map.of(
            PersistentTree.Order.InOrder, List.of(two, one),
            PersistentTree.Order.PreOrder, List.of(one, two),
            PersistentTree.Order.PostOrder, List.of(two, one)
        ));
        checkOrdersOfTree(threeTree, Map.of(
            PersistentTree.Order.InOrder, List.of(three, one),
            PersistentTree.Order.PreOrder, List.of(one, three),
            PersistentTree.Order.PostOrder, List.of(three, one)
        ));
    }

    @Test
    public void testRandomValues() {
        var random = new Random();
        var previous = EMPTY;
        var values = new ArrayList<Integer>();

        for (var i = 0; i < 10000; i++) {
            int value = random.nextInt();
            var next = previous.add(value);
            if (previous.contains(value)) {
                assertNull(next);
            } else {
                assertNotNull(next);
                assertTrue(next.checkIsRedBlackTree());
                assertNotEquals(previous, next);
                assertTrue(next.contains(value));
                assertFalse(previous.contains(value));
                previous = next;
                values.add(value);
            }
        }

        Collections.shuffle(values);

        for (var value: values) {
            var next = previous.delete(value);
            assertNotNull(next);
            assertTrue(next.checkIsRedBlackTree());
            assertNotEquals(previous, next);
            assertFalse(next.contains(value));
            assertTrue(previous.contains(value));
            previous = next;
        }
    }

    @Test
    public void testInOrderIterator() {
        PersistentRedBlackTree<Integer> tree = EMPTY.add(1).add(2).add(3).add(4).add(5).add(6).add(7);

        List<Integer> list = new ArrayList<>();
        Iterator<Integer> iterator = tree.iterator(PersistentTree.Order.InOrder);
        iterator.forEachRemaining(list::add);

        List<Integer> inOrder = List.of(1, 2, 3, 4, 5, 6, 7);
        assertEquals(inOrder, list);
    }

    @Test
    public void testPreOrderIterator() {
        PersistentRedBlackTree<Integer> tree = EMPTY.add(1).add(2).add(3).add(4).add(5).add(6).add(7);

        List<Integer> list = new ArrayList<>();
        Iterator<Integer> iterator = tree.iterator(PersistentTree.Order.PreOrder);
        iterator.forEachRemaining(list::add);

        List<Integer> preOrder = List.of(2, 1, 4, 3, 6, 5, 7);
        assertEquals(preOrder, list);
    }

    @Test
    public void testPostOrderIterator() {
        PersistentRedBlackTree<Integer> tree = EMPTY.add(1).add(2).add(3).add(4).add(5).add(6).add(7);

        List<Integer> list = new ArrayList<>();
        Iterator<Integer> iterator = tree.iterator(PersistentTree.Order.PostOrder);
        iterator.forEachRemaining(list::add);

        List<Integer> postOrder = List.of(1, 3, 5, 7, 6, 4, 2);
        assertEquals(postOrder, list);
    }
}
