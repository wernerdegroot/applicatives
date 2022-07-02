package nl.wernerdegroot.applicatives.processor.generator;

import nl.wernerdegroot.applicatives.processor.domain.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static nl.wernerdegroot.applicatives.processor.conflicts.Conflicts.*;
import static nl.wernerdegroot.applicatives.processor.domain.type.Type.*;
import static nl.wernerdegroot.applicatives.processor.domain.typeconstructor.TypeConstructor.placeholder;
import static nl.wernerdegroot.applicatives.processor.generator.ContravariantGenerator.generator;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ContravariantGeneratorTest implements GeneratorTest {

    @Test
    public void givenComparator() throws IOException {
        String expected = getResourceFileAsString("ComparatorsOverloads");
        String toVerify = generator()
                .withPackageName(PackageName.of("nl.wernerdegroot.applicatives"))
                .withClassNameToGenerate("ComparatorsOverloads")
                .withClassTypeParameters(emptyList())
                .withOptionalInitializer(Optional.empty())
                .withAccumulator(
                        Accumulator.of(
                                "combineImpl",
                                COMPARATOR.with(placeholder().contravariant()),
                                COMPARATOR.with(placeholder().contravariant()),
                                COMPARATOR.with(placeholder().invariant())
                        )
                )
                .withOptionalFinalizer(Optional.empty())
                .withParticipantTypeParameters(PARTICIPANT_TYPE_PARAMETERS)
                .withIntermediateTypeParameter(INTERMEDIATE_TYPE_PARAMETER)
                .withCompositeTypeParameter(COMPOSITE_TYPE_PARAMETER)
                .withInputParameterNames(INPUT_PARAMETER_NAMES)
                .withDecompositionParameterName(DECOMPOSITION_PARAMETER_NAME)
                .withValueParameterName(VALUE_PARAMETER_NAME)
                .withSelfParameterName(SELF_PARAMETER_NAME)
                .withToIntermediateParameterName(TO_INTERMEDIATE_PARAMETER_NAME)
                .withExtractLeftParameterName(EXTRACT_LEFT_PARAMETER_NAME)
                .withExtractRightParameterName(EXTRACT_RIGHT_PARAMETER_NAME)
                .withCombineMethodToGenerate("combine")
                .withLiftMethodToGenerate("lift")
                .withMaxArity(4)
                .generate();

        assertEquals(expected, toVerify);

        ensureResourceFileCompiles("ComparatorsOverloads");
    }

    @Test
    public void givenFunction() throws IOException {
        TypeParameterName C1 = TypeParameterName.of("C1");

        String expected = getResourceFileAsString("ParametersOverloads");
        String toVerify = generator()
                .withPackageName(PackageName.of("nl.wernerdegroot.applicatives"))
                .withClassNameToGenerate("ParametersOverloads")
                .withClassTypeParameters(singletonList(C1.asTypeParameter()))
                .withOptionalInitializer(
                        Optional.of(
                                Initializer.of(
                                        "initialize",
                                        FUNCTION.with(placeholder().contravariant(), STRING.asTypeConstructor().covariant()),
                                        FUNCTION.with(placeholder().invariant(), STRING_BUILDER.asTypeConstructor().invariant())
                                )
                        )
                )
                .withAccumulator(
                        Accumulator.of(
                                "combineImpl",
                                FUNCTION.with(placeholder().contravariant(), STRING.asTypeConstructor().covariant()),
                                FUNCTION.with(placeholder().contravariant(), STRING_BUILDER.asTypeConstructor().covariant()),
                                FUNCTION.with(placeholder().invariant(), STRING_BUILDER.asTypeConstructor().invariant())
                        )
                )
                .withOptionalFinalizer(Optional.empty())
                .withParticipantTypeParameters(PARTICIPANT_TYPE_PARAMETERS)
                .withIntermediateTypeParameter(INTERMEDIATE_TYPE_PARAMETER)
                .withCompositeTypeParameter(COMPOSITE_TYPE_PARAMETER)
                .withInputParameterNames(INPUT_PARAMETER_NAMES)
                .withDecompositionParameterName(DECOMPOSITION_PARAMETER_NAME)
                .withValueParameterName(VALUE_PARAMETER_NAME)
                .withSelfParameterName(SELF_PARAMETER_NAME)
                .withToIntermediateParameterName(TO_INTERMEDIATE_PARAMETER_NAME)
                .withExtractLeftParameterName(EXTRACT_LEFT_PARAMETER_NAME)
                .withExtractRightParameterName(EXTRACT_RIGHT_PARAMETER_NAME)
                .withCombineMethodToGenerate("combine")
                .withLiftMethodToGenerate("lift")
                .withMaxArity(4)
                .generate();

        assertEquals(expected, toVerify);

        ensureResourceFileCompiles("ParametersOverloads");
    }

    @Test
    public void givenPredicate() throws IOException {
        String expected = getResourceFileAsString("PredicatesOverloads");
        String toVerify = generator()
                .withPackageName(PackageName.of("nl.wernerdegroot.applicatives"))
                .withClassNameToGenerate("PredicatesOverloads")
                .withClassTypeParameters(emptyList())
                .withParticipantTypeParameters(PARTICIPANT_TYPE_PARAMETERS)
                .withIntermediateTypeParameter(INTERMEDIATE_TYPE_PARAMETER)
                .withCompositeTypeParameter(COMPOSITE_TYPE_PARAMETER)
                .withOptionalInitializer(Optional.empty())
                .withAccumulator(
                        Accumulator.of(
                                "combineImpl",
                                PREDICATE.with(placeholder().contravariant()),
                                FUNCTION.with(placeholder().contravariant(), BOOLEAN.asTypeConstructor().covariant()),
                                FUNCTION.with(placeholder().invariant(), BOOLEAN.asTypeConstructor().covariant())
                        )
                )
                .withOptionalFinalizer(
                        Optional.of(
                                Finalizer.of(
                                        "finalize",
                                        FUNCTION.with(placeholder().contravariant(), BOOLEAN.asTypeConstructor().covariant()),
                                        PREDICATE.with(placeholder().invariant())
                                )
                        )
                )
                .withInputParameterNames(INPUT_PARAMETER_NAMES)
                .withDecompositionParameterName(DECOMPOSITION_PARAMETER_NAME)
                .withValueParameterName(VALUE_PARAMETER_NAME)
                .withSelfParameterName(SELF_PARAMETER_NAME)
                .withToIntermediateParameterName(TO_INTERMEDIATE_PARAMETER_NAME)
                .withExtractLeftParameterName(EXTRACT_LEFT_PARAMETER_NAME)
                .withExtractRightParameterName(EXTRACT_RIGHT_PARAMETER_NAME)
                .withCombineMethodToGenerate("combine")
                .withLiftMethodToGenerate("lift")
                .withMaxArity(4)
                .generate();

        assertEquals(expected, toVerify);

        ensureResourceFileCompiles("PredicatesOverloads");
    }
}
