package nl.wernerdegroot.applicatives.processor;

import nl.wernerdegroot.applicatives.processor.domain.containing.ContainingClass;
import nl.wernerdegroot.applicatives.processor.validation.Validator;

import static nl.wernerdegroot.applicatives.processor.conflicts.Conflicts.*;
import static nl.wernerdegroot.applicatives.processor.generator.CovariantGenerator.generator;

public interface CovariantProcessorTemplate<Annotation, ElementToProcess, MethodOrMethods> extends ProcessorTemplate<Annotation, ElementToProcess, MethodOrMethods> {

    @Override
    default String generate(ContainingClass containingClass, String classNameToGenerate, String combineMethodNameToGenerate, String liftMethodNameToGenerate, int maxArity, Validator.Result conflictFree) {
        return generator()
                .withPackageName(containingClass.getPackageName())
                .withClassNameToGenerate(classNameToGenerate)
                .withClassTypeParameters(conflictFree.getClassTypeParameters())
                .withOptionalInitializer(conflictFree.getOptionalInitializer())
                .withAccumulator(conflictFree.getAccumulator())
                .withOptionalFinalizer(conflictFree.getOptionalFinalizer())
                .withParameterTypeConstructorArguments(PARAMETER_TYPE_CONSTRUCTOR_ARGUMENTS)
                .withReturnTypeConstructorArgument(RETURN_TYPE_CONSTRUCTOR_ARGUMENT)
                .withInputParameterNames(INPUT_PARAMETER_NAMES)
                .withValueParameterName(VALUE_PARAMETER_NAME)
                .withCombinatorParameterName(COMBINATOR_PARAMETER_NAME)
                .withLiftMethodToGenerate(liftMethodNameToGenerate)
                .withMaxTupleSizeParameterName(MAX_TUPLE_SIZE_PARAMETER_NAME)
                .withMaxArity(maxArity)
                .withSelfParameterName(SELF_PARAMETER_NAME)
                .withTupleParameterName(TUPLE_PARAMETER_NAME)
                .withElementParameterName(ELEMENT_PARAMETER_NAME)
                .generate();
    }
}
