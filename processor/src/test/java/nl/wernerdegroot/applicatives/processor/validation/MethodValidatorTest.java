package nl.wernerdegroot.applicatives.processor.validation;

import nl.wernerdegroot.applicatives.processor.domain.*;
import nl.wernerdegroot.applicatives.processor.domain.containing.ContainingClass;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.toSet;
import static nl.wernerdegroot.applicatives.processor.domain.Modifier.*;
import static nl.wernerdegroot.applicatives.processor.domain.type.Type.*;
import static nl.wernerdegroot.applicatives.processor.domain.typeconstructor.TypeConstructor.placeholder;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MethodValidatorTest {

    private final TypeParameter A = TypeParameterName.of("A").asTypeParameter();
    private final TypeParameter B = TypeParameterName.of("B").asTypeParameter();
    private final TypeParameter C = TypeParameterName.of("C").asTypeParameter();
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
                ),
                ContainingClass.of(PackageName.of("nl.wernerdegroot.applicatives"), ClassName.of("Optionals"))
        );

        Validated<AccumulatorMethod> expected = Validated.invalid("Method needs to return something");
        Validated<AccumulatorMethod> toVerify = MethodValidator.validate(toValidate);

        assertEquals(expected, toVerify);
    }

    @Test
    public void validateGivenStaticMethod() {
        Method toValidate = Method.of(
                emptySet(),
                modifiers(STATIC),
                asList(T.asTypeParameter(), U.asTypeParameter(), V.asTypeParameter()),
                Optional.of(OPTIONAL.with(V)),
                "myFunction",
                asList(
                        Parameter.of(OPTIONAL.with(T), "left"),
                        Parameter.of(OPTIONAL.with(U), "right"),
                        Parameter.of(BI_FUNCTION.with(T.asType().contravariant(), U.asType().contravariant(), V.asType().covariant()), "compose")
                ),
                ContainingClass.of(PackageName.of("nl.wernerdegroot.applicatives"), ClassName.of("Optionals"))
        );

        Validated<AccumulatorMethod> expected = Validated.invalid("Method is static and cannot implement an abstract method");
        Validated<AccumulatorMethod> toVerify = MethodValidator.validate(toValidate);

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
                ),
                ContainingClass.of(PackageName.of("nl.wernerdegroot.applicatives"), ClassName.of("Optionals"))
        );

        Validated<AccumulatorMethod> expected = Validated.invalid("Method is private and cannot implement an abstract method");
        Validated<AccumulatorMethod> toVerify = MethodValidator.validate(toValidate);

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
                ),
                ContainingClass.of(PackageName.of("nl.wernerdegroot.applicatives"), ClassName.of("Optionals"))
        );

        Validated<AccumulatorMethod> expected = Validated.invalid("The type parameters need to be unbounded");
        Validated<AccumulatorMethod> toVerify = MethodValidator.validate(toValidate);

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
                ),
                ContainingClass.of(PackageName.of("nl.wernerdegroot.applicatives"), ClassName.of("Optionals"))
        );

        Validated<AccumulatorMethod> expected = Validated.valid(
                AccumulatorMethod.of(
                        OPTIONAL.asTypeConstructor(),
                        OPTIONAL.asTypeConstructor(),
                        OPTIONAL.asTypeConstructor()
                )
        );
        Validated<AccumulatorMethod> toVerify = MethodValidator.validate(toValidate);

        assertEquals(expected, toVerify);
    }

    @Test
    public void validateGivenMethodWithLessThanThreeTypeParameters() {
        Method toValidate = Method.of(
                emptySet(),
                modifiers(PUBLIC),
                asList(T.asTypeParameter()),
                Optional.of(OPTIONAL.with(T)),
                "myFunction",
                asList(
                        Parameter.of(OPTIONAL.with(T), "left"),
                        Parameter.of(OPTIONAL.with(T), "right"),
                        Parameter.of(BI_FUNCTION.with(T, T, T), "compose")
                ),
                ContainingClass.of(PackageName.of("nl.wernerdegroot.applicatives"), ClassName.of("Optionals"))
        );

        Validated<AccumulatorMethod> expected = Validated.invalid("Method requires exactly 3 type parameters, but found 1");
        Validated<AccumulatorMethod> toVerify = MethodValidator.validate(toValidate);

        assertEquals(expected, toVerify);
    }

    @Test
    public void validateGivenMethodWithMoreThanThreeTypeParameters() {
        Method toValidate = Method.of(
                emptySet(),
                modifiers(PUBLIC),
                asList(T.asTypeParameter(), U.asTypeParameter(), V.asTypeParameter(), P.asTypeParameter()),
                Optional.of(FUNCTION.with(P, V)),
                "myFunction",
                asList(
                        Parameter.of(FUNCTION.with(P, T), "left"),
                        Parameter.of(FUNCTION.with(P, U), "right"),
                        Parameter.of(BI_FUNCTION.with(T.asType().contravariant(), U.asType().contravariant(), V.asType().covariant()), "compose")
                ),
                ContainingClass.of(PackageName.of("nl.wernerdegroot.applicatives"), ClassName.of("Functions"))
        );

        Validated<AccumulatorMethod> expected = Validated.invalid("Method requires exactly 3 type parameters, but found 4");
        Validated<AccumulatorMethod> toVerify = MethodValidator.validate(toValidate);

        assertEquals(expected, toVerify);
    }

    @Test
    public void validateGivenMethodWithLessThanThreeParameters() {
        Method toValidate = Method.of(
                emptySet(),
                modifiers(PUBLIC),
                asList(T.asTypeParameter(), U.asTypeParameter(), V.asTypeParameter()),
                Optional.of(OPTIONAL.with(V)),
                "myFunction",
                asList(
                        Parameter.of(OPTIONAL.with(T), "left"),
                        Parameter.of(OPTIONAL.with(U), "right")
                ),
                ContainingClass.of(PackageName.of("nl.wernerdegroot.applicatives"), ClassName.of("Optionals"))
        );

        Validated<AccumulatorMethod> expected = Validated.invalid("Method requires exactly 3 parameters, but found 2");
        Validated<AccumulatorMethod> toVerify = MethodValidator.validate(toValidate);

        assertEquals(expected, toVerify);
    }

    @Test
    public void validateGivenMethodWithMoreThanThreeParameters() {
        Method toValidate = Method.of(
                emptySet(),
                modifiers(PUBLIC),
                asList(T.asTypeParameter(), U.asTypeParameter(), V.asTypeParameter()),
                Optional.of(COMPLETABLE_FUTURE.with(V)),
                "myFunction",
                asList(
                        Parameter.of(COMPLETABLE_FUTURE.with(T), "left"),
                        Parameter.of(COMPLETABLE_FUTURE.with(U), "right"),
                        Parameter.of(BI_FUNCTION.with(T.asType().contravariant(), U.asType().contravariant(), V.asType().covariant()), "compose"),
                        Parameter.of(EXECUTOR, "executor")
                ),
                ContainingClass.of(PackageName.of("nl.wernerdegroot.applicatives"), ClassName.of("Futures"))
        );

        Validated<AccumulatorMethod> expected = Validated.invalid("Method requires exactly 3 parameters, but found 4");
        Validated<AccumulatorMethod> toVerify = MethodValidator.validate(toValidate);

        assertEquals(expected, toVerify);
    }

    @Test
    public void validateGivenMethodWithThirdParameterThatIsNotABiFunction() {
        Method toValidate = Method.of(
                emptySet(),
                modifiers(PUBLIC),
                asList(T.asTypeParameter(), U.asTypeParameter(), V.asTypeParameter()),
                Optional.of(OPTIONAL.with(V)),
                "myFunction",
                asList(
                        Parameter.of(OPTIONAL.with(T), "left"),
                        Parameter.of(OPTIONAL.with(U), "right"),
                        Parameter.of(OBJECT, "someObject")
                ),
                ContainingClass.of(PackageName.of("nl.wernerdegroot.applicatives"), ClassName.of("Optionals"))
        );

        Validated<AccumulatorMethod> expected = Validated.invalid("Expected third argument to be a java.util.function.BiFunction<? super T, ? super U, ? extends V> but was java.lang.Object");
        Validated<AccumulatorMethod> toVerify = MethodValidator.validate(toValidate);

        assertEquals(expected, toVerify);
    }

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
                ),
                ContainingClass.of(PackageName.of("nl.wernerdegroot.applicatives"), ClassName.of("Optionals"))
        );

        Validated<AccumulatorMethod> expected = Validated.invalid("Expected third argument to be a java.util.function.BiFunction<? super T, ? super U, ? extends V> but was java.util.function.BiFunction<W, W, W>");
        Validated<AccumulatorMethod> toVerify = MethodValidator.validate(toValidate);

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
                ),
                ContainingClass.of(PackageName.of("nl.wernerdegroot.applicatives"), ClassName.of("Weird"))
        );

        Validated<AccumulatorMethod> expected = Validated.valid(
                AccumulatorMethod.of(
                        OPTIONAL.asTypeConstructor(),
                        OPTIONAL.asTypeConstructor(),
                        COMPLETABLE_FUTURE.asTypeConstructor()
                )
        );
        Validated<AccumulatorMethod> toVerify = MethodValidator.validate(toValidate);

        assertEquals(expected, toVerify);
    }

    @Test
    public void validateGivenMethodWithNoSharedTypeConstructorBetweenLeftInputParameterAndResultType() {
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
                ),
                ContainingClass.of(PackageName.of("nl.wernerdegroot.applicatives"), ClassName.of("Optionals"), asList(P.asTypeParameter()))
        );

        Validated<AccumulatorMethod> expected = Validated.invalid("No shared type constructor between parameters (java.util.Optional<T> and java.util.Optional<U>) and result (java.util.concurrent.CompletableFuture<V>)");
        Validated<AccumulatorMethod> toVerify = MethodValidator.validate(toValidate);

        assertEquals(expected, toVerify);
    }

    @Test
    public void validateGivenMethodWithAccumulationTypeConstructorThatIsAssignableToPermissiveAccumulationTypeConstructor() {
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
                ),
                ContainingClass.of(PackageName.of("nl.wernerdegroot.applicatives"), ClassName.of("Functions"), asList(P.asTypeParameter()))
        );

        Validated<AccumulatorMethod> expected = Validated.valid(
                AccumulatorMethod.of(
                        FUNCTION.with(P.asTypeConstructor().invariant(), placeholder().invariant()),
                        FUNCTION.with(P.asTypeConstructor().contravariant(), placeholder().covariant()),
                        FUNCTION.with(P.asTypeConstructor().contravariant(), placeholder().covariant())
                )
        );
        Validated<AccumulatorMethod> toVerify = MethodValidator.validate(toValidate);

        assertEquals(expected, toVerify);
    }

    @SafeVarargs
    private final <T> Set<T> modifiers(T... elements) {
        return Stream.of(elements).collect(toSet());
    }
}
