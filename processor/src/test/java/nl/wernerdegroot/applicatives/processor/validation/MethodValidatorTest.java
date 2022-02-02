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
import static nl.wernerdegroot.applicatives.processor.domain.BoundType.EXTENDS;
import static nl.wernerdegroot.applicatives.processor.domain.BoundType.SUPER;
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
                        Parameter.of(OPTIONAL.of(T), "left"),
                        Parameter.of(OPTIONAL.of(U), "right"),
                        Parameter.of(BI_FUNCTION.of(SUPER.type(T), SUPER.type(U), EXTENDS.type(V)), "compose")
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
                Optional.of(OPTIONAL.of(V)),
                "myFunction",
                asList(
                        Parameter.of(OPTIONAL.of(T), "left"),
                        Parameter.of(OPTIONAL.of(U), "right"),
                        Parameter.of(BI_FUNCTION.of(SUPER.type(T), SUPER.type(U), EXTENDS.type(V)), "compose")
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
                Optional.of(OPTIONAL.of(V)),
                "myFunction",
                asList(
                        Parameter.of(OPTIONAL.of(T), "left"),
                        Parameter.of(OPTIONAL.of(U), "right"),
                        Parameter.of(BI_FUNCTION.of(SUPER.type(T), SUPER.type(U), EXTENDS.type(V)), "compose")
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
                asList(T.extending(COMPARABLE.of(T)), U.extending(COMPARABLE.of(U)), V.extending(COMPARABLE.of(V))),
                Optional.of(OPTIONAL.of(V)),
                "myFunction",
                asList(
                        Parameter.of(OPTIONAL.of(T), "left"),
                        Parameter.of(OPTIONAL.of(U), "right"),
                        Parameter.of(BI_FUNCTION.of(SUPER.type(T), SUPER.type(U), EXTENDS.type(V)), "compose")
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
                Optional.of(OPTIONAL.of(V)),
                "myFunction",
                asList(
                        Parameter.of(OPTIONAL.of(T), "left"),
                        Parameter.of(OPTIONAL.of(U), "right"),
                        Parameter.of(BI_FUNCTION.of(SUPER.type(T), SUPER.type(U), EXTENDS.type(V)), "compose")
                ),
                ContainingClass.withoutTypeParameters(PackageName.of("nl.wernerdegroot.applicatives"), ClassName.of("Optionals"))
        );

        ValidatedMethod expected = ValidatedMethod.valid(
                emptyList(),
                emptyList(),
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
                Optional.of(OPTIONAL.of(T)),
                "myFunction",
                asList(
                        Parameter.of(OPTIONAL.of(T), "left"),
                        Parameter.of(OPTIONAL.of(T), "right"),
                        Parameter.of(BI_FUNCTION.of(T, T, T), "compose")
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
                Optional.of(FUNCTION.of(P, V)),
                "myFunction",
                asList(
                        Parameter.of(FUNCTION.of(P, T), "left"),
                        Parameter.of(FUNCTION.of(P, U), "right"),
                        Parameter.of(BI_FUNCTION.of(SUPER.type(T), SUPER.type(U), EXTENDS.type(V)), "compose")
                ),
                ContainingClass.withoutTypeParameters(PackageName.of("nl.wernerdegroot.applicatives"), ClassName.of("Functions"))
        );

        ValidatedMethod expected = ValidatedMethod.valid(
                asList(P.asTypeParameter()),
                emptyList(),
                FUNCTION.of(P.asTypeConstructor(), placeholder()),
                FUNCTION.of(P.asTypeConstructor(), placeholder()),
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
                Optional.of(OPTIONAL.of(V)),
                "myFunction",
                asList(
                        Parameter.of(OPTIONAL.of(T), "left"),
                        Parameter.of(OPTIONAL.of(U), "right")
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
                Optional.of(COMPLETABLE_FUTURE.of(V)),
                "myFunction",
                asList(
                        Parameter.of(COMPLETABLE_FUTURE.of(T), "left"),
                        Parameter.of(COMPLETABLE_FUTURE.of(U), "right"),
                        Parameter.of(BI_FUNCTION.of(SUPER.type(T), SUPER.type(U), EXTENDS.type(V)), "compose"),
                        Parameter.of(EXECUTOR, "executor")
                ),
                ContainingClass.withoutTypeParameters(PackageName.of("nl.wernerdegroot.applicatives"), ClassName.of("Futures"))
        );

        ValidatedMethod expected = ValidatedMethod.valid(
                emptyList(),
                asList(Parameter.of(EXECUTOR, "executor")),
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
                Optional.of(OPTIONAL.of(V)),
                "myFunction",
                asList(
                        Parameter.of(OPTIONAL.of(T), "left"),
                        Parameter.of(OPTIONAL.of(U), "right"),
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
                Optional.of(OPTIONAL.of(V)),
                "myFunction",
                asList(
                        Parameter.of(OPTIONAL.of(T), "left"),
                        Parameter.of(OPTIONAL.of(U), "right"),
                        Parameter.of(BI_FUNCTION.of(W, W, W), "compose")
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
                Optional.of(OPTIONAL.of(V)),
                "myFunction",
                asList(
                        Parameter.of(OPTIONAL.of(T), "left"),
                        Parameter.of(OPTIONAL.of(U), "right"),
                        Parameter.of(BI_FUNCTION.of(SUPER.type(T), SUPER.type(U), EXTENDS.type(V)), "compose"),
                        Parameter.of(COMPARABLE.of(T), "comparable")
                ),
                ContainingClass.withoutTypeParameters(PackageName.of("nl.wernerdegroot.applicatives"), ClassName.of("Optionals"))
        );

        ValidatedMethod expected = ValidatedMethod.invalid("Parameter with name \"comparable\" cannot reference T, U or V");
        ValidatedMethod toVerify = MethodValidator.validate(toValidate);

        assertEquals(expected, toVerify);
    }

    @Test
    public void shouldReturnInvalidWhenThereIsNoSharedTypeConstructorBetweenBothParameters() {
        Method toValidate = Method.of(
                modifiers(PUBLIC),
                asList(T.asTypeParameter(), U.asTypeParameter(), V.asTypeParameter()),
                Optional.of(OPTIONAL.of(V)),
                "myFunction",
                asList(
                        Parameter.of(OPTIONAL.of(T), "left"),
                        Parameter.of(COMPLETABLE_FUTURE.of(U), "right"),
                        Parameter.of(BI_FUNCTION.of(SUPER.type(T), SUPER.type(U), EXTENDS.type(V)), "compose")
                ),
                ContainingClass.withoutTypeParameters(PackageName.of("nl.wernerdegroot.applicatives"), ClassName.of("Weird"))
        );

        ValidatedMethod expected = ValidatedMethod.invalid("No shared type constructor between left parameter (java.util.Optional<T>) and right parameter (java.util.concurrent.CompletableFuture<U>)");
        ValidatedMethod toVerify = MethodValidator.validate(toValidate);

        assertEquals(expected, toVerify);
    }

    @Test
    public void shouldReturnInvalidWhenThereIsNoSharedTypeConstructorBetweenParametersAndResult() {
        Method toValidate = Method.of(
                modifiers(PUBLIC),
                asList(T.asTypeParameter(), U.asTypeParameter(), V.asTypeParameter(), P.asTypeParameter()),
                Optional.of(COMPLETABLE_FUTURE.of(V)),
                "myFunction",
                asList(
                        Parameter.of(OPTIONAL.of(T), "left"),
                        Parameter.of(OPTIONAL.of(U), "right"),
                        Parameter.of(BI_FUNCTION.of(SUPER.type(T), SUPER.type(U), EXTENDS.type(V)), "compose")
                ),
                ContainingClass.withoutTypeParameters(PackageName.of("nl.wernerdegroot.applicatives"), ClassName.of("Optionals"))
        );

        ValidatedMethod expected = ValidatedMethod.invalid("No shared type constructor between parameters (java.util.Optional<T> and java.util.Optional<U>) and result (java.util.concurrent.CompletableFuture<V>)");
        ValidatedMethod toVerify = MethodValidator.validate(toValidate);

        assertEquals(expected, toVerify);
    }

    @Test
    public void shouldReturnInvalidWhenTheContainingClassIsANonStaticInnerClass() {
        Method toValidate = Method.of(
                modifiers(PUBLIC),
                asList(T.asTypeParameter(), U.asTypeParameter(), V.asTypeParameter()),
                Optional.of(OPTIONAL.of(V)),
                "myFunction",
                asList(
                        Parameter.of(OPTIONAL.of(T), "left"),
                        Parameter.of(OPTIONAL.of(U), "right"),
                        Parameter.of(BI_FUNCTION.of(SUPER.type(T), SUPER.type(U), EXTENDS.type(V)), "compose")
                ),
                PackageName.of("nl.wernerdegroot.applicatives").asPackage().containingClass(emptySet(), ClassName.of("Outer")).containingClass(emptySet(), ClassName.of("Inner"))
        );

        ValidatedMethod expected = ValidatedMethod.invalid("Only outer classes and static inner classes are supported");
        ValidatedMethod toVerify = MethodValidator.validate(toValidate);

        assertEquals(expected, toVerify);
    }

    @Test
    public void shouldReturnValidWhenTheContainClassIsAStaticInnerClass() {
        Method toValidate = Method.of(
                modifiers(PUBLIC),
                asList(T.asTypeParameter(), U.asTypeParameter(), V.asTypeParameter()),
                Optional.of(OPTIONAL.of(V)),
                "myFunction",
                asList(
                        Parameter.of(OPTIONAL.of(T), "left"),
                        Parameter.of(OPTIONAL.of(U), "right"),
                        Parameter.of(BI_FUNCTION.of(SUPER.type(T), SUPER.type(U), EXTENDS.type(V)), "compose")
                ),
                PackageName.of("nl.wernerdegroot.applicatives").asPackage().containingClass(emptySet(), ClassName.of("Outer"), A, B).containingClass(modifiers(STATIC), ClassName.of("Inner"), B, C)
        );

        ValidatedMethod expected = ValidatedMethod.valid(
                emptyList(),
                emptyList(),
                OPTIONAL.asTypeConstructor(),
                OPTIONAL.asTypeConstructor(),
                asList(B, C)
        );
        ValidatedMethod toVerify = MethodValidator.validate(toValidate);

        assertEquals(expected, toVerify);
    }

    @Test
    public void shouldReturnValidWhenTheContainClassIsAOuterClass() {
        Method toValidate = Method.of(
                modifiers(PUBLIC),
                asList(T.asTypeParameter(), U.asTypeParameter(), V.asTypeParameter()),
                Optional.of(OPTIONAL.of(V)),
                "myFunction",
                asList(
                        Parameter.of(OPTIONAL.of(T), "left"),
                        Parameter.of(OPTIONAL.of(U), "right"),
                        Parameter.of(BI_FUNCTION.of(SUPER.type(T), SUPER.type(U), EXTENDS.type(V)), "compose")
                ),
                PackageName.of("nl.wernerdegroot.applicatives").asPackage().containingClass(emptySet(), ClassName.of("Outer"), A, B)
        );

        ValidatedMethod expected = ValidatedMethod.valid(
                emptyList(),
                emptyList(),
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
