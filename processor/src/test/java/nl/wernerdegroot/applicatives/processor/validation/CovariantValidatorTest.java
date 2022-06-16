package nl.wernerdegroot.applicatives.processor.validation;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.wernerdegroot.applicatives.processor.domain.*;
import nl.wernerdegroot.applicatives.processor.domain.containing.ContainingClass;
import nl.wernerdegroot.applicatives.processor.domain.typeconstructor.TypeConstructor;
import nl.wernerdegroot.applicatives.processor.logging.Log;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toSet;
import static nl.wernerdegroot.applicatives.processor.Classes.*;
import static nl.wernerdegroot.applicatives.processor.domain.Modifier.*;
import static nl.wernerdegroot.applicatives.processor.domain.type.Type.*;
import static nl.wernerdegroot.applicatives.processor.domain.typeconstructor.TypeConstructor.placeholder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class CovariantValidatorTest {

    private final TypeParameterName T = TypeParameterName.of("T");
    private final TypeParameterName U = TypeParameterName.of("U");
    private final TypeParameterName V = TypeParameterName.of("V");

    @Test
    public void givenValidClassAndValidMethod() {
        ContainingClass containingClass = getValidContainingClass();
        Method accumulator = getAccumulator(
                withAnnotations(COVARIANT_FULLY_QUALIFIED_NAME),
                withModifiers(PUBLIC),
                withInputTypeConstructor(LIST.with(placeholder().covariant())),
                withPartiallyAccumulatedTypeConstructor(ARRAY_LIST.with(placeholder().covariant())),
                withAccumulatedTypeConstructor(ARRAY_LIST.with(placeholder().invariant()))
        );

        Validated<Log, CovariantValidator.Result> expected = Validated.valid(
                CovariantValidator.Result.of(
                        containingClass.getTypeParameters(),
                        Optional.empty(),
                        CovariantAccumulator.of(
                                accumulator.getName(),
                                LIST.with(placeholder().covariant()),
                                ARRAY_LIST.with(placeholder().covariant()),
                                ARRAY_LIST.with(placeholder().invariant())
                        ),
                        Optional.empty()
                )
        );

        Validated<Log, CovariantValidator.Result> toVerify = CovariantValidator.validate(containingClass, accumulator);

        assertEquals(expected, toVerify);
    }

    @Test
    public void givenInvalidClassAndValidMethod() {
        Validated<Log, CovariantValidator.Result> toVerify = CovariantValidator.validate(
                getInvalidContainingClass(),
                getAccumulator(withAnnotations(COVARIANT_FULLY_QUALIFIED_NAME), withModifiers(PUBLIC), withTypeConstructors(OPTIONAL))
        );

        assertFalse(toVerify.isValid());
    }

    @Test
    public void givenValidClassAndInvalidMethod() {
        Validated<Log, CovariantValidator.Result> toVerify = CovariantValidator.validate(
                getValidContainingClass(),
                getAccumulator(withAnnotations(COVARIANT_FULLY_QUALIFIED_NAME), withModifiers(PRIVATE), withTypeConstructors(OPTIONAL))
        );

        assertFalse(toVerify.isValid());
    }

    @Test
    public void givenInvalidClassAndInvalidMethod() {
        Validated<Log, CovariantValidator.Result> toVerify = CovariantValidator.validate(
                getInvalidContainingClass(),
                getAccumulator(withAnnotations(COVARIANT_FULLY_QUALIFIED_NAME), withModifiers(PRIVATE), withTypeConstructors(OPTIONAL))
        );

        assertFalse(toVerify.isValid());
    }

    @Test
    public void givenValidClassAndMethodsContainingNoAccumulators() {
        Validated<Log, CovariantValidator.Result> toVerify = CovariantValidator.validate(
                getValidContainingClass(),
                asList()
        );

        assertFalse(toVerify.isValid());
    }

    @Test
    public void givenValidClassAndMethodsContainingMultipleAccumulators() {
        Validated<Log, CovariantValidator.Result> toVerify = CovariantValidator.validate(
                getValidContainingClass(),
                asList(
                        getAccumulator(
                                withAnnotations(ACCUMULATOR_FULLY_QUALIFIED_NAME),
                                withModifiers(PUBLIC),
                                withInputTypeConstructor(LIST.with(placeholder().covariant())),
                                withPartiallyAccumulatedTypeConstructor(ARRAY_LIST.with(placeholder().covariant())),
                                withAccumulatedTypeConstructor(ARRAY_LIST.with(placeholder().invariant())),
                                "firstAccumulator"
                        ),
                        getAccumulator(
                                withAnnotations(ACCUMULATOR_FULLY_QUALIFIED_NAME),
                                withModifiers(PUBLIC),
                                withInputTypeConstructor(LIST.with(placeholder().covariant())),
                                withPartiallyAccumulatedTypeConstructor(ARRAY_LIST.with(placeholder().covariant())),
                                withAccumulatedTypeConstructor(ARRAY_LIST.with(placeholder().invariant())),
                                "secondAccumulator"
                        )
                )
        );

        assertFalse(toVerify.isValid());
    }

    @Test
    public void givenValidClassAndMethodsContainingMultipleInitializers() {
        Validated<Log, CovariantValidator.Result> toVerify = CovariantValidator.validate(
                getValidContainingClass(),
                asList(
                        getInitializer(
                                withAnnotations(INITIALIZER_FULLY_QUALIFIED_NAME),
                                withModifiers(PUBLIC),
                                withToInitializeTypeConstructor(LIST.with(placeholder().covariant())),
                                withToInitializeTypeConstructor(ARRAY_LIST.with(placeholder().invariant())),
                                "firstInitializer"
                        ),
                        getInitializer(
                                withAnnotations(INITIALIZER_FULLY_QUALIFIED_NAME),
                                withModifiers(PUBLIC),
                                withToInitializeTypeConstructor(LIST.with(placeholder().covariant())),
                                withToInitializeTypeConstructor(ARRAY_LIST.with(placeholder().invariant())),
                                "secondInitializer"
                        ),
                        getAccumulator(
                                withAnnotations(ACCUMULATOR_FULLY_QUALIFIED_NAME),
                                withModifiers(PUBLIC),
                                withInputTypeConstructor(LIST.with(placeholder().covariant())),
                                withPartiallyAccumulatedTypeConstructor(ARRAY_LIST.with(placeholder().covariant())),
                                withAccumulatedTypeConstructor(ARRAY_LIST.with(placeholder().invariant()))
                        )
                )
        );

        assertFalse(toVerify.isValid());
    }

    @Test
    public void givenValidClassAndMethodsContainingValidAccumulatorAndValidInitializer() {
        ContainingClass containingClass = getValidContainingClass();

        Method initializer = getInitializer(
                withAnnotations(INITIALIZER_FULLY_QUALIFIED_NAME),
                withModifiers(PUBLIC),
                withToInitializeTypeConstructor(LIST.with(placeholder().covariant())),
                withToInitializeTypeConstructor(ARRAY_LIST.with(placeholder().invariant()))
        );

        Method accumulator = getAccumulator(
                withAnnotations(ACCUMULATOR_FULLY_QUALIFIED_NAME),
                withModifiers(PUBLIC),
                withInputTypeConstructor(LIST.with(placeholder().covariant())),
                withPartiallyAccumulatedTypeConstructor(ARRAY_LIST.with(placeholder().covariant())),
                withAccumulatedTypeConstructor(ARRAY_LIST.with(placeholder().invariant()))
        );

        Validated<Log, CovariantValidator.Result> expected = Validated.valid(
                CovariantValidator.Result.of(
                        containingClass.getTypeParameters(),
                        Optional.of(CovariantInitializer.of(initializer.getName(), LIST.with(placeholder().covariant()), ARRAY_LIST.with(placeholder().invariant()))),
                        CovariantAccumulator.of(
                                accumulator.getName(),
                                LIST.with(placeholder().covariant()),
                                ARRAY_LIST.with(placeholder().covariant()),
                                ARRAY_LIST.with(placeholder().invariant())
                        ),
                        Optional.empty()
                )
        );

        Validated<Log, CovariantValidator.Result> toVerify = CovariantValidator.validate(containingClass, asList(accumulator, initializer));

        assertEquals(expected, toVerify);
    }

    @Test
    public void givenValidClassAndMethodsContainingInvalidAccumulatorAndValidInitializer() {
        Validated<Log, CovariantValidator.Result> toVerify = CovariantValidator.validate(
                getValidContainingClass(),
                asList(
                        getInitializer(withAnnotations(INITIALIZER_FULLY_QUALIFIED_NAME), withModifiers(PUBLIC), withTypeConstructor(LIST), withTypeConstructor(ARRAY_LIST)),
                        getAccumulator(withAnnotations(ACCUMULATOR_FULLY_QUALIFIED_NAME), withModifiers(PRIVATE), withTypeConstructor(LIST), withTypeConstructor(ARRAY_LIST), withTypeConstructor(ARRAY_LIST))
                )
        );

        assertFalse(toVerify.isValid());
    }

    @Test
    public void givenValidClassAndMethodsContainingValidAccumulatorAndInvalidInitializer() {
        Validated<Log, CovariantValidator.Result> toVerify = CovariantValidator.validate(
                getValidContainingClass(),
                asList(
                        getInitializer(withAnnotations(INITIALIZER_FULLY_QUALIFIED_NAME), withModifiers(PRIVATE), withTypeConstructor(LIST), withTypeConstructor(ARRAY_LIST)),
                        getAccumulator(withAnnotations(ACCUMULATOR_FULLY_QUALIFIED_NAME), withModifiers(PUBLIC), withTypeConstructor(LIST), withTypeConstructor(ARRAY_LIST), withTypeConstructor(ARRAY_LIST))
                )
        );

        assertFalse(toVerify.isValid());
    }

    @Test
    public void givenInvalidClassAndMethodsContainingValidAccumulatorAndValidInitializer() {
        Validated<Log, CovariantValidator.Result> toVerify = CovariantValidator.validate(
                getInvalidContainingClass(),
                asList(
                        getInitializer(withAnnotations(INITIALIZER_FULLY_QUALIFIED_NAME), withModifiers(PUBLIC), withTypeConstructor(LIST), withTypeConstructor(ARRAY_LIST)),
                        getAccumulator(withAnnotations(ACCUMULATOR_FULLY_QUALIFIED_NAME), withModifiers(PUBLIC), withTypeConstructor(LIST), withTypeConstructor(ARRAY_LIST), withTypeConstructor(ARRAY_LIST))
                )
        );

        assertFalse(toVerify.isValid());
    }

    @Test
    public void givenValidClassAndMethodsContainingValidAccumulatorAndValidInitializerButWithoutSharedInputTypeConstructor() {
        Validated<Log, CovariantValidator.Result> toVerify = CovariantValidator.validate(
                getValidContainingClass(),
                asList(
                        getInitializer(withAnnotations(INITIALIZER_FULLY_QUALIFIED_NAME), withModifiers(PUBLIC), withTypeConstructor(ARRAY_LIST), withTypeConstructor(OPTIONAL)),
                        getAccumulator(withAnnotations(ACCUMULATOR_FULLY_QUALIFIED_NAME), withModifiers(PUBLIC), withTypeConstructor(LIST), withTypeConstructor(ARRAY_LIST), withTypeConstructor(ARRAY_LIST))
                )
        );

        assertFalse(toVerify.isValid());
    }

    @Test
    public void givenValidClassAndMethodsContainingValidAccumulatorAndValidInitializerButWithoutSharedPartiallyAccumulatedTypeConstructor() {
        Validated<Log, CovariantValidator.Result> toVerify = CovariantValidator.validate(
                getValidContainingClass(),
                asList(
                        getInitializer(withAnnotations(INITIALIZER_FULLY_QUALIFIED_NAME), withModifiers(PUBLIC), withTypeConstructor(OPTIONAL), withTypeConstructor(ARRAY_LIST)),
                        getAccumulator(withAnnotations(ACCUMULATOR_FULLY_QUALIFIED_NAME), withModifiers(PUBLIC), withTypeConstructor(LIST), withTypeConstructor(ARRAY_LIST), withTypeConstructor(ARRAY_LIST))
                )
        );

        assertFalse(toVerify.isValid());
    }

    @Test
    public void givenValidClassAndMethodsContainingMultipleFinalizers() {
        Validated<Log, CovariantValidator.Result> toVerify = CovariantValidator.validate(
                getValidContainingClass(),
                asList(
                        getAccumulator(
                                withAnnotations(ACCUMULATOR_FULLY_QUALIFIED_NAME),
                                withModifiers(PUBLIC),
                                withInputTypeConstructor(LIST.with(placeholder().covariant())),
                                withPartiallyAccumulatedTypeConstructor(ARRAY_LIST.with(placeholder().covariant())),
                                withAccumulatedTypeConstructor(ARRAY_LIST.with(placeholder().invariant()))
                        ),
                        getFinalizer(
                                withAnnotations(FINALIZER_FULLY_QUALIFIED_NAME),
                                withModifiers(PUBLIC),
                                withToFinalizeTypeConstructor(ARRAY_LIST.with(placeholder().covariant())),
                                withToFinalizeTypeConstructor(LIST.with(placeholder().invariant())),
                                "firstFinalizer"
                        ),
                        getFinalizer(
                                withAnnotations(FINALIZER_FULLY_QUALIFIED_NAME),
                                withModifiers(PUBLIC),
                                withToFinalizeTypeConstructor(ARRAY_LIST.with(placeholder().covariant())),
                                withToFinalizeTypeConstructor(LIST.with(placeholder().invariant())),
                                "secondFinalizer"
                        )
                )
        );

        assertFalse(toVerify.isValid());
    }

    @Test
    public void givenValidClassAndMethodsContainingValidAccumulatorAndValidFinalizer() {
        ContainingClass containingClass = getValidContainingClass();

        Method accumulator = getAccumulator(
                withAnnotations(ACCUMULATOR_FULLY_QUALIFIED_NAME),
                withModifiers(PUBLIC),
                withInputTypeConstructor(LIST.with(placeholder().covariant())),
                withPartiallyAccumulatedTypeConstructor(ARRAY_LIST.with(placeholder().covariant())),
                withAccumulatedTypeConstructor(ARRAY_LIST.with(placeholder().invariant()))
        );

        Method finalizer = getFinalizer(
                withAnnotations(FINALIZER_FULLY_QUALIFIED_NAME),
                withModifiers(PUBLIC),
                withToFinalizeTypeConstructor(ARRAY_LIST.with(placeholder().covariant())),
                withFinalizedTypeConstructor(LIST.with(placeholder().invariant()))
        );

        Validated<Log, CovariantValidator.Result> expected = Validated.valid(
                CovariantValidator.Result.of(
                        containingClass.getTypeParameters(),
                        Optional.empty(),
                        CovariantAccumulator.of(
                                accumulator.getName(),
                                LIST.with(placeholder().covariant()),
                                ARRAY_LIST.with(placeholder().covariant()),
                                ARRAY_LIST.with(placeholder().invariant())
                        ),
                        Optional.of(CovariantFinalizer.of(finalizer.getName(), ARRAY_LIST.with(placeholder().covariant()), LIST.with(placeholder().invariant())))
                )
        );

        Validated<Log, CovariantValidator.Result> toVerify = CovariantValidator.validate(containingClass, asList(accumulator, finalizer));

        assertEquals(expected, toVerify);
    }

    @Test
    public void givenValidClassAndMethodsContainingInvalidAccumulatorAndValidFinalizer() {
        Validated<Log, CovariantValidator.Result> toVerify = CovariantValidator.validate(
                getValidContainingClass(),
                asList(
                        getAccumulator(withAnnotations(ACCUMULATOR_FULLY_QUALIFIED_NAME), withModifiers(PRIVATE), withTypeConstructor(LIST), withTypeConstructor(ARRAY_LIST), withTypeConstructor(ARRAY_LIST)),
                        getFinalizer(withAnnotations(FINALIZER_FULLY_QUALIFIED_NAME), withModifiers(PUBLIC), withTypeConstructor(ARRAY_LIST), withTypeConstructor(LIST))
                )
        );

        assertFalse(toVerify.isValid());
    }

    @Test
    public void givenValidClassAndMethodsContainingValidAccumulatorAndInvalidFinalizer() {
        Validated<Log, CovariantValidator.Result> toVerify = CovariantValidator.validate(
                getValidContainingClass(),
                asList(
                        getAccumulator(withAnnotations(ACCUMULATOR_FULLY_QUALIFIED_NAME), withModifiers(PUBLIC), withTypeConstructor(LIST), withTypeConstructor(ARRAY_LIST), withTypeConstructor(ARRAY_LIST)),
                        getFinalizer(withAnnotations(FINALIZER_FULLY_QUALIFIED_NAME), withModifiers(PRIVATE), withTypeConstructor(ARRAY_LIST), withTypeConstructor(LIST))
                )
        );

        assertFalse(toVerify.isValid());
    }

    @Test
    public void givenInvalidClassAndMethodsContainingValidAccumulatorAndValidFinalizer() {
        Validated<Log, CovariantValidator.Result> toVerify = CovariantValidator.validate(
                getInvalidContainingClass(),
                asList(
                        getAccumulator(withAnnotations(ACCUMULATOR_FULLY_QUALIFIED_NAME), withModifiers(PUBLIC), withTypeConstructor(LIST), withTypeConstructor(ARRAY_LIST), withTypeConstructor(ARRAY_LIST)),
                        getFinalizer(withAnnotations(FINALIZER_FULLY_QUALIFIED_NAME), withModifiers(PUBLIC), withTypeConstructor(ARRAY_LIST), withTypeConstructor(LIST))
                )
        );

        assertFalse(toVerify.isValid());
    }

    @Test
    public void givenValidClassAndMethodsContainingValidAccumulatorAndValidFinalizerButWithoutSharedTypeConstructor() {
        Validated<Log, CovariantValidator.Result> toVerify = CovariantValidator.validate(
                getValidContainingClass(),
                asList(
                        getAccumulator(withAnnotations(ACCUMULATOR_FULLY_QUALIFIED_NAME), withModifiers(PUBLIC), withTypeConstructor(LIST), withTypeConstructor(ARRAY_LIST), withTypeConstructor(ARRAY_LIST)),
                        getFinalizer(withAnnotations(FINALIZER_FULLY_QUALIFIED_NAME), withModifiers(PUBLIC), withTypeConstructor(OPTIONAL), withTypeConstructor(LIST))
                )
        );

        assertFalse(toVerify.isValid());
    }

    @Test
    public void resultEquals() {
        EqualsVerifier.forClass(CovariantValidator.Result.class).verify();
    }

    private ContainingClass getValidContainingClass() {
        return PackageName.of("nl.wernerdegroot.applicatives")
                .asPackage()
                .containingClass(withModifiers(), ClassName.of("Outer"))
                .containingClass(withModifiers(STATIC), ClassName.of("Inner"));
    }

    private ContainingClass getInvalidContainingClass() {
        return PackageName.of("nl.wernerdegroot.applicatives")
                .asPackage()
                .containingClass(withModifiers(), ClassName.of("Outer"))
                .containingClass(withModifiers(), ClassName.of("Inner"));
    }

    private Set<FullyQualifiedName> withAnnotations(FullyQualifiedName... annotations) {
        return Stream.of(annotations).collect(toSet());
    }

    private TypeConstructor withToInitializeTypeConstructor(TypeConstructor typeConstructor) {
        return typeConstructor;
    }

    private TypeConstructor withInitializedTypeConstructor(TypeConstructor typeConstructor) {
        return typeConstructor;
    }

    private TypeConstructor withAccumulatedTypeConstructor(TypeConstructor typeConstructor) {
        return typeConstructor;
    }

    private TypeConstructor withPartiallyAccumulatedTypeConstructor(TypeConstructor typeConstructor) {
        return typeConstructor;
    }

    private TypeConstructor withInputTypeConstructor(TypeConstructor typeConstructor) {
        return typeConstructor;
    }

    private TypeConstructor withToFinalizeTypeConstructor(TypeConstructor typeConstructor) {
        return typeConstructor;
    }

    private TypeConstructor withFinalizedTypeConstructor(TypeConstructor typeConstructor) {
        return typeConstructor;
    }

    private TypeConstructor withTypeConstructors(TypeBuilder typeBuilder) {
        return typeBuilder.asTypeConstructor();
    }

    private TypeConstructor withTypeConstructor(TypeBuilder typeBuilder) {
        return typeBuilder.asTypeConstructor();
    }

    private Set<Modifier> withModifiers(Modifier... modifiers) {
        return Stream.of(modifiers).collect(toSet());
    }

    private Method getInitializer(Set<FullyQualifiedName> annotations, Set<Modifier> modifiers, TypeConstructor typeConstructor) {
        return getInitializer(annotations, modifiers, typeConstructor, typeConstructor, "initializer");
    }

    private Method getInitializer(Set<FullyQualifiedName> annotations, Set<Modifier> modifiers, TypeConstructor toInitializeTypeConstructor, TypeConstructor initializedTypeConstructor) {
        return getInitializer(annotations, modifiers, toInitializeTypeConstructor, initializedTypeConstructor, "initializer");
    }

    private Method getInitializer(Set<FullyQualifiedName> annotations, Set<Modifier> modifiers, TypeConstructor toInitializeTypeConstructor, TypeConstructor initializedTypeConstructor, String name) {
        return Method.of(
                annotations,
                modifiers,
                asList(T.asTypeParameter()),
                Optional.of(initializedTypeConstructor.apply(T.asType())),
                name,
                asList(Parameter.of(toInitializeTypeConstructor.apply(T.asType()), "value"))
        );
    }

    private Method getAccumulator(Set<FullyQualifiedName> annotations, Set<Modifier> modifiers, TypeConstructor typeConstructor) {
        return getAccumulator(annotations, modifiers, typeConstructor, typeConstructor, typeConstructor);
    }

    private Method getAccumulator(Set<FullyQualifiedName> annotations, Set<Modifier> modifiers, TypeConstructor inputTypeConstructor, TypeConstructor partiallyAccumulatedTypeConstructor, TypeConstructor accumulatedTypeConstructor) {
        return getAccumulator(annotations, modifiers, inputTypeConstructor, partiallyAccumulatedTypeConstructor, accumulatedTypeConstructor, "accumulator");
    }

    private Method getAccumulator(Set<FullyQualifiedName> annotations, Set<Modifier> modifiers, TypeConstructor inputTypeConstructor, TypeConstructor partiallyAccumulatedTypeConstructor, TypeConstructor accumulatedTypeConstructor, String name) {
        return Method.of(
                annotations,
                modifiers,
                asList(T.asTypeParameter(), U.asTypeParameter(), V.asTypeParameter()),
                Optional.of(accumulatedTypeConstructor.apply(V.asType())),
                name,
                asList(
                        Parameter.of(partiallyAccumulatedTypeConstructor.apply(T.asType()), "left"),
                        Parameter.of(inputTypeConstructor.apply(U.asType()), "right"),
                        Parameter.of(BI_FUNCTION.with(T.asType().contravariant(), U.asType().contravariant(), V.asType().covariant()), "compose")
                )
        );
    }

    private Method getFinalizer(Set<FullyQualifiedName> annotations, Set<Modifier> modifiers, TypeConstructor toFinalizeTypeConstructor, TypeConstructor finalizedTypeConstructor) {
        return getFinalizer(annotations, modifiers, toFinalizeTypeConstructor, finalizedTypeConstructor, "finalize");
    }

    private Method getFinalizer(Set<FullyQualifiedName> annotations, Set<Modifier> modifiers, TypeConstructor toFinalizeTypeConstructor, TypeConstructor finalizedTypeConstructor, String name) {
        return Method.of(
                annotations,
                modifiers,
                asList(T.asTypeParameter()),
                Optional.of(finalizedTypeConstructor.apply(T.asType())),
                name,
                asList(Parameter.of(toFinalizeTypeConstructor.apply(T.asType()), "value"))
        );
    }
}