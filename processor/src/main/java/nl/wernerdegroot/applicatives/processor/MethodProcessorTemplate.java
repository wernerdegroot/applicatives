package nl.wernerdegroot.applicatives.processor;

import nl.wernerdegroot.applicatives.processor.converters.ContainingClassConverter;
import nl.wernerdegroot.applicatives.processor.converters.MethodConverter;
import nl.wernerdegroot.applicatives.processor.domain.Method;
import nl.wernerdegroot.applicatives.processor.domain.containing.ContainingClass;
import nl.wernerdegroot.applicatives.processor.logging.Log;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import java.util.Set;

public interface MethodProcessorTemplate<Annotation extends java.lang.annotation.Annotation> extends ProcessorTemplate<Annotation, Element, Method> {

    @Override
    default Annotation getAnnotation(Element element) {
        return element.getAnnotation(getAnnotationType());
    }

    @Override
    default Element getElementToProcess(Element element) {
        if (element.getKind() != ElementKind.METHOD) {
            // Very unlikely to happen, since the annotations
            // should have the right @Target (ElementType.METHOD)
            String message = String.format("Element %s of type %s is not a method", element.getSimpleName(), element.getKind());
            throw new IllegalArgumentException(message);
        }
        return element;
    }

    @Override
    default void noteAnnotationFound(Element element, String classNameToGenerate, String combineMethodNameToGenerate, String liftMethodNameToGenerate, int maxArity) {
        Element enclosingElement = element.getEnclosingElement();
        String enclosingElementDescription = "???";
        if (enclosingElement != null && enclosingElement instanceof TypeElement) {
            TypeElement enclosingElementAsTypeElement = (TypeElement) enclosingElement;
            enclosingElementDescription = "class '" + enclosingElementAsTypeElement.getQualifiedName() + "'";
        }

        Log.of("Found annotation of type '%s' on method '%s' in %s", getAnnotationType().getCanonicalName(), element.getSimpleName(), enclosingElementDescription)
                .withDetail("Class name", classNameToGenerate)
                .withDetail("Method name for `combine`", combineMethodNameToGenerate)
                .withDetail("Method name for `lift`", liftMethodNameToGenerate)
                .withDetail("Maximum arity", maxArity, i -> Integer.toString(i))
                .append(asNote());
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
    default void noteConversionToDomainFailed(Element element) {
        Log.of("Failure transforming from objects from 'javax.lang.model' to objects from 'nl.wernerdegroot.applicatives.processor.domain' for method with signature '%s'", element).append(asError());
    }

    @Override
    default void noteContainingClassAndMethodOrMethods(ContainingClass containingClass, Method method) {
        noteMethodFound(containingClass, method);
    }

    @Override
    default void errorValidationFailed(ContainingClass containingClass, Method method, Set<Log> errorMessages) {
        Log.of("Method '%s' in class '%s' does not meet all criteria for code generation", method.getName(), containingClass.getFullyQualifiedName().raw())
                .withLogs(errorMessages)
                .append(asError());
    }
}
