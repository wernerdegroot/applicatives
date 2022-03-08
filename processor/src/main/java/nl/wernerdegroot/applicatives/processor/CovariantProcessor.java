package nl.wernerdegroot.applicatives.processor;

import com.google.auto.service.AutoService;
import nl.wernerdegroot.applicatives.processor.conflicts.ConflictFree;
import nl.wernerdegroot.applicatives.processor.conflicts.ConflictPrevention;
import nl.wernerdegroot.applicatives.processor.converters.MethodConverter;
import nl.wernerdegroot.applicatives.processor.domain.ClassName;
import nl.wernerdegroot.applicatives.processor.domain.FullyQualifiedName;
import nl.wernerdegroot.applicatives.processor.domain.Method;
import nl.wernerdegroot.applicatives.processor.domain.Modifier;
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
import nl.wernerdegroot.applicatives.processor.validation.ValidatedMethod;
import nl.wernerdegroot.applicatives.runtime.Covariant;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static nl.wernerdegroot.applicatives.processor.generator.Generator.generator;

@SupportedOptions({CovariantProcessor.VERBOSE_ARGUMENT})
@SupportedAnnotationTypes(CovariantProcessor.COVARIANT_CLASS_NAME)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class CovariantProcessor extends AbstractProcessor {

    public static final String VERBOSE_ARGUMENT = "applicatives.verbose";
    public static final String COVARIANT_CLASS_NAME = "nl.wernerdegroot.applicatives.runtime.Covariant";
    public static final Class<?> COVARIANT_CLASS;

    static {
        try {
            COVARIANT_CLASS = Class.forName(COVARIANT_CLASS_NAME);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(String.format("Can't find annotation class %s", COVARIANT_CLASS_NAME), e);
        }
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        annotations.forEach(annotation -> {
            Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(annotation);
            annotatedElements.forEach(element -> {
                try {
                    if (element.getKind() != ElementKind.METHOD) {
                        throw new IllegalArgumentException("Not a method");
                    }

                    Covariant covariantAnnotation = ((ExecutableElement) element).getAnnotation(Covariant.class);

                    note("Found annotation of type '%s'", COVARIANT_CLASS_NAME)
                            .withDetail("Class name", covariantAnnotation.className())
                            .withDetail("Method name for `lift`", covariantAnnotation.liftMethodName())
                            .withDetail("Maximum arity", covariantAnnotation.maxArity(), i -> Integer.toString(i))
                            .append();

                    Method method;
                    try {
                        method = MethodConverter.toDomain(element);
                        note("Successfully transformed objects from 'javax.lang.model' to objects from 'nl.wernerdegroot.applicatives.processor.domain'").append();
                    } catch (Throwable e) {
                        // If we have issues transforming to `nl.wernerdegroot.applicatives.processor.domain`
                        // (which makes it a lot easier to log where the annotation was found) make
                        // sure we log the method's raw signature so the client can troubleshoot.
                        error("Failure transforming from objects from 'javax.lang.model' to objects from 'nl.wernerdegroot.applicatives.processor.domain' for method with signature '%s'", element).append();
                        throw e;
                    }

                    note("Found method '%s' in package '%s'", method.getName(), method.getContainingClass().getPackageName().raw())
                            .withDetail("Modifiers", method.getModifiers(), Modifier::toString)
                            .withDetail("Return type", method.getReturnType(), TypeGenerator::generateFrom)
                            .withDetail("Parameters", method.getParameters(), ParameterGenerator::generateFrom)
                            .withDetail("Containing class", method.getContainingClass(), this::containingToString)
                            .append();

                    ValidatedMethod validatedMethod = MethodValidator.validate(method);
                    validatedMethod.match(
                            valid -> {
                                note("Method meets all criteria for code generation")
                                        .withDetail("Secondary type parameters", valid.getSecondaryMethodTypeParameters(), TypeParameterGenerator::generateFrom)
                                        .withDetail("Secondary parameters", valid.getSecondaryParameters(), ParameterGenerator::generateFrom)
                                        .withDetail("Left parameter type constructor", valid.getLeftParameterTypeConstructor(), this::typeConstructorToString)
                                        .withDetail("Right parameter type constructor", valid.getRightParameterTypeConstructor(), this::typeConstructorToString)
                                        .withDetail("Result type constructor", valid.getResultTypeConstructor(), this::typeConstructorToString)
                                        .withDetail("Class type parameters", valid.getClassTypeParameters(), TypeParameterGenerator::generateFrom)
                                        .append();

                                ConflictFree conflictFreeConflictFree = ConflictPrevention.preventConflicts(
                                        valid.getSecondaryMethodTypeParameters(),
                                        valid.getClassTypeParameters(),
                                        valid.getSecondaryParameters(),
                                        valid.getLeftParameterTypeConstructor(),
                                        valid.getRightParameterTypeConstructor(),
                                        valid.getResultTypeConstructor()
                                );

                                note("Resolved (potential) conflicts between existing type parameters and new, generated type parameters (and likewise for secondary parameters)")
                                        .withDetail("Primary method type parameters", conflictFreeConflictFree.getPrimaryMethodTypeParameters(), TypeParameterGenerator::generateFrom)
                                        .withDetail("Result type parameter", conflictFreeConflictFree.getResultTypeParameter(), TypeParameterGenerator::generateFrom)
                                        .withDetail("Secondary method type parameters", conflictFreeConflictFree.getSecondaryMethodTypeParameters(), TypeParameterGenerator::generateFrom)
                                        .withDetail("Class type parameters", conflictFreeConflictFree.getClassTypeParameters(), TypeParameterGenerator::generateFrom)
                                        .withDetail("Primary parameter names", conflictFreeConflictFree.getPrimaryParameterNames())
                                        .withDetail("Self parameter name", conflictFreeConflictFree.getSelfParameterName())
                                        .withDetail("Combinator parameter name", conflictFreeConflictFree.getCombinatorParameterName())
                                        .withDetail("Maximum tuple size parameter name", conflictFreeConflictFree.getMaxTupleSizeParameterName())
                                        .withDetail("Left parameter type constructor", conflictFreeConflictFree.getLeftParameterTypeConstructor(), this::typeConstructorToString)
                                        .withDetail("Right parameter type constructor", conflictFreeConflictFree.getRightParameterTypeConstructor(), this::typeConstructorToString)
                                        .withDetail("Result type constructor", conflictFreeConflictFree.getResultTypeConstructor(), this::typeConstructorToString)
                                        .append();

                                String generated = generator()
                                        .withPackageName(method.getContainingClass().getPackageName())
                                        .withClassNameToGenerate(covariantAnnotation.className())
                                        .withClassTypeParameters(conflictFreeConflictFree.getClassTypeParameters())
                                        .withPrimaryMethodTypeParameters(conflictFreeConflictFree.getPrimaryMethodTypeParameters())
                                        .withResultTypeParameter(conflictFreeConflictFree.getResultTypeParameter())
                                        .withSecondaryMethodTypeParameters(conflictFreeConflictFree.getSecondaryMethodTypeParameters())
                                        .withMethodName(method.getName())
                                        .withPrimaryParameterNames(conflictFreeConflictFree.getPrimaryParameterNames())
                                        .withSecondaryParameters(conflictFreeConflictFree.getSecondaryParameters())
                                        .withSelfParameterName(conflictFreeConflictFree.getSelfParameterName())
                                        .withCombinatorParameterName(conflictFreeConflictFree.getCombinatorParameterName())
                                        .withMaxTupleSizeParameterName(conflictFreeConflictFree.getMaxTupleSizeParameterName())
                                        .withLeftParameterTypeConstructor(conflictFreeConflictFree.getLeftParameterTypeConstructor())
                                        .withRightParameterTypeConstructor(conflictFreeConflictFree.getRightParameterTypeConstructor())
                                        .withResultTypeConstructor(conflictFreeConflictFree.getResultTypeConstructor())
                                        .withLiftMethodName(covariantAnnotation.liftMethodName())
                                        .withMaxArity(covariantAnnotation.maxArity())
                                        .generate();

                                note("Done generating code").append();

                                FullyQualifiedName fullyQualifiedNameOfGeneratedClass = method.getContainingClass().getPackageName().withClassName(ClassName.of(covariantAnnotation.className()));
                                try {
                                    JavaFileObject builderFile = processingEnv.getFiler().createSourceFile(method.getContainingClass().getPackageName().withClassName(ClassName.of(covariantAnnotation.className())).raw());
                                    try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {
                                        out.print(generated);
                                        note("Saved generated code to .java-file on disk (%s)", fullyQualifiedNameOfGeneratedClass.raw()).append();
                                    }
                                } catch (IOException e) {
                                    error("Error saving generated code to .java-file on disk (%s)", fullyQualifiedNameOfGeneratedClass.raw()).append();
                                }
                            },
                            invalid -> {
                                error("Method '%s' in package '%s' does not meet all criteria for code generation", method.getName(), method.getContainingClass().getPackageName().raw())
                                        .withDetails(invalid.getErrorMessages())
                                        .append();
                            }
                    );
                } catch (Throwable t) {
                    error("Error occurred while processing annotation of type '%s': %s", COVARIANT_CLASS, t.getMessage()).append();
                    error("(Enable verbose logging to see a stack trace)").append();
                    printStackTraceToMessagerAsNote(t);
                }
            });
        });
        return true;
    }

    private <C> Optional<C> getAnnotationProperty(AnnotationMirror annotationMirror, String classNamePropertyName, Class<C> clazz) {
        return annotationMirror.getElementValues()
                .entrySet()
                .stream()
                .filter(entry -> Objects.equals(entry.getKey().getSimpleName().toString(), classNamePropertyName))
                .map(Map.Entry::getValue)
                .map(annotationValue -> clazz.cast(annotationValue.getValue()))
                .findAny();
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

    private boolean shouldLogNotes() {
        return Objects.equals(processingEnv.getOptions().getOrDefault(VERBOSE_ARGUMENT, "false"), "true");
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
}
