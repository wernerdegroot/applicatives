package nl.wernerdegroot.applicatives.processor.logging;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class LogTest {

    @Test
    public void givenSimpleMessage() {
        StringBuilderLoggingBackend loggingBackend = new StringBuilderLoggingBackend();
        Log.of("This is a simple message").append(loggingBackend);
        assertEquals("This is a simple message\n", loggingBackend.build());
    }

    @Test
    public void withDetailsGivenCollectionOfDetails() {
        StringBuilderLoggingBackend loggingBackend = new StringBuilderLoggingBackend();

        Log.of("This is a simple message")
                .withDetails(asList("and", "these"))
                .withDetails(asList("are", "details"))
                .append(loggingBackend);

        String expected = "This is a simple message\n" +
                " - and\n" +
                " - these\n" +
                " - are\n" +
                " - details\n";

        assertEquals(expected, loggingBackend.build());
    }

    @Test
    public void withDetailGivenLog() {
        StringBuilderLoggingBackend loggingBackend = new StringBuilderLoggingBackend();

        Log.of("This is a simple message")
                .withDetail(Log.of("This is a detail...").withDetail(Log.of("...which contains its own details")))
                .withDetail(Log.of("This is another detail"))
                .append(loggingBackend);

        String expected = "This is a simple message\n" +
                " - This is a detail...\n" +
                "    - ...which contains its own details\n" +
                " - This is another detail\n";

        assertEquals(expected, loggingBackend.build());
    }

    @Test
    public void withDetailGivenCollectionOfDetailsAndPrinter() {
        StringBuilderLoggingBackend loggingBackend = new StringBuilderLoggingBackend();

        Log.of("This is a simple message")
                .withDetail("It has many things to inform you about", asList(16, 15, 14), Integer::toHexString)
                .append(loggingBackend);

        String expected = "This is a simple message\n" +
                " - It has many things to inform you about: 10, f, e\n";

        assertEquals(expected, loggingBackend.build());
    }

    @Test
    public void withDetailGivenNonEmptyDetail() {
        StringBuilderLoggingBackend loggingBackend = new StringBuilderLoggingBackend();

        Log.of("This is a simple message")
                .withDetail("It may have some things to inform you about", Optional.of("Indeed"))
                .append(loggingBackend);

        String expected = "This is a simple message\n" +
                " - It may have some things to inform you about: Indeed\n";

        assertEquals(expected, loggingBackend.build());
    }

    @Test
    public void withDetailGivenEmptyDetail() {
        StringBuilderLoggingBackend loggingBackend = new StringBuilderLoggingBackend();

        Log.of("This is a simple message")
                .withDetail("It may have some things to inform you about", Optional.empty())
                .append(loggingBackend);

        String expected = "This is a simple message\n" +
                " - It may have some things to inform you about: none\n";

        assertEquals(expected, loggingBackend.build());
    }

    @Test
    public void withDetailGivenNonEmptyDetailAndPrinter() {
        StringBuilderLoggingBackend loggingBackend = new StringBuilderLoggingBackend();

        Log.of("This is a simple message")
                .withDetail("It may have some things to inform you about", Optional.of(15), Integer::toHexString)
                .append(loggingBackend);

        String expected = "This is a simple message\n" +
                " - It may have some things to inform you about: f\n";

        assertEquals(expected, loggingBackend.build());
    }

    @Test
    public void withDetailGivenEmptyDetailAndPrinter() {
        StringBuilderLoggingBackend loggingBackend = new StringBuilderLoggingBackend();

        Log.of("This is a simple message")
                .withDetail("It may have some things to inform you about", Optional.empty(), Integer::toHexString)
                .append(loggingBackend);

        String expected = "This is a simple message\n" +
                " - It may have some things to inform you about: none\n";

        assertEquals(expected, loggingBackend.build());
    }

    @Test
    public void withDetailGivenCollectionOfDetails() {
        StringBuilderLoggingBackend loggingBackend = new StringBuilderLoggingBackend();

        Log.of("This is a simple message")
                .withDetail("It has many things to inform you about", asList("10", "f", "e"))
                .append(loggingBackend);

        String expected = "This is a simple message\n" +
                " - It has many things to inform you about: 10, f, e\n";

        assertEquals(expected, loggingBackend.build());
    }

    @Test
    public void withDetailGivenDetail() {
        StringBuilderLoggingBackend loggingBackend = new StringBuilderLoggingBackend();

        Log.of("This is a simple message")
                .withDetail("It has something to inform you about", "f")
                .append(loggingBackend);

        String expected = "This is a simple message\n" +
                " - It has something to inform you about: f\n";

        assertEquals(expected, loggingBackend.build());
    }

    @Test
    public void withDetailGivenDetailAndPrinter() {
        StringBuilderLoggingBackend loggingBackend = new StringBuilderLoggingBackend();

        Log.of("This is a simple message")
                .withDetail("It has something to inform you about", 15, Integer::toHexString)
                .append(loggingBackend);

        String expected = "This is a simple message\n" +
                " - It has something to inform you about: f\n";

        assertEquals(expected, loggingBackend.build());
    }
}
