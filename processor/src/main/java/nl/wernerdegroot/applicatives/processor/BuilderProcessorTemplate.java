package nl.wernerdegroot.applicatives.processor;

import nl.wernerdegroot.applicatives.processor.converters.ContainingClassConverter;
import nl.wernerdegroot.applicatives.processor.converters.MethodConverter;
import nl.wernerdegroot.applicatives.processor.domain.FullyQualifiedName;
import nl.wernerdegroot.applicatives.processor.domain.Method;
import nl.wernerdegroot.applicatives.processor.domain.containing.ContainingClass;
import nl.wernerdegroot.applicatives.processor.generator.ContainingClassGenerator;
import nl.wernerdegroot.applicatives.processor.logging.Log;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static javax.lang.model.element.ElementKind.METHOD;
import static nl.wernerdegroot.applicatives.processor.Classes.*;
import static nl.wernerdegroot.applicatives.processor.Classes.FINALIZER_FULLY_QUALIFIED_NAME;

public interface BuilderProcessorTemplate<Annotation extends java.lang.annotation.Annotation> extends ProcessorTemplate<Annotation, TypeElement, List<Method>> {

    Set<FullyQualifiedName> SUPPORTED_ANNOTATIONS = Stream.of(
            INITIALIZER_FULLY_QUALIFIED_NAME,
            ACCUMULATOR_FULLY_QUALIFIED_NAME,
            FINALIZER_FULLY_QUALIFIED_NAME
    ).collect(toSet());

    @Override
    default Annotation getAnnotation(TypeElement typeElement) {
        return typeElement.getAnnotation(getAnnotationType());
    }

    @Override
    default TypeElement getElementToProcess(Element element) {
        if (element.getKind() != ElementKind.CLASS) {
            // Very unlikely to happen, since the annotations
            // should have the right @Target (ElementType.TYPE)
            String message = String.format("Element %s of type %s is not a class", element.getSimpleName(), element.getKind());
            throw new IllegalArgumentException(message);
        }
        return (TypeElement) element;
    }

    @Override
    default void noteAnnotationFound(TypeElement typeElement, String classNameToGenerate, String combineMethodNameToGenerate, String liftMethodNameToGenerate, int maxArity) {
        Log.of("Found annotation of type '%s' on class '%s'", getAnnotationType().getCanonicalName(), typeElement.getQualifiedName())
                .withDetail("Class name to generate", classNameToGenerate)
                .withDetail("Method name for `combine`", combineMethodNameToGenerate)
                .withDetail("Method name for `lift`", liftMethodNameToGenerate)
                .withDetail("Maximum arity", maxArity, i -> Integer.toString(i))
                .append(asNote());
    }

    @Override
    default ContainingClass toContainingClass(TypeElement typeElement) {
        return ContainingClassConverter.toDomain(typeElement);
    }

    @Override
    default List<Method> toMethodOrMethods(TypeElement typeElement) {
        return typeElement
                .getEnclosedElements()
                .stream()
                .filter(enclosedElement -> enclosedElement.getKind() == METHOD)
                .map(MethodConverter::toDomain)
                .filter(method -> method.hasAnnotationOf(SUPPORTED_ANNOTATIONS))
                .collect(toList());
    }

    @Override
    default void noteConversionToDomainFailed(TypeElement typeElement) {
        Log.of("Failure transforming from objects from 'javax.lang.model' to objects from 'nl.wernerdegroot.applicatives.processor.domain' for class '%s'", typeElement.getQualifiedName()).append(asError());
    }

    @Override
    default void noteContainingClassAndMethodOrMethods(ContainingClass containingClass, List<Method> methods) {
        Log.of("Found class '%s'", ContainingClassGenerator.generateFrom(containingClass)).append(asNote());
        methods.forEach(method -> {
            noteMethodFound(containingClass, method);
        });
    }

    @Override
    default void errorValidationFailed(ContainingClass containingClass, List<Method> methods, Set<Log> errorMessages) {
        Log.of("Class '%s' does not meet all criteria for code generation", containingClass.getFullyQualifiedName().raw())
                .withLogs(errorMessages)
                .append(asError());
    }
}
