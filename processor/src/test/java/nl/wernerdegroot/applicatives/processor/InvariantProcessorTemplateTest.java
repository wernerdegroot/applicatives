package nl.wernerdegroot.applicatives.processor;

import nl.wernerdegroot.applicatives.processor.domain.*;
import nl.wernerdegroot.applicatives.processor.domain.containing.ContainingClass;
import nl.wernerdegroot.applicatives.processor.validation.Validator;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static nl.wernerdegroot.applicatives.processor.domain.type.Type.*;
import static nl.wernerdegroot.applicatives.processor.domain.typeconstructor.TypeConstructor.placeholder;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class InvariantProcessorTemplateTest implements VarianceProcessorTemplateTest {

    InvariantProcessorTemplate invariantProcessorTemplate = new InvariantProcessorTemplate() {
    };

    @Test
    public void givenUnaryOperator() throws IOException {
        String expected = getResourceClassFileAsString("UnaryOperatorsOverloads");
        String toVerify = invariantProcessorTemplate.generate(
                ContainingClass.of(
                        PackageName.of("nl.wernerdegroot.applicatives"),
                        ClassName.of("UnaryOperators")
                ),
                "UnaryOperatorsOverloads",
                "combine",
                "lift",
                4,
                Validator.Result.of(
                        emptyList(),
                        Optional.empty(),
                        Accumulator.of(
                                "combineImpl",
                                UNARY_OPERATOR.with(placeholder().invariant()),
                                UNARY_OPERATOR.with(placeholder().invariant()),
                                UNARY_OPERATOR.with(placeholder().invariant())
                        ),
                        Optional.empty()
                )
        );

        assertEquals(expected, toVerify);

        ensureResourceFileCompiles("UnaryOperatorsOverloads");
    }

    @Test
    public void givenBinaryOperator() throws IOException {
        String expected = getResourceClassFileAsString("BinaryOperatorsOverloads");
        String toVerify = invariantProcessorTemplate.generate(
                ContainingClass.of(
                        PackageName.of("nl.wernerdegroot.applicatives"),
                        ClassName.of("BinaryOperators")
                ),
                "BinaryOperatorsOverloads",
                "combine",
                "lift",
                4,
                Validator.Result.of(
                        emptyList(),
                        Optional.of(
                                Initializer.of(
                                        "initialize",
                                        BINARY_OPERATOR.with(placeholder().invariant()),
                                        BI_FUNCTION.with(placeholder().invariant(), placeholder().invariant(), placeholder().invariant())
                                )
                        ),
                        Accumulator.of(
                                "combineImpl",
                                BINARY_OPERATOR.with(placeholder().invariant()),
                                BI_FUNCTION.with(placeholder().invariant(), placeholder().invariant(), placeholder().invariant()),
                                BI_FUNCTION.with(placeholder().invariant(), placeholder().invariant(), placeholder().invariant())
                        ),
                        Optional.empty()
                )
        );

        assertEquals(expected, toVerify);

        ensureResourceFileCompiles("BinaryOperatorsOverloads");
    }

    @Test
    public void givenSet() throws IOException {
        String expected = getResourceClassFileAsString("SetsOverloads");
        String toVerify = invariantProcessorTemplate.generate(
                ContainingClass.of(
                        PackageName.of("nl.wernerdegroot.applicatives"),
                        ClassName.of("Sets")
                ),
                "SetsOverloads",
                "combine",
                "lift",
                4,
                Validator.Result.of(
                        emptyList(),
                        Optional.empty(),
                        Accumulator.of(
                                "combineImpl",
                                SET.with(placeholder().invariant()),
                                BIT_SET.asType().asTypeConstructor(),
                                BIT_SET.asType().asTypeConstructor()
                        ),
                        Optional.of(
                                Finalizer.of(
                                        "finalize",
                                        BIT_SET.asType().asTypeConstructor(),
                                        SET.with(placeholder().invariant())
                                )
                        )
                )
        );

        assertEquals(expected, toVerify);

        ensureResourceFileCompiles("SetsOverloads");
    }
}
