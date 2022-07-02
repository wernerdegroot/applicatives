package nl.wernerdegroot.applicatives.processor.generator;

import nl.wernerdegroot.applicatives.processor.domain.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static nl.wernerdegroot.applicatives.processor.conflicts.Conflicts.*;
import static nl.wernerdegroot.applicatives.processor.domain.type.Type.*;
import static nl.wernerdegroot.applicatives.processor.domain.typeconstructor.TypeConstructor.placeholder;
import static nl.wernerdegroot.applicatives.processor.generator.CovariantGenerator.generator;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CovariantGeneratorTest implements GeneratorTest {

    @Test
    public void givenOptional() throws IOException {
        String expected = getResourceFileAsString("OptionalsOverloads");
        String toVerify = generator()
                .withPackageName(PackageName.of("nl.wernerdegroot.applicatives"))
                .withClassNameToGenerate("OptionalsOverloads")
                .withClassTypeParameters(emptyList())
                .withOptionalInitializer(Optional.empty())
                .withAccumulator(
                        Accumulator.of(
                                "combine",
                                OPTIONAL.with(placeholder().covariant()),
                                OPTIONAL.with(placeholder().covariant()),
                                OPTIONAL.with(placeholder().invariant())
                        )
                )
                .withOptionalFinalizer(Optional.empty())
                .withParticipantTypeParameters(PARTICIPANT_TYPE_PARAMETERS)
                .withCompositeTypeParameter(COMPOSITE_TYPE_PARAMETER)
                .withInputParameterNames(INPUT_PARAMETER_NAMES)
                .withValueParameterName(VALUE_PARAMETER_NAME)
                .withSelfParameterName(SELF_PARAMETER_NAME)
                .withCombinatorParameterName(COMBINATOR_PARAMETER_NAME)
                .withMaxTupleSizeParameterName(MAX_TUPLE_SIZE_PARAMETER_NAME)
                .withTupleParameterName(TUPLE_PARAMETER_NAME)
                .withElementParameterName(ELEMENT_PARAMETER_NAME)
                .withCombineMethodToGenerate("combine")
                .withLiftMethodToGenerate("lift")
                .withMaxArity(2)
                .generate();

        assertEquals(expected, toVerify);

        ensureResourceFileCompiles("OptionalsOverloads");
    }

    @Test
    public void givenCompletableFutures() throws IOException {
        String expected = getResourceFileAsString("CompletableFuturesOverloads");
        String toVerify = generator()
                .withPackageName(PackageName.of("nl.wernerdegroot.applicatives"))
                .withClassNameToGenerate("CompletableFuturesOverloads")
                .withClassTypeParameters(emptyList())
                .withOptionalInitializer(Optional.empty())
                .withAccumulator(
                        Accumulator.of(
                                "combineImpl",
                                COMPLETABLE_FUTURE.with(placeholder().covariant()),
                                COMPLETABLE_FUTURE.with(placeholder().covariant()),
                                COMPLETABLE_FUTURE.with(placeholder().invariant())
                        )
                )
                .withOptionalFinalizer(Optional.empty())
                .withParticipantTypeParameters(PARTICIPANT_TYPE_PARAMETERS)
                .withCompositeTypeParameter(COMPOSITE_TYPE_PARAMETER)
                .withInputParameterNames(INPUT_PARAMETER_NAMES)
                .withValueParameterName(VALUE_PARAMETER_NAME)
                .withSelfParameterName(SELF_PARAMETER_NAME)
                .withCombinatorParameterName(COMBINATOR_PARAMETER_NAME)
                .withMaxTupleSizeParameterName(MAX_TUPLE_SIZE_PARAMETER_NAME)
                .withTupleParameterName(TUPLE_PARAMETER_NAME)
                .withElementParameterName(ELEMENT_PARAMETER_NAME)
                .withCombineMethodToGenerate("combine")
                .withLiftMethodToGenerate("lift")
                .withMaxArity(2)
                .generate();

        assertEquals(expected, toVerify);

        ensureResourceFileCompiles("CompletableFuturesOverloads");
    }

    @Test
    public void givenMap() throws IOException {
        TypeParameterName K = TypeParameterName.of("K");

        String expected = getResourceFileAsString("MapsOverloads");
        String toVerify = generator()
                .withPackageName(PackageName.of("nl.wernerdegroot.applicatives"))
                .withClassNameToGenerate("MapsOverloads")
                .withClassTypeParameters(singletonList(K.extending(COMPARABLE.with(K.asType().contravariant()))))
                .withOptionalInitializer(
                        Optional.of(
                                Initializer.of(
                                        "initialize",
                                        MAP.with(K.asTypeConstructor().contravariant(), placeholder().covariant()),
                                        TREE_MAP.with(K.asTypeConstructor().contravariant(), placeholder().invariant())
                                )
                        )
                )
                .withAccumulator(
                        Accumulator.of(
                                "combineImpl",
                                MAP.with(K.asTypeConstructor().contravariant(), placeholder().covariant()),
                                TREE_MAP.with(K.asTypeConstructor().contravariant(), placeholder().covariant()),
                                TREE_MAP.with(K.asTypeConstructor().contravariant(), placeholder().invariant())
                        )
                )
                .withOptionalFinalizer(Optional.empty())
                .withParticipantTypeParameters(PARTICIPANT_TYPE_PARAMETERS)
                .withCompositeTypeParameter(COMPOSITE_TYPE_PARAMETER)
                .withInputParameterNames(INPUT_PARAMETER_NAMES)
                .withValueParameterName(VALUE_PARAMETER_NAME)
                .withSelfParameterName(SELF_PARAMETER_NAME)
                .withCombinatorParameterName(COMBINATOR_PARAMETER_NAME)
                .withMaxTupleSizeParameterName(MAX_TUPLE_SIZE_PARAMETER_NAME)
                .withTupleParameterName(TUPLE_PARAMETER_NAME)
                .withElementParameterName(ELEMENT_PARAMETER_NAME)
                .withCombineMethodToGenerate("combine")
                .withLiftMethodToGenerate("lift")
                .withMaxArity(3)
                .generate();

        assertEquals(expected, toVerify);

        ensureResourceFileCompiles("MapsOverloads");
    }

    @Test
    public void givenList() throws IOException {
        String expected = getResourceFileAsString("ListsOverloads");
        String toVerify = generator()
                .withPackageName(PackageName.of("nl.wernerdegroot.applicatives"))
                .withClassNameToGenerate("ListsOverloads")
                .withClassTypeParameters(emptyList())
                .withOptionalInitializer(Optional.empty())
                .withAccumulator(
                        Accumulator.of(
                                "combineImpl",
                                LIST.with(placeholder().covariant()),
                                ARRAY_LIST.with(placeholder().covariant()),
                                ARRAY_LIST.with(placeholder().invariant())
                        )
                )
                .withOptionalFinalizer(
                        Optional.of(
                                Finalizer.of(
                                        "finalize",
                                        ARRAY_LIST.with(placeholder().covariant()),
                                        LIST.with(placeholder().invariant())
                                )
                        )
                )
                .withParticipantTypeParameters(PARTICIPANT_TYPE_PARAMETERS)
                .withCompositeTypeParameter(COMPOSITE_TYPE_PARAMETER)
                .withInputParameterNames(INPUT_PARAMETER_NAMES)
                .withValueParameterName(VALUE_PARAMETER_NAME)
                .withSelfParameterName(SELF_PARAMETER_NAME)
                .withCombinatorParameterName(COMBINATOR_PARAMETER_NAME)
                .withMaxTupleSizeParameterName(MAX_TUPLE_SIZE_PARAMETER_NAME)
                .withTupleParameterName(TUPLE_PARAMETER_NAME)
                .withElementParameterName(ELEMENT_PARAMETER_NAME)
                .withCombineMethodToGenerate("combine")
                .withLiftMethodToGenerate("lift")
                .withMaxArity(3)
                .generate();

        assertEquals(expected, toVerify);

        ensureResourceFileCompiles("ListsOverloads");
    }

    @Test
    public void givenFunction() throws IOException {
        TypeParameterName P = TypeParameterName.of("P");

        String expected = getResourceFileAsString("ResultsOverloads");
        String toVerify = generator()
                .withPackageName(PackageName.of("nl.wernerdegroot.applicatives"))
                .withClassNameToGenerate("ResultsOverloads")
                .withClassTypeParameters(asList(P.extending(OBJECT)))
                .withOptionalInitializer(Optional.empty())
                .withAccumulator(
                        Accumulator.of(
                                "combineImpl",
                                FUNCTION.with(P.asTypeConstructor().invariant(), placeholder().invariant()),
                                FUNCTION.with(P.asTypeConstructor().invariant(), placeholder().invariant()),
                                FUNCTION.with(P.asTypeConstructor().invariant(), placeholder().invariant())
                        )
                )
                .withOptionalFinalizer(Optional.empty())
                .withParticipantTypeParameters(PARTICIPANT_TYPE_PARAMETERS)
                .withCompositeTypeParameter(COMPOSITE_TYPE_PARAMETER)
                .withInputParameterNames(INPUT_PARAMETER_NAMES)
                .withValueParameterName(VALUE_PARAMETER_NAME)
                .withSelfParameterName(SELF_PARAMETER_NAME)
                .withCombinatorParameterName(COMBINATOR_PARAMETER_NAME)
                .withMaxTupleSizeParameterName(MAX_TUPLE_SIZE_PARAMETER_NAME)
                .withTupleParameterName(TUPLE_PARAMETER_NAME)
                .withElementParameterName(ELEMENT_PARAMETER_NAME)
                .withCombineMethodToGenerate("combine")
                .withLiftMethodToGenerate("lift")
                .withMaxArity(4)
                .generate();

        assertEquals(expected, toVerify);

        ensureResourceFileCompiles("ResultsOverloads");
    }
}
