package nl.wernerdegroot.applicatives.processor;

import com.google.auto.service.AutoService;
import nl.wernerdegroot.applicatives.processor.conflicts.ConflictFree;
import nl.wernerdegroot.applicatives.processor.conflicts.ConflictPrevention;
import nl.wernerdegroot.applicatives.processor.converters.MethodConverter;
import nl.wernerdegroot.applicatives.processor.domain.*;
import nl.wernerdegroot.applicatives.processor.domain.containing.Containing;
import nl.wernerdegroot.applicatives.processor.domain.type.Type;
import nl.wernerdegroot.applicatives.processor.domain.typeconstructor.TypeConstructor;
import nl.wernerdegroot.applicatives.processor.generator.ParameterGenerator;
import nl.wernerdegroot.applicatives.processor.generator.TypeGenerator;
import nl.wernerdegroot.applicatives.processor.generator.TypeParameterGenerator;
import nl.wernerdegroot.applicatives.processor.generator.TypeParametersGenerator;
import nl.wernerdegroot.applicatives.processor.logging.Log;
import nl.wernerdegroot.applicatives.processor.logging.LoggingBackend;
import nl.wernerdegroot.applicatives.processor.logging.MessagerLoggingBackend;
import nl.wernerdegroot.applicatives.processor.logging.NoLoggingBackend;
import nl.wernerdegroot.applicatives.processor.validation.MethodValidator;
import nl.wernerdegroot.applicatives.processor.validation.Validated;
import nl.wernerdegroot.applicatives.runtime.Accumulator;
import nl.wernerdegroot.applicatives.runtime.Covariant;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static javax.lang.model.element.ElementKind.METHOD;
import static nl.wernerdegroot.applicatives.processor.generator.Generator.generator;

