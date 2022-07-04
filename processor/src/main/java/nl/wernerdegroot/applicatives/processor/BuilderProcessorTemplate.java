package nl.wernerdegroot.applicatives.processor;

import nl.wernerdegroot.applicatives.processor.converters.ContainingClassConverter;
import nl.wernerdegroot.applicatives.processor.converters.EnclosedMethodsConverter;
import nl.wernerdegroot.applicatives.processor.domain.Method;
import nl.wernerdegroot.applicatives.processor.domain.containing.ContainingClass;
import nl.wernerdegroot.applicatives.processor.generator.ContainingClassGenerator;
import nl.wernerdegroot.applicatives.processor.logging.Log;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import java.util.List;

import static nl.wernerdegroot.applicatives.processor.Classes.*;

public interface BuilderProcessorTemplate<Annotation extends java.lang.annotation.Annotation> extends ProcessorTemplate<Annotation, Element, TypeElement, List<Method>> {

    @Override
    default Annotation getAnnotation(TypeElement typeElement) {
        return typeElement.getAnnotation(getAnnotationType());
    }

    @Override
    default TypeElement getElementToProcess(Element annotatedElement) {
        if (annotatedElement.getKind() != ElementKind.CLASS) {
            // Very unlikely to happen, since the annotations
            // should have the right @Target (ElementType.TYPE)
            String message = String.format("Element %s of type %s is not a class", annotatedElement.getSimpleName(), annotatedElement.getKind());
            throw new IllegalArgumentException(message);
        }
        return (TypeElement) annotatedElement;
    }

    @Override
    default String describeElementToProcess(TypeElement typeElement) {
        return String.format("class '%s'", typeElement.getQualifiedName());
    }

    @Override
    default ContainingClass toContainingClass(TypeElement typeElement) {
        return ContainingClassConverter.toDomain(typeElement);
    }

    @Override
    default List<Method> toMethodOrMethods(TypeElement typeElement) {
        return EnclosedMethodsConverter.toDomain(typeElement, INITIALIZER_FULLY_QUALIFIED_NAME, ACCUMULATOR_FULLY_QUALIFIED_NAME, FINALIZER_FULLY_QUALIFIED_NAME);
    }

    @Override
    default void noteContainingClassAndMethodOrMethods(ContainingClass containingClass, List<Method> methods) {
        Log.of("Found class '%s'", ContainingClassGenerator.generateFrom(containingClass)).append(asNote());
        methods.forEach(method -> {
            noteMethodFound(containingClass, method);
        });
    }

    @Override
    default void errorValidationFailed(ContainingClass containingClass, List<Method> methods, List<Log> errorMessages) {
        Log.of("Class '%s' does not meet all criteria for code generation", containingClass.getFullyQualifiedName().raw())
                .withLogs(errorMessages)
                .append(asError());
    }
}
