package nl.wernerdegroot.applicatives.processor;

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
import nl.wernerdegroot.applicatives.processor.validation.ConfigValidator;
import nl.wernerdegroot.applicatives.processor.validation.Validated;
import nl.wernerdegroot.applicatives.processor.validation.Validator;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static nl.wernerdegroot.applicatives.processor.conflicts.ConflictFinder.findClassTypeParameterNameReplacements;

public interface ProcessorTemplate<Annotation, ElementToProcess, MethodOrMethods> {

    default void process(Element element) {
        try {
            ElementToProcess elementToProcess = getElementToProcess(element);
            Annotation annotation = getAnnotation(elementToProcess);
            String classNameToGenerate = getClassNameToGenerate(annotation);
            String combineMethodNameToGenerate = getCombineMethodNameToGenerate(annotation);
            String liftMethodNameToGenerate = getLiftMethodNameToGenerate(annotation);
            int maxArity = getMaxArity(annotation);
            noteAnnotationFound(elementToProcess, classNameToGenerate, combineMethodNameToGenerate, liftMethodNameToGenerate, maxArity);
            ContainingClass containingClass;
            MethodOrMethods methodOrMethods;
            try {
                containingClass = toContainingClass(elementToProcess);
                methodOrMethods = toMethodOrMethods(elementToProcess);
                noteConversionToDomainSuccess();
            } catch (Throwable e) {
                // If we have issues transforming to `nl.wernerdegroot.applicatives.processor.domain`
                // (which makes it a lot easier to log where the annotation was found) make sure we
                // log the raw signature of the method or class so the client can troubleshoot.
                noteConversionToDomainFailed(elementToProcess);
                throw e;
            }
            noteContainingClassAndMethodOrMethods(containingClass, methodOrMethods);
            String resolvedClassNameToGenerate = resolveClassNameToGenerate(classNameToGenerate, containingClass);
            Validated<Log, Validator.Result> validatedResult = validate(containingClass, methodOrMethods);
            if (!validatedResult.isValid()) {
                errorValidationFailed(containingClass, methodOrMethods, validatedResult.getErrorMessages());
                return;
            }
            Validator.Result result = validatedResult.getValue();
            noteValidationSuccess(result);
            String resolvedCombineMethodName = resolveCombineMethodNameToGenerate(combineMethodNameToGenerate, result);
            Validated<String, Void> validatedConfig = validateConfig(liftMethodNameToGenerate, maxArity, resolvedClassNameToGenerate, resolvedCombineMethodName);
            if (!validatedConfig.isValid()) {
                errorConfigNotValid(validatedConfig.getErrorMessages());
                return;
            }
            Validator.Result conflictFree = resolveConflicts(result);
            noteConflictsResolved(conflictFree);
            String generated = generate(containingClass, resolvedClassNameToGenerate, resolvedCombineMethodName, liftMethodNameToGenerate, maxArity, conflictFree);
            writeGeneratedFile(containingClass, resolvedClassNameToGenerate, generated);
            logDoneGenerating();
        } catch (Throwable t) {
            Log.of("Error occurred while processing annotation of type '%s': %s", getAnnotationType(), t.getMessage()).append(asError());
            printStackTraceToMessagerAsNote(t);
            if (!shouldLogNotes()) {
                Log.of("Enable verbose logging to see a stack trace.").append(asError());
            }
        }
    }

    ProcessingEnvironment getProcessingEnvironment();

    Class<?> getAnnotationType();

    ElementToProcess getElementToProcess(Element element);

    Annotation getAnnotation(ElementToProcess element);

    String getClassNameToGenerate(Annotation annotation);

    String getCombineMethodNameToGenerate(Annotation annotation);

    String getLiftMethodNameToGenerate(Annotation annotation);

    int getMaxArity(Annotation annotation);

    void noteAnnotationFound(ElementToProcess elementToProcess, String classNameToGenerate, String combineMethodNameToGenerate, String liftMethodNameToGenerate, int maxArity);

