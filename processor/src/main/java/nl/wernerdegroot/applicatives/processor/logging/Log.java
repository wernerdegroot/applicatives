package nl.wernerdegroot.applicatives.processor.logging;

import java.util.*;
import java.util.function.Function;

import static java.util.Collections.nCopies;
import static java.util.stream.Collectors.joining;

public class Log {

    private static final String LIST_ITEM_PREFIX = " - ";
    private static final String INDENT = nCopies(LIST_ITEM_PREFIX.length(), " ").stream().collect(joining());

    private final String message;
    private final List<String> details = new ArrayList<>();

    public Log(String message, Object... arguments) {
        this.message = String.format(message, arguments);
    }

    public static Log of(String message, Object... arguments) {
        return new Log(message, arguments);
    }

    public Log withDetails(Collection<String> toAdd) {
        toAdd.forEach(detail -> {
            details.add(LIST_ITEM_PREFIX + detail);
        });

        return this;
    }

    public Log withLog(Log toAdd) {
        details.add(LIST_ITEM_PREFIX + toAdd.message);

        for (String detailToAdd : toAdd.details) {
            details.add(INDENT + detailToAdd);
        }

        return this;
    }

    public Log withLogs(Collection<Log> toAdd) {
        toAdd.forEach(this::withLog);
        return this;
    }

    public <T> Log withDetail(String detail, Collection<? extends T> values, Function<? super T, String> printer) {
        String valuesAsString = values.isEmpty()
                ? "none"
                : values.stream().map(printer).collect(joining(", "));
        details.add(LIST_ITEM_PREFIX + detail + ": " + valuesAsString);
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

    public Log withDetail(String detail, Optional<String> value) {
        return withDetail(detail, value, Function.identity());
    }

    public Log withDetail(String detail, String value) {
        return withDetail(detail, Collections.singletonList(value), Function.identity());
    }

    public void append(LoggingBackend loggingBackend) {
        loggingBackend.log(message);
        details.forEach(loggingBackend::log);
    }
}
