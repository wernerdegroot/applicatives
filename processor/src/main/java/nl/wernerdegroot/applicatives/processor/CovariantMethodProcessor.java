package nl.wernerdegroot.applicatives.processor;

import com.google.auto.service.AutoService;
import nl.wernerdegroot.applicatives.processor.domain.Method;
import nl.wernerdegroot.applicatives.processor.domain.containing.ContainingClass;
import nl.wernerdegroot.applicatives.processor.logging.Log;
import nl.wernerdegroot.applicatives.processor.validation.CovariantParametersAndTypeParametersValidator;
import nl.wernerdegroot.applicatives.processor.validation.Validated;
import nl.wernerdegroot.applicatives.processor.validation.Validator;
import nl.wernerdegroot.applicatives.runtime.Covariant;

import javax.annotation.processing.Processor;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;

import static nl.wernerdegroot.applicatives.processor.Classes.COVARIANT_CLASS;
import static nl.wernerdegroot.applicatives.processor.Classes.COVARIANT_CLASS_NAME;

@SupportedOptions({Options.VERBOSE_ARGUMENT})
@SupportedAnnotationTypes(COVARIANT_CLASS_NAME)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class CovariantMethodProcessor extends AbstractProcessor<Covariant, Element, Method> implements CovariantProcessorTemplate<Covariant, Element, Method>, MethodProcessorTemplate<Covariant> {

    @Override
    public Class<Covariant> getAnnotationType() {
        return COVARIANT_CLASS;
    }

    @Override
    public String getClassNameToGenerate(Covariant covariant) {
        return covariant.className();
    }

    @Override
    public String getCombineMethodNameToGenerate(Covariant covariant) {
        return covariant.combineMethodName();
    }

    @Override
    public String getLiftMethodNameToGenerate(Covariant covariant) {
        return covariant.liftMethodName();
    }

    @Override
    public int getMaxArity(Covariant covariant) {
        return covariant.maxArity();
    }

    @Override
    public Validated<Log, Validator.Result> validate(ContainingClass containingClass, Method method) {
        return Validator.validate(containingClass, method, new CovariantParametersAndTypeParametersValidator());
    }
}
