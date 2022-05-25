package nl.wernerdegroot.applicatives.processor;

import nl.wernerdegroot.applicatives.processor.domain.*;
import nl.wernerdegroot.applicatives.processor.domain.type.Type;
import nl.wernerdegroot.applicatives.processor.domain.typeconstructor.TypeConstructor;
import nl.wernerdegroot.applicatives.processor.generator.TypeGenerator;
import nl.wernerdegroot.applicatives.processor.generator.TypeParameterGenerator;
import nl.wernerdegroot.applicatives.processor.logging.Log;
import nl.wernerdegroot.applicatives.processor.logging.LoggingBackend;
import nl.wernerdegroot.applicatives.processor.logging.MessagerLoggingBackend;
import nl.wernerdegroot.applicatives.processor.logging.NoLoggingBackend;
import nl.wernerdegroot.applicatives.processor.validation.TemplateClassWithMethods;

import javax.annotation.processing.AbstractProcessor;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;
import java.util.Objects;

import static nl.wernerdegroot.applicatives.processor.conflicts.ConflictFinder.findClassTypeParameterNameReplacements;
import static nl.wernerdegroot.applicatives.processor.conflicts.Conflicts.*;
import static nl.wernerdegroot.applicatives.processor.generator.Generator.generator;

public abstract class AbstractCovariantProcessor extends AbstractProcessor {

    public void resolveConflictsAndGenerate(String className, String liftMethodName, int maxArity, PackageName packageName, TemplateClassWithMethods templateClassWithMethods) {
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
                .withPackageName(packageName)
                .withClassNameToGenerate(className)
                .withClassTypeParameters(conflictFree.getClassTypeParameters())
                .withOptionalInitializer(conflictFree.getOptionalInitializer())
                .withAccumulator(conflictFree.getAccumulator())
                .withOptionalFinalizer(conflictFree.getOptionalFinalizer())
                .withParameterTypeConstructorArguments(PARAMETER_TYPE_CONSTRUCTOR_ARGUMENTS)
                .withReturnTypeConstructorArgument(RETURN_TYPE_CONSTRUCTOR_ARGUMENT)
                .withInputParameterNames(INPUT_PARAMETER_NAMES)
                .withValueParameterName(VALUE_PARAMETER_NAME)
                .withCombinatorParameterName(COMBINATOR_PARAMETER_NAME)
                .withLiftMethodName(liftMethodName)
                .withMaxTupleSizeParameterName(MAX_TUPLE_SIZE_PARAMETER_NAME)
                .withMaxArity(maxArity)
                .withSelfParameterName(SELF_PARAMETER_NAME)
                .withTupleParameterName(TUPLE_PARAMETER_NAME)
                .withElementParameterName(ELEMENT_PARAMETER_NAME)
                .generate();

        Log.of("Done generating code").append(asNote());

        FullyQualifiedName fullyQualifiedNameOfGeneratedClass = packageName.withClassName(ClassName.of(className));
        try {
            JavaFileObject builderFile = processingEnv.getFiler().createSourceFile(fullyQualifiedNameOfGeneratedClass.raw());
            try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {
                out.print(generated);
                Log.of("Saved generated code to .java-file on disk (%s)", fullyQualifiedNameOfGeneratedClass.raw()).append(asNote());
            }
        } catch (IOException e) {
            Log.of("Error saving generated code to .java-file on disk (%s)", fullyQualifiedNameOfGeneratedClass.raw()).append(asError());
        }
    }

    private String typeConstructorToString(TypeConstructor typeConstructor) {
        Type substituteForPlaceholder = FullyQualifiedName.of("*").asType();
        Type typeConstructorAsType = typeConstructor.apply(substituteForPlaceholder);
        return TypeGenerator.generateFrom(typeConstructorAsType);
    }


    protected void printStackTraceToMessagerAsNote(Throwable e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, sw.toString());
    }

    protected boolean shouldLogNotes() {
        return Objects.equals(processingEnv.getOptions().getOrDefault(Options.VERBOSE_ARGUMENT, "false"), "true");
    }

    protected LoggingBackend getMessagerLoggingBackend(Diagnostic.Kind diagnosticKind) {
        return MessagerLoggingBackend.of(processingEnv, diagnosticKind);
    }

    protected LoggingBackend asNote() {
        return shouldLogNotes() ? getMessagerLoggingBackend(Diagnostic.Kind.NOTE) : NoLoggingBackend.INSTANCE;
    }

    protected LoggingBackend asError() {
        return getMessagerLoggingBackend(Diagnostic.Kind.ERROR);
    }
}
