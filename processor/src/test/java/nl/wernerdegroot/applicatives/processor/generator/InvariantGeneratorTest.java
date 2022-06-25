package nl.wernerdegroot.applicatives.processor.generator;

import nl.wernerdegroot.applicatives.processor.domain.Accumulator;
import nl.wernerdegroot.applicatives.processor.domain.Finalizer;
import nl.wernerdegroot.applicatives.processor.domain.Initializer;
import nl.wernerdegroot.applicatives.processor.domain.PackageName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static nl.wernerdegroot.applicatives.processor.conflicts.Conflicts.*;
import static nl.wernerdegroot.applicatives.processor.domain.type.Type.*;
import static nl.wernerdegroot.applicatives.processor.domain.typeconstructor.TypeConstructor.placeholder;
import static nl.wernerdegroot.applicatives.processor.generator.InvariantGenerator.generator;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class InvariantGeneratorTest implements GeneratorTest {

    @Test
    public void givenUnaryOperator() throws IOException {
        String expected = getResourceFileAsString("/UnaryOperatorsOverloads.java");
        String toVerify = generator()
                .withPackageName(PackageName.of("nl.wernerdegroot.applicatives"))
                .withClassNameToGenerate("UnaryOperatorsOverloads")
                .withClassTypeParameters(emptyList())
                .withOptionalInitializer(Optional.empty())
                .withAccumulator(
                        Accumulator.of(
                                "combineImpl",
                                UNARY_OPERATOR.with(placeholder().invariant()),
                                UNARY_OPERATOR.with(placeholder().invariant()),
                                UNARY_OPERATOR.with(placeholder().invariant())
                        )
                )
                .withOptionalFinalizer(Optional.empty())
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
                .withCombineMethodToGenerate("combine")
                .withLiftMethodToGenerate("lift")
                .withMaxArity(4)
                .generate();

        assertEquals(expected, toVerify);
    }

    @Test
    public void givenBinaryOperator() throws IOException {
        String expected = getResourceFileAsString("/BinaryOperatorsOverloads.java");
        String toVerify = generator()
                .withPackageName(PackageName.of("nl.wernerdegroot.applicatives"))
                .withClassNameToGenerate("BinaryOperatorsOverloads")
                .withClassTypeParameters(emptyList())
                .withOptionalInitializer(
                        Optional.of(
                                Initializer.of(
                                        "initialize",
                                        BINARY_OPERATOR.with(placeholder().invariant()),
                                        BI_FUNCTION.with(placeholder().invariant(), placeholder().invariant(), placeholder().invariant())
                                )
                        )
                )
                .withAccumulator(
                        Accumulator.of(
                                "combineImpl",
                                BINARY_OPERATOR.with(placeholder().invariant()),
                                BI_FUNCTION.with(placeholder().invariant(), placeholder().invariant(), placeholder().invariant()),
                                BI_FUNCTION.with(placeholder().invariant(), placeholder().invariant(), placeholder().invariant())
                        )
                )
                .withOptionalFinalizer(Optional.empty())
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
                .withCombineMethodToGenerate("combine")
                .withLiftMethodToGenerate("lift")
                .withMaxArity(4)
                .generate();

        assertEquals(expected, toVerify);
    }

    @Test
    public void givenInvariantSets() throws IOException {
        String expected = getResourceFileAsString("/SetsOverloads.java");
        String toVerify = generator()
                .withPackageName(PackageName.of("nl.wernerdegroot.applicatives"))
                .withClassNameToGenerate("SetsOverloads")
                .withClassTypeParameters(emptyList())
                .withOptionalInitializer(Optional.empty())
                .withAccumulator(
                        Accumulator.of(
                                "combineImpl",
                                SET.with(placeholder().invariant()),
                                BIT_SET.asType().asTypeConstructor(),
                                BIT_SET.asType().asTypeConstructor()
                        )
                )
                .withOptionalFinalizer(
                        Optional.of(
                                Finalizer.of(
                                        "finalize",
                                        BIT_SET.asType().asTypeConstructor(),
                                        SET.with(placeholder().invariant())
                                )
                        )
                )
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
                .withCombineMethodToGenerate("combine")
                .withLiftMethodToGenerate("lift")
                .withMaxArity(4)
                .generate();

        assertEquals(expected, toVerify);
    }
}
