package nl.wernerdegroot.applicatives.processor;

import nl.wernerdegroot.applicatives.processor.domain.*;
import nl.wernerdegroot.applicatives.processor.domain.containing.ContainingClass;
import nl.wernerdegroot.applicatives.processor.domain.type.Type;
import nl.wernerdegroot.applicatives.processor.domain.typeconstructor.TypeConstructor;
import nl.wernerdegroot.applicatives.processor.generator.ParameterGenerator;
import nl.wernerdegroot.applicatives.processor.generator.TypeGenerator;
import nl.wernerdegroot.applicatives.processor.generator.TypeParameterGenerator;
import nl.wernerdegroot.applicatives.processor.generator.VarianceProcessorTemplate;
import nl.wernerdegroot.applicatives.processor.logging.Log;
import nl.wernerdegroot.applicatives.processor.logging.LoggingBackend;
import nl.wernerdegroot.applicatives.processor.logging.NoLoggingBackend;
import nl.wernerdegroot.applicatives.processor.validation.ConfigValidator;
import nl.wernerdegroot.applicatives.processor.validation.Validated;
import nl.wernerdegroot.applicatives.processor.validation.Validator;

import javax.tools.Diagnostic;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static nl.wernerdegroot.applicatives.processor.conflicts.ConflictFinder.findClassTypeParameterNameReplacements;

public interface ProcessorTemplate<Annotation, AnnotatedElement, ElementToProcess, MethodOrMethods> extends VarianceProcessorTemplate {

    default void process(AnnotatedElement annotatedElement) {
        try {
            ElementToProcess elementToProcess = getElementToProcess(annotatedElement);
            Annotation annotation = getAnnotation(elementToProcess);
            String classNameToGenerate = getClassNameToGenerate(annotation);
            String combineMethodNameToGenerate = getCombineMethodNameToGenerate(annotation);
            String liftMethodNameToGenerate = getLiftMethodNameToGenerate(annotation);
            int maxArity = getMaxArity(annotation);
            noteAnnotationFound(describeElementToProcess(elementToProcess), classNameToGenerate, combineMethodNameToGenerate, liftMethodNameToGenerate, maxArity);
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
                errorConversionToDomainFailed(describeElementToProcess(elementToProcess), e);
                printStackTraceToMessengerAsNote(e);
                return;
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
            printStackTraceToMessengerAsNote(t);
        }
    }

    Class<Annotation> getAnnotationType();

    Annotation getAnnotation(ElementToProcess element);

    ElementToProcess getElementToProcess(AnnotatedElement annotatedElement);

    String getClassNameToGenerate(Annotation annotation);

    String getCombineMethodNameToGenerate(Annotation annotation);

    String getLiftMethodNameToGenerate(Annotation annotation);

    int getMaxArity(Annotation annotation);

    String describeElementToProcess(ElementToProcess elementToProcess);

    default void noteAnnotationFound(String elementToProcessDescription, String classNameToGenerate, String combineMethodNameToGenerate, String liftMethodNameToGenerate, int maxArity) {
        Log.of("Found annotation of type '%s' on %s", getAnnotationType().getCanonicalName(), elementToProcessDescription)
                .withDetail("Class name", classNameToGenerate)
                .withDetail("Method name for 'combine'", combineMethodNameToGenerate)
                .withDetail("Method name for 'lift'", liftMethodNameToGenerate)
                .withDetail("Maximum arity", maxArity, i -> Integer.toString(i))
                .append(asNote());
    }

    ContainingClass toContainingClass(ElementToProcess elementToProcess);

    MethodOrMethods toMethodOrMethods(ElementToProcess elementToProcess);

    default void noteConversionToDomainSuccess() {
        Log.of("Successfully transformed objects from 'javax.lang.model' to objects from 'nl.wernerdegroot.applicatives.processor.domain'").append(asNote());
    }

    default void errorConversionToDomainFailed(String elementToProcessDescription, Throwable throwable) {
        Log.of("Failure transforming from objects from 'javax.lang.model' to objects from 'nl.wernerdegroot.applicatives.processor.domain' for %s: %s", elementToProcessDescription, throwable.getMessage()).append(asError());
    }

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

    void errorValidationFailed(ContainingClass containingClass, MethodOrMethods methodOrMethods, List<Log> errorMessages);

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

    default void errorConfigNotValid(List<String> errorMessages) {
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

    default void writeGeneratedFile(ContainingClass containingClass, String classNameToGenerate, String generated) {
        FullyQualifiedName fullyQualifiedNameOfGeneratedClass = containingClass.getPackageName().withClassName(ClassName.of(classNameToGenerate));
        try {
            try (PrintWriter printWriter = getPrintWriterForFile(fullyQualifiedNameOfGeneratedClass)) {
                printWriter.print(generated);
                Log.of("Saved generated code to .java-file on disk (%s)", fullyQualifiedNameOfGeneratedClass.raw()).append(asNote());
            }
        } catch (IOException e) {
            Log.of("Error saving generated code to .java-file on disk (%s): %s", fullyQualifiedNameOfGeneratedClass.raw(), e.getMessage()).append(asError());
            printStackTraceToMessengerAsNote(e);
        }
    }

    PrintWriter getPrintWriterForFile(FullyQualifiedName fullyQualifiedName) throws IOException;

    default void logDoneGenerating() {
        Log.of("Done generating code").append(asNote());
    }

    default boolean shouldLogNotes() {
        return Objects.equals(getConfiguration().getOrDefault(Options.VERBOSE_ARGUMENT, "false"), "true");
    }

    Map<String, String> getConfiguration();

    default void printStackTraceToMessengerAsNote(Throwable e) {
        if (shouldLogNotes()) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            asNote().log(sw.toString());
        } else {
            Log.of("Enable verbose logging to see a stack trace.").append(asError());
        }
    }

    LoggingBackend getMessengerLoggingBackend(Diagnostic.Kind diagnosticKind);

    default LoggingBackend asNote() {
        return shouldLogNotes() ? getMessengerLoggingBackend(Diagnostic.Kind.NOTE) : NoLoggingBackend.INSTANCE;
    }

    default LoggingBackend asError() {
        return getMessengerLoggingBackend(Diagnostic.Kind.ERROR);
    }
}
