package nl.wernerdegroot.applicatives.processor;

import com.google.auto.service.AutoService;
import nl.wernerdegroot.applicatives.processor.converters.ContainingClassConverter;
import nl.wernerdegroot.applicatives.processor.converters.MethodConverter;
import nl.wernerdegroot.applicatives.processor.domain.FullyQualifiedName;
import nl.wernerdegroot.applicatives.processor.domain.Method;
import nl.wernerdegroot.applicatives.processor.domain.Modifier;
import nl.wernerdegroot.applicatives.processor.domain.containing.ContainingClass;
import nl.wernerdegroot.applicatives.processor.generator.ContainingClassGenerator;
import nl.wernerdegroot.applicatives.processor.generator.ParameterGenerator;
import nl.wernerdegroot.applicatives.processor.generator.TypeGenerator;
import nl.wernerdegroot.applicatives.processor.logging.Log;
import nl.wernerdegroot.applicatives.processor.validation.TemplateClassWithMethods;
import nl.wernerdegroot.applicatives.processor.validation.TemplateClassWithMethodsValidator;
import nl.wernerdegroot.applicatives.processor.validation.Validated;
import nl.wernerdegroot.applicatives.runtime.Covariant;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
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

@SupportedOptions({Options.VERBOSE_ARGUMENT})
@SupportedAnnotationTypes(COVARIANT_BUILDER_CANONICAL_NAME)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class CovariantBuilderProcessor extends AbstractCovariantProcessor {

    public static final Set<FullyQualifiedName> SUPPORTED_ANNOTATIONS = Stream.of(
            INITIALIZER,
            ACCUMULATOR,
            FINALIZER
    ).collect(toSet());

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        annotations.forEach(annotation -> {
            Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(annotation);
            annotatedElements.forEach(element -> {
                try {
                    if (element.getKind() != ElementKind.CLASS) {
                        throw new IllegalArgumentException("Not a class");
                    }
                    TypeElement typeElement = (TypeElement) element;

                    Covariant.Builder covariantBuilderAnnotation = element.getAnnotation(Covariant.Builder.class);

                    Log.of("Found annotation of type '%s' on class '%s'", COVARIANT_BUILDER_CANONICAL_NAME, typeElement.getQualifiedName())
                            .withDetail("Class name to generate", covariantBuilderAnnotation.className())
                            .withDetail("Method name for `lift`", covariantBuilderAnnotation.liftMethodName())
                            .withDetail("Maximum arity", covariantBuilderAnnotation.maxArity(), i -> Integer.toString(i))
                            .append(asNote());

                    ContainingClass containingClass;
                    List<Method> methods;
                    try {
                        containingClass = ContainingClassConverter.toDomain(typeElement);
                        methods = typeElement
                                .getEnclosedElements()
                                .stream()
                                .filter(enclosedElement -> enclosedElement.getKind() == METHOD)
                                .map(MethodConverter::toDomain)
                                .filter(method -> method.hasAnnotationOf(SUPPORTED_ANNOTATIONS))
                                .collect(toList());

                        Log.of("Successfully transformed objects from 'javax.lang.model' to objects from 'nl.wernerdegroot.applicatives.processor.domain'").append(asNote());
                    } catch (Throwable e) {
                        // If we have issues transforming to `nl.wernerdegroot.applicatives.processor.domain`
                        // (which makes it a lot easier to log where the annotation was found) make
                        // sure we log the method's raw signature so the client can troubleshoot.
                        Log.of("Failure transforming from objects from 'javax.lang.model' to objects from 'nl.wernerdegroot.applicatives.processor.domain' for class '%s'", typeElement.getQualifiedName()).append(asError());
                        throw e;
                    }

                    Log.of("Found class '%s'", ContainingClassGenerator.generateFrom(containingClass)).append(asNote());
                    methods.forEach(method -> {
                        Log.of("Found method '%s' in class '%s'", method.getName(), containingClass.getFullyQualifiedName().raw())
                                .withDetail("Annotations", method.getAnnotations(), FullyQualifiedName::raw)
                                .withDetail("Modifiers", method.getModifiers(), Modifier::toString)
                                .withDetail("Return type", method.getReturnType(), TypeGenerator::generateFrom)
                                .withDetail("Parameters", method.getParameters(), ParameterGenerator::generateFrom)
                                .append(asNote());
                    });

                    Validated<TemplateClassWithMethods> validatedTemplateClassWithMethods = TemplateClassWithMethodsValidator.validate(containingClass, methods);
                    if (!validatedTemplateClassWithMethods.isValid()) {
                        Log.of("Class '%s' does not meet all criteria for code generation", containingClass.getFullyQualifiedName().raw())
                                .withDetails(validatedTemplateClassWithMethods.getErrorMessages())
                                .append(asError());
                        return;
                    }

                    TemplateClassWithMethods templateClassWithMethods = validatedTemplateClassWithMethods.getValue();
                    resolveConflictsAndGenerate(
                            covariantBuilderAnnotation.className(),
                            covariantBuilderAnnotation.liftMethodName(),
                            covariantBuilderAnnotation.maxArity(),
                            containingClass.getPackageName(),
                            templateClassWithMethods
                    );
                } catch (Throwable t) {
                    Log.of("Error occurred while processing annotation of type '%s': %s", COVARIANT_BUILDER_CLASS, t.getMessage()).append(asError());
                    if (shouldLogNotes()) {
                        printStackTraceToMessagerAsNote(t);
                    } else {
                        Log.of("Enable verbose logging to see a stack trace.").append(asError());
                    }
                }
            });
        });
        return false;
    }
}
