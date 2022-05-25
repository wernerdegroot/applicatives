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
import static java.util.stream.Collectors.joining;
import static nl.wernerdegroot.applicatives.processor.conflicts.Conflicts.*;
import static nl.wernerdegroot.applicatives.processor.domain.type.Type.*;
import static nl.wernerdegroot.applicatives.processor.domain.typeconstructor.TypeConstructor.placeholder;
import static nl.wernerdegroot.applicatives.processor.generator.Generator.generator;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GeneratorTest {

    // @TODO FIX ME
    // Options:
    //  * Class type parameters (Y/N)
    //  * Secondary method type parameters (Y/N)
    //  * Secondary parameters (Y/N)
    //  * Different type constructors (Y/N)
    //  * Provided class name
    //  * Provided lift method name
    //  * Provided maximum arity
    //
    // Instead of testing each combination (which would be a lot of tests)
    // we only test the following cases:
    //
    // |           | Class type | Secondary method | Secondary  | Provided       | Provided lift | Provided   | Diffent type |
    // |           | parameters | type parameters  | parameters | class name     | method name   | max. arity | constructors |
    // |-----------|------------|------------------|------------|----------------|---------------|------------|--------------|
    // | Optionals | N          | N                | N          | OptionalsMixin | lift          | 2          | N            |
    // | Lists     | N          | N                | N          | ListsMixin     | lift          | 3          | Y            |
    // | Functions | Y          | N                | N          | FunctionsMixin | lift          | 4          | N            |
    // | Eithers   | N          | Y                | Y          | EithersMixin   | liftEither    | 26         | N            |

    @Test
    public void simple() throws IOException {
        String expected = getResourceFileAsString("/Optionals.generated");
        String toVerify = generator()
                .withPackageName(PackageName.of("nl.wernerdegroot.applicatives"))
                .withClassNameToGenerate("OptionalsMixin")
                .withClassTypeParameters(emptyList())
                .withParameterTypeConstructorArguments(PARAMETER_TYPE_CONSTRUCTOR_ARGUMENTS)
                .withReturnTypeConstructorArgument(RETURN_TYPE_CONSTRUCTOR_ARGUMENT)
                .withOptionalInitializer(Optional.empty())
                .withAccumulator(
                        CovariantAccumulator.of(
                                "compose",
                                OPTIONAL.with(placeholder().covariant()),
                                OPTIONAL.with(placeholder().covariant()),
                                OPTIONAL.with(placeholder().invariant())
                        )
                )
                .withOptionalFinalizer(Optional.empty())
                .withInputParameterNames(INPUT_PARAMETER_NAMES)
                .withValueParameterName(VALUE_PARAMETER_NAME)
                .withSelfParameterName(SELF_PARAMETER_NAME)
                .withCombinatorParameterName(COMBINATOR_PARAMETER_NAME)
                .withMaxTupleSizeParameterName(MAX_TUPLE_SIZE_PARAMETER_NAME)
                .withTupleParameterName(TUPLE_PARAMETER_NAME)
                .withElementParameterName(ELEMENT_PARAMETER_NAME)
                .withLiftMethodName("lift")
                .withMaxArity(2)
                .generate();

        assertEquals(expected, toVerify);
    }

    @Test
    public void withDifferentLeftTypeConstructorAndRightTypeConstructor() throws IOException {
        String expected = getResourceFileAsString("/Lists.generated");
        String toVerify = generator()
                .withPackageName(PackageName.of("nl.wernerdegroot.applicatives"))
                .withClassNameToGenerate("ListsMixin")
                .withClassTypeParameters(emptyList())
                .withParameterTypeConstructorArguments(PARAMETER_TYPE_CONSTRUCTOR_ARGUMENTS)
                .withReturnTypeConstructorArgument(RETURN_TYPE_CONSTRUCTOR_ARGUMENT)
                .withOptionalInitializer(
                        Optional.of(
                                CovariantInitializer.of(
                                        "singleton",
                                        ARRAY_LIST.asTypeConstructor()
                                )
                        )
                )
                .withAccumulator(
                        CovariantAccumulator.of(
                                "compose",
                                LIST.with(placeholder().covariant()),
                                ARRAY_LIST.with(placeholder().covariant()),
                                ARRAY_LIST.with(placeholder().invariant())
                        )
                )
                .withOptionalFinalizer(
                        Optional.of(
                                CovariantFinalizer.of(
                                        "finalize",
                                        ARRAY_LIST.with(placeholder().covariant()),
                                        LIST.with(placeholder().invariant())
                                )
                        )
                )
                .withInputParameterNames(INPUT_PARAMETER_NAMES)
                .withValueParameterName(VALUE_PARAMETER_NAME)
                .withSelfParameterName(SELF_PARAMETER_NAME)
                .withCombinatorParameterName(COMBINATOR_PARAMETER_NAME)
                .withMaxTupleSizeParameterName(MAX_TUPLE_SIZE_PARAMETER_NAME)
                .withTupleParameterName(TUPLE_PARAMETER_NAME)
                .withElementParameterName(ELEMENT_PARAMETER_NAME)
                .withLiftMethodName("lift")
                .withMaxArity(3)
                .generate();

        assertEquals(expected, toVerify);
    }

    @Test
    public void withClassTypeParameters() throws IOException {
        TypeParameterName P = TypeParameterName.of("P");

        String expected = getResourceFileAsString("/Functions.generated");
        String toVerify = generator()
                .withPackageName(PackageName.of("nl.wernerdegroot.applicatives"))
                .withClassNameToGenerate("FunctionsMixin")
                .withClassTypeParameters(asList(P.extending(OBJECT)))
                .withParameterTypeConstructorArguments(PARAMETER_TYPE_CONSTRUCTOR_ARGUMENTS)
                .withReturnTypeConstructorArgument(RETURN_TYPE_CONSTRUCTOR_ARGUMENT)
                .withOptionalInitializer(Optional.empty())
                .withAccumulator(
                        CovariantAccumulator.of(
                                "compose",
                                FUNCTION.with(P.asTypeConstructor().invariant(), placeholder().invariant()),
                                FUNCTION.with(P.asTypeConstructor().invariant(), placeholder().invariant()),
                                FUNCTION.with(P.asTypeConstructor().invariant(), placeholder().invariant())
                        )
                )
                .withOptionalFinalizer(Optional.empty())
                .withInputParameterNames(INPUT_PARAMETER_NAMES)
                .withValueParameterName(VALUE_PARAMETER_NAME)
                .withSelfParameterName(SELF_PARAMETER_NAME)
                .withCombinatorParameterName(COMBINATOR_PARAMETER_NAME)
                .withMaxTupleSizeParameterName(MAX_TUPLE_SIZE_PARAMETER_NAME)
                .withTupleParameterName(TUPLE_PARAMETER_NAME)
                .withElementParameterName(ELEMENT_PARAMETER_NAME)
                .withLiftMethodName("lift")
                .withMaxArity(4)
                .generate();

        assertEquals(expected, toVerify);
    }

    private static String getResourceFileAsString(String fileName) throws IOException {
        try (InputStream is = GeneratorTest.class.getResourceAsStream(fileName)) {
            if (is == null) {
                throw new NullPointerException();
            }

            try (InputStreamReader isr = new InputStreamReader(is); BufferedReader reader = new BufferedReader(isr)) {
                return reader.lines().collect(joining("\n"));
            }
        }
    }
}