@SupportedOptions({Options.VERBOSE_ARGUMENT})
@SupportedAnnotationTypes(CovariantBuilderProcessor.COVARIANT_BUILDER_CANONICAL_NAME)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class CovariantBuilderProcessor extends AbstractProcessor {

    public static final String COVARIANT_BUILDER_CANONICAL_NAME = "nl.wernerdegroot.applicatives.runtime.Covariant.Builder";
    public static final String COVARIANT_BUILDER_CLASS_NAME = "nl.wernerdegroot.applicatives.runtime.Covariant$Builder";
    public static final Class<?> COVARIANT_BUILDER_CLASS;

    public static final Set<Class<? extends Annotation>> SUPPORTED_ANNOTATIONS = Stream.of(
            Accumulator.class
    ).collect(toSet());

    public static final FullyQualifiedName ACCUMULATOR = FullyQualifiedName.of(Accumulator.class.getCanonicalName());

    static {
        try {
            COVARIANT_BUILDER_CLASS = Class.forName(COVARIANT_BUILDER_CLASS_NAME);
            if (!Objects.equals(COVARIANT_BUILDER_CANONICAL_NAME, COVARIANT_BUILDER_CLASS.getCanonicalName())) {
                throw new RuntimeException(String.format("Can't find annotation class %s", COVARIANT_BUILDER_CANONICAL_NAME));
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(String.format("Can't find annotation class %s", COVARIANT_BUILDER_CLASS_NAME), e);
        }
    }

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

                    note("Found annotation of type '%s' on class '%s'", COVARIANT_BUILDER_CANONICAL_NAME, typeElement.getQualifiedName())
                            .withDetail("Class name", covariantBuilderAnnotation.className())
                            .withDetail("Method name for `lift`", covariantBuilderAnnotation.liftMethodName())
                            .withDetail("Maximum arity", covariantBuilderAnnotation.maxArity(), i -> Integer.toString(i))
                            .append();

                    Method accumulator;
                    try {
                        List<Method> methods = typeElement
                                .getEnclosedElements()
                                .stream()
                                .filter(enclosedElement -> enclosedElement.getKind() == METHOD)
                                .map(methodElement -> MethodConverter.toDomain(methodElement, SUPPORTED_ANNOTATIONS))
                                .collect(toList());

                        List<Method> accumulatorCandidates = methods
                                .stream()
                                .filter(method -> method.hasAnnotation(ACCUMULATOR))
                                .collect(toList());

                        if (accumulatorCandidates.size() == 0) {
                            error("No method in '%s' annotated with '%s'", typeElement.getQualifiedName(), ACCUMULATOR.raw()).append();
                            return;
                        } else if (accumulatorCandidates.size() > 1) {
                            error("More than one method in '%s' annotated with '%s'", typeElement.getQualifiedName(), ACCUMULATOR.raw()).append();
                            return;
                        }

                        accumulator = accumulatorCandidates.iterator().next();
                        note("Successfully transformed objects from 'javax.lang.model' to objects from 'nl.wernerdegroot.applicatives.processor.domain'").append();
                    } catch (Throwable e) {
                        // If we have issues transforming to `nl.wernerdegroot.applicatives.processor.domain`
                        // (which makes it a lot easier to log where the annotation was found) make
                        // sure we log the method's raw signature so the client can troubleshoot.
                        error("Failure transforming from objects from 'javax.lang.model' to objects from 'nl.wernerdegroot.applicatives.processor.domain' for class '%s'", typeElement.getQualifiedName()).append();
                        throw e;
                    }

                    note("Found method '%s' in package '%s'", accumulator.getName(), accumulator.getContainingClass().getPackageName().raw())
                            .withDetail("Modifiers", accumulator.getModifiers(), Modifier::toString)
                            .withDetail("Return type", accumulator.getReturnType(), TypeGenerator::generateFrom)
                            .withDetail("Parameters", accumulator.getParameters(), ParameterGenerator::generateFrom)
                            .withDetail("Containing class", accumulator.getContainingClass(), this::containingToString)
                            .append();

                    Validated<AccumulatorMethod> validatedMethod = MethodValidator.validate(accumulator);
                    if (!validatedMethod.isValid()) {
                        error("Method '%s' in package '%s' does not meet all criteria for code generation", accumulator.getName(), accumulator.getContainingClass().getPackageName().raw())
                                .withDetails(validatedMethod.getErrorMessages())
                                .append();
                        return;
                    }

                    AccumulatorMethod valid = validatedMethod.getValue();
                    note("Method meets all criteria for code generation")
                            .withDetail("Accumulation type constructor", valid.getAccumulationTypeConstructor(), this::typeConstructorToString)
                            .withDetail("Permissive accumulation type constructor", valid.getPermissiveAccumulationTypeConstructor(), this::typeConstructorToString)
                            .withDetail("Input type constructor", valid.getInputTypeConstructor(), this::typeConstructorToString)
                            .withDetail("Class type parameters", valid.getClassTypeParameters(), TypeParameterGenerator::generateFrom)
                            .append();

                    ConflictFree conflictFree = ConflictPrevention.preventConflicts(
                            valid.getClassTypeParameters(),
                            valid.getAccumulationTypeConstructor(),
                            valid.getPermissiveAccumulationTypeConstructor(),
                            valid.getInputTypeConstructor()
                    );

                    note("Resolved (potential) conflicts between existing type parameters and new, generated type parameters")
                            .withDetail("Input type constructor arguments", conflictFree.getInputTypeConstructorArguments(), TypeParameterGenerator::generateFrom)
                            .withDetail("Result type constructor arguments", conflictFree.getResultTypeConstructorArguments(), TypeParameterGenerator::generateFrom)
                            .withDetail("Class type parameters", conflictFree.getClassTypeParameters(), TypeParameterGenerator::generateFrom)
                            .withDetail("Input parameter names", conflictFree.getInputParameterNames())
                            .withDetail("Self parameter name", conflictFree.getSelfParameterName())
                            .withDetail("Combinator parameter name", conflictFree.getCombinatorParameterName())
                            .withDetail("Maximum tuple size parameter name", conflictFree.getMaxTupleSizeParameterName())
                            .withDetail("Accumulation type constructor", conflictFree.getAccumulationTypeConstructor(), this::typeConstructorToString)
                            .withDetail("Permissive accumulation type constructor", conflictFree.getPermissiveAccumulationTypeConstructor(), this::typeConstructorToString)
                            .withDetail("Input type constructor", conflictFree.getInputTypeConstructor(), this::typeConstructorToString)
                            .append();

                    String generated = generator()
                            .withPackageName(accumulator.getContainingClass().getPackageName())
                            .withClassNameToGenerate(covariantBuilderAnnotation.className())
                            .withClassTypeParameters(conflictFree.getClassTypeParameters())
                            .withInputTypeConstructorArguments(conflictFree.getInputTypeConstructorArguments())
                            .withResultTypeConstructorArgument(conflictFree.getResultTypeConstructorArguments())
                            .withMethodName(accumulator.getName())
                            .withInputParameterNames(conflictFree.getInputParameterNames())
                            .withSelfParameterName(conflictFree.getSelfParameterName())
                            .withCombinatorParameterName(conflictFree.getCombinatorParameterName())
                            .withMaxTupleSizeParameterName(conflictFree.getMaxTupleSizeParameterName())
                            .withAccumulationTypeConstructor(conflictFree.getAccumulationTypeConstructor())
                            .withPermissiveAccumulationTypeConstructor(conflictFree.getPermissiveAccumulationTypeConstructor())
                            .withInputTypeConstructor(conflictFree.getInputTypeConstructor())
                            .withLiftMethodName(covariantBuilderAnnotation.liftMethodName())
                            .withMaxArity(covariantBuilderAnnotation.maxArity())
                            .generate();

                    note("Done generating code").append();

                    FullyQualifiedName fullyQualifiedNameOfGeneratedClass = accumulator.getContainingClass().getPackageName().withClassName(ClassName.of(covariantBuilderAnnotation.className()));
                    try {
                        JavaFileObject builderFile = processingEnv.getFiler().createSourceFile(accumulator.getContainingClass().getPackageName().withClassName(ClassName.of(covariantBuilderAnnotation.className())).raw());
                        try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {
                            out.print(generated);
                            note("Saved generated code to .java-file on disk (%s)", fullyQualifiedNameOfGeneratedClass.raw()).append();
                        }
                    } catch (IOException e) {
                        error("Error saving generated code to .java-file on disk (%s)", fullyQualifiedNameOfGeneratedClass.raw()).append();
                    }
                } catch (Throwable t) {
                    error("Error occurred while processing annotation of type '%s': %s", COVARIANT_BUILDER_CLASS, t.getMessage()).append();
                    error("(Enable verbose logging to see a stack trace)").append();
                    printStackTraceToMessagerAsNote(t);
                }
            });
        });

        return false;
    }

    private boolean shouldLogNotes() {
        return Objects.equals(processingEnv.getOptions().getOrDefault(Options.VERBOSE_ARGUMENT, "false"), "true");
    }

    private LoggingBackend getMessagerLoggingBackend(Diagnostic.Kind diagnosticKind) {
        return MessagerLoggingBackend.of(processingEnv, diagnosticKind);
    }

    private Log error(String format, Object... arguments) {
        return Log.of(
                getMessagerLoggingBackend(Diagnostic.Kind.ERROR),
                String.format(format, arguments)
        );
    }

    private Log note(String format, Object... arguments) {
        return Log.of(
                shouldLogNotes() ? getMessagerLoggingBackend(Diagnostic.Kind.NOTE) : NoLoggingBackend.INSTANCE,
                String.format(format, arguments)
        );
    }

    private void printStackTraceToMessagerAsNote(Throwable e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, sw.toString());
    }

    private String typeConstructorToString(TypeConstructor typeConstructor) {
        Type substituteForPlaceholder = FullyQualifiedName.of("*").asType();
        Type typeConstructorAsType = typeConstructor.apply(substituteForPlaceholder);
        return TypeGenerator.generateFrom(typeConstructorAsType);
    }

    private String containingToString(Containing containing) {
        return containing.match(
                p -> p.getPackageName().raw(),
                c -> {
                    String parentAsString = containingToString(c.getParent());
                    String classNameAsString = c.getClassName().raw();
                    String typeParametersAsString = Optional.of(c.getTypeParameters())
                            .filter(typeParameters -> !typeParameters.isEmpty())
                            .map(TypeParametersGenerator::generatoreFrom)
                            .orElse("");
                    return parentAsString + "." + classNameAsString + typeParametersAsString;
                }
        );
    }
}
