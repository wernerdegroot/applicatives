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

public class CovariantProcessorTemplateTest implements VarianceProcessorTemplateTest {

    CovariantProcessorTemplate covariantProcessorTemplate = new CovariantProcessorTemplate() {
    };

    @Test
    public void givenOptional() throws IOException {
        String expected = getResourceClassFileAsString("OptionalsOverloads");
        String toVerify = covariantProcessorTemplate.generate(
                ContainingClass.of(
                        PackageName.of("nl.wernerdegroot.applicatives"),
                        ClassName.of("Optionals")
                ),
                "OptionalsOverloads",
                "combine",
                "lift",
                2,
                Validator.Result.of(
                        emptyList(),
                        Optional.empty(),
                        Accumulator.of(
                                "combine",
                                OPTIONAL.with(placeholder().covariant()),
                                OPTIONAL.with(placeholder().covariant()),
                                OPTIONAL.with(placeholder().invariant())
                        ),
                        Optional.empty()
                )
        );

        assertEquals(expected, toVerify);

        ensureResourceFileCompiles("OptionalsOverloads");
    }

    @Test
    public void givenCompletableFutures() throws IOException {
        String expected = getResourceClassFileAsString("CompletableFuturesOverloads");
        String toVerify = covariantProcessorTemplate.generate(
                ContainingClass.of(
                        PackageName.of("nl.wernerdegroot.applicatives"),
                        ClassName.of("CompletableFutures")
                ),
                "CompletableFuturesOverloads",
                "combine",
                "lift",
                2,
                Validator.Result.of(
                        emptyList(),
                        Optional.empty(),
                        Accumulator.of(
                                "combineImpl",
                                COMPLETABLE_FUTURE.with(placeholder().covariant()),
                                COMPLETABLE_FUTURE.with(placeholder().covariant()),
                                COMPLETABLE_FUTURE.with(placeholder().invariant())
                        ),
                        Optional.empty()
                )
        );

        assertEquals(expected, toVerify);

        ensureResourceFileCompiles("CompletableFuturesOverloads");
    }

    @Test
    public void givenMap() throws IOException {
        TypeParameterName K = TypeParameterName.of("K");

        String expected = getResourceClassFileAsString("MapsOverloads");
        String toVerify = covariantProcessorTemplate.generate(
                ContainingClass.of(
                        PackageName.of("nl.wernerdegroot.applicatives"),
                        ClassName.of("Maps")
                ),
                "MapsOverloads",
                "combine",
                "lift",
                3,
                Validator.Result.of(
                        singletonList(K.extending(COMPARABLE.with(K.asType().contravariant()))),
                        Optional.of(
                                Initializer.of(
                                        "initialize",
                                        MAP.with(K.asTypeConstructor().contravariant(), placeholder().covariant()),
                                        TREE_MAP.with(K.asTypeConstructor().contravariant(), placeholder().invariant())
                                )
                        ),
                        Accumulator.of(
                                "combineImpl",
                                MAP.with(K.asTypeConstructor().contravariant(), placeholder().covariant()),
                                TREE_MAP.with(K.asTypeConstructor().contravariant(), placeholder().covariant()),
                                TREE_MAP.with(K.asTypeConstructor().contravariant(), placeholder().invariant())
                        ),
                        Optional.empty()
                )
        );

        assertEquals(expected, toVerify);

        ensureResourceFileCompiles("MapsOverloads");
    }

    @Test
    public void givenList() throws IOException {
        String expected = getResourceClassFileAsString("ListsOverloads");
        String toVerify = covariantProcessorTemplate.generate(
                ContainingClass.of(
                        PackageName.of("nl.wernerdegroot.applicatives"),
                        ClassName.of("Lists")
                ),
                "ListsOverloads",
                "combine",
                "lift",
                3,
                Validator.Result.of(
                        emptyList(),
                        Optional.empty(),
                        Accumulator.of(
                                "combineImpl",
                                LIST.with(placeholder().covariant()),
                                ARRAY_LIST.with(placeholder().covariant()),
                                ARRAY_LIST.with(placeholder().invariant())
                        ),
                        Optional.of(
                                Finalizer.of(
                                        "finalize",
                                        ARRAY_LIST.with(placeholder().covariant()),
                                        LIST.with(placeholder().invariant())
                                )
                        )
                )
        );

        assertEquals(expected, toVerify);

        ensureResourceFileCompiles("ListsOverloads");
    }

    @Test
    public void givenFunction() throws IOException {
        TypeParameterName P = TypeParameterName.of("P");

        String expected = getResourceClassFileAsString("ResultsOverloads");
        String toVerify = covariantProcessorTemplate.generate(
                ContainingClass.of(
                        PackageName.of("nl.wernerdegroot.applicatives"),
                        ClassName.of("Results")
                ),
                "ResultsOverloads",
                "combine",
                "lift",
                4,
                Validator.Result.of(
                        singletonList(P.extending(OBJECT)),
                        Optional.empty(),
                        Accumulator.of(
                                "combineImpl",
                                FUNCTION.with(P.asTypeConstructor().invariant(), placeholder().invariant()),
                                FUNCTION.with(P.asTypeConstructor().invariant(), placeholder().invariant()),
                                FUNCTION.with(P.asTypeConstructor().invariant(), placeholder().invariant())
                        ),
                        Optional.empty()
                )
        );

        assertEquals(expected, toVerify);

        ensureResourceFileCompiles("ResultsOverloads");
    }
}