    ContainingClass toContainingClass(ElementToProcess elementToProcess);

    MethodOrMethods toMethodOrMethods(ElementToProcess elementToProcess);

    default void noteConversionToDomainSuccess() {
        Log.of("Successfully transformed objects from 'javax.lang.model' to objects from 'nl.wernerdegroot.applicatives.processor.domain'").append(asNote());
    }

    void noteConversionToDomainFailed(ElementToProcess elementToProcess);

    void noteContainingClassAndMethodOrMethods(ContainingClass containingClass, MethodOrMethods methodOrMethods);

    default void noteMethodFound(ContainingClass containingClass, Method method) {
        Log.of("Found method '%s' in class '%s'", method.getName(), containingClass.getFullyQualifiedName().raw())
                .withDetail("Annotations", method.getAnnotations(), FullyQualifiedName::raw)
                .withDetail("Modifiers", method.getModifiers(), Modifier::toString)
                .withDetail("Type parameters", method.getTypeParameters(), TypeParameterGenerator::generateFrom)
                .withDetail("Return type", method.getReturnType(), TypeGenerator::generateFrom)
                .withDetail("Parameters", method.getParameters(), ParameterGenerator::generateFrom)
                .append(asNote());
    }

    default String resolveClassNameToGenerate(String classNameToGenerate, ContainingClass containingClass) {
        return classNameToGenerate.replace("*", containingClass.getClassName().raw());
    }

    Validated<Log, Validator.Result> validate(ContainingClass containingClass, MethodOrMethods methodOrMethods);

    void errorValidationFailed(ContainingClass containingClass, MethodOrMethods methodOrMethods, Set<Log> errorMessages);

    default void noteValidationSuccess(Validator.Result result) {
        Log.of("All criteria for code generation satisfied")
                .withDetail("Class type parameters", result.getClassTypeParameters(), TypeParameterGenerator::generateFrom)
                .withDetail("Name of initializer method", result.getOptionalInitializer().map(Initializer::getName))
                .withDetail("Initialized type constructor", result.getOptionalInitializer().map(Initializer::getInitializedTypeConstructor), this::typeConstructorToString)
                .withDetail("Name of accumulator method", result.getAccumulator().getName())
                .withDetail("Input type constructor", result.getAccumulator().getInputTypeConstructor(), this::typeConstructorToString)
                .withDetail("Partially accumulated type constructor", result.getAccumulator().getPartiallyAccumulatedTypeConstructor(), this::typeConstructorToString)
                .withDetail("Accumulated type constructor", result.getAccumulator().getAccumulatedTypeConstructor(), this::typeConstructorToString)
                .withDetail("Name of finalizer method", result.getOptionalFinalizer().map(Finalizer::getName))
                .withDetail("To finalize type constructor", result.getOptionalFinalizer().map(Finalizer::getToFinalizeTypeConstructor), this::typeConstructorToString)
                .withDetail("Finalized type constructor", result.getOptionalFinalizer().map(Finalizer::getFinalizedTypeConstructor), this::typeConstructorToString)
                .append(asNote());
    }

    default String typeConstructorToString(TypeConstructor typeConstructor) {
        Type substituteForPlaceholder = FullyQualifiedName.of("*").asType();
        Type typeConstructorAsType = typeConstructor.apply(substituteForPlaceholder);
        return TypeGenerator.generateFrom(typeConstructorAsType);
    }

    default String resolveCombineMethodNameToGenerate(String combineMethodNameToGenerate, Validator.Result result) {
        return combineMethodNameToGenerate.replace("*", result.getAccumulator().getName());
    }

    default Validated<String, Void> validateConfig(String liftMethodNameToGenerate, int maxArity, String resolvedClassNameToGenerate, String resolvedCombineMethodName) {
        return ConfigValidator.validate(resolvedClassNameToGenerate, resolvedCombineMethodName, liftMethodNameToGenerate, maxArity);
    }

