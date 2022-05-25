package nl.wernerdegroot.applicatives.processor;

import com.google.auto.service.AutoService;
import nl.wernerdegroot.applicatives.processor.converters.ContainingClassConverter;
import nl.wernerdegroot.applicatives.processor.converters.MethodConverter;
import nl.wernerdegroot.applicatives.processor.domain.*;
import nl.wernerdegroot.applicatives.processor.domain.containing.ContainingClass;
import nl.wernerdegroot.applicatives.processor.domain.type.Type;
import nl.wernerdegroot.applicatives.processor.domain.typeconstructor.TypeConstructor;
import nl.wernerdegroot.applicatives.processor.generator.ContainingClassGenerator;
import nl.wernerdegroot.applicatives.processor.generator.ParameterGenerator;
import nl.wernerdegroot.applicatives.processor.generator.TypeGenerator;
import nl.wernerdegroot.applicatives.processor.generator.TypeParameterGenerator;
import nl.wernerdegroot.applicatives.processor.logging.Log;
import nl.wernerdegroot.applicatives.processor.logging.LoggingBackend;
import nl.wernerdegroot.applicatives.processor.logging.MessagerLoggingBackend;
import nl.wernerdegroot.applicatives.processor.logging.NoLoggingBackend;
import nl.wernerdegroot.applicatives.processor.validation.TemplateClassWithMethods;
import nl.wernerdegroot.applicatives.processor.validation.TemplateClassWithMethodsValidator;
import nl.wernerdegroot.applicatives.processor.validation.Validated;
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
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static javax.lang.model.element.ElementKind.METHOD;
import static nl.wernerdegroot.applicatives.processor.Classes.*;
import static nl.wernerdegroot.applicatives.processor.conflicts.ConflictFinder.findClassTypeParameterNameReplacements;
import static nl.wernerdegroot.applicatives.processor.conflicts.Conflicts.*;
import static nl.wernerdegroot.applicatives.processor.generator.Generator.generator;

