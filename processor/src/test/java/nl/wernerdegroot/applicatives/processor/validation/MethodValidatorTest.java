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
    public void shouldReturnInvalidWhenTheMethodDoesNotReturnAnything() {
        Method toValidate = Method.of(
                modifiers(PUBLIC),
                asList(T.asTypeParameter(), U.asTypeParameter(), V.asTypeParameter()),
                Optional.empty(),
                "myFunction",
                asList(
                        Parameter.of(OPTIONAL.with(T), "left"),
                        Parameter.of(OPTIONAL.with(U), "right"),
                        Parameter.of(BI_FUNCTION.with(T.asType().contravariant(), U.asType().contravariant(), V.asType().covariant()), "compose")
                ),
                ContainingClass.withoutTypeParameters(PackageName.of("nl.wernerdegroot.applicatives"), ClassName.of("Optionals"))
        );

        ValidatedMethod expected = ValidatedMethod.invalid("Method needs to return something");
        ValidatedMethod toVerify = MethodValidator.validate(toValidate);

        assertEquals(expected, toVerify);
    }

    @Test
    public void shouldReturnInvalidWhenFunctionIsStatic() {
        Method toValidate = Method.of(
                modifiers(STATIC),
                asList(T.asTypeParameter(), U.asTypeParameter(), V.asTypeParameter()),
                Optional.of(OPTIONAL.with(V)),
                "myFunction",
                asList(
                        Parameter.of(OPTIONAL.with(T), "left"),
                        Parameter.of(OPTIONAL.with(U), "right"),
                        Parameter.of(BI_FUNCTION.with(T.asType().contravariant(), U.asType().contravariant(), V.asType().covariant()), "compose")
                ),
                ContainingClass.withoutTypeParameters(PackageName.of("nl.wernerdegroot.applicatives"), ClassName.of("Optionals"))
        );

        ValidatedMethod expected = ValidatedMethod.invalid("Method is static and cannot implement an abstract method");
        ValidatedMethod toVerify = MethodValidator.validate(toValidate);

        assertEquals(expected, toVerify);
    }

    @Test
    public void shouldReturnInvalidWhenFunctionIsPrivate() {
        Method toValidate = Method.of(
                modifiers(PRIVATE),
                asList(T.asTypeParameter(), U.asTypeParameter(), V.asTypeParameter()),
                Optional.of(OPTIONAL.with(V)),
                "myFunction",
                asList(
                        Parameter.of(OPTIONAL.with(T), "left"),
                        Parameter.of(OPTIONAL.with(U), "right"),
                        Parameter.of(BI_FUNCTION.with(T.asType().contravariant(), U.asType().contravariant(), V.asType().covariant()), "compose")
                ),
                ContainingClass.withoutTypeParameters(PackageName.of("nl.wernerdegroot.applicatives"), ClassName.of("Optionals"))
        );

        ValidatedMethod expected = ValidatedMethod.invalid("Method is private and cannot implement an abstract method");
        ValidatedMethod toVerify = MethodValidator.validate(toValidate);

        assertEquals(expected, toVerify);
    }

    @Test
    public void shouldReturnInvalidWhenOneOfTheFirstThreeTypeParametersHasAnUpperBoundOtherThanObject() {
        Method toValidate = Method.of(
                modifiers(PUBLIC),
                asList(T.extending(COMPARABLE.with(T)), U.extending(COMPARABLE.with(U)), V.extending(COMPARABLE.with(V))),
                Optional.of(OPTIONAL.with(V)),
                "myFunction",
                asList(
                        Parameter.of(OPTIONAL.with(T), "left"),
                        Parameter.of(OPTIONAL.with(U), "right"),
                        Parameter.of(BI_FUNCTION.with(T.asType().contravariant(), U.asType().contravariant(), V.asType().covariant()), "compose")
                ),
                ContainingClass.withoutTypeParameters(PackageName.of("nl.wernerdegroot.applicatives"), ClassName.of("Optionals"))
        );

        ValidatedMethod expected = ValidatedMethod.invalid("The first 3 type parameters need to be unbounded");
        ValidatedMethod toVerify = MethodValidator.validate(toValidate);

        assertEquals(expected, toVerify);
    }

    @Test
    public void shouldReturnValidWhenTheFirstThreeTypeParametersOnlyExtendObject() {
        Method toValidate = Method.of(
                modifiers(PUBLIC),
                asList(T.extending(OBJECT), U.extending(OBJECT), V.extending(OBJECT)),
                Optional.of(OPTIONAL.with(V)),
                "myFunction",
                asList(
                        Parameter.of(OPTIONAL.with(T), "left"),
                        Parameter.of(OPTIONAL.with(U), "right"),
                        Parameter.of(BI_FUNCTION.with(T.asType().contravariant(), U.asType().contravariant(), V.asType().covariant()), "compose")
                ),
                ContainingClass.withoutTypeParameters(PackageName.of("nl.wernerdegroot.applicatives"), ClassName.of("Optionals"))
        );

        ValidatedMethod expected = ValidatedMethod.valid(
                emptyList(),
                emptyList(),
                OPTIONAL.asTypeConstructor(),
                OPTIONAL.asTypeConstructor(),
                OPTIONAL.asTypeConstructor(),
                emptyList()
        );
        ValidatedMethod toVerify = MethodValidator.validate(toValidate);

        assertEquals(expected, toVerify);
    }

    @Test
    public void shouldReturnInvalidWhenFunctionHasLessThanThreeGenerics() {
        Method toValidate = Method.of(
                modifiers(PUBLIC),
                asList(T.asTypeParameter()),
                Optional.of(OPTIONAL.with(T)),
                "myFunction",
                asList(
                        Parameter.of(OPTIONAL.with(T), "left"),
                        Parameter.of(OPTIONAL.with(T), "right"),
                        Parameter.of(BI_FUNCTION.with(T, T, T), "compose")
                ),
                ContainingClass.withoutTypeParameters(PackageName.of("nl.wernerdegroot.applicatives"), ClassName.of("Optionals"))
        );

        ValidatedMethod expected = ValidatedMethod.invalid("Method needs at least 3 type parameters, but found only 1");
        ValidatedMethod toVerify = MethodValidator.validate(toValidate);

        assertEquals(expected, toVerify);
    }

    @Test
    public void shouldReturnValidWhenFunctionHasMoreThanThreeGenerics() {
        Method toValidate = Method.of(
                modifiers(PUBLIC),
                asList(T.asTypeParameter(), U.asTypeParameter(), V.asTypeParameter(), P.asTypeParameter()),
                Optional.of(FUNCTION.with(P, V)),
                "myFunction",
                asList(
                        Parameter.of(FUNCTION.with(P, T), "left"),
                        Parameter.of(FUNCTION.with(P, U), "right"),
                        Parameter.of(BI_FUNCTION.with(T.asType().contravariant(), U.asType().contravariant(), V.asType().covariant()), "compose")
                ),
                ContainingClass.withoutTypeParameters(PackageName.of("nl.wernerdegroot.applicatives"), ClassName.of("Functions"))
        );

        ValidatedMethod expected = ValidatedMethod.valid(
                asList(P.asTypeParameter()),
                emptyList(),
                FUNCTION.with(P.asTypeConstructor().invariant(), placeholder().invariant()),
                FUNCTION.with(P.asTypeConstructor().invariant(), placeholder().invariant()),
                FUNCTION.with(P.asTypeConstructor().invariant(), placeholder().invariant()),
                emptyList()
        );
        ValidatedMethod toVerify = MethodValidator.validate(toValidate);

        assertEquals(expected, toVerify);
    }

    @Test
    public void shouldReturnInvalidWhenMethodHasLessThanThreeParameters() {
        Method toValidate = Method.of(
                modifiers(PUBLIC),
                asList(T.asTypeParameter(), U.asTypeParameter(), V.asTypeParameter()),
                Optional.of(OPTIONAL.with(V)),
                "myFunction",
                asList(
                        Parameter.of(OPTIONAL.with(T), "left"),
                        Parameter.of(OPTIONAL.with(U), "right")
                ),
                ContainingClass.withoutTypeParameters(PackageName.of("nl.wernerdegroot.applicatives"), ClassName.of("Optionals"))
        );

        ValidatedMethod expected = ValidatedMethod.invalid("Method needs at least 3 parameters, but found only 2");
        ValidatedMethod toVerify = MethodValidator.validate(toValidate);

        assertEquals(expected, toVerify);
    }

    @Test
    public void shouldReturnValidWhenFunctionHasMoreThanThreeParameters() {
        Method toValidate = Method.of(
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
                ContainingClass.withoutTypeParameters(PackageName.of("nl.wernerdegroot.applicatives"), ClassName.of("Futures"))
        );

        ValidatedMethod expected = ValidatedMethod.valid(
                emptyList(),
                asList(Parameter.of(EXECUTOR, "executor")),
                COMPLETABLE_FUTURE.asTypeConstructor(),
                COMPLETABLE_FUTURE.asTypeConstructor(),
                COMPLETABLE_FUTURE.asTypeConstructor(),
                emptyList()
        );
        ValidatedMethod toVerify = MethodValidator.validate(toValidate);

        assertEquals(expected, toVerify);
    }

    @Test
    public void shouldReturnInvalidWhenThirdParameterIsNotABiFunction() {
        Method toValidate = Method.of(
                modifiers(PUBLIC),
                asList(T.asTypeParameter(), U.asTypeParameter(), V.asTypeParameter()),
                Optional.of(OPTIONAL.with(V)),
                "myFunction",
                asList(
                        Parameter.of(OPTIONAL.with(T), "left"),
                        Parameter.of(OPTIONAL.with(U), "right"),
                        Parameter.of(OBJECT, "someObject")
                ),
                ContainingClass.withoutTypeParameters(PackageName.of("nl.wernerdegroot.applicatives"), ClassName.of("Optionals"))
        );

        ValidatedMethod expected = ValidatedMethod.invalid("Expected third argument to be a java.util.function.BiFunction<? super T, ? super U, ? extends V> but was java.lang.Object");
        ValidatedMethod toVerify = MethodValidator.validate(toValidate);

        assertEquals(expected, toVerify);
    }

    @Test
    public void shouldReturnInvalidWhenBiFunctionHasWrongParameters() {
        Method toValidate = Method.of(
                modifiers(PUBLIC),
                asList(T.asTypeParameter(), U.asTypeParameter(), V.asTypeParameter()),
                Optional.of(OPTIONAL.with(V)),
                "myFunction",
                asList(
                        Parameter.of(OPTIONAL.with(T), "left"),
                        Parameter.of(OPTIONAL.with(U), "right"),
                        Parameter.of(BI_FUNCTION.with(W, W, W), "compose")
                ),
                ContainingClass.withoutTypeParameters(PackageName.of("nl.wernerdegroot.applicatives"), ClassName.of("Optionals"))
        );

        ValidatedMethod expected = ValidatedMethod.invalid("Expected third argument to be a java.util.function.BiFunction<? super T, ? super U, ? extends V> but was java.util.function.BiFunction<W, W, W>");
        ValidatedMethod toVerify = MethodValidator.validate(toValidate);

        assertEquals(expected, toVerify);
    }

    @Test
    public void shouldReturnInvalidIfTypesOfSecondaryParametersContainOneOfTheFirstThreeTypeParameters() {
        Method toValidate = Method.of(
                modifiers(PUBLIC),
                asList(T.asTypeParameter(), U.asTypeParameter(), V.asTypeParameter()),
                Optional.of(OPTIONAL.with(V)),
                "myFunction",
                asList(
                        Parameter.of(OPTIONAL.with(T), "left"),
                        Parameter.of(OPTIONAL.with(U), "right"),
                        Parameter.of(BI_FUNCTION.with(T.asType().contravariant(), U.asType().contravariant(), V.asType().covariant()), "compose"),
                        Parameter.of(COMPARABLE.with(T), "comparable")
                ),
                ContainingClass.withoutTypeParameters(PackageName.of("nl.wernerdegroot.applicatives"), ClassName.of("Optionals"))
        );

        ValidatedMethod expected = ValidatedMethod.invalid("Parameter with name \"comparable\" cannot reference T, U or V");
        ValidatedMethod toVerify = MethodValidator.validate(toValidate);

        assertEquals(expected, toVerify);
    }

    @Test
    public void shouldReturnValidWhenThereIsNoSharedTypeConstructorBetweenBothParameters() {
        Method toValidate = Method.of(
                modifiers(PUBLIC),
                asList(T.asTypeParameter(), U.asTypeParameter(), V.asTypeParameter()),
                Optional.of(OPTIONAL.with(V)),
                "myFunction",
                asList(
                        Parameter.of(OPTIONAL.with(T), "left"),
                        Parameter.of(COMPLETABLE_FUTURE.with(U), "right"),
                        Parameter.of(BI_FUNCTION.with(T.asType().contravariant(), U.asType().contravariant(), V.asType().covariant()), "compose")
                ),
                ContainingClass.withoutTypeParameters(PackageName.of("nl.wernerdegroot.applicatives"), ClassName.of("Weird"))
        );

        ValidatedMethod expected = ValidatedMethod.valid(
                emptyList(),
                emptyList(),
                OPTIONAL.asTypeConstructor(),
                COMPLETABLE_FUTURE.asTypeConstructor(),
                OPTIONAL.asTypeConstructor(),
                emptyList()
        );
        ValidatedMethod toVerify = MethodValidator.validate(toValidate);

        assertEquals(expected, toVerify);
    }

    @Test
    public void shouldReturnInvalidWhenThereIsNoSharedTypeConstructorBetweenParametersAndResult() {
        Method toValidate = Method.of(
                modifiers(PUBLIC),
                asList(T.asTypeParameter(), U.asTypeParameter(), V.asTypeParameter(), P.asTypeParameter()),
                Optional.of(COMPLETABLE_FUTURE.with(V)),
                "myFunction",
                asList(
                        Parameter.of(OPTIONAL.with(T), "left"),
                        Parameter.of(OPTIONAL.with(U), "right"),
                        Parameter.of(BI_FUNCTION.with(T.asType().contravariant(), U.asType().contravariant(), V.asType().covariant()), "compose")
                ),
                ContainingClass.withoutTypeParameters(PackageName.of("nl.wernerdegroot.applicatives"), ClassName.of("Optionals"))
        );

        ValidatedMethod expected = ValidatedMethod.invalid("No shared type constructor between parameters (java.util.Optional<T> and java.util.Optional<U>) and result (java.util.concurrent.CompletableFuture<V>)");
        ValidatedMethod toVerify = MethodValidator.validate(toValidate);

        assertEquals(expected, toVerify);
    }


    @Test
    public void shouldReturnInvalidWhenThereIsNoSharedTypeConstructorBetweenLeftParameterAndResult() {
        Method toValidate = Method.of(
                modifiers(PUBLIC),
                asList(T.asTypeParameter(), U.asTypeParameter(), V.asTypeParameter(), P.asTypeParameter()),
                Optional.of(COMPLETABLE_FUTURE.with(V)),
                "myFunction",
                asList(
                        Parameter.of(OPTIONAL.with(T), "left"),
                        Parameter.of(COMPLETABLE_FUTURE.with(U), "right"),
                        Parameter.of(BI_FUNCTION.with(T.asType().contravariant(), U.asType().contravariant(), V.asType().covariant()), "compose")
                ),
                ContainingClass.withoutTypeParameters(PackageName.of("nl.wernerdegroot.applicatives"), ClassName.of("Optionals"))
        );

        ValidatedMethod expected = ValidatedMethod.invalid("No shared type constructor between left parameter (java.util.Optional<T>) and result (java.util.concurrent.CompletableFuture<V>)");
        ValidatedMethod toVerify = MethodValidator.validate(toValidate);

        assertEquals(expected, toVerify);
    }

    @Test
    public void shouldReturnValidWhenResultTypeConstructorIsAssignableToParameterTypeConstructor() {
        Method toValidate = Method.of(
                modifiers(PUBLIC),
                asList(T.asTypeParameter(), U.asTypeParameter(), V.asTypeParameter(), P.asTypeParameter()),
                Optional.of(FUNCTION.with(P, V)),
                "myFunction",
                asList(
                        Parameter.of(FUNCTION.with(P.asType().contravariant(), T.asType().covariant()), "left"),
                        Parameter.of(FUNCTION.with(P.asType().contravariant(), U.asType().covariant()), "right"),
                        Parameter.of(BI_FUNCTION.with(T.asType().contravariant(), U.asType().contravariant(), V.asType().covariant()), "compose")
                ),
                ContainingClass.withoutTypeParameters(PackageName.of("nl.wernerdegroot.applicatives"), ClassName.of("Functions"))
        );

        ValidatedMethod expected = ValidatedMethod.valid(
                asList(P.asTypeParameter()),
                emptyList(),
                FUNCTION.with(P.asTypeConstructor().contravariant(), placeholder().covariant()),
                FUNCTION.with(P.asTypeConstructor().contravariant(), placeholder().covariant()),
                FUNCTION.with(P.asTypeConstructor().invariant(), placeholder().invariant()),
                emptyList()
        );
        ValidatedMethod toVerify = MethodValidator.validate(toValidate);

        assertEquals(expected, toVerify);
    }

    @Test
    public void shouldReturnInvalidWhenTheContainingClassIsANonStaticInnerClass() {
        Method toValidate = Method.of(
                modifiers(PUBLIC),
                asList(T.asTypeParameter(), U.asTypeParameter(), V.asTypeParameter()),
                Optional.of(OPTIONAL.with(V)),
                "myFunction",
                asList(
                        Parameter.of(OPTIONAL.with(T), "left"),
                        Parameter.of(OPTIONAL.with(U), "right"),
                        Parameter.of(BI_FUNCTION.with(T.asType().contravariant(), U.asType().contravariant(), V.asType().covariant()), "compose")
                ),
                PackageName.of("nl.wernerdegroot.applicatives").asPackage().containingClass(emptySet(), ClassName.of("Outer")).containingClass(emptySet(), ClassName.of("Inner"))
        );

        ValidatedMethod expected = ValidatedMethod.invalid("Only outer classes and static inner classes are supported");
        ValidatedMethod toVerify = MethodValidator.validate(toValidate);

        assertEquals(expected, toVerify);
    }

    @Test
    public void shouldReturnValidWhenTheContainingClassIsAStaticInnerClass() {
        Method toValidate = Method.of(
                modifiers(PUBLIC),
                asList(T.asTypeParameter(), U.asTypeParameter(), V.asTypeParameter()),
                Optional.of(OPTIONAL.with(V)),
                "myFunction",
                asList(
                        Parameter.of(OPTIONAL.with(T), "left"),
                        Parameter.of(OPTIONAL.with(U), "right"),
                        Parameter.of(BI_FUNCTION.with(T.asType().contravariant(), U.asType().contravariant(), V.asType().covariant()), "compose")
                ),
                PackageName.of("nl.wernerdegroot.applicatives").asPackage().containingClass(emptySet(), ClassName.of("Outer"), A, B).containingClass(modifiers(STATIC), ClassName.of("Inner"), B, C)
        );

        ValidatedMethod expected = ValidatedMethod.valid(
                emptyList(),
                emptyList(),
                OPTIONAL.asTypeConstructor(),
                OPTIONAL.asTypeConstructor(),
                OPTIONAL.asTypeConstructor(),
                asList(B, C)
        );
        ValidatedMethod toVerify = MethodValidator.validate(toValidate);

        assertEquals(expected, toVerify);
    }

    @Test
    public void shouldReturnValidWhenTheContainingClassIsAOuterClass() {
        Method toValidate = Method.of(
                modifiers(PUBLIC),
                asList(T.asTypeParameter(), U.asTypeParameter(), V.asTypeParameter()),
                Optional.of(OPTIONAL.with(V)),
                "myFunction",
                asList(
                        Parameter.of(OPTIONAL.with(T), "left"),
                        Parameter.of(OPTIONAL.with(U), "right"),
                        Parameter.of(BI_FUNCTION.with(T.asType().contravariant(), U.asType().contravariant(), V.asType().covariant()), "compose")
                ),
                PackageName.of("nl.wernerdegroot.applicatives").asPackage().containingClass(emptySet(), ClassName.of("Outer"), A, B)
        );

        ValidatedMethod expected = ValidatedMethod.valid(
                emptyList(),
                emptyList(),
                OPTIONAL.asTypeConstructor(),
                OPTIONAL.asTypeConstructor(),
                OPTIONAL.asTypeConstructor(),
                asList(A, B)
        );
        ValidatedMethod toVerify = MethodValidator.validate(toValidate);

        assertEquals(expected, toVerify);
    }

    @SafeVarargs
    private final <T> Set<T> modifiers(T... elements) {
        return Stream.of(elements).collect(toSet());
    }
}
