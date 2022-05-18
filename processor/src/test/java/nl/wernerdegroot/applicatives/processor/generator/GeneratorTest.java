package nl.wernerdegroot.applicatives.processor.generator;

import nl.wernerdegroot.applicatives.processor.domain.PackageName;
import nl.wernerdegroot.applicatives.processor.domain.TypeParameterName;
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
                .withOptionalInitializerMethodName(Optional.empty())
                .withOptionalInitializedTypeConstructor(Optional.empty())
                .withAccumulatorMethodName("compose")
                .withInputTypeConstructor(OPTIONAL.with(placeholder().covariant()))
                .withPartiallyAccumulatedTypeConstructor(OPTIONAL.with(placeholder().covariant()))
                .withAccumulatedTypeConstructor(OPTIONAL.with(placeholder().invariant()))
                .withOptionalFinalizerMethodName(Optional.empty())
                .withOptionalToFinalizeTypeConstructor(Optional.empty())
                .withOptionalFinalizedTypeConstructor(Optional.empty())
                .withInputParameterNames(INPUT_PARAMETER_NAMES)
                .withValueParameterName(VALUE_PARAMETER_NAME)
                .withSelfParameterName(SELF_PARAMETER_NAME)
                .withCombinatorParameterName(COMBINATOR_PARAMETER_NAME)
                .withMaxTupleSizeParameterName(MAX_TUPLE_SIZE_PARAMETER_NAME)
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
                .withOptionalInitializerMethodName(Optional.of("singleton"))
                .withOptionalInitializedTypeConstructor(Optional.of(ARRAY_LIST.asTypeConstructor()))
                .withAccumulatorMethodName("compose")
                .withInputTypeConstructor(LIST.with(placeholder().covariant()))
                .withPartiallyAccumulatedTypeConstructor(ARRAY_LIST.with(placeholder().covariant()))
                .withAccumulatedTypeConstructor(ARRAY_LIST.with(placeholder().invariant()))
                .withOptionalFinalizerMethodName(Optional.of("finalize"))
                .withOptionalToFinalizeTypeConstructor(Optional.of(ARRAY_LIST.with(placeholder().covariant())))
                .withOptionalFinalizedTypeConstructor(Optional.of(LIST.with(placeholder().invariant())))
                .withInputParameterNames(INPUT_PARAMETER_NAMES)
                .withValueParameterName(VALUE_PARAMETER_NAME)
                .withSelfParameterName(SELF_PARAMETER_NAME)
                .withCombinatorParameterName(COMBINATOR_PARAMETER_NAME)
                .withMaxTupleSizeParameterName(MAX_TUPLE_SIZE_PARAMETER_NAME)
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
                .withOptionalInitializerMethodName(Optional.empty())
                .withOptionalInitializedTypeConstructor(Optional.empty())
                .withAccumulatorMethodName("compose")
                .withInputTypeConstructor(FUNCTION.with(P.asTypeConstructor().invariant(), placeholder().invariant()))
                .withPartiallyAccumulatedTypeConstructor(FUNCTION.with(P.asTypeConstructor().invariant(), placeholder().invariant()))
                .withAccumulatedTypeConstructor(FUNCTION.with(P.asTypeConstructor().invariant(), placeholder().invariant()))
                .withOptionalFinalizerMethodName(Optional.empty())
                .withOptionalToFinalizeTypeConstructor(Optional.empty())
                .withOptionalFinalizedTypeConstructor(Optional.empty())
                .withInputParameterNames(INPUT_PARAMETER_NAMES)
                .withValueParameterName(VALUE_PARAMETER_NAME)
                .withSelfParameterName(SELF_PARAMETER_NAME)
                .withCombinatorParameterName(COMBINATOR_PARAMETER_NAME)
                .withMaxTupleSizeParameterName(MAX_TUPLE_SIZE_PARAMETER_NAME)
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
