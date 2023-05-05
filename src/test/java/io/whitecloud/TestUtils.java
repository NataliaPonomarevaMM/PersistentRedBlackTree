package io.whitecloud;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.whitecloud.impl.PersistentRedBlackTree;

public class TestUtils {
    public static final PersistentRedBlackTree<Integer> EMPTY = new PersistentRedBlackTree<>();

    public static <E extends Comparable<E>> void checkOrdersOfTree(PersistentRedBlackTree<E> tree,
                                                                   Map<PersistentTree.Order, List<E>> orders)
    {
        for (var entry: orders.entrySet()) {
            List<E> list = new ArrayList<>();
            Iterator<E> iterator = tree.iterator(entry.getKey());
            iterator.forEachRemaining(list::add);

            assertEquals(entry.getValue(), list);
        }
    }

    public static class CustomInteger implements Comparable<CustomInteger> {
        int value;

        public CustomInteger(int value) {
            this.value = value;
        }

        @Override
        public int compareTo(CustomInteger o) {
            return Integer.compare(o.value, value);
        }
    }
}
