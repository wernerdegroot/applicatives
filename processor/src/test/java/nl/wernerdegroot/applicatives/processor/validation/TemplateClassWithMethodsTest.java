package nl.wernerdegroot.applicatives.processor.validation;

import nl.wernerdegroot.applicatives.processor.domain.*;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static nl.wernerdegroot.applicatives.processor.domain.type.Type.COMPARABLE;
import static nl.wernerdegroot.applicatives.processor.domain.typeconstructor.TypeConstructor.placeholder;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TemplateClassWithMethodsTest {

    private final TypeParameterName A = TypeParameterName.of("A");
    private final TypeParameterName B = TypeParameterName.of("B");

    private static final TypeBuilder ERUDITE = new TypeBuilder(FullyQualifiedName.of("nl.wernerdegroot.Erudite"));
    private static final TypeBuilder PROFUSE = new TypeBuilder(FullyQualifiedName.of("nl.wernerdegroot.Profuse"));
    private static final TypeBuilder ARDUOUS = new TypeBuilder(FullyQualifiedName.of("nl.wernerdegroot.Arduous"));

    @Test
    public void replaceTypeParameterNamesWithNonEmptyOptionals() {
        Map<TypeParameterName, TypeParameterName> replacements = new HashMap<>();
        replacements.put(A, B);
        replacements.put(B, A);

        CovariantValidator.Result templateClassWithMethods = CovariantValidator.Result.of(
                asList(A.extending(COMPARABLE.with(B)), B.asTypeParameter()),
                Optional.of(
                        CovariantInitializer.of(
                                "initializer",
                                ARDUOUS.with(A.asTypeConstructor().covariant(), placeholder().invariant(), B.asTypeConstructor().contravariant()),
                                ERUDITE.with(A.asTypeConstructor().invariant(), placeholder().invariant(), B.asTypeConstructor().invariant())
                        )
                ),
                CovariantAccumulator.of(
                        "accumulator",
                        PROFUSE.with(A.asTypeConstructor().covariant(), placeholder().invariant(), B.asTypeConstructor().contravariant()),
                        ERUDITE.with(A.asTypeConstructor().covariant(), placeholder().invariant(), B.asTypeConstructor().contravariant()),
                        ERUDITE.with(A.asTypeConstructor().invariant(), placeholder().invariant(), B.asTypeConstructor().invariant())
                ),
                Optional.of(
                        CovariantFinalizer.of(
                                "finalizer",
                                ERUDITE.with(A.asTypeConstructor().covariant(), placeholder().invariant(), B.asTypeConstructor().contravariant()),
                                ARDUOUS.with(A.asTypeConstructor().invariant(), placeholder().invariant(), B.asTypeConstructor().invariant())
                        )
                )
        );

        CovariantValidator.Result expected = CovariantValidator.Result.of(
                asList(B.extending(COMPARABLE.with(A)), A.asTypeParameter()),
                Optional.of(
                        CovariantInitializer.of(
                                "initializer",
                                ARDUOUS.with(B.asTypeConstructor().covariant(), placeholder().invariant(), A.asTypeConstructor().contravariant()),
                                ERUDITE.with(B.asTypeConstructor().invariant(), placeholder().invariant(), A.asTypeConstructor().invariant())
                        )
                ),
                CovariantAccumulator.of(
                        "accumulator",
                        PROFUSE.with(B.asTypeConstructor().covariant(), placeholder().invariant(), A.asTypeConstructor().contravariant()),
                        ERUDITE.with(B.asTypeConstructor().covariant(), placeholder().invariant(), A.asTypeConstructor().contravariant()),
                        ERUDITE.with(B.asTypeConstructor().invariant(), placeholder().invariant(), A.asTypeConstructor().invariant())
                ),
                Optional.of(
                        CovariantFinalizer.of(
                                "finalizer",
                                ERUDITE.with(B.asTypeConstructor().covariant(), placeholder().invariant(), A.asTypeConstructor().contravariant()),
                                ARDUOUS.with(B.asTypeConstructor().invariant(), placeholder().invariant(), A.asTypeConstructor().invariant())
                        )
                )
        );

        CovariantValidator.Result toVerify = templateClassWithMethods.replaceTypeParameterNames(replacements);

        assertEquals(expected, toVerify);
    }

    @Test
    public void replaceTypeParameterNamesWithEmptyOptionals() {
        Map<TypeParameterName, TypeParameterName> replacements = new HashMap<>();
        replacements.put(A, B);
        replacements.put(B, A);

        CovariantValidator.Result templateClassWithMethods = CovariantValidator.Result.of(
                emptyList(),
                Optional.empty(),
                CovariantAccumulator.of(
                        "accumulator",
                        PROFUSE.with(A.asTypeConstructor().covariant(), placeholder().invariant(), B.asTypeConstructor().contravariant()),
                        ERUDITE.with(A.asTypeConstructor().covariant(), placeholder().invariant(), B.asTypeConstructor().contravariant()),
                        ERUDITE.with(A.asTypeConstructor().invariant(), placeholder().invariant(), B.asTypeConstructor().invariant())
                ),
                Optional.empty()
        );

        CovariantValidator.Result expected = CovariantValidator.Result.of(
                emptyList(),
                Optional.empty(),
                CovariantAccumulator.of(
                        "accumulator",
                        PROFUSE.with(B.asTypeConstructor().covariant(), placeholder().invariant(), A.asTypeConstructor().contravariant()),
                        ERUDITE.with(B.asTypeConstructor().covariant(), placeholder().invariant(), A.asTypeConstructor().contravariant()),
                        ERUDITE.with(B.asTypeConstructor().invariant(), placeholder().invariant(), A.asTypeConstructor().invariant())
                ),
                Optional.empty()
        );

        CovariantValidator.Result toVerify = templateClassWithMethods.replaceTypeParameterNames(replacements);

        assertEquals(expected, toVerify);
    }
}
