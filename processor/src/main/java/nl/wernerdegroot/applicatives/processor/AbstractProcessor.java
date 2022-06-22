package nl.wernerdegroot.applicatives.processor;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;
import java.util.Set;

public abstract class AbstractProcessor<Annotation, ElementToProcess, MethodOrMethods> extends javax.annotation.processing.AbstractProcessor implements ProcessorTemplate<Annotation, ElementToProcess, MethodOrMethods> {

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
    public ProcessingEnvironment getProcessingEnvironment() {
        return processingEnv;
    }
}
