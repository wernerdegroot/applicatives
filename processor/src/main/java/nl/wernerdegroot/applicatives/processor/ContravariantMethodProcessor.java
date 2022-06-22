package nl.wernerdegroot.applicatives.processor;

import com.google.auto.service.AutoService;
import nl.wernerdegroot.applicatives.processor.domain.Method;
import nl.wernerdegroot.applicatives.processor.domain.containing.ContainingClass;
import nl.wernerdegroot.applicatives.processor.logging.Log;
import nl.wernerdegroot.applicatives.processor.validation.ContravariantParametersAndTypeParametersValidator;
import nl.wernerdegroot.applicatives.processor.validation.Validated;
import nl.wernerdegroot.applicatives.processor.validation.Validator;
import nl.wernerdegroot.applicatives.runtime.Contravariant;

import javax.annotation.processing.Processor;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;

import static nl.wernerdegroot.applicatives.processor.Classes.CONTRAVARIANT_CLASS;
import static nl.wernerdegroot.applicatives.processor.Classes.CONTRAVARIANT_CLASS_NAME;

@SupportedOptions({Options.VERBOSE_ARGUMENT})
@SupportedAnnotationTypes(CONTRAVARIANT_CLASS_NAME)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class ContravariantMethodProcessor extends AbstractProcessor<Contravariant, Element, Method> implements ContravariantProcessorTemplate<Contravariant, Element, Method>, MethodProcessorTemplate<Contravariant> {

    @Override
    public Class<Contravariant> getAnnotationType() {
        return CONTRAVARIANT_CLASS;
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
