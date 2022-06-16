package nl.wernerdegroot.applicatives.processor.generator;

import nl.wernerdegroot.applicatives.processor.domain.ContravariantAccumulator;
import nl.wernerdegroot.applicatives.processor.domain.PackageName;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.joining;
import static nl.wernerdegroot.applicatives.processor.conflicts.Conflicts.*;
import static nl.wernerdegroot.applicatives.processor.domain.type.Type.COMPARATOR;
import static nl.wernerdegroot.applicatives.processor.domain.typeconstructor.TypeConstructor.placeholder;
import static nl.wernerdegroot.applicatives.processor.generator.ContravariantGenerator.generator;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ContravariantGeneratorTest {

    @Test
    public void givenComparator() throws IOException {
        String expected = getResourceFileAsString("/Comparators.generated");
        String toVerify = generator()
                .withPackageName(PackageName.of("nl.wernerdegroot.applicatives"))
                .withClassNameToGenerate("ComparatorsOverloads")
                .withClassTypeParameters(emptyList())
                .withParameterTypeConstructorArguments(PARAMETER_TYPE_CONSTRUCTOR_ARGUMENTS)
                .withIntermediateTypeConstructorArgument(INTERMEDIATE_TYPE_CONSTRUCTOR_ARGUMENT)
                .withReturnTypeConstructorArgument(RETURN_TYPE_CONSTRUCTOR_ARGUMENT)
                .withAccumulator(
                        ContravariantAccumulator.of(
                                "whatever",
                                COMPARATOR.with(placeholder().contravariant()),
                                COMPARATOR.with(placeholder().contravariant()),
                                COMPARATOR.with(placeholder().invariant())
                        )
                )
                .withInputParameterNames(INPUT_PARAMETER_NAMES)
                .withDecompositionParameterName(DECOMPOSITION_PARAMETER_NAME)
                .withSelfParameterName(SELF_PARAMETER_NAME)
                .withToIntermediateParameterName(TO_INTERMEDIATE_PARAMETER_NAME)
                .withExtractLeftParameterName(EXTRACT_LEFT_PARAMETER_NAME)
                .withExtractRightParameterName(EXTRACT_RIGHT_PARAMETER_NAME)
                .withCombineMethodName("combine")
                .withMaxArity(4)
                .generate();

        assertEquals(expected, toVerify);
    }

    private static String getResourceFileAsString(String fileName) throws IOException {
        try (InputStream is = CovariantGeneratorTest.class.getResourceAsStream(fileName)) {
            if (is == null) {
                throw new NullPointerException();
            }

            try (InputStreamReader isr = new InputStreamReader(is); BufferedReader reader = new BufferedReader(isr)) {
                return reader.lines().collect(joining("\n"));
            }
        }
    }
}
