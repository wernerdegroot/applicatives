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
import javax.lang.model.element.TypeElement;
import java.util.List;

import static nl.wernerdegroot.applicatives.processor.Classes.COVARIANT_BUILDER_CANONICAL_NAME;
import static nl.wernerdegroot.applicatives.processor.Classes.COVARIANT_BUILDER_CLASS;

@SupportedOptions({Options.VERBOSE_ARGUMENT})
@SupportedAnnotationTypes(COVARIANT_BUILDER_CANONICAL_NAME)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class CovariantBuilderProcessor extends AbstractProcessor<Covariant.Builder, TypeElement, List<Method>> implements CovariantProcessorTemplate, BuilderProcessorTemplate<Covariant.Builder> {

    @Override
    public Class<Covariant.Builder> getAnnotationType() {
        return COVARIANT_BUILDER_CLASS;
    }

    @Override
    public String getClassNameToGenerate(Covariant.Builder builder) {
        return builder.className();
    }

    @Override
    public String getCombineMethodNameToGenerate(Covariant.Builder builder) {
        return builder.combineMethodName();
    }

    @Override
    public String getLiftMethodNameToGenerate(Covariant.Builder builder) {
        return builder.liftMethodName();
    }

    @Override
    public int getMaxArity(Covariant.Builder builder) {
        return builder.maxArity();
    }

    @Override
    public Validated<Log, Validator.Result> validate(ContainingClass containingClass, List<Method> methods) {
        return Validator.validate(containingClass, methods, new CovariantParametersAndTypeParametersValidator());
    }
}
