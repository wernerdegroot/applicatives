package nl.wernerdegroot.applicatives.processor;

import com.google.auto.service.AutoService;
import nl.wernerdegroot.applicatives.processor.converters.ContainingClassConverter;
import nl.wernerdegroot.applicatives.processor.converters.MethodConverter;
import nl.wernerdegroot.applicatives.processor.domain.*;
import nl.wernerdegroot.applicatives.processor.domain.containing.ContainingClass;
import nl.wernerdegroot.applicatives.processor.generator.TypeParameterGenerator;
import nl.wernerdegroot.applicatives.processor.logging.Log;
import nl.wernerdegroot.applicatives.processor.validation.ConfigValidator;
import nl.wernerdegroot.applicatives.processor.validation.ContravariantValidator;
import nl.wernerdegroot.applicatives.processor.validation.Validated;
import nl.wernerdegroot.applicatives.runtime.Contravariant;

import javax.annotation.processing.Processor;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import static nl.wernerdegroot.applicatives.processor.Classes.*;
import static nl.wernerdegroot.applicatives.processor.conflicts.ConflictFinder.findClassTypeParameterNameReplacements;
import static nl.wernerdegroot.applicatives.processor.conflicts.Conflicts.*;
import static nl.wernerdegroot.applicatives.processor.generator.ContravariantGenerator.generator;

