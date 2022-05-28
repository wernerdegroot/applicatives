package nl.wernerdegroot.applicatives.prelude;

import org.junit.jupiter.api.Test;

import java.util.stream.Collector;
import java.util.stream.Stream;

import static java.util.stream.Collectors.summingInt;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CollectorsTest {

    @Test
    public void combine() {
        Collector<Integer, ?, Integer> collectSum = summingInt(this::identity);
        Collector<Integer, ?, Integer> collectSumOfSquares = summingInt(this::square);
        Collector<Integer, ?, Integer> collectCount = summingInt(this::tally);
        Collector<Integer, ?, Double> collectStandardDeviation = Collectors.<Integer>instance().combine(
                collectSum,
                collectSumOfSquares,
                collectCount,
                (sum, sumOfSquares, count) -> {
                    double countAsDouble = (double) count;
                    return Math.sqrt(sumOfSquares / countAsDouble - square(sum / countAsDouble));
                }
        );

        // From https://revisionmaths.com/gcse-maths-revision/statistics-handling-data/standard-deviation
        double expected = 3.94;
        double toVerify = Stream.of(4, 9, 11, 12, 17, 5, 8, 12, 14).collect(collectStandardDeviation);
        assertTrue(Math.abs(expected - toVerify) < 0.005); // Up to rounding error
    }

    private int identity(Integer i) {
        return i;
    }

    private int square(Integer i) {
        return i * i;
    }

    private double square(Double i) {
        return i * i;
    }

    private int tally(Integer i) {
        return 1;
    }
}
