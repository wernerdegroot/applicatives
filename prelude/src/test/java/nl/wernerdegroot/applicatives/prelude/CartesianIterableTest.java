package nl.wernerdegroot.applicatives.prelude;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CartesianIterableTest {

    @Test
    public void wrapperGetSize() {
        assertEquals(4, CartesianIterable.of(asList("one", "two", "three", "four")).getSize());
    }

    @Test
    public void wrapperIterator() {
        assertEquals(asList("one", "two", "three", "four"), collect(CartesianIterable.of(asList("one", "two", "three", "four")).iterator()));
    }

    @Test
    public void compositeGetSizeGivenEmptyLeft() {
        List<Integer> left = asList();
        List<Boolean> right = asList(true, false);
        assertEquals(0, CartesianIterable.of(CartesianIterable.of(left), right, this::combineIntAndBoolean).getSize());
    }

    @Test
    public void compositeGetSizeGivenEmptyRight() {
        List<Integer> left = asList(1, 2, 3);
        List<Boolean> right = asList();
        assertEquals(0, CartesianIterable.of(CartesianIterable.of(left), right, this::combineIntAndBoolean).getSize());
    }

    @Test
    public void compositeGetSize() {
        List<Integer> left = asList(1, 2, 3);
        List<Boolean> right = asList(true, false);
        assertEquals(6, CartesianIterable.of(CartesianIterable.of(left), right, this::combineIntAndBoolean).getSize());
    }

    @Test
    public void compositeIteratorGivenEmptyLeft() {
        List<Integer> left = asList();
        List<Boolean> right = asList(true, false);
        assertEquals(asList(), collect(CartesianIterable.of(CartesianIterable.of(left), right, this::combineIntAndBoolean).iterator()));
    }

    @Test
    public void compositeIteratorGivenEmptyRight() {
        List<Integer> left = asList(1, 2, 3);
        List<Boolean> right = asList();
        assertEquals(asList(), collect(CartesianIterable.of(CartesianIterable.of(left), right, this::combineIntAndBoolean).iterator()));
    }

    @Test
    public void compositeIterator() {
        List<Integer> left = asList(1, 2, 3);
        List<Boolean> right = asList(true, false);
        assertEquals(asList("1!", "1?", "2!", "2?", "3!", "3?"), collect(CartesianIterable.of(CartesianIterable.of(left), right, this::combineIntAndBoolean).iterator()));
    }

    private String combineIntAndBoolean(int i, boolean b) {
        return i + (b ? "!" : "?");
    }

    private <T> List<T> collect(Iterator<T> iterator) {
        List<T> collected = new ArrayList<>();
        while (iterator.hasNext()) {
            collected.add(iterator.next());
        }
        return collected;
    }
}