@SupportedOptions({Options.VERBOSE_ARGUMENT})
@SupportedAnnotationTypes(CONTRAVARIANT_CLASS_NAME)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class ContravariantProcessor extends AbstractCovariantProcessor {

    @Override
    public Class<?> getAnnotationType() {
        return CONTRAVARIANT_CLASS;
    }

    @Override
    public void processElement(Element element) {

        if (element.getKind() != ElementKind.METHOD) {
            throw new IllegalArgumentException("Not a method");
        }

        Contravariant contravariantAnnotation = element.getAnnotation(Contravariant.class);

        noteAnnotationFound(element, contravariantAnnotation);

        ContainingClass containingClass;
        Method method;
        try {
            containingClass = ContainingClassConverter.toDomain(element.getEnclosingElement());
            method = MethodConverter.toDomain(element);
            noteConversionToDomainSuccess();
        } catch (Throwable e) {
            // If we have issues transforming to `nl.wernerdegroot.applicatives.processor.domain`
            // (which makes it a lot easier to log where the annotation was found) make
            // sure we log the method's raw signature so the client can troubleshoot.
            Log.of("Failure transforming from objects from 'javax.lang.model' to objects from 'nl.wernerdegroot.applicatives.processor.domain' for method with signature '%s'", element).append(asError());
            throw e;
        }

        noteMethodFound(containingClass, method);

        String classNameToGenerate = getClassNameToGenerate(contravariantAnnotation.className(), containingClass);
        String combineMethodName = getCombineMethodNameToGenerate(contravariantAnnotation.combineMethodName(), method);
        String liftMethodName = contravariantAnnotation.liftMethodName();
        int maxArity = contravariantAnnotation.maxArity();

        Validated<String, Void> validatedConfig = ConfigValidator.validate(classNameToGenerate, liftMethodName, maxArity);

        if (!validatedConfig.isValid()) {
            errorConfigNotValid(validatedConfig);
            return;
        }

        Validated<Log, ContravariantValidator.Result> validatedContravariant = ContravariantValidator.validate(containingClass, method);
        if (!validatedContravariant.isValid()) {
            errorValidationFailed(containingClass, method, validatedContravariant);
            return;
        }

        ContravariantValidator.Result contravariant = validatedContravariant.getValue();

        noteValidationSuccess(contravariant);

//        resolveConflictsAndGenerate(
//                classNameToGenerate,
//                liftMethodName,
//                maxArity,
//                containingClass.getPackageName(),
//                contravariant
//        );

        Map<TypeParameterName, TypeParameterName> classTypeParameterNameReplacements = findClassTypeParameterNameReplacements(contravariant.getClassTypeParameters());
        ContravariantValidator.Result conflictFree = contravariant.replaceTypeParameterNames(classTypeParameterNameReplacements);

        Log.of("Resolved (potential) conflicts between existing type parameters and new, generated type parameters")
                .withDetail("Class type parameters", conflictFree.getClassTypeParameters(), TypeParameterGenerator::generateFrom)
//                .withDetail("Name of initializer method", conflictFree.getOptionalInitializer().map(CovariantInitializer::getName))
//                .withDetail("Initialized type constructor", conflictFree.getOptionalInitializer().map(CovariantInitializer::getInitializedTypeConstructor), this::typeConstructorToString)
                .withDetail("Name of accumulator method", conflictFree.getAccumulator().getName())
                .withDetail("Input type constructor", conflictFree.getAccumulator().getInputTypeConstructor(), this::typeConstructorToString)
                .withDetail("Partially accumulated type constructor", conflictFree.getAccumulator().getPartiallyAccumulatedTypeConstructor(), this::typeConstructorToString)
                .withDetail("Accumulated type constructor", conflictFree.getAccumulator().getAccumulatedTypeConstructor(), this::typeConstructorToString)
//                .withDetail("Name of finalizer method", conflictFree.getOptionalFinalizer().map(CovariantFinalizer::getName))
//                .withDetail("To finalize type constructor", conflictFree.getOptionalFinalizer().map(CovariantFinalizer::getToFinalizeTypeConstructor), this::typeConstructorToString)
//                .withDetail("Finalized type constructor", conflictFree.getOptionalFinalizer().map(CovariantFinalizer::getFinalizedTypeConstructor), this::typeConstructorToString)
                .append(asNote());

        String generated = generator()
                .withPackageName(containingClass.getPackageName())
                .withClassNameToGenerate(classNameToGenerate)
                .withClassTypeParameters(conflictFree.getClassTypeParameters())
//                .withOptionalInitializer(conflictFree.getOptionalInitializer())
                .withAccumulator(conflictFree.getAccumulator())
//                .withOptionalFinalizer(conflictFree.getOptionalFinalizer())
                .withParameterTypeConstructorArguments(PARAMETER_TYPE_CONSTRUCTOR_ARGUMENTS)
                .withIntermediateTypeConstructorArgument(INTERMEDIATE_TYPE_CONSTRUCTOR_ARGUMENT)
                .withReturnTypeConstructorArgument(RETURN_TYPE_CONSTRUCTOR_ARGUMENT)
                .withInputParameterNames(INPUT_PARAMETER_NAMES)
                .withDecompositionParameterName(DECOMPOSITION_PARAMETER_NAME)
                .withSelfParameterName(SELF_PARAMETER_NAME)
                .withToIntermediateParameterName(TO_INTERMEDIATE_PARAMETER_NAME)
                .withExtractLeftParameterName(EXTRACT_LEFT_PARAMETER_NAME)
                .withExtractRightParameterName(EXTRACT_RIGHT_PARAMETER_NAME)
                .withCombineMethodName(combineMethodName)
                .withMaxArity(maxArity)
                .generate();

        Log.of("Done generating code").append(asNote());

        FullyQualifiedName fullyQualifiedNameOfGeneratedClass = containingClass.getPackageName().withClassName(ClassName.of(classNameToGenerate));
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

    private void errorValidationFailed(ContainingClass containingClass, Method method, Validated<Log, ContravariantValidator.Result> validatedTemplateClassWithMethods) {
        Log.of("Method '%s' in class '%s' does not meet all criteria for code generation", method.getName(), containingClass.getFullyQualifiedName().raw())
                .withLogs(validatedTemplateClassWithMethods.getErrorMessages())
                .append(asError());
    }

    protected void noteValidationSuccess(ContravariantValidator.Result templateClassWithMethods) {
        Log.of("All criteria for code generation satisfied")
                .withDetail("Class type parameters", templateClassWithMethods.getClassTypeParameters(), TypeParameterGenerator::generateFrom)
                .withDetail("Name of accumulator method", templateClassWithMethods.getAccumulator().getName())
                .withDetail("Input type constructor", templateClassWithMethods.getAccumulator().getInputTypeConstructor(), this::typeConstructorToString)
                .withDetail("Partially accumulated type constructor", templateClassWithMethods.getAccumulator().getPartiallyAccumulatedTypeConstructor(), this::typeConstructorToString)
                .withDetail("Accumulated type constructor", templateClassWithMethods.getAccumulator().getAccumulatedTypeConstructor(), this::typeConstructorToString)
                .append(asNote());
    }

    private void noteAnnotationFound(Element element, Contravariant contravariantAnnotation) {
        if (!(element.getEnclosingElement() instanceof TypeElement)) {
            throw new IllegalArgumentException("Enclosing element is not a class, interface or record");
        }
        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();

        Log.of("Found annotation of type '%s' on method '%s' in class '%s'", CONTRAVARIANT_CLASS_NAME, element.getSimpleName(), enclosingElement.getQualifiedName())
                .withDetail("Class name", contravariantAnnotation.className())
                .withDetail("Method name for `lift`", contravariantAnnotation.liftMethodName())
                .withDetail("Maximum arity", contravariantAnnotation.maxArity(), i -> Integer.toString(i))
                .append(asNote());
    }
}
