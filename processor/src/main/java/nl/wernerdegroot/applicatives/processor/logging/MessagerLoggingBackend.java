package nl.wernerdegroot.applicatives.processor.logging;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.Diagnostic;

public class MessagerLoggingBackend implements LoggingBackend {

    private final ProcessingEnvironment processingEnvironment;
    private final Diagnostic.Kind diagnosticKind;

    public MessagerLoggingBackend(ProcessingEnvironment processingEnvironment, Diagnostic.Kind diagnosticKind) {
        this.processingEnvironment = processingEnvironment;
        this.diagnosticKind = diagnosticKind;
    }

    public static MessagerLoggingBackend of(ProcessingEnvironment processingEnvironment, Diagnostic.Kind diagnosticKind) {
        return new MessagerLoggingBackend(processingEnvironment, diagnosticKind);
    }

    @Override
    public void log(String message) {
        processingEnvironment.getMessager().printMessage(diagnosticKind, message);
    }
}
