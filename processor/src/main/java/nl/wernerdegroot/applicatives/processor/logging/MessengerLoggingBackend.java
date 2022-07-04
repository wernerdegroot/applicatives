package nl.wernerdegroot.applicatives.processor.logging;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.Diagnostic;

public class MessengerLoggingBackend implements LoggingBackend {

    private final ProcessingEnvironment processingEnvironment;
    private final Diagnostic.Kind diagnosticKind;

    public MessengerLoggingBackend(ProcessingEnvironment processingEnvironment, Diagnostic.Kind diagnosticKind) {
        this.processingEnvironment = processingEnvironment;
        this.diagnosticKind = diagnosticKind;
    }

    public static MessengerLoggingBackend of(ProcessingEnvironment processingEnvironment, Diagnostic.Kind diagnosticKind) {
        return new MessengerLoggingBackend(processingEnvironment, diagnosticKind);
    }

    @Override
    public void log(String message) {
        processingEnvironment.getMessager().printMessage(diagnosticKind, message);
    }
}
