package nl.wernerdegroot.applicatives.processor.validation;

import com.jparams.verifier.tostring.ToStringVerifier;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.wernerdegroot.applicatives.processor.domain.Method;
import nl.wernerdegroot.applicatives.processor.domain.Parameter;
import nl.wernerdegroot.applicatives.processor.domain.TypeParameterName;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.toSet;
import static nl.wernerdegroot.applicatives.processor.domain.Modifier.*;
import static nl.wernerdegroot.applicatives.processor.domain.type.Type.*;
import static nl.wernerdegroot.applicatives.processor.domain.typeconstructor.TypeConstructor.placeholder;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AccumulatorValidatorTest {

    private final TypeParameterName T = TypeParameterName.of("T");
    private final TypeParameterName U = TypeParameterName.of("U");
    private final TypeParameterName V = TypeParameterName.of("V");
    private final TypeParameterName P = TypeParameterName.of("P");
    private final TypeParameterName W = TypeParameterName.of("W");

    @Test
    public void validateGivenMethodThatDoesNotReturnAnything() {
        Method toValidate = Method.of(
                emptySet(),
                modifiers(PUBLIC),
                asList(T.asTypeParameter(), U.asTypeParameter(), V.asTypeParameter()),
                Optional.empty(),
                "myFunction",
                asList(
                        Parameter.of(OPTIONAL.with(T), "left"),
                        Parameter.of(OPTIONAL.with(U), "right"),
                        Parameter.of(BI_FUNCTION.with(T.asType().contravariant(), U.asType().contravariant(), V.asType().covariant()), "compose")
                )
        );

        Validated<String, AccumulatorValidator.Result> expected = Validated.invalid("Method needs to return something");
        Validated<String, AccumulatorValidator.Result> toVerify = AccumulatorValidator.validate(toValidate, new CovariantParametersAndTypeParametersValidator());

        assertEquals(expected, toVerify);
    }

    @Test
    public void validateGivenStaticMethod() {
        Method toValidate = Method.of(
                emptySet(),
                modifiers(PUBLIC, STATIC),
                asList(T.asTypeParameter(), U.asTypeParameter(), V.asTypeParameter()),
                Optional.of(OPTIONAL.with(V)),
                "myFunction",
                asList(
                        Parameter.of(OPTIONAL.with(T), "left"),
                        Parameter.of(OPTIONAL.with(U), "right"),
                        Parameter.of(BI_FUNCTION.with(T.asType().contravariant(), U.asType().contravariant(), V.asType().covariant()), "compose")
                )
        );

        Validated<String, AccumulatorValidator.Result> expected = Validated.invalid("Method is static and cannot implement an abstract method");
        Validated<String, AccumulatorValidator.Result> toVerify = AccumulatorValidator.validate(toValidate, new CovariantParametersAndTypeParametersValidator());

        assertEquals(expected, toVerify);
    }

    @Test
    public void validateGivenPrivateMethod() {
        Method toValidate = Method.of(
                emptySet(),
                modifiers(PRIVATE),
                asList(T.asTypeParameter(), U.asTypeParameter(), V.asTypeParameter()),
                Optional.of(OPTIONAL.with(V)),
                "myFunction",
                asList(
                        Parameter.of(OPTIONAL.with(T), "left"),
                        Parameter.of(OPTIONAL.with(U), "right"),
                        Parameter.of(BI_FUNCTION.with(T.asType().contravariant(), U.asType().contravariant(), V.asType().covariant()), "compose")
                )
        );

        Validated<String, AccumulatorValidator.Result> expected = Validated.invalid("Method needs to be public to implement an abstract method");
        Validated<String, AccumulatorValidator.Result> toVerify = AccumulatorValidator.validate(toValidate, new CovariantParametersAndTypeParametersValidator());

        assertEquals(expected, toVerify);
    }

    @Test
    public void validateGivenMethodWithTypeParameterWithUpperBoundOtherThanObject() {
        Method toValidate = Method.of(
                emptySet(),
                modifiers(PUBLIC),
                asList(T.asTypeParameter(), U.extending(COMPARABLE.with(U)), V.asTypeParameter()),
                Optional.of(OPTIONAL.with(V)),
                "myFunction",
                asList(
                        Parameter.of(OPTIONAL.with(T), "left"),
                        Parameter.of(OPTIONAL.with(U), "right"),
                        Parameter.of(BI_FUNCTION.with(T.asType().contravariant(), U.asType().contravariant(), V.asType().covariant()), "compose")
                )
        );

        Validated<String, AccumulatorValidator.Result> expected = Validated.invalid("The type parameters need to be unbounded");
        Validated<String, AccumulatorValidator.Result> toVerify = AccumulatorValidator.validate(toValidate, new CovariantParametersAndTypeParametersValidator());

        assertEquals(expected, toVerify);
    }

    @Test
    public void validateGivenMethodWithTypeParametersThatHaveNoUpperBound() {
        Method toValidate = Method.of(
                emptySet(),
                modifiers(PUBLIC),
                asList(T.asTypeParameter(), U.extending(OBJECT), V.asTypeParameter()),
                Optional.of(OPTIONAL.with(V)),
                "myFunction",
                asList(
                        Parameter.of(OPTIONAL.with(T), "left"),
                        Parameter.of(OPTIONAL.with(U), "right"),
                        Parameter.of(BI_FUNCTION.with(T.asType().contravariant(), U.asType().contravariant(), V.asType().covariant()), "compose")
                )
        );

        Validated<String, AccumulatorValidator.Result> expected = Validated.valid(
                AccumulatorValidator.Result.of(
                        "myFunction",
                        OPTIONAL.asTypeConstructor(),
                        OPTIONAL.asTypeConstructor(),
                        OPTIONAL.asTypeConstructor(),
                        OPTIONAL.with(T),
                        OPTIONAL.with(U),
                        OPTIONAL.with(V)
                )
        );
        Validated<String, AccumulatorValidator.Result> toVerify = AccumulatorValidator.validate(toValidate, new CovariantParametersAndTypeParametersValidator());

        assertEquals(expected, toVerify);
    }

    // Taken as representative. Should prove that the provided `ParametersAndTypeParametersValidator` is called.
    @Test
    public void validateGivenMethodWithThirdParameterThatIsBiFunctionWithWrongTypeArguments() {
        Method toValidate = Method.of(
                emptySet(),
                modifiers(PUBLIC),
                asList(T.asTypeParameter(), U.asTypeParameter(), V.asTypeParameter()),
                Optional.of(OPTIONAL.with(V)),
                "myFunction",
                asList(
                        Parameter.of(OPTIONAL.with(T), "left"),
                        Parameter.of(OPTIONAL.with(U), "right"),
                        Parameter.of(BI_FUNCTION.with(W, W, W), "compose")
                )
        );

        Validated<String, AccumulatorValidator.Result> expected = Validated.invalid("Expected third argument to be a java.util.function.BiFunction<? super T, ? super U, ? extends V> but was java.util.function.BiFunction<W, W, W>");
        Validated<String, AccumulatorValidator.Result> toVerify = AccumulatorValidator.validate(toValidate, new CovariantParametersAndTypeParametersValidator());

        assertEquals(expected, toVerify);
    }

    @Test
    public void validateGivenMethodWithNoSharedTypeConstructorBetweenInputParameters() {
        Method toValidate = Method.of(
                emptySet(),
                modifiers(PUBLIC),
                asList(T.asTypeParameter(), U.asTypeParameter(), V.asTypeParameter()),
                Optional.of(OPTIONAL.with(V)),
                "myFunction",
                asList(
                        Parameter.of(OPTIONAL.with(T), "left"),
                        Parameter.of(COMPLETABLE_FUTURE.with(U), "right"),
                        Parameter.of(BI_FUNCTION.with(T.asType().contravariant(), U.asType().contravariant(), V.asType().covariant()), "compose")
                )
        );

        Validated<String, AccumulatorValidator.Result> expected = Validated.valid(
                AccumulatorValidator.Result.of(
                        "myFunction",
                        COMPLETABLE_FUTURE.asTypeConstructor(),
                        OPTIONAL.asTypeConstructor(),
                        OPTIONAL.asTypeConstructor(),
                        OPTIONAL.with(T),
                        COMPLETABLE_FUTURE.with(U),
                        OPTIONAL.with(V)
                )
        );
        Validated<String, AccumulatorValidator.Result> toVerify = AccumulatorValidator.validate(toValidate, new CovariantParametersAndTypeParametersValidator());

        assertEquals(expected, toVerify);
    }

    @Test
    public void validateGivenMethodWithNoSharedTypeConstructorBetweenInputParametersAndReturnType() {
        Method toValidate = Method.of(
                emptySet(),
                modifiers(PUBLIC),
                asList(T.asTypeParameter(), U.asTypeParameter(), V.asTypeParameter()),
                Optional.of(COMPLETABLE_FUTURE.with(V)),
                "myFunction",
                asList(
                        Parameter.of(OPTIONAL.with(T), "left"),
                        Parameter.of(OPTIONAL.with(U), "right"),
                        Parameter.of(BI_FUNCTION.with(T.asType().contravariant(), U.asType().contravariant(), V.asType().covariant()), "compose")
                )
        );

        Validated<String, AccumulatorValidator.Result> expected = Validated.invalid("No shared type constructor between parameters (java.util.Optional<T> and java.util.Optional<U>) and result (java.util.concurrent.CompletableFuture<V>)");
        Validated<String, AccumulatorValidator.Result> toVerify = AccumulatorValidator.validate(toValidate, new CovariantParametersAndTypeParametersValidator());

        assertEquals(expected, toVerify);
    }

    @Test
    public void validateGivenMethodWithNoSharedTypeConstructorBetweenLeftInputParameterAndReturnType() {
        Method toValidate = Method.of(
                emptySet(),
                modifiers(PUBLIC),
                asList(T.asTypeParameter(), U.asTypeParameter(), V.asTypeParameter()),
                Optional.of(COMPLETABLE_FUTURE.with(V)),
                "myFunction",
                asList(
                        Parameter.of(LIST.with(T), "left"),
                        Parameter.of(OPTIONAL.with(U), "right"),
                        Parameter.of(BI_FUNCTION.with(T.asType().contravariant(), U.asType().contravariant(), V.asType().covariant()), "compose")
                )
        );

        Validated<String, AccumulatorValidator.Result> expected = Validated.invalid("No shared type constructor between first parameter (java.util.List<T>) and result (java.util.concurrent.CompletableFuture<V>)");
        Validated<String, AccumulatorValidator.Result> toVerify = AccumulatorValidator.validate(toValidate, new CovariantParametersAndTypeParametersValidator());

        assertEquals(expected, toVerify);
    }

    @Test
    public void validateGivenMethodWithAccumulatedTypeConstructorThatIsAssignableToPartiallyAccumulatedTypeConstructor() {
        Method toValidate = Method.of(
                emptySet(),
                modifiers(PUBLIC),
                asList(T.asTypeParameter(), U.asTypeParameter(), V.asTypeParameter()),
                Optional.of(FUNCTION.with(P, V)),
                "myFunction",
                asList(
                        Parameter.of(FUNCTION.with(P.asType().contravariant(), T.asType().covariant()), "left"),
                        Parameter.of(FUNCTION.with(P.asType().contravariant(), U.asType().covariant()), "right"),
                        Parameter.of(BI_FUNCTION.with(T.asType().contravariant(), U.asType().contravariant(), V.asType().covariant()), "compose")
                )
        );

        Validated<String, AccumulatorValidator.Result> expected = Validated.valid(
                AccumulatorValidator.Result.of(
                        "myFunction",
                        FUNCTION.with(P.asTypeConstructor().contravariant(), placeholder().covariant()),
                        FUNCTION.with(P.asTypeConstructor().contravariant(), placeholder().covariant()),
                        FUNCTION.with(P.asTypeConstructor().invariant(), placeholder().invariant()),
                        FUNCTION.with(P.asType().contravariant(), T.asType().covariant()),
                        FUNCTION.with(P.asType().contravariant(), U.asType().covariant()),
                        FUNCTION.with(P.asType().invariant(), V.asType().invariant())
                )
        );
        Validated<String, AccumulatorValidator.Result> toVerify = AccumulatorValidator.validate(toValidate, new CovariantParametersAndTypeParametersValidator());

        assertEquals(expected, toVerify);
    }

    @Test
    public void validateGivenParametersThatReferenceTheWrongTypeParameters() {

        // Note: it is currently impossible for the accumulated type constructor and
        // the partially accumulated type constructor to mention the first or the second
        // type parameter. If they do, they accumulated type constructor can never
        // be assigned to the partially accumulated type constructor.

        Method toValidate = Method.of(
                emptySet(),
                modifiers(PUBLIC),
                asList(T.asTypeParameter(), U.asTypeParameter(), V.asTypeParameter()),
                Optional.of(FUNCTION.with(U, V)),
                "myFunction",
                asList(
                        Parameter.of(FUNCTION.with(U, T), "left"),
                        Parameter.of(FUNCTION.with(U, U), "right"),
                        Parameter.of(BI_FUNCTION.with(T.asType().contravariant(), U.asType().contravariant(), V.asType().covariant()), "compose")
                )
        );

        Validated<String, AccumulatorValidator.Result> expected = Validated.invalid(
                "The type of the first parameter (java.util.function.Function<U, T>) is not allowed to reference type parameter 'U'",
                "The return type (java.util.function.Function<U, V>) is not allowed to reference type parameter 'U'"
        );
        Validated<String, AccumulatorValidator.Result> toVerify = AccumulatorValidator.validate(toValidate, new CovariantParametersAndTypeParametersValidator());

        assertEquals(expected, toVerify);
    }

    @Test
    public void resultEqualsHashCodeToString() {
        EqualsVerifier.forClass(AccumulatorValidator.Result.class).verify();
        ToStringVerifier.forClass(AccumulatorValidator.Result.class).verify();
    }

    @SafeVarargs
    private final <T> Set<T> modifiers(T... elements) {
        return Stream.of(elements).collect(toSet());
    }
}
