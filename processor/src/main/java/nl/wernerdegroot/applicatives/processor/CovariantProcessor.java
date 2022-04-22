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
import nl.wernerdegroot.applicatives.processor.validation.TemplateClassWithMethodsValidator;
import nl.wernerdegroot.applicatives.processor.validation.Validated;
import nl.wernerdegroot.applicatives.runtime.Covariant;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
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

@SupportedOptions({Options.VERBOSE_ARGUMENT})
@SupportedAnnotationTypes(CovariantProcessor.COVARIANT_CLASS_NAME)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class CovariantProcessor extends AbstractProcessor {

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

                    Covariant covariantAnnotation = element.getAnnotation(Covariant.class);

                    Log.of("Found annotation of type '%s'", COVARIANT_CLASS_NAME)
                            .withDetail("Class name", covariantAnnotation.className())
                            .withDetail("Method name for `lift`", covariantAnnotation.liftMethodName())
                            .withDetail("Maximum arity", covariantAnnotation.maxArity(), i -> Integer.toString(i))
                            .append(asNote());

                    Method method;
                    try {
                        method = MethodConverter.toDomain(element);
                        Log.of("Successfully transformed objects from 'javax.lang.model' to objects from 'nl.wernerdegroot.applicatives.processor.domain'").append(asNote());
                    } catch (Throwable e) {
                        // If we have issues transforming to `nl.wernerdegroot.applicatives.processor.domain`
                        // (which makes it a lot easier to log where the annotation was found) make
                        // sure we log the method's raw signature so the client can troubleshoot.
                        Log.of("Failure transforming from objects from 'javax.lang.model' to objects from 'nl.wernerdegroot.applicatives.processor.domain' for method with signature '%s'", element).append(asError());
                        throw e;
                    }

                    Log.of("Found method '%s' in class '%s'", method.getName(), method.getContainingClass().getFullyQualifiedName().raw())
                            .withDetail("Modifiers", method.getModifiers(), Modifier::toString)
                            .withDetail("Return type", method.getReturnType(), TypeGenerator::generateFrom)
                            .withDetail("Parameters", method.getParameters(), ParameterGenerator::generateFrom)
                            .withDetail("Containing class", method.getContainingClass(), this::containingToString)
                            .append(asNote());

                    Validated<TemplateClassWithMethods> validatedTemplateClassWithMethods = TemplateClassWithMethodsValidator.validate(method.getContainingClass(), method);
                    if (!validatedTemplateClassWithMethods.isValid()) {
                        Log.of("Method '%s' in class '%s' does not meet all criteria for code generation", method.getName(), method.getContainingClass().getFullyQualifiedName().raw())
                                .withDetails(validatedTemplateClassWithMethods.getErrorMessages())
                                .append(asError());
                        return;
                    }

                    TemplateClassWithMethods validTemplateClassWithMethods = validatedTemplateClassWithMethods.getValue();
                    TemplateClass validTemplateClass = validTemplateClassWithMethods.getTemplateClass();
                    Log.of("Class meets all criteria for code generation")
                            .withDetail("Type parameters", validTemplateClass.getTypeParameters(), TypeParameterGenerator::generateFrom)
                            .append(asNote());

                    AccumulatorMethod validAccumulatorMethod = validTemplateClassWithMethods.getAccumulatorMethod();
                    Log.of("Method meets all criteria for code generation")
                            .withDetail("Accumulation type constructor", validAccumulatorMethod.getAccumulationTypeConstructor(), this::typeConstructorToString)
                            .withDetail("Permissive accumulation type constructor", validAccumulatorMethod.getPermissiveAccumulationTypeConstructor(), this::typeConstructorToString)
                            .withDetail("Input type constructor", validAccumulatorMethod.getInputTypeConstructor(), this::typeConstructorToString)
                            .append(asNote());

                    ConflictFree conflictFree = ConflictPrevention.preventConflicts(
                            validTemplateClass.getTypeParameters(),
                            validAccumulatorMethod.getAccumulationTypeConstructor(),
                            validAccumulatorMethod.getPermissiveAccumulationTypeConstructor(),
                            validAccumulatorMethod.getInputTypeConstructor()
                    );

                    Log.of("Resolved (potential) conflicts between existing type parameters and new, generated type parameters")
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
                            .append(asNote());

                    String generated = generator()
                            .withPackageName(method.getContainingClass().getPackageName())
                            .withClassNameToGenerate(covariantAnnotation.className())
                            .withClassTypeParameters(conflictFree.getClassTypeParameters())
                            .withInputTypeConstructorArguments(conflictFree.getInputTypeConstructorArguments())
                            .withResultTypeConstructorArgument(conflictFree.getResultTypeConstructorArguments())
                            .withMethodName(method.getName())
                            .withInputParameterNames(conflictFree.getInputParameterNames())
                            .withSelfParameterName(conflictFree.getSelfParameterName())
                            .withCombinatorParameterName(conflictFree.getCombinatorParameterName())
                            .withMaxTupleSizeParameterName(conflictFree.getMaxTupleSizeParameterName())
                            .withAccumulationTypeConstructor(conflictFree.getAccumulationTypeConstructor())
                            .withPermissiveAccumulationTypeConstructor(conflictFree.getPermissiveAccumulationTypeConstructor())
                            .withInputTypeConstructor(conflictFree.getInputTypeConstructor())
                            .withLiftMethodName(covariantAnnotation.liftMethodName())
                            .withMaxArity(covariantAnnotation.maxArity())
                            .generate();

                    Log.of("Done generating code").append(asNote());

                    FullyQualifiedName fullyQualifiedNameOfGeneratedClass = method.getContainingClass().getPackageName().withClassName(ClassName.of(covariantAnnotation.className()));
                    try {
                        JavaFileObject builderFile = processingEnv.getFiler().createSourceFile(method.getContainingClass().getPackageName().withClassName(ClassName.of(covariantAnnotation.className())).raw());
                        try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {
                            out.print(generated);
                            Log.of("Saved generated code to .java-file on disk (%s)", fullyQualifiedNameOfGeneratedClass.raw()).append(asNote());
                        }
                    } catch (IOException e) {
                        Log.of("Error saving generated code to .java-file on disk (%s)", fullyQualifiedNameOfGeneratedClass.raw()).append(asError());
                    }
                } catch (Throwable t) {
                    Log.of("Error occurred while processing annotation of type '%s': %s", COVARIANT_CLASS, t.getMessage()).append(asError());
                    if (shouldLogNotes()) {
                        printStackTraceToMessagerAsNote(t);
                    } else {
                        Log.of("Enable verbose logging to see a stack trace.").append(asError());
                    }
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
        return Objects.equals(processingEnv.getOptions().getOrDefault(Options.VERBOSE_ARGUMENT, "false"), "true");
    }

    private LoggingBackend getMessagerLoggingBackend(Diagnostic.Kind diagnosticKind) {
        return MessagerLoggingBackend.of(processingEnv, diagnosticKind);
    }

    private LoggingBackend asNote() {
        return shouldLogNotes() ? getMessagerLoggingBackend(Diagnostic.Kind.NOTE) : NoLoggingBackend.INSTANCE;
    }

    private LoggingBackend asError() {
        return getMessagerLoggingBackend(Diagnostic.Kind.ERROR);
    }
}
