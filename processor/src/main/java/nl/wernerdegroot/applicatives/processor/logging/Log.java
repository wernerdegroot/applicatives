package nl.wernerdegroot.applicatives.processor.logging;

import java.util.*;
import java.util.function.Function;

import static java.util.stream.Collectors.joining;

public class Log {
    private final LoggingBackend loggingBackend;
    private final String message;
    private final List<String> details = new ArrayList<>();

    public Log(LoggingBackend loggingBackend, String message) {
        this.loggingBackend = loggingBackend;
        this.message = message;
    }

    public static Log of(LoggingBackend loggingBackend, String message) {
        return new Log(loggingBackend, message);
    }

    public Log withDetails(Collection<String> toAdd) {
        toAdd.forEach(detail -> {
            details.add(" - " + detail);
        });
        return this;
    }

    public <T> Log withDetail(String detail, Collection<? extends T> values, Function<? super T, String> printer) {
        String valuesAsString = values.isEmpty()
                ? "none"
                : values.stream().map(printer).collect(joining(", "));
        details.add(" - " + detail + ": " + valuesAsString);
        return this;
    }

    public <T> Log withDetail(String detail, Optional<? extends T> value, Function<? super T, String> printer) {
        return withDetail(detail, value.map(Collections::singletonList).orElseGet(Collections::emptyList), printer);
    }

    public <T> Log withDetail(String detail, T value, Function<? super T, String> printer) {
        return withDetail(detail, Collections.singletonList(value), printer);
    }

    public Log withDetail(String detail, Collection<String> value) {
        return withDetail(detail, value, Function.identity());
    }

    public Log withDetail(String detail, String value) {
        return withDetail(detail, Collections.singletonList(value), Function.identity());
    }

    public void append() {
        loggingBackend.log(message);
        details.forEach(loggingBackend::log);
    }
}
