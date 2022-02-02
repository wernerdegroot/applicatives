package nl.wernerdegroot.applicatives.processor.logging;

public class NoLoggingBackend implements LoggingBackend {

    public static final NoLoggingBackend INSTANCE = new NoLoggingBackend();

    @Override
    public void log(String message) {
        // Do nothing
    }
}
