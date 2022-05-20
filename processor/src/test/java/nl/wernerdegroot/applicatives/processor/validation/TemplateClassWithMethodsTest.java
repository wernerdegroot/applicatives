package nl.wernerdegroot.applicatives.processor.validation;

import nl.wernerdegroot.applicatives.processor.domain.FullyQualifiedName;
import nl.wernerdegroot.applicatives.processor.domain.TypeBuilder;
import nl.wernerdegroot.applicatives.processor.domain.TypeParameterName;
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

        TemplateClassWithMethods templateClassWithMethods = TemplateClassWithMethods.of(
                asList(A.extending(COMPARABLE.with(B)), B.asTypeParameter()),
                Optional.of("initializer"),
                Optional.of(ERUDITE.with(A.asTypeConstructor().invariant(), placeholder().invariant(), B.asTypeConstructor().invariant())),
                "accumulator",
                PROFUSE.with(A.asTypeConstructor().covariant(), placeholder().invariant(), B.asTypeConstructor().contravariant()),
                ERUDITE.with(A.asTypeConstructor().covariant(), placeholder().invariant(), B.asTypeConstructor().contravariant()),
                ERUDITE.with(A.asTypeConstructor().invariant(), placeholder().invariant(), B.asTypeConstructor().invariant()),
                Optional.of("finalizer"),
                Optional.of(ERUDITE.with(A.asTypeConstructor().covariant(), placeholder().invariant(), B.asTypeConstructor().contravariant())),
                Optional.of(ARDUOUS.with(A.asTypeConstructor().invariant(), placeholder().invariant(), B.asTypeConstructor().invariant()))
        );

        TemplateClassWithMethods expected = TemplateClassWithMethods.of(
                asList(B.extending(COMPARABLE.with(A)), A.asTypeParameter()),
                Optional.of("initializer"),
                Optional.of(ERUDITE.with(B.asTypeConstructor().invariant(), placeholder().invariant(), A.asTypeConstructor().invariant())),
                "accumulator",
                PROFUSE.with(B.asTypeConstructor().covariant(), placeholder().invariant(), A.asTypeConstructor().contravariant()),
                ERUDITE.with(B.asTypeConstructor().covariant(), placeholder().invariant(), A.asTypeConstructor().contravariant()),
                ERUDITE.with(B.asTypeConstructor().invariant(), placeholder().invariant(), A.asTypeConstructor().invariant()),
                Optional.of("finalizer"),
                Optional.of(ERUDITE.with(B.asTypeConstructor().covariant(), placeholder().invariant(), A.asTypeConstructor().contravariant())),
                Optional.of(ARDUOUS.with(B.asTypeConstructor().invariant(), placeholder().invariant(), A.asTypeConstructor().invariant()))
        );

        TemplateClassWithMethods toVerify = templateClassWithMethods.replaceTypeParameterNames(replacements);

        assertEquals(expected, toVerify);
    }

    @Test
    public void replaceTypeParameterNamesWithEmptyOptionals() {
        Map<TypeParameterName, TypeParameterName> replacements = new HashMap<>();
        replacements.put(A, B);
        replacements.put(B, A);

        TemplateClassWithMethods templateClassWithMethods = TemplateClassWithMethods.of(
                emptyList(),
                Optional.empty(),
                Optional.empty(),
                "accumulator",
                PROFUSE.with(A.asTypeConstructor().covariant(), placeholder().invariant(), B.asTypeConstructor().contravariant()),
                ERUDITE.with(A.asTypeConstructor().covariant(), placeholder().invariant(), B.asTypeConstructor().contravariant()),
                ERUDITE.with(A.asTypeConstructor().invariant(), placeholder().invariant(), B.asTypeConstructor().invariant()),
                Optional.empty(),
                Optional.empty(),
                Optional.empty()
        );

        TemplateClassWithMethods expected = TemplateClassWithMethods.of(
                emptyList(),
                Optional.empty(),
                Optional.empty(),
                "accumulator",
                PROFUSE.with(B.asTypeConstructor().covariant(), placeholder().invariant(), A.asTypeConstructor().contravariant()),
                ERUDITE.with(B.asTypeConstructor().covariant(), placeholder().invariant(), A.asTypeConstructor().contravariant()),
                ERUDITE.with(B.asTypeConstructor().invariant(), placeholder().invariant(), A.asTypeConstructor().invariant()),
                Optional.empty(),
                Optional.empty(),
                Optional.empty()
        );

        TemplateClassWithMethods toVerify = templateClassWithMethods.replaceTypeParameterNames(replacements);

        assertEquals(expected, toVerify);
    }
}
