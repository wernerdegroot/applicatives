package nl.wernerdegroot.applicatives.prelude;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toSet;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CartesianCollectableTest {

    @Test
    public void test() {
        List<Set<Integer>> toVerify = Stream.of(1, 2, 3)
                .map(i -> asList(i, i + 1))
                .collect(CartesianCollectable.collector(toSet()));

        List<Set<Integer>> expected = asList(
                Stream.of(1, 2, 3).collect(toSet()),
                Stream.of(1, 2, 4).collect(toSet()),
                Stream.of(1, 3).collect(toSet()),
                Stream.of(1, 3, 4).collect(toSet()),
                Stream.of(2, 3).collect(toSet()),
                Stream.of(2, 4).collect(toSet()),
                Stream.of(2, 3).collect(toSet()),
                Stream.of(2, 3, 4).collect(toSet())
        );

        assertEquals(expected, toVerify);
    }
}
