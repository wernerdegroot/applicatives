package nl.wernerdegroot.applicatives.processor.generator;

import org.junit.jupiter.api.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static nl.wernerdegroot.applicatives.processor.generator.Lines.lines;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class LinesTest {

    @Test
    public void givenMultipleLines() {
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

}
