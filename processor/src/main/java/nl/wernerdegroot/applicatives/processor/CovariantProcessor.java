package nl.wernerdegroot.applicatives.processor;

import com.google.auto.service.AutoService;
import nl.wernerdegroot.applicatives.processor.converters.ContainingClassConverter;
import nl.wernerdegroot.applicatives.processor.converters.MethodConverter;
import nl.wernerdegroot.applicatives.processor.domain.Method;
import nl.wernerdegroot.applicatives.processor.domain.containing.ContainingClass;
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

import static nl.wernerdegroot.applicatives.processor.Classes.COVARIANT_CLASS;
import static nl.wernerdegroot.applicatives.processor.Classes.COVARIANT_CLASS_NAME;

@SupportedOptions({Options.VERBOSE_ARGUMENT})
@SupportedAnnotationTypes(COVARIANT_CLASS_NAME)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class CovariantProcessor extends AbstractCovariantProcessor<Covariant> {

    @Override
    public Class<?> getAnnotationType() {
        return COVARIANT_CLASS;
    }

    @Override
    public Covariant getAnnotation(Element element) {
        return element.getAnnotation(Covariant.class);
    }

    @Override
    public String getClassNameToGenerate(Covariant annotation) {
        return annotation.className();
    }

    @Override
    public String getLiftMethodName(Covariant annotation) {
        return annotation.liftMethodName();
    }

    @Override
    public int getMaxArity(Covariant annotation) {
        return annotation.maxArity();
    }

    @Override
    public void processElement(Element element) {
        if (element.getKind() != ElementKind.METHOD) {
            throw new IllegalArgumentException("Not a method");
        }

        Covariant covariantAnnotation = element.getAnnotation(Covariant.class);

        noteAnnotationFound(element, covariantAnnotation);

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

        Validated<TemplateClassWithMethodsValidator.Result> validatedTemplateClassWithMethods = TemplateClassWithMethodsValidator.validate(containingClass, method);
        if (!validatedTemplateClassWithMethods.isValid()) {
            errorValidationFailed(containingClass, method, validatedTemplateClassWithMethods);
            return;
        }

        TemplateClassWithMethodsValidator.Result templateClassWithMethods = validatedTemplateClassWithMethods.getValue();

        noteValidationSuccess(templateClassWithMethods);

        resolveConflictsAndGenerate(
                covariantAnnotation.className(),
                covariantAnnotation.liftMethodName(),
                covariantAnnotation.maxArity(),
                containingClass.getPackageName(),
                templateClassWithMethods
        );
    }

    private void errorValidationFailed(ContainingClass containingClass, Method method, Validated<TemplateClassWithMethodsValidator.Result> validatedTemplateClassWithMethods) {
        Log.of("Method '%s' in class '%s' does not meet all criteria for code generation", method.getName(), containingClass.getFullyQualifiedName().raw())
                .withDetails(validatedTemplateClassWithMethods.getErrorMessages())
                .append(asError());
    }

    private void noteAnnotationFound(Element element, Covariant covariantAnnotation) {
        if (!(element.getEnclosingElement() instanceof TypeElement)) {
            throw new IllegalArgumentException("Enclosing element is not a class, interface or record");
        }
        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();

        Log.of("Found annotation of type '%s' on method '%s' in class '%s'", COVARIANT_CLASS_NAME, element.getSimpleName(), enclosingElement.getQualifiedName())
                .withDetail("Class name to generate", covariantAnnotation.className())
                .withDetail("Method name for `lift`", covariantAnnotation.liftMethodName())
                .withDetail("Maximum arity", covariantAnnotation.maxArity(), i -> Integer.toString(i))
                .append(asNote());
    }
}
