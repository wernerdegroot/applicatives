package nl.wernerdegroot.applicatives.processor.generator;

import org.junit.jupiter.api.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class BodyGeneratorTest {

    @Test
    public void shouldAcceptMultipleLines() {
        List<String> toVerify = toTest()
                .withBody("Line 1", "Line 2")
                .withBody(asList("Line 3", "Line 4"))
                .withBody("Line 5")
                .getBodyGenerator()
                .lines();

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
    public void shouldIndentNonEmptyLinesWithFourSpaces() {
        List<String> toVerify = toTest()
                .withBody("Line 1", "Line 2")
                .getBodyGenerator()
                .indent()
                .indent()
                .lines();

        List<String> expected = asList(
                "        Line 1",
                "        Line 2"
        );

        assertEquals(expected, toVerify);
    }

    @Test
    public void shouldNotIndentEmptyLines() {
        List<String> toVerify = toTest()
                .withBody("Line 1", "", "Line 2")
                .getBodyGenerator()
                .indent()
                .indent()
                .lines();

        List<String> expected = asList(
                "        Line 1",
                "",
                "        Line 2"
        );

        assertEquals(expected, toVerify);
    }

    private static ToTest toTest() {
        return new ToTest();
    }

    private static class ToTest implements BodyGenerator.HasBodyGenerator<ToTest> {

        private BodyGenerator bodyGenerator = new BodyGenerator();

        @Override
        public BodyGenerator getBodyGenerator() {
            return bodyGenerator;
        }

        @Override
        public ToTest getThis() {
            return this;
        }
    }
}
