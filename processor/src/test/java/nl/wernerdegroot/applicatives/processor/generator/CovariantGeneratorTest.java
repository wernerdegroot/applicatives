package nl.wernerdegroot.applicatives.processor.generator;

import nl.wernerdegroot.applicatives.processor.domain.*;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.joining;
import static nl.wernerdegroot.applicatives.processor.conflicts.Conflicts.*;
import static nl.wernerdegroot.applicatives.processor.domain.type.Type.*;
import static nl.wernerdegroot.applicatives.processor.domain.typeconstructor.TypeConstructor.placeholder;
import static nl.wernerdegroot.applicatives.processor.generator.CovariantGenerator.generator;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CovariantGeneratorTest implements GeneratorTest {

    @Test
    public void givenOptional() throws IOException {
        String expected = getResourceFileAsString("/OptionalsOverloads.java");
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
                .withParameterTypeConstructorArguments(PARAMETER_TYPE_CONSTRUCTOR_ARGUMENTS)
                .withReturnTypeConstructorArgument(RETURN_TYPE_CONSTRUCTOR_ARGUMENT)
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
    }

    @Test
    public void givenCompletableFutures() throws IOException {
        String expected = getResourceFileAsString("/CompletableFuturesOverloads.java");
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
                .withParameterTypeConstructorArguments(PARAMETER_TYPE_CONSTRUCTOR_ARGUMENTS)
                .withReturnTypeConstructorArgument(RETURN_TYPE_CONSTRUCTOR_ARGUMENT)
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
    }

    @Test
    public void givenMap() throws IOException {
        TypeParameterName K = TypeParameterName.of("K");

        String expected = getResourceFileAsString("/MapsOverloads.java");
        String toVerify = generator()
                .withPackageName(PackageName.of("nl.wernerdegroot.applicatives"))
                .withClassNameToGenerate("MapsOverloads")
                .withClassTypeParameters(singletonList(K.extending(COMPARABLE.with(K.asType().contravariant()))))
                .withOptionalInitializer(
                        Optional.of(
                                Initializer.of(
                                        "initialize",
                                        MAP.with(K.asTypeConstructor().contravariant(), placeholder().covariant()),
                                        TREE_MAP.with(K.asTypeConstructor().contravariant(), placeholder().contravariant())
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
                .withParameterTypeConstructorArguments(PARAMETER_TYPE_CONSTRUCTOR_ARGUMENTS)
                .withReturnTypeConstructorArgument(RETURN_TYPE_CONSTRUCTOR_ARGUMENT)
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
    }

    @Test
    public void givenList() throws IOException {
        String expected = getResourceFileAsString("/ListsOverloads.java");
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
                .withParameterTypeConstructorArguments(PARAMETER_TYPE_CONSTRUCTOR_ARGUMENTS)
                .withReturnTypeConstructorArgument(RETURN_TYPE_CONSTRUCTOR_ARGUMENT)
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
    }

    @Test
    public void givenFunction() throws IOException {
        TypeParameterName P = TypeParameterName.of("P");

        String expected = getResourceFileAsString("/ResultsOverloads.java");
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
                .withParameterTypeConstructorArguments(PARAMETER_TYPE_CONSTRUCTOR_ARGUMENTS)
                .withReturnTypeConstructorArgument(RETURN_TYPE_CONSTRUCTOR_ARGUMENT)
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
    }
}
