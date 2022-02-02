package nl.wernerdegroot.applicatives.processor.generator;

import nl.wernerdegroot.applicatives.processor.domain.FullyQualifiedName;
import nl.wernerdegroot.applicatives.processor.domain.PackageName;
import nl.wernerdegroot.applicatives.processor.domain.Parameter;
import nl.wernerdegroot.applicatives.processor.domain.TypeParameterName;
import nl.wernerdegroot.applicatives.processor.domain.typeconstructor.TypeConstructor;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.joining;
import static nl.wernerdegroot.applicatives.processor.conflicts.Conflicts.*;
import static nl.wernerdegroot.applicatives.processor.domain.BoundType.EXTENDS;
import static nl.wernerdegroot.applicatives.processor.domain.BoundType.SUPER;
import static nl.wernerdegroot.applicatives.processor.domain.type.Type.*;
import static nl.wernerdegroot.applicatives.processor.domain.typeconstructor.TypeConstructor.placeholder;
import static nl.wernerdegroot.applicatives.processor.generator.Generator.generator;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GeneratorTest {

    // Options:
    //  * Class type parameters (Y/N)
    //  * Secondary method type parameters (Y/N)
    //  * Secondary parameters (Y/N)
    //  * Provided class name
    //  * Provided lift method name
    //  * Provided maximum arity
    //
    // Instead of testing each combination (which would be a lot of tests)
    // we only test the following cases:
    //
    // |           | Class type | Secondary method | Secondary  | Provided       | Provided lift | Provided   |
    // |           | parameters | type parameters  | parameters | class name     | method name   | max. arity |
    // |-----------|------------|------------------|------------|----------------|---------------|------------|
    // | Optionals | N          | N                | N          | OptionalsMixin | lift          | 2          |
    // | Functions | Y          | N                | N          | FunctionsMixin | lift          | 4          |
    // | Eithers   | N          | Y                | Y          | EithersMixin   | liftEither    | 26         |

    @Test
    public void simple() throws IOException {
        String expected = getResourceFileAsString("/Optionals.generated");
        String toVerify = generator()
                .withPackageName(PackageName.of("nl.wernerdegroot.applicatives"))
                .withClassNameToGenerate("OptionalsMixin")
                .withClassTypeParameters(emptyList())
                .withPrimaryMethodTypeParameters(PRIMARY_METHOD_TYPE_PARAMETERS)
                .withResultTypeParameter(RESULT_TYPE_PARAMETER)
                .withSecondaryMethodTypeParameters(emptyList())
                .withMethodName("compose")
                .withPrimaryParameterNames(PRIMARY_PARAMETER_NAMES)
                .withSecondaryParameters(emptyList())
                .withSelfParameterName(SELF_PARAMETER_NAME)
                .withCombinatorParameterName(COMBINATOR_PARAMETER_NAME)
                .withMaxTupleSizeParameterName(MAX_TUPLE_SIZE_PARAMETER_NAME)
                .withParameterTypeConstructor(OPTIONAL.of(EXTENDS.type(placeholder())))
                .withResultTypeConstructor(OPTIONAL.of(placeholder()))
                .withLiftMethodName("lift")
                .withMaxArity(2)
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
                .withPrimaryMethodTypeParameters(PRIMARY_METHOD_TYPE_PARAMETERS)
                .withResultTypeParameter(RESULT_TYPE_PARAMETER)
                .withSecondaryMethodTypeParameters(emptyList())
                .withMethodName("compose")
                .withPrimaryParameterNames(PRIMARY_PARAMETER_NAMES)
                .withSecondaryParameters(emptyList())
                .withSelfParameterName(SELF_PARAMETER_NAME)
                .withCombinatorParameterName(COMBINATOR_PARAMETER_NAME)
                .withMaxTupleSizeParameterName(MAX_TUPLE_SIZE_PARAMETER_NAME)
                .withParameterTypeConstructor(FUNCTION.of(P.asTypeConstructor(), placeholder()))
                .withResultTypeConstructor(FUNCTION.of(P.asTypeConstructor(), placeholder()))
                .withLiftMethodName("lift")
                .withMaxArity(4)
                .generate();

        assertEquals(expected, toVerify);
    }

    @Test
    public void withSecondaryTypeParametersAndSecondaryParameters() throws IOException {
        TypeParameterName P = TypeParameterName.of("P");
        TypeConstructor EITHER = TypeConstructor.concrete(
                FullyQualifiedName.of("nl.wernerdegroot.applicatives.Either"),
                P.asTypeConstructor(),
                placeholder()
        );

        String expected = getResourceFileAsString("/Eithers.generated");
        String toVerify = generator()
                .withPackageName(PackageName.of("nl.wernerdegroot.applicatives"))
                .withClassNameToGenerate("EithersMixin")
                .withClassTypeParameters(emptyList())
                .withPrimaryMethodTypeParameters(PRIMARY_METHOD_TYPE_PARAMETERS)
                .withResultTypeParameter(RESULT_TYPE_PARAMETER)
                .withSecondaryMethodTypeParameters(asList(P.extending(OBJECT)))
                .withMethodName("compose")
                .withPrimaryParameterNames(PRIMARY_PARAMETER_NAMES)
                .withSecondaryParameters(asList(Parameter.of(BI_FUNCTION.of(SUPER.type(P), SUPER.type(P), EXTENDS.type(P)), "composeLeft")))
                .withSelfParameterName(SELF_PARAMETER_NAME)
                .withCombinatorParameterName(COMBINATOR_PARAMETER_NAME)
                .withMaxTupleSizeParameterName(MAX_TUPLE_SIZE_PARAMETER_NAME)
                .withParameterTypeConstructor(EITHER)
                .withResultTypeConstructor(EITHER)
                .withLiftMethodName("liftEither")
                .withMaxArity(26)
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
