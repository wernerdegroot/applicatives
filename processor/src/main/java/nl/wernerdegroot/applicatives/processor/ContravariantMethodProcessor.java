package nl.wernerdegroot.applicatives.processor;

import com.google.auto.service.AutoService;
import nl.wernerdegroot.applicatives.processor.domain.Method;
import nl.wernerdegroot.applicatives.processor.domain.containing.ContainingClass;
import nl.wernerdegroot.applicatives.processor.logging.Log;
import nl.wernerdegroot.applicatives.processor.validation.ContravariantParametersAndTypeParametersValidator;
import nl.wernerdegroot.applicatives.processor.validation.Validated;
import nl.wernerdegroot.applicatives.processor.validation.Validator;
import nl.wernerdegroot.applicatives.runtime.Contravariant;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.Set;

import static nl.wernerdegroot.applicatives.processor.Classes.CONTRAVARIANT_CLASS_NAME;

@SupportedOptions({Options.VERBOSE_ARGUMENT})
@SupportedAnnotationTypes(CONTRAVARIANT_CLASS_NAME)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class ContravariantMethodProcessor extends AbstractProcessor implements ContravariantProcessorTemplate<Contravariant, Element, Method>, MethodProcessorTemplate<Contravariant> {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        annotations.forEach(annotation -> {
            roundEnv.getElementsAnnotatedWith(annotation).forEach(element -> {
                process(element);
            });
        });
        return false;
    }

    @Override
    public ProcessingEnvironment getProcessingEnvironment() {
        return processingEnv;
    }

    @Override
    public Class<?> getAnnotationType() {
        return Contravariant.class;
    }

    @Override
    public Contravariant getAnnotation(Element element) {
        return element.getAnnotation(Contravariant.class);
    }

    @Override
    public String getClassNameToGenerate(Contravariant contravariant) {
        return contravariant.className();
    }

    @Override
    public String getCombineMethodNameToGenerate(Contravariant contravariant) {
        return contravariant.combineMethodName();
    }

    @Override
    public String getLiftMethodNameToGenerate(Contravariant contravariant) {
        return contravariant.liftMethodName();
    }

    @Override
    public int getMaxArity(Contravariant contravariant) {
        return contravariant.maxArity();
    }

    @Override
    public Validated<Log, Validator.Result> validate(ContainingClass containingClass, Method method) {
        return Validator.validate(containingClass, method, new ContravariantParametersAndTypeParametersValidator());
    }
}
