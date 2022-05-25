package nl.wernerdegroot.applicatives.processor;

import com.google.auto.service.AutoService;
import nl.wernerdegroot.applicatives.processor.converters.ContainingClassConverter;
import nl.wernerdegroot.applicatives.processor.converters.MethodConverter;
import nl.wernerdegroot.applicatives.processor.domain.FullyQualifiedName;
import nl.wernerdegroot.applicatives.processor.domain.Method;
import nl.wernerdegroot.applicatives.processor.domain.Modifier;
import nl.wernerdegroot.applicatives.processor.domain.containing.ContainingClass;
import nl.wernerdegroot.applicatives.processor.generator.ParameterGenerator;
import nl.wernerdegroot.applicatives.processor.generator.TypeGenerator;
import nl.wernerdegroot.applicatives.processor.logging.Log;
import nl.wernerdegroot.applicatives.processor.validation.TemplateClassWithMethods;
import nl.wernerdegroot.applicatives.processor.validation.TemplateClassWithMethodsValidator;
import nl.wernerdegroot.applicatives.processor.validation.Validated;
import nl.wernerdegroot.applicatives.runtime.Covariant;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import java.util.Set;

import static nl.wernerdegroot.applicatives.processor.Classes.COVARIANT_CLASS;
import static nl.wernerdegroot.applicatives.processor.Classes.COVARIANT_CLASS_NAME;

@SupportedOptions({Options.VERBOSE_ARGUMENT})
@SupportedAnnotationTypes(COVARIANT_CLASS_NAME)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class CovariantProcessor extends AbstractCovariantProcessor {

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
                    resolveConflictsAndGenerate(
                            covariantAnnotation.className(),
                            covariantAnnotation.liftMethodName(),
                            covariantAnnotation.maxArity(),
                            containingClass.getPackageName(),
                            templateClassWithMethods
                    );
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
}
