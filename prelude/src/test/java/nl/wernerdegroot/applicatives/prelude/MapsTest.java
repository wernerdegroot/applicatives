package nl.wernerdegroot.applicatives.prelude;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MapsTest {

    @Test
    public void combine() {
        Map<Integer, String> left = new HashMap<>();
        left.put(1, "One");
        left.put(2, "Two");
        left.put(3, "Three");

        Map<Integer, String> right = new HashMap<>();
        right.put(2, "Twee");
        right.put(3, "Drie");
        right.put(4, "Vier");

        Map<Integer, String> expected = new HashMap<>();
        expected.put(2, "TwoTwee");
        expected.put(3, "ThreeDrie");
        Map<Integer, String> toVerify = Maps.<Integer>instance().compose(left, right, String::concat);

        assertEquals(expected, toVerify);
    }
}
