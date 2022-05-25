package nl.wernerdegroot.applicatives.processor.generator;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collector;
import java.util.stream.Stream;

import static java.lang.Integer.toHexString;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static nl.wernerdegroot.applicatives.processor.generator.Lines.lines;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class LinesTest {

    @Test
    public void generateGivenMultipleLines() {
        List<String> toVerify = lines()
                .append("Line 1", "Line 2")
                .append(asList("Line 3", "Line 4"))
                .append("Line 5");

        List<String> expected = asList(
                "Line 1",
                "Line 2",
                "Line 3",
                "Line 4",
                "Line 5"
        );

        assertEquals(expected, toVerify);
    }

    @Test
    public void collectingWithNoLines() {
        List<String> expected = emptyList();
        List<String> toVerify = Stream.<Integer>of().collect(collectingIntegers());

        assertEquals(expected, toVerify);
    }
    @Test
    public void collectingWithSingleLines() {
        List<String> expected = asList("1", "1");
        List<String> toVerify = Stream.of(1).collect(collectingIntegers());

        assertEquals(expected, toVerify);
    }

    @Test
    public void collectingWithMultipleLines() {
        List<String> expected = asList("1", "1", "", "15", "f");
        List<String> toVerify = Stream.of(1, 15).collect(collectingIntegers());

        assertEquals(expected, toVerify);
    }

    @Test
    public void collectingParallelLines() {
        ForkJoinPool pool = new ForkJoinPool(2);

        List<String> expected = asList("202", "ca", "", "254", "fe", "", "186", "ba", "", "190", "be");
        List<String> toVerify = pool.submit(() -> Stream.of(202, 254, 186, 190).parallel().collect(collectingIntegers())).join();

        pool.shutdown();

        assertEquals(expected, toVerify);
    }

    private static Collector<Integer, ?, List<String>> collectingIntegers() {
        return Lines.collecting(i -> asList(Integer.toString(i), toHexString(i)));
    }

}