@SupportedOptions({Options.VERBOSE_ARGUMENT})
@SupportedAnnotationTypes(COVARIANT_BUILDER_CANONICAL_NAME)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class CovariantBuilderProcessor extends AbstractProcessor {

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
                    Log.of("All criteria for code generation satisfied")
                            .withDetail("Class type parameters", templateClassWithMethods.getClassTypeParameters(), TypeParameterGenerator::generateFrom)
                            .withDetail("Name of initializer method", templateClassWithMethods.getOptionalInitializer().map(CovariantInitializer::getName))
                            .withDetail("Initialized type constructor", templateClassWithMethods.getOptionalInitializer().map(CovariantInitializer::getInitializedTypeConstructor), this::typeConstructorToString)
                            .withDetail("Name of accumulator method", templateClassWithMethods.getAccumulator().getName())
                            .withDetail("Input type constructor", templateClassWithMethods.getAccumulator().getInputTypeConstructor(), this::typeConstructorToString)
                            .withDetail("Partially accumulated type constructor", templateClassWithMethods.getAccumulator().getPartiallyAccumulatedTypeConstructor(), this::typeConstructorToString)
                            .withDetail("Accumulated type constructor", templateClassWithMethods.getAccumulator().getAccumulatedTypeConstructor(), this::typeConstructorToString)
                            .withDetail("Name of finalizer method", templateClassWithMethods.getOptionalFinalizer().map(CovariantFinalizer::getName))
                            .withDetail("To finalize type constructor", templateClassWithMethods.getOptionalFinalizer().map(CovariantFinalizer::getToFinalizeTypeConstructor), this::typeConstructorToString)
                            .withDetail("Finalized type constructor", templateClassWithMethods.getOptionalFinalizer().map(CovariantFinalizer::getFinalizedTypeConstructor), this::typeConstructorToString)
                            .append(asNote());

                    Map<TypeParameterName, TypeParameterName> classTypeParameterNameReplacements = findClassTypeParameterNameReplacements(templateClassWithMethods.getClassTypeParameters());
                    TemplateClassWithMethods conflictFree = templateClassWithMethods.replaceTypeParameterNames(classTypeParameterNameReplacements);

                    Log.of("Resolved (potential) conflicts between existing type parameters and new, generated type parameters")
                            .withDetail("Class type parameters", conflictFree.getClassTypeParameters(), TypeParameterGenerator::generateFrom)
                            .withDetail("Name of initializer method", conflictFree.getOptionalInitializer().map(CovariantInitializer::getName))
                            .withDetail("Initialized type constructor", conflictFree.getOptionalInitializer().map(CovariantInitializer::getInitializedTypeConstructor), this::typeConstructorToString)
                            .withDetail("Name of accumulator method", conflictFree.getAccumulator().getName())
                            .withDetail("Input type constructor", conflictFree.getAccumulator().getInputTypeConstructor(), this::typeConstructorToString)
                            .withDetail("Partially accumulated type constructor", conflictFree.getAccumulator().getPartiallyAccumulatedTypeConstructor(), this::typeConstructorToString)
                            .withDetail("Accumulated type constructor", conflictFree.getAccumulator().getAccumulatedTypeConstructor(), this::typeConstructorToString)
                            .withDetail("Name of finalizer method", conflictFree.getOptionalFinalizer().map(CovariantFinalizer::getName))
                            .withDetail("To finalize type constructor", conflictFree.getOptionalFinalizer().map(CovariantFinalizer::getToFinalizeTypeConstructor), this::typeConstructorToString)
                            .withDetail("Finalized type constructor", conflictFree.getOptionalFinalizer().map(CovariantFinalizer::getFinalizedTypeConstructor), this::typeConstructorToString)
                            .append(asNote());

                    String generated = generator()
                            .withPackageName(containingClass.getPackageName())
                            .withClassNameToGenerate(covariantBuilderAnnotation.className())
                            .withClassTypeParameters(conflictFree.getClassTypeParameters())
                            .withOptionalInitializer(conflictFree.getOptionalInitializer())
                            .withAccumulator(conflictFree.getAccumulator())
                            .withOptionalFinalizer(conflictFree.getOptionalFinalizer())
                            .withParameterTypeConstructorArguments(PARAMETER_TYPE_CONSTRUCTOR_ARGUMENTS)
                            .withReturnTypeConstructorArgument(RETURN_TYPE_CONSTRUCTOR_ARGUMENT)
                            .withInputParameterNames(INPUT_PARAMETER_NAMES)
                            .withValueParameterName(VALUE_PARAMETER_NAME)
                            .withCombinatorParameterName(COMBINATOR_PARAMETER_NAME)
                            .withLiftMethodName(covariantBuilderAnnotation.liftMethodName())
                            .withMaxTupleSizeParameterName(MAX_TUPLE_SIZE_PARAMETER_NAME)
                            .withMaxArity(covariantBuilderAnnotation.maxArity())
                            .withSelfParameterName(SELF_PARAMETER_NAME)
                            .withTupleParameterName(TUPLE_PARAMETER_NAME)
                            .withElementParameterName(ELEMENT_PARAMETER_NAME)
                            .generate();

                    Log.of("Done generating code").append(asNote());

                    FullyQualifiedName fullyQualifiedNameOfGeneratedClass = containingClass.getPackageName().withClassName(ClassName.of(covariantBuilderAnnotation.className()));
                    try {
                        JavaFileObject builderFile = processingEnv.getFiler().createSourceFile(containingClass.getPackageName().withClassName(ClassName.of(covariantBuilderAnnotation.className())).raw());
                        try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {
                            out.print(generated);
                            Log.of("Saved generated code to .java-file on disk (%s)", fullyQualifiedNameOfGeneratedClass.raw()).append(asNote());
                        }
                    } catch (IOException e) {
                        Log.of("Error saving generated code to .java-file on disk (%s)", fullyQualifiedNameOfGeneratedClass.raw()).append(asError());
                    }
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
}
