package nl.wernerdegroot.applicatives.processor;

import com.google.auto.service.AutoService;
import nl.wernerdegroot.applicatives.processor.domain.Method;
import nl.wernerdegroot.applicatives.processor.domain.containing.ContainingClass;
import nl.wernerdegroot.applicatives.processor.logging.Log;
import nl.wernerdegroot.applicatives.processor.validation.CovariantParametersAndTypeParametersValidator;
import nl.wernerdegroot.applicatives.processor.validation.InvariantParametersAndTypeParametersValidator;
import nl.wernerdegroot.applicatives.processor.validation.Validated;
import nl.wernerdegroot.applicatives.processor.validation.Validator;
import nl.wernerdegroot.applicatives.runtime.Covariant;
import nl.wernerdegroot.applicatives.runtime.Invariant;

import javax.annotation.processing.Processor;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;

import static nl.wernerdegroot.applicatives.processor.Classes.*;

@SupportedOptions({Options.VERBOSE_ARGUMENT})
@SupportedAnnotationTypes(INVARIANT_CLASS_NAME)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class InvariantMethodProcessor extends AbstractProcessor<Invariant, Element, Method> implements InvariantProcessorTemplate<Invariant, Element, Method>, MethodProcessorTemplate<Invariant> {

    @Override
    public Class<Invariant> getAnnotationType() {
        return INVARIANT_CLASS;
    }

    @Override
    public String getClassNameToGenerate(Invariant invariant) {
        return invariant.className();
    }

    @Override
    public String getCombineMethodNameToGenerate(Invariant invariant) {
        return invariant.combineMethodName();
    }

    @Override
    public String getLiftMethodNameToGenerate(Invariant invariant) {
        return invariant.liftMethodName();
    }

    @Override
    public int getMaxArity(Invariant invariant) {
        return invariant.maxArity();
    }

    @Override
    public Validated<Log, Validator.Result> validate(ContainingClass containingClass, Method method) {
        return Validator.validate(containingClass, method, new InvariantParametersAndTypeParametersValidator());
    }
}
