package nl.wernerdegroot.applicatives.processor.logging;

public class StringBuilderLoggingBackend implements LoggingBackend {

    private final StringBuilder builder = new StringBuilder();

    @Override
    public void log(String message) {
        builder.append(message).append("\n");
    }

    public String build() {
        return builder.toString();
    }
}
