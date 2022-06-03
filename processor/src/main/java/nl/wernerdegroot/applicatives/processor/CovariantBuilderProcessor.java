package nl.wernerdegroot.applicatives.processor;

import com.google.auto.service.AutoService;
import nl.wernerdegroot.applicatives.processor.converters.ContainingClassConverter;
import nl.wernerdegroot.applicatives.processor.converters.MethodConverter;
import nl.wernerdegroot.applicatives.processor.domain.FullyQualifiedName;
import nl.wernerdegroot.applicatives.processor.domain.Method;
import nl.wernerdegroot.applicatives.processor.domain.containing.ContainingClass;
import nl.wernerdegroot.applicatives.processor.generator.ContainingClassGenerator;
import nl.wernerdegroot.applicatives.processor.logging.Log;
import nl.wernerdegroot.applicatives.processor.validation.TemplateClassWithMethodsValidator;
import nl.wernerdegroot.applicatives.processor.validation.Validated;
import nl.wernerdegroot.applicatives.runtime.Covariant;

import javax.annotation.processing.Processor;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
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
public class CovariantBuilderProcessor extends AbstractCovariantProcessor<Covariant.Builder> {

    public static final Set<FullyQualifiedName> SUPPORTED_ANNOTATIONS = Stream.of(
            INITIALIZER,
            ACCUMULATOR,
            FINALIZER
    ).collect(toSet());

    @Override
    public Class<?> getAnnotationType() {
        return COVARIANT_BUILDER_CLASS;
    }

    @Override
    public Covariant.Builder getAnnotation(Element element) {
        return element.getAnnotation(Covariant.Builder.class);
    }

    @Override
    public String getClassNameToGenerate(Covariant.Builder annotation) {
        return annotation.className();
    }

    @Override
    public String getLiftMethodName(Covariant.Builder annotation) {
        return annotation.liftMethodName();
    }

    @Override
    public int getMaxArity(Covariant.Builder annotation) {
        return annotation.maxArity();
    }

    @Override
    public void processElement(Element element) {
        if (element.getKind() != ElementKind.CLASS) {
            throw new IllegalArgumentException("Not a class");
        }
        TypeElement typeElement = (TypeElement) element;

        Covariant.Builder covariantBuilderAnnotation = element.getAnnotation(Covariant.Builder.class);

        noteAnnotationFound(typeElement, covariantBuilderAnnotation);

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

            noteConversionToDomainSuccess();
        } catch (Throwable e) {
            // If we have issues transforming to `nl.wernerdegroot.applicatives.processor.domain`
            // (which makes it a lot easier to log where the annotation was found) make
            // sure we log the method's raw signature so the client can troubleshoot.
            Log.of("Failure transforming from objects from 'javax.lang.model' to objects from 'nl.wernerdegroot.applicatives.processor.domain' for class '%s'", typeElement.getQualifiedName()).append(asError());
            throw e;
        }

        noteClassFound(containingClass, methods);

        Validated<String, TemplateClassWithMethodsValidator.Result> validatedTemplateClassWithMethods = TemplateClassWithMethodsValidator.validate(containingClass, methods);
        if (!validatedTemplateClassWithMethods.isValid()) {
            errorValidationFailed(containingClass, validatedTemplateClassWithMethods);
            return;
        }

        TemplateClassWithMethodsValidator.Result templateClassWithMethods = validatedTemplateClassWithMethods.getValue();

        noteValidationSuccess(templateClassWithMethods);

        resolveConflictsAndGenerate(
                covariantBuilderAnnotation.className(),
                covariantBuilderAnnotation.liftMethodName(),
                covariantBuilderAnnotation.maxArity(),
                containingClass.getPackageName(),
                templateClassWithMethods
        );
    }

    private void noteClassFound(ContainingClass containingClass, List<Method> methods) {
        Log.of("Found class '%s'", ContainingClassGenerator.generateFrom(containingClass)).append(asNote());
        methods.forEach(method -> {
            noteMethodFound(containingClass, method);
        });
    }

    private void errorValidationFailed(ContainingClass containingClass, Validated<String, TemplateClassWithMethodsValidator.Result> validatedTemplateClassWithMethods) {
        Log.of("Class '%s' does not meet all criteria for code generation", containingClass.getFullyQualifiedName().raw())
                .withDetails(validatedTemplateClassWithMethods.getErrorMessages())
                .append(asError());
    }

    private void noteAnnotationFound(TypeElement typeElement, Covariant.Builder covariantBuilderAnnotation) {
        Log.of("Found annotation of type '%s' on class '%s'", COVARIANT_BUILDER_CANONICAL_NAME, typeElement.getQualifiedName())
                .withDetail("Class name to generate", covariantBuilderAnnotation.className())
                .withDetail("Method name for `lift`", covariantBuilderAnnotation.liftMethodName())
                .withDetail("Maximum arity", covariantBuilderAnnotation.maxArity(), i -> Integer.toString(i))
                .append(asNote());
    }
}
