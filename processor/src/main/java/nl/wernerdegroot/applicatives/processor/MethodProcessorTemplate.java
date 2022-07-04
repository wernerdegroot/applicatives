package nl.wernerdegroot.applicatives.processor;

import nl.wernerdegroot.applicatives.processor.converters.ContainingClassConverter;
import nl.wernerdegroot.applicatives.processor.converters.MethodConverter;
import nl.wernerdegroot.applicatives.processor.domain.Method;
import nl.wernerdegroot.applicatives.processor.domain.containing.ContainingClass;
import nl.wernerdegroot.applicatives.processor.logging.Log;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import java.util.List;

public interface MethodProcessorTemplate<Annotation extends java.lang.annotation.Annotation> extends ProcessorTemplate<Annotation, Element, Element, Method> {

    @Override
    default Annotation getAnnotation(Element element) {
        return element.getAnnotation(getAnnotationType());
    }

    @Override
    default Element getElementToProcess(Element annotatedElement) {
        if (annotatedElement.getKind() != ElementKind.METHOD) {
            // Very unlikely to happen, since the annotations
            // should have the right @Target (ElementType.METHOD)
            String message = String.format("Element %s of type %s is not a method", annotatedElement.getSimpleName(), annotatedElement.getKind());
            throw new IllegalArgumentException(message);
        }
        return annotatedElement;
    }

    @Override
    default String describeElementToProcess(Element element) {
        Element enclosingElement = element.getEnclosingElement();
        String enclosingElementDescription = "???";
        if (enclosingElement != null && enclosingElement instanceof TypeElement) {
            TypeElement enclosingElementAsTypeElement = (TypeElement) enclosingElement;
            enclosingElementDescription = "class '" + enclosingElementAsTypeElement.getQualifiedName() + "'";
        }
        return String.format("method '%s' in %s", element.getSimpleName(), enclosingElementDescription);
    }

    @Override
    default ContainingClass toContainingClass(Element element) {
        return ContainingClassConverter.toDomain(element.getEnclosingElement());
    }

    @Override
    default Method toMethodOrMethods(Element element) {
        return MethodConverter.toDomain(element);
    }

    @Override
    default void noteContainingClassAndMethodOrMethods(ContainingClass containingClass, Method method) {
        noteMethodFound(containingClass, method);
    }

    @Override
    default void errorValidationFailed(ContainingClass containingClass, Method method, List<Log> errorMessages) {
        Log.of("Method '%s' in class '%s' does not meet all criteria for code generation", method.getName(), containingClass.getFullyQualifiedName().raw())
                .withLogs(errorMessages)
                .append(asError());
    }
}
