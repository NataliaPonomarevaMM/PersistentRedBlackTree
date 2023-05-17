package io.whitecloud;

import java.util.List;
import java.util.Map;

import static io.whitecloud.TestUtils.EMPTY;
import static io.whitecloud.TestUtils.checkOrdersOfTree;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.whitecloud.impl.PersistentRedBlackTree;
import org.junit.jupiter.api.Test;

public class PersistentRedBlackTreeAddTest {
    @Test
    public void testAddIncreasing() {
        PersistentRedBlackTree<Integer> tree = EMPTY.add(1).add(2).add(3).add(4).add(5).add(6).add(7);
        assertNotNull(tree);
        for (int i = 1; i < 8; i++) {
            assertTrue(tree.contains(i));
        }
        assertFalse(tree.contains(0));
        assertFalse(tree.contains(10000));

        checkOrdersOfTree(tree, Map.of(
            PersistentTree.Order.InOrder, List.of(1, 2, 3, 4, 5, 6, 7),
            PersistentTree.Order.PreOrder, List.of(2, 1, 4, 3, 6, 5, 7),
            PersistentTree.Order.PostOrder, List.of(1, 3, 5, 7, 6, 4, 2)
        ));
    }

    @Test
    public void testAddDecreasing() {
        PersistentRedBlackTree<Integer> tree = EMPTY.add(7).add(6).add(5).add(4).add(3).add(2).add(1);
        assertNotNull(tree);
        for (int i = 1; i < 8; i++) {
            assertTrue(tree.contains(i));
        }
        assertFalse(tree.contains(0));
        assertFalse(tree.contains(10000));

        checkOrdersOfTree(tree, Map.of(
            PersistentTree.Order.InOrder, List.of(1, 2, 3, 4, 5, 6, 7),
            PersistentTree.Order.PreOrder, List.of(6, 4, 2, 1, 3, 5, 7),
            PersistentTree.Order.PostOrder, List.of(1, 3, 2, 5, 4, 7, 6)
        ));
    }

//        10
//     5      15
//  2
    @Test
    public void testAddUncleRedLeftLeft() {
        PersistentRedBlackTree<Integer> tree = EMPTY.add(10).add(15).add(5);
        assertNotNull(tree);
        checkOrdersOfTree(tree, Map.of(PersistentTree.Order.PreOrder, List.of(10, 5, 15)));

        PersistentRedBlackTree<Integer> added = tree.add(2);
        assertNotEquals(tree, added);

        checkOrdersOfTree(tree, Map.of(PersistentTree.Order.PreOrder, List.of(10, 5, 15)));
        checkOrdersOfTree(added, Map.of(
            PersistentTree.Order.InOrder, List.of(2, 5, 10, 15),
            PersistentTree.Order.PreOrder, List.of(10, 5, 2, 15),
            PersistentTree.Order.PostOrder, List.of(2, 5, 15, 10)
        ));
    }

//        10
//     5      15
//       7
    @Test
    public void testAddUncleRedLeftRight() {
        PersistentRedBlackTree<Integer> tree = EMPTY.add(10).add(15).add(5);
        assertNotNull(tree);
        checkOrdersOfTree(tree, Map.of(PersistentTree.Order.PreOrder, List.of(10, 5, 15)));

        PersistentRedBlackTree<Integer> added = tree.add(7);
        assertNotEquals(tree, added);

        checkOrdersOfTree(tree, Map.of(PersistentTree.Order.PreOrder, List.of(10, 5, 15)));
        checkOrdersOfTree(added, Map.of(
            PersistentTree.Order.InOrder, List.of(5, 7, 10, 15),
            PersistentTree.Order.PreOrder, List.of(10, 5, 7, 15),
            PersistentTree.Order.PostOrder, List.of(7, 5, 15, 10)
        ));
    }

//        10
//     5      15
//          12
    @Test
    public void testAddUncleRedRightLeft() {
        PersistentRedBlackTree<Integer> tree = EMPTY.add(10).add(15).add(5);
        assertNotNull(tree);
        checkOrdersOfTree(tree, Map.of(PersistentTree.Order.PreOrder, List.of(10, 5, 15)));

        PersistentRedBlackTree<Integer> added = tree.add(12);
        assertNotEquals(tree, added);

        checkOrdersOfTree(tree, Map.of(PersistentTree.Order.PreOrder, List.of(10, 5, 15)));
        checkOrdersOfTree(added, Map.of(
            PersistentTree.Order.InOrder, List.of(5, 10, 12, 15),
            PersistentTree.Order.PreOrder, List.of(10, 5, 15, 12),
            PersistentTree.Order.PostOrder, List.of(5, 12, 15, 10)
        ));
    }

//        10
//     5      15
//               18
    @Test
    public void testAddUncleRedRightRight() {
        PersistentRedBlackTree<Integer> tree = EMPTY.add(10).add(15).add(5);
        assertNotNull(tree);
        checkOrdersOfTree(tree, Map.of(PersistentTree.Order.PreOrder, List.of(10, 5, 15)));

        PersistentRedBlackTree<Integer> added = tree.add(18);
        assertNotEquals(tree, added);

        checkOrdersOfTree(tree, Map.of(PersistentTree.Order.PreOrder, List.of(10, 5, 15)));
        checkOrdersOfTree(added, Map.of(
            PersistentTree.Order.InOrder, List.of(5, 10, 15, 18),
            PersistentTree.Order.PreOrder, List.of(10, 5, 15, 18),
            PersistentTree.Order.PostOrder, List.of(5, 18, 15, 10)
        ));
    }

//                10                                           7
//         7              12            ->              4             10
//    4        8      11         13                 2       5     8        12
// 2     5                                     1                        11    13
    @Test
    public void testAddUncleBlackLeftLeft() {
        PersistentRedBlackTree<Integer> tree = EMPTY.add(10).add(7).add(12).add(4).add(8).add(11).add(13).add(2).add(5);
        assertNotNull(tree);
        checkOrdersOfTree(tree, Map.of(PersistentTree.Order.PreOrder, List.of(10, 7, 4, 2, 5, 8, 12, 11, 13)));

        PersistentRedBlackTree<Integer> added = tree.add(1);
        assertNotEquals(tree, added);

        checkOrdersOfTree(tree, Map.of(PersistentTree.Order.PreOrder, List.of(10, 7, 4, 2, 5, 8, 12, 11, 13)));
        checkOrdersOfTree(added, Map.of(
            PersistentTree.Order.InOrder, List.of(1, 2, 4, 5, 7, 8, 10, 11, 12, 13),
            PersistentTree.Order.PreOrder, List.of(7, 4, 2, 1, 5, 10, 8, 12, 11, 13),
            PersistentTree.Order.PostOrder, List.of(1, 2, 5, 4, 8, 11, 13, 12, 10, 7)
        ));
    }

//                10                                           7
//         5              12            ->              5             10
//    4        7      11      13                    4      6      8        12
//         6      8                                                 9    11    13
    @Test
    public void testAddUncleBlackLeftRight() {
        PersistentRedBlackTree<Integer> tree = EMPTY.add(10).add(5).add(12).add(4).add(7).add(11).add(13).add(6).add(8);
        assertNotNull(tree);
        checkOrdersOfTree(tree, Map.of(PersistentTree.Order.PreOrder, List.of(10, 5, 4, 7, 6, 8, 12, 11, 13)));

        PersistentRedBlackTree<Integer> added = tree.add(9);
        assertNotEquals(tree, added);

        checkOrdersOfTree(tree, Map.of(PersistentTree.Order.PreOrder, List.of(10, 5, 4, 7, 6, 8, 12, 11, 13)));
        checkOrdersOfTree(added, Map.of(
            PersistentTree.Order.InOrder, List.of(4, 5, 6, 7, 8, 9, 10, 11, 12, 13),
            PersistentTree.Order.PreOrder, List.of(7, 5, 4, 6, 10, 8, 9, 12, 11, 13),
            PersistentTree.Order.PostOrder, List.of(4, 6, 5, 9, 8, 11, 13, 12, 10, 7)
        ));
    }

//             5                                         7
//     3               10            ->          5               10
//        4       7         11              3       6        8       11
//             6     8                        4                9
    @Test
    public void testAddUncleBlackRightLeft() {
        PersistentRedBlackTree<Integer> tree = EMPTY.add(5).add(3).add(10).add(4).add(7).add(11).add(6).add(8);
        assertNotNull(tree);
        checkOrdersOfTree(tree, Map.of(PersistentTree.Order.PreOrder, List.of(5, 3, 4, 10, 7, 6, 8, 11)));

        PersistentRedBlackTree<Integer> added = tree.add(9);
        assertNotEquals(tree, added);

        checkOrdersOfTree(tree, Map.of(PersistentTree.Order.PreOrder, List.of(5, 3, 4, 10, 7, 6, 8, 11)));
        checkOrdersOfTree(added, Map.of(
            PersistentTree.Order.InOrder, List.of(3, 4, 5, 6, 7, 8, 9, 10, 11),
            PersistentTree.Order.PreOrder, List.of(7, 5, 3, 4, 6, 10, 8, 9, 11),
            PersistentTree.Order.PostOrder, List.of(4, 3, 6, 5, 9, 8, 11, 10, 7)
        ));
    }

//             5                                         10
//     3               10            ->          5              12
//        4       7         12              3       7      11         13
//                       11     13             4                           14
    @Test
    public void testAddUncleBlackRightRight() {
        PersistentRedBlackTree<Integer> tree = EMPTY.add(5).add(3).add(10).add(4).add(7).add(12).add(11).add(13);
        assertNotNull(tree);
        checkOrdersOfTree(tree, Map.of(PersistentTree.Order.PreOrder, List.of(5, 3, 4, 10, 7, 12, 11, 13)));

        PersistentRedBlackTree<Integer> added = tree.add(14);
        assertNotEquals(tree, added);

        checkOrdersOfTree(tree, Map.of(PersistentTree.Order.PreOrder, List.of(5, 3, 4, 10, 7, 12, 11, 13)));
        checkOrdersOfTree(added, Map.of(
            PersistentTree.Order.InOrder, List.of(3, 4, 5, 7, 10, 11, 12, 13, 14),
            PersistentTree.Order.PreOrder, List.of(10, 5, 3, 4, 7, 12, 11, 13, 14),
            PersistentTree.Order.PostOrder, List.of(4, 3, 7, 5, 11, 14, 13, 12, 10)
        ));
    }
}
