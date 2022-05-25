package nl.wernerdegroot.applicatives.processor.logging;

import org.junit.jupiter.api.Test;

public class NoLoggingBackendTest {

    @Test
    public void doesNothing() {
        NoLoggingBackend.INSTANCE.log("Poof!");
    }
}
