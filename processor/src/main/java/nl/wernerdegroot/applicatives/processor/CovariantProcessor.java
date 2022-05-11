package nl.wernerdegroot.applicatives.processor;

import com.google.auto.service.AutoService;
import nl.wernerdegroot.applicatives.processor.conflicts.ConflictFree;
import nl.wernerdegroot.applicatives.processor.conflicts.ConflictPrevention;
import nl.wernerdegroot.applicatives.processor.converters.ContainingClassConverter;
import nl.wernerdegroot.applicatives.processor.converters.MethodConverter;
import nl.wernerdegroot.applicatives.processor.domain.*;
import nl.wernerdegroot.applicatives.processor.domain.containing.ContainingClass;
import nl.wernerdegroot.applicatives.processor.domain.type.Type;
import nl.wernerdegroot.applicatives.processor.domain.typeconstructor.TypeConstructor;
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
import java.util.Objects;
import java.util.Set;

import static nl.wernerdegroot.applicatives.processor.Classes.COVARIANT_CLASS;
import static nl.wernerdegroot.applicatives.processor.Classes.COVARIANT_CLASS_NAME;
import static nl.wernerdegroot.applicatives.processor.generator.Generator.generator;

@SupportedOptions({Options.VERBOSE_ARGUMENT})
@SupportedAnnotationTypes(COVARIANT_CLASS_NAME)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class CovariantProcessor extends AbstractProcessor {

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
                            .withDetail("Class name to generate", covariantAnnotation.className())
                            .withDetail("Method name for `lift`", covariantAnnotation.liftMethodName())
                            .withDetail("Maximum arity", covariantAnnotation.maxArity(), i -> Integer.toString(i))
                            .append(asNote());

                    ContainingClass containingClass;
                    Method method;
                    try {
                        containingClass = ContainingClassConverter.toDomain(element.getEnclosingElement());
                        method = MethodConverter.toDomain(element);
                        Log.of("Successfully transformed objects from 'javax.lang.model' to objects from 'nl.wernerdegroot.applicatives.processor.domain'").append(asNote());
                    } catch (Throwable e) {
                        // If we have issues transforming to `nl.wernerdegroot.applicatives.processor.domain`
                        // (which makes it a lot easier to log where the annotation was found) make
                        // sure we log the method's raw signature so the client can troubleshoot.
                        Log.of("Failure transforming from objects from 'javax.lang.model' to objects from 'nl.wernerdegroot.applicatives.processor.domain' for method with signature '%s'", element).append(asError());
                        throw e;
                    }

                    Log.of("Found method '%s' in class '%s'", method.getName(), containingClass.getFullyQualifiedName().raw())
                            .withDetail("Annotations", method.getAnnotations(), FullyQualifiedName::raw)
                            .withDetail("Modifiers", method.getModifiers(), Modifier::toString)
                            .withDetail("Return type", method.getReturnType(), TypeGenerator::generateFrom)
                            .withDetail("Parameters", method.getParameters(), ParameterGenerator::generateFrom)
                            .append(asNote());

                    Validated<TemplateClassWithMethods> validatedTemplateClassWithMethods = TemplateClassWithMethodsValidator.validate(containingClass, method);
                    if (!validatedTemplateClassWithMethods.isValid()) {
                        Log.of("Method '%s' in class '%s' does not meet all criteria for code generation", method.getName(), containingClass.getFullyQualifiedName().raw())
                                .withDetails(validatedTemplateClassWithMethods.getErrorMessages())
                                .append(asError());
                        return;
                    }

                    TemplateClassWithMethods templateClassWithMethods = validatedTemplateClassWithMethods.getValue();
                    Log.of("All criteria for code generation satisfied")
                            .withDetail("Class type parameters", templateClassWithMethods.getClassTypeParameters(), TypeParameterGenerator::generateFrom)
                            .withDetail("Accumulation type constructor", templateClassWithMethods.getAccumulationTypeConstructor(), this::typeConstructorToString)
                            .withDetail("Permissive accumulation type constructor", templateClassWithMethods.getPermissiveAccumulationTypeConstructor(), this::typeConstructorToString)
                            .withDetail("Input type constructor", templateClassWithMethods.getInputTypeConstructor(), this::typeConstructorToString)
                            .withDetail("Name of initializer method", templateClassWithMethods.getOptionalInitializerMethodName())
                            .withDetail("Name of accumulator method", templateClassWithMethods.getAccumulatorMethodName())
                            .append(asNote());

                    ConflictFree conflictFree = ConflictPrevention.preventConflicts(
                            templateClassWithMethods.getClassTypeParameters(),
                            templateClassWithMethods.getAccumulationTypeConstructor(),
                            templateClassWithMethods.getPermissiveAccumulationTypeConstructor(),
                            templateClassWithMethods.getInputTypeConstructor()
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
                            .withPackageName(containingClass.getPackageName())
                            .withClassNameToGenerate(covariantAnnotation.className())
                            .withClassTypeParameters(conflictFree.getClassTypeParameters())
                            .withInputTypeConstructorArguments(conflictFree.getInputTypeConstructorArguments())
                            .withResultTypeConstructorArgument(conflictFree.getResultTypeConstructorArguments())
                            .withOptionalInitializerMethodName(templateClassWithMethods.getOptionalInitializerMethodName())
                            .withAccumulatorMethodName(templateClassWithMethods.getAccumulatorMethodName())
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

                    FullyQualifiedName fullyQualifiedNameOfGeneratedClass = containingClass.getPackageName().withClassName(ClassName.of(covariantAnnotation.className()));
                    try {
                        JavaFileObject builderFile = processingEnv.getFiler().createSourceFile(containingClass.getPackageName().withClassName(ClassName.of(covariantAnnotation.className())).raw());
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
