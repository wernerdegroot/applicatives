package nl.wernerdegroot.applicatives.processor;

import nl.wernerdegroot.applicatives.processor.domain.containing.ContainingClass;
import nl.wernerdegroot.applicatives.processor.validation.Validator;

import static nl.wernerdegroot.applicatives.processor.conflicts.Conflicts.*;
import static nl.wernerdegroot.applicatives.processor.generator.InvariantGenerator.generator;

public interface InvariantProcessorTemplate<Annotation, ElementToProcess, MethodOrMethods> extends ProcessorTemplate<Annotation, ElementToProcess, MethodOrMethods> {

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
                .withIntermediateTypeConstructorArgument(INTERMEDIATE_TYPE_CONSTRUCTOR_ARGUMENT)
                .withReturnTypeConstructorArgument(RETURN_TYPE_CONSTRUCTOR_ARGUMENT)
                .withInputParameterNames(INPUT_PARAMETER_NAMES)
                .withDecompositionParameterName(DECOMPOSITION_PARAMETER_NAME)
                .withValueParameterName(VALUE_PARAMETER_NAME)
                .withCombinatorParameterName(COMBINATOR_PARAMETER_NAME)
                .withMaxTupleSizeParameterName(MAX_TUPLE_SIZE_PARAMETER_NAME)
                .withSelfParameterName(SELF_PARAMETER_NAME)
                .withToIntermediateParameterName(TO_INTERMEDIATE_PARAMETER_NAME)
                .withExtractLeftParameterName(EXTRACT_LEFT_PARAMETER_NAME)
                .withExtractRightParameterName(EXTRACT_RIGHT_PARAMETER_NAME)
                .withTupleParameterName(TUPLE_PARAMETER_NAME)
                .withElementParameterName(ELEMENT_PARAMETER_NAME)
                .withCombineMethodToGenerate(combineMethodNameToGenerate)
                .withLiftMethodToGenerate(liftMethodNameToGenerate)
                .withMaxArity(maxArity)
                .generate();
    }
}
