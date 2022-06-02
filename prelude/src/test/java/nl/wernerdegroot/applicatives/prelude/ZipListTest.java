package nl.wernerdegroot.applicatives.prelude;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ZipListTest {

    @Test
    public void wrapperGetSize() {
        assertEquals(4, ZipList.of(asList("one", "two", "three", "four")).getSize());
    }

    @Test
    public void wrapperIterator() {
        assertEquals(asList("one", "two", "three", "four"), collect(ZipList.of(asList("one", "two", "three", "four")).iterator()));
    }

    @Test
    public void compositeGetSizeGivenEmptyLeft() {
        List<Integer> left = asList();
        List<Boolean> right = asList(true, false);
        assertEquals(0, ZipList.of(ZipList.of(left), right, this::combineIntAndBoolean).getSize());
    }

    @Test
    public void compositeGetSizeGivenEmptyRight() {
        List<Integer> left = asList(1, 2, 3);
        List<Boolean> right = asList();
        assertEquals(0, ZipList.of(ZipList.of(left), right, this::combineIntAndBoolean).getSize());
    }

    @Test
    public void compositeGetSize() {
        List<Integer> left = asList(1, 2, 3);
        List<Boolean> right = asList(true, false);
        assertEquals(2, ZipList.of(ZipList.of(left), right, this::combineIntAndBoolean).getSize());
    }

    @Test
    public void compositeIteratorGivenEmptyLeft() {
        List<Integer> left = asList();
        List<Boolean> right = asList(true, false);
        assertEquals(asList(), collect(ZipList.of(ZipList.of(left), right, this::combineIntAndBoolean).iterator()));
    }

    @Test
    public void compositeIteratorGivenEmptyRight() {
        List<Integer> left = asList(1, 2, 3);
        List<Boolean> right = asList();
        assertEquals(asList(), collect(ZipList.of(ZipList.of(left), right, this::combineIntAndBoolean).iterator()));
    }

    @Test
    public void compositeIterator() {
        List<Integer> left = asList(1, 2, 3);
        List<Boolean> right = asList(true, false);
        assertEquals(asList("1!", "2?"), collect(ZipList.of(ZipList.of(left), right, this::combineIntAndBoolean).iterator()));
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
