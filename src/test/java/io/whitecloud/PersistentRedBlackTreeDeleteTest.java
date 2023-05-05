package io.whitecloud;

import java.util.List;
import java.util.Map;

import static io.whitecloud.TestUtils.EMPTY;
import static io.whitecloud.TestUtils.checkOrdersOfTree;
import static org.junit.jupiter.api.Assertions.*;

import io.whitecloud.impl.PersistentRedBlackTree;
import org.junit.jupiter.api.Test;

public class PersistentRedBlackTreeDeleteTest {
    @Test
    public void testDeleteIncreasing() {
        PersistentRedBlackTree<Integer>
            tree = EMPTY.add(1).add(2).add(3).add(4).add(5).add(6).add(7);
        assertNotNull(tree);
        for (int i = 1; i < 8; i++) {
            assertTrue(tree.contains(i));
        }
        PersistentRedBlackTree<Integer> deleted = tree.delete(1).delete(2).delete(3).delete(4).delete(5).delete(6).delete(7);
        assertNotNull(deleted);
        assertNotEquals(deleted, tree);
        for (int i = 1; i < 8; i++) {
            assertFalse(deleted.contains(i));
        }
    }

    @Test
    public void testDeleteDecreasing() {
        PersistentRedBlackTree<Integer> tree = EMPTY.add(7).add(6).add(5).add(4).add(3).add(2).add(1);
        assertNotNull(tree);
        for (int i = 1; i < 8; i++) {
            assertTrue(tree.contains(i));
        }
        PersistentRedBlackTree<Integer> deleted = tree.delete(7).delete(6).delete(5).delete(4).delete(3).delete(2).delete(1);
        assertNotNull(deleted);
        assertNotEquals(deleted, tree);
        for (int i = 1; i < 8; i++) {
            assertFalse(deleted.contains(i));
        }
    }


//                10                                           11
//         5              15            ->              5             15
//    4        6      12      17                    4      6      12       17
//                 11    13                                          13
    @Test
    public void testDeleteTwoChildren() {
        PersistentRedBlackTree<Integer> tree = EMPTY.add(10).add(5).add(15).add(4).add(6).add(12).add(17).add(11).add(13);
        assertNotNull(tree);
        checkOrdersOfTree(tree, Map.of(PersistentTree.Order.PreOrder, List.of(10, 5, 4, 6, 15, 12, 11, 13, 17)));

        PersistentRedBlackTree<Integer> deleted = tree.delete(10);
        assertNotEquals(tree, deleted);

        checkOrdersOfTree(tree, Map.of(PersistentTree.Order.PreOrder, List.of(10, 5, 4, 6, 15, 12, 11, 13, 17)));
        checkOrdersOfTree(deleted, Map.of(
            PersistentTree.Order.InOrder, List.of(4, 5, 6, 11, 12, 13, 15, 17),
            PersistentTree.Order.PreOrder, List.of(11, 5, 4, 6, 15, 12, 13, 17),
            PersistentTree.Order.PostOrder, List.of(4, 6, 5, 13, 12, 17, 15, 11)
        ));
    }

//                5                                           10
//          3             10            ->              6          11
//                    7      11                      5     7
//                 6
    @Test
    public void testDeleteRedSiblingLeft() {
        PersistentRedBlackTree<Integer> tree = EMPTY.add(5).add(3).add(10).add(7).add(11).add(6);
        assertNotNull(tree);
        checkOrdersOfTree(tree, Map.of(PersistentTree.Order.PreOrder, List.of(5, 3, 10, 7, 6, 11)));

        PersistentRedBlackTree<Integer> deleted = tree.delete(3);
        assertNotEquals(tree, deleted);

        checkOrdersOfTree(tree, Map.of(PersistentTree.Order.PreOrder, List.of(5, 3, 10, 7, 6, 11)));
        checkOrdersOfTree(deleted, Map.of(
            PersistentTree.Order.InOrder, List.of(5, 6, 7, 10, 11),
            PersistentTree.Order.PreOrder, List.of(10, 6, 5, 7, 11),
            PersistentTree.Order.PostOrder, List.of(5, 7, 6, 11, 10)
        ));
    }

//                      15                                     10
//              10            20         ->               7          15
//           7      11                                 6          11
//        6
    @Test
    public void testDeleteRedSiblingRight() {
        PersistentRedBlackTree<Integer> tree = EMPTY.add(15).add(10).add(20).add(7).add(11).add(6);
        assertNotNull(tree);
        checkOrdersOfTree(tree, Map.of(PersistentTree.Order.PreOrder, List.of(15, 10, 7, 6, 11, 20)));

        PersistentRedBlackTree<Integer> deleted = tree.delete(20);
        assertNotEquals(tree, deleted);

        checkOrdersOfTree(tree, Map.of(PersistentTree.Order.PreOrder, List.of(15, 10, 7, 6, 11, 20)));
        checkOrdersOfTree(deleted, Map.of(
            PersistentTree.Order.InOrder, List.of(6, 7, 10, 11, 15),
            PersistentTree.Order.PreOrder, List.of(10, 7, 6, 15, 11),
            PersistentTree.Order.PostOrder, List.of(6, 7, 11, 15, 10)
        ));
    }

//                  12                                          12
//          5               15            ->             6              15
//     3        10      13       17                 3        10      13     17
//       4   7      11     14                         4    7    11      14
//         6  8                                             8
    @Test
    public void testDeleteBlackSiblingBlackChildrenLeft() {
        PersistentRedBlackTree<Integer> tree = EMPTY.add(12).add(5).add(15).add(3).add(10).add(13).add(17).add(4).add(7).add(11).add(14).add(6).add(8);
        assertNotNull(tree);
        checkOrdersOfTree(tree, Map.of(PersistentTree.Order.PreOrder, List.of(12, 5, 3, 4, 10, 7, 6, 8, 11, 15, 13, 14, 17)));

        PersistentRedBlackTree<Integer> deleted = tree.delete(5);
        assertNotEquals(tree, deleted);

        checkOrdersOfTree(tree, Map.of(PersistentTree.Order.PreOrder, List.of(12, 5, 3, 4, 10, 7, 6, 8, 11, 15, 13, 14, 17)));
        checkOrdersOfTree(deleted, Map.of(
            PersistentTree.Order.InOrder, List.of(3, 4, 6, 7, 8, 10, 11, 12, 13, 14, 15, 17),
            PersistentTree.Order.PreOrder, List.of(12, 6, 3, 4, 10, 7, 8, 11, 15, 13, 14, 17),
            PersistentTree.Order.PostOrder, List.of(4, 3, 8, 7, 11, 10, 6, 14, 13, 17, 15, 12)
        ));
    }

//                  12                                          12
//          5               15            ->              5               16
//     3        10     13          20                 3        10    13         20
//            9           14    17    21                    9           14   17      21
//                            16  18                                           18
    @Test
    public void testDeleteBlackSiblingBlackChildrenRight() {
        PersistentRedBlackTree<Integer> tree = EMPTY.add(12).add(5).add(15).add(3).add(10).add(13).add(20).add(9).add(14).add(17).add(21).add(16).add(18);
        assertNotNull(tree);
        checkOrdersOfTree(tree, Map.of(PersistentTree.Order.PreOrder, List.of(12, 5, 3, 10, 9, 15, 13, 14, 20, 17, 16, 18, 21)));

        PersistentRedBlackTree<Integer> deleted = tree.delete(15);
        assertNotEquals(tree, deleted);

        checkOrdersOfTree(tree, Map.of(PersistentTree.Order.PreOrder, List.of(12, 5, 3, 10, 9, 15, 13, 14, 20, 17, 16, 18, 21)));
        checkOrdersOfTree(deleted, Map.of(
            PersistentTree.Order.InOrder, List.of(3, 5, 9, 10, 12, 13, 14, 16, 17, 18, 20, 21),
            PersistentTree.Order.PreOrder, List.of(12, 5, 3, 10, 9, 16, 13, 14, 20, 17, 18, 21),
            PersistentTree.Order.PostOrder, List.of(3, 9, 10, 5, 14, 13, 18, 17, 21, 20, 16, 12)
        ));
    }

//                  12                                            12
//          5                19            ->              9               19
//     3       10        17        20                   5     10       17       20
//          9         15    18        21                            15   18         21
//                 13                                             13
    @Test
    public void testDeleteBlackSiblingRedBlackChildrenLeft() {
        PersistentRedBlackTree<Integer> tree = EMPTY.add(12).add(5).add(19).add(3).add(10).add(17).add(20).add(9).add(15).add(18).add(21).add(13);
        assertNotNull(tree);
        checkOrdersOfTree(tree, Map.of(PersistentTree.Order.PreOrder, List.of(12, 5, 3, 10, 9, 19, 17, 15, 13, 18, 20, 21)));

        PersistentRedBlackTree<Integer> deleted = tree.delete(3);
        assertNotEquals(tree, deleted);

        checkOrdersOfTree(tree, Map.of(PersistentTree.Order.PreOrder, List.of(12, 5, 3, 10, 9, 19, 17, 15, 13, 18, 20, 21)));
        checkOrdersOfTree(deleted, Map.of(
            PersistentTree.Order.InOrder, List.of(5, 9, 10, 12, 13, 15, 17, 18, 19, 20, 21),
            PersistentTree.Order.PreOrder, List.of(12, 9, 5, 10, 19, 17, 15, 13, 18, 20, 21),
            PersistentTree.Order.PostOrder, List.of(5, 10, 9, 13, 15, 18, 17, 21, 20, 19, 12)
        ));
    }

