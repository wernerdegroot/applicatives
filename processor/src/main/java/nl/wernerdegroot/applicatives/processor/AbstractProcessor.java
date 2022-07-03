package nl.wernerdegroot.applicatives.processor;

import nl.wernerdegroot.applicatives.processor.domain.FullyQualifiedName;
import nl.wernerdegroot.applicatives.processor.logging.LoggingBackend;
import nl.wernerdegroot.applicatives.processor.logging.MessengerLoggingBackend;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Set;

public abstract class AbstractProcessor<Annotation, ElementToProcess, MethodOrMethods> extends javax.annotation.processing.AbstractProcessor implements ProcessorTemplate<Annotation, Element, ElementToProcess, MethodOrMethods> {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        annotations.forEach(annotation -> {
            roundEnv.getElementsAnnotatedWith(annotation).forEach(element -> {
                process(element);
            });
        });
        return false;
    }

    @Override
    public PrintWriter getPrintWriterForFile(FullyQualifiedName fullyQualifiedName) throws IOException {
        JavaFileObject builderFile = processingEnv.getFiler().createSourceFile(fullyQualifiedName.raw());
        return new PrintWriter(builderFile.openWriter());
    }

    @Override
    public Map<String, String> getConfiguration() {
        return processingEnv.getOptions();
    }

    @Override
    public LoggingBackend getMessengerLoggingBackend(Diagnostic.Kind diagnosticKind) {
        return MessengerLoggingBackend.of(processingEnv, diagnosticKind);
    }

}
