package nl.wernerdegroot.applicatives.processor;

import nl.wernerdegroot.applicatives.processor.domain.*;
import nl.wernerdegroot.applicatives.processor.domain.containing.ContainingClass;
import nl.wernerdegroot.applicatives.processor.validation.Validator;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static nl.wernerdegroot.applicatives.processor.domain.type.Type.*;
import static nl.wernerdegroot.applicatives.processor.domain.typeconstructor.TypeConstructor.placeholder;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ContravariantProcessorTemplateTest implements VarianceProcessorTemplateTest {

    ContravariantProcessorTemplate contravariantProcessorTemplate = new ContravariantProcessorTemplate() {
    };

    @Test
    public void givenComparator() throws IOException {
        String expected = getResourceFileAsString("ComparatorsOverloads");
        String toVerify = contravariantProcessorTemplate.generate(
                ContainingClass.of(
                        PackageName.of("nl.wernerdegroot.applicatives"),
                        ClassName.of("Comparators")
                ),
                "ComparatorsOverloads",
                "combine",
                "lift",
                4,
                Validator.Result.of(
                        emptyList(),
                        Optional.empty(),
                        Accumulator.of(
                                "combineImpl",
                                COMPARATOR.with(placeholder().contravariant()),
                                COMPARATOR.with(placeholder().contravariant()),
                                COMPARATOR.with(placeholder().invariant())
                        ),
                        Optional.empty()
                )
        );

        assertEquals(expected, toVerify);

        ensureResourceFileCompiles("ComparatorsOverloads");
    }

    @Test
    public void givenFunction() throws IOException {
        TypeParameterName C1 = TypeParameterName.of("C1");

        String expected = getResourceFileAsString("ParametersOverloads");
        String toVerify = contravariantProcessorTemplate.generate(
                ContainingClass.of(
                        PackageName.of("nl.wernerdegroot.applicatives"),
                        ClassName.of("Parameters")
                ),
                "ParametersOverloads",
                "combine",
                "lift",
                4,
                Validator.Result.of(
                        singletonList(C1.asTypeParameter()),
                        Optional.of(
                                Initializer.of(
                                        "initialize",
                                        FUNCTION.with(placeholder().contravariant(), STRING.asTypeConstructor().covariant()),
                                        FUNCTION.with(placeholder().invariant(), STRING_BUILDER.asTypeConstructor().invariant())
                                )
                        ),
                        Accumulator.of(
                                "combineImpl",
                                FUNCTION.with(placeholder().contravariant(), STRING.asTypeConstructor().covariant()),
                                FUNCTION.with(placeholder().contravariant(), STRING_BUILDER.asTypeConstructor().covariant()),
                                FUNCTION.with(placeholder().invariant(), STRING_BUILDER.asTypeConstructor().invariant())
                        ),
                        Optional.empty()
                )
        );

        assertEquals(expected, toVerify);

        ensureResourceFileCompiles("ParametersOverloads");
    }

    @Test
    public void givenPredicate() throws IOException {
        String expected = getResourceFileAsString("PredicatesOverloads");
        String toVerify = contravariantProcessorTemplate.generate(
                ContainingClass.of(
                        PackageName.of("nl.wernerdegroot.applicatives"),
                        ClassName.of("Predicates")
                ),
                "PredicatesOverloads",
                "combine",
                "lift",
                4,
                Validator.Result.of(
                        emptyList(),
                        Optional.empty(),
                        Accumulator.of(
                                "combineImpl",
                                PREDICATE.with(placeholder().contravariant()),
                                FUNCTION.with(placeholder().contravariant(), BOOLEAN.asTypeConstructor().covariant()),
                                FUNCTION.with(placeholder().invariant(), BOOLEAN.asTypeConstructor().covariant())
                        ),
                        Optional.of(
                                Finalizer.of(
                                        "finalize",
                                        FUNCTION.with(placeholder().contravariant(), BOOLEAN.asTypeConstructor().covariant()),
                                        PREDICATE.with(placeholder().invariant())
                                )
                        )
                )
        );

        assertEquals(expected, toVerify);

        ensureResourceFileCompiles("PredicatesOverloads");
    }
}