    @Test
    public void testDeleteBlackSiblingRedBlackNullChildrenLeft() {
        PersistentRedBlackTree<Integer> tree = EMPTY.add(5).add(2).add(7).add(6);
        assertNotNull(tree);
        checkOrdersOfTree(tree, Map.of(PersistentTree.Order.PreOrder, List.of(5, 2, 7, 6)));

        PersistentRedBlackTree<Integer> deleted = tree.delete(2);
        assertNotEquals(tree, deleted);

        checkOrdersOfTree(tree, Map.of(PersistentTree.Order.PreOrder, List.of(5, 2, 7, 6)));
        checkOrdersOfTree(deleted, Map.of(
            PersistentTree.Order.InOrder, List.of(5, 6, 7),
            PersistentTree.Order.PreOrder, List.of(6, 5, 7),
            PersistentTree.Order.PostOrder, List.of(5, 7, 6)
        ));
    }


    @Test
    public void testDeleteBlackSiblingRedBlackNullChildrenRight() {
        PersistentRedBlackTree<Integer> tree = EMPTY.add(5).add(3).add(6).add(2);
        assertNotNull(tree);
        checkOrdersOfTree(tree, Map.of(PersistentTree.Order.PreOrder, List.of(5, 3, 2, 6)));

        PersistentRedBlackTree<Integer> deleted = tree.delete(6);
        assertNotEquals(tree, deleted);

        checkOrdersOfTree(tree, Map.of(PersistentTree.Order.PreOrder, List.of(5, 3, 2, 6)));
        checkOrdersOfTree(deleted, Map.of(
            PersistentTree.Order.InOrder, List.of(2, 3, 5),
            PersistentTree.Order.PreOrder, List.of(3, 2, 5),
            PersistentTree.Order.PostOrder, List.of(2, 5, 3)
        ));
    }

//                  12                                            12
//          5                19            ->              5               20
//     3       10        17        20                   3     10       19       21
//  2       8     11                   21             2      8   11
//        7                                                7
    @Test
    public void testDeleteBlackSiblingRedBlackChildrenRight() {
        PersistentRedBlackTree<Integer> tree = EMPTY.add(12).add(5).add(19).add(3).add(10).add(17).add(20).add(2).add(8).add(11).add(21).add(7);
        assertNotNull(tree);
        checkOrdersOfTree(tree, Map.of(PersistentTree.Order.PreOrder, List.of(12, 5, 3, 2, 10, 8, 7, 11, 19, 17, 20, 21)));

        PersistentRedBlackTree<Integer> deleted = tree.delete(17);
        assertNotEquals(tree, deleted);

        checkOrdersOfTree(tree, Map.of(PersistentTree.Order.PreOrder, List.of(12, 5, 3, 2, 10, 8, 7, 11, 19, 17, 20, 21)));
        checkOrdersOfTree(deleted, Map.of(
            PersistentTree.Order.InOrder, List.of(2, 3, 5, 7, 8, 10, 11, 12, 19, 20, 21),
            PersistentTree.Order.PreOrder, List.of(12, 5, 3, 2, 10, 8, 7, 11, 20, 19, 21),
            PersistentTree.Order.PostOrder, List.of(2, 3, 7, 8, 11, 10, 5, 19, 21, 20, 12)
        ));
    }

//                5                                           10
//          3             10            ->                5        11
//                    7       11                             7
    @Test
    public void testDeleteBlackSiblingRedChildrenLeft() {
        PersistentRedBlackTree<Integer> tree = EMPTY.add(5).add(3).add(10).add(7).add(11);
        assertNotNull(tree);
        checkOrdersOfTree(tree, Map.of(PersistentTree.Order.PreOrder, List.of(5, 3, 10, 7, 11)));

        PersistentRedBlackTree<Integer> deleted = tree.delete(3);
        assertNotEquals(tree, deleted);

        checkOrdersOfTree(tree, Map.of(PersistentTree.Order.PreOrder, List.of(5, 3, 10, 7, 11)));
        checkOrdersOfTree(deleted, Map.of(
            PersistentTree.Order.InOrder, List.of(5, 7, 10, 11),
            PersistentTree.Order.PreOrder, List.of(10, 5, 7, 11),
            PersistentTree.Order.PostOrder, List.of(7, 5, 11, 10)
        ));
    }

//                5                                         3
//          3           10            ->                1        5
//       1     4                                              4
    @Test
    public void testDeleteBlackSiblingRedChildrenRight() {
        PersistentRedBlackTree<Integer> tree = EMPTY.add(5).add(3).add(10).add(1).add(4);
        assertNotNull(tree);
        checkOrdersOfTree(tree, Map.of(PersistentTree.Order.PreOrder, List.of(5, 3, 1, 4, 10)));

        PersistentRedBlackTree<Integer> deleted = tree.delete(10);
        assertNotEquals(tree, deleted);

        checkOrdersOfTree(tree, Map.of(PersistentTree.Order.PreOrder, List.of(5, 3, 1, 4, 10)));
        checkOrdersOfTree(deleted, Map.of(
            PersistentTree.Order.InOrder, List.of(1, 3, 4, 5),
            PersistentTree.Order.PreOrder, List.of(3, 1, 5, 4),
            PersistentTree.Order.PostOrder, List.of(1, 4, 5, 3)
        ));
    }
}
