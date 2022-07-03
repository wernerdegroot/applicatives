package nl.wernerdegroot.applicatives.processor;

import com.google.auto.service.AutoService;
import nl.wernerdegroot.applicatives.processor.domain.Method;
import nl.wernerdegroot.applicatives.processor.domain.containing.ContainingClass;
import nl.wernerdegroot.applicatives.processor.logging.Log;
import nl.wernerdegroot.applicatives.processor.validation.InvariantParametersAndTypeParametersValidator;
import nl.wernerdegroot.applicatives.processor.validation.Validated;
import nl.wernerdegroot.applicatives.processor.validation.Validator;
import nl.wernerdegroot.applicatives.runtime.Invariant;

import javax.annotation.processing.Processor;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import java.util.List;

import static nl.wernerdegroot.applicatives.processor.Classes.INVARIANT_BUILDER_CANONICAL_NAME;
import static nl.wernerdegroot.applicatives.processor.Classes.INVARIANT_BUILDER_CLASS;

@SupportedOptions({Options.VERBOSE_ARGUMENT})
@SupportedAnnotationTypes(INVARIANT_BUILDER_CANONICAL_NAME)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class InvariantBuilderProcessor extends AbstractProcessor<Invariant.Builder, TypeElement, List<Method>> implements InvariantProcessorTemplate, BuilderProcessorTemplate<Invariant.Builder> {

    @Override
    public Class<Invariant.Builder> getAnnotationType() {
        return INVARIANT_BUILDER_CLASS;
    }

    @Override
    public String getClassNameToGenerate(Invariant.Builder builder) {
        return builder.className();
    }

    @Override
    public String getCombineMethodNameToGenerate(Invariant.Builder builder) {
        return builder.combineMethodName();
    }

    @Override
    public String getLiftMethodNameToGenerate(Invariant.Builder builder) {
        return builder.liftMethodName();
    }

    @Override
    public int getMaxArity(Invariant.Builder builder) {
        return builder.maxArity();
    }

    @Override
    public Validated<Log, Validator.Result> validate(ContainingClass containingClass, List<Method> methods) {
        return Validator.validate(containingClass, methods, new InvariantParametersAndTypeParametersValidator());
    }
}