    default void errorConfigNotValid(Set<String> errorMessages) {
        Log.of("Configuration of '%s' not valid", getAnnotationType().getCanonicalName())
                .withDetails(errorMessages)
                .append(asError());
    }

    default Validator.Result resolveConflicts(Validator.Result result) {
        Map<TypeParameterName, TypeParameterName> classTypeParameterNameReplacements = findClassTypeParameterNameReplacements(result.getClassTypeParameters());
        return result.replaceTypeParameterNames(classTypeParameterNameReplacements);
    }

    default void noteConflictsResolved(Validator.Result result) {
        Log.of("Resolved (potential) conflicts between existing type parameters and new, generated type parameters")
                .withDetail("Class type parameters", result.getClassTypeParameters(), TypeParameterGenerator::generateFrom)
                .withDetail("Name of initializer method", result.getOptionalInitializer().map(Initializer::getName))
                .withDetail("Initialized type constructor", result.getOptionalInitializer().map(Initializer::getInitializedTypeConstructor), this::typeConstructorToString)
                .withDetail("Name of accumulator method", result.getAccumulator().getName())
                .withDetail("Input type constructor", result.getAccumulator().getInputTypeConstructor(), this::typeConstructorToString)
                .withDetail("Partially accumulated type constructor", result.getAccumulator().getPartiallyAccumulatedTypeConstructor(), this::typeConstructorToString)
                .withDetail("Accumulated type constructor", result.getAccumulator().getAccumulatedTypeConstructor(), this::typeConstructorToString)
                .withDetail("Name of finalizer method", result.getOptionalFinalizer().map(Finalizer::getName))
                .withDetail("To finalize type constructor", result.getOptionalFinalizer().map(Finalizer::getToFinalizeTypeConstructor), this::typeConstructorToString)
                .withDetail("Finalized type constructor", result.getOptionalFinalizer().map(Finalizer::getFinalizedTypeConstructor), this::typeConstructorToString)
                .append(asNote());
    }

    String generate(ContainingClass containingClass, String classNameToGenerate, String combineMethodName, String liftMethodName, int maxArity, Validator.Result conflictFree);

    default void writeGeneratedFile(ContainingClass containingClass, String classNameToGenerate, String generated) {
        FullyQualifiedName fullyQualifiedNameOfGeneratedClass = containingClass.getPackageName().withClassName(ClassName.of(classNameToGenerate));
        try {
            JavaFileObject builderFile = getProcessingEnvironment().getFiler().createSourceFile(fullyQualifiedNameOfGeneratedClass.raw());
            try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {
                out.print(generated);
                Log.of("Saved generated code to .java-file on disk (%s)", fullyQualifiedNameOfGeneratedClass.raw()).append(asNote());
            }
        } catch (IOException e) {
            Log.of("Error saving generated code to .java-file on disk (%s)", fullyQualifiedNameOfGeneratedClass.raw()).append(asError());
        }
    }

    default void logDoneGenerating() {
        Log.of("Done generating code").append(asNote());
    }

    default boolean shouldLogNotes() {
        return Objects.equals(getProcessingEnvironment().getOptions().getOrDefault(Options.VERBOSE_ARGUMENT, "false"), "true");
    }

    default void printStackTraceToMessagerAsNote(Throwable e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        asNote().log(sw.toString());
    }

    default LoggingBackend getMessagerLoggingBackend(Diagnostic.Kind diagnosticKind) {
        return MessagerLoggingBackend.of(getProcessingEnvironment(), diagnosticKind);
    }

    default LoggingBackend asNote() {
        return shouldLogNotes() ? getMessagerLoggingBackend(Diagnostic.Kind.NOTE) : NoLoggingBackend.INSTANCE;
    }

    default LoggingBackend asError() {
        return getMessagerLoggingBackend(Diagnostic.Kind.ERROR);
    }
}
