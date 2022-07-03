package nl.wernerdegroot.applicatives.processor.generator;

import nl.wernerdegroot.applicatives.processor.domain.containing.ContainingClass;
import nl.wernerdegroot.applicatives.processor.validation.Validator;

public interface VarianceProcessorTemplate {

    String generate(ContainingClass containingClass, String classNameToGenerate, String combineMethodName, String liftMethodName, int maxArity, Validator.Result conflictFree);
}
