package nl.wernerdegroot.applicatives.processor.validation;

import nl.wernerdegroot.applicatives.processor.domain.*;
import nl.wernerdegroot.applicatives.processor.domain.containing.ContainingClass;
import nl.wernerdegroot.applicatives.processor.domain.typeconstructor.TypeConstructor;
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

public class TemplateClassWithMethodsValidatorTest {

    private final TypeParameterName T = TypeParameterName.of("T");
    private final TypeParameterName U = TypeParameterName.of("U");
    private final TypeParameterName V = TypeParameterName.of("V");

    @Test
    public void givenValidClassAndValidMethod() {
        ContainingClass containingClass = getValidContainingClass();
        Method accumulator = getAccumulator(
                withAnnotations(COVARIANT),
                withModifiers(PUBLIC),
                withInputTypeConstructor(LIST.with(placeholder().covariant())), withPartiallyAccumulatedTypeConstructor(ARRAY_LIST.with(placeholder().covariant())), withAccumulatedTypeConstructor(ARRAY_LIST.with(placeholder().invariant()))
        );

        Validated<TemplateClassWithMethods> expected = Validated.valid(
                TemplateClassWithMethods.of(
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

        Validated<TemplateClassWithMethods> toVerify = TemplateClassWithMethodsValidator.validate(containingClass, accumulator);

        assertEquals(expected, toVerify);
    }

    @Test
    public void givenInvalidClassAndValidMethod() {
        Validated<TemplateClassWithMethods> toVerify = TemplateClassWithMethodsValidator.validate(
                getInvalidContainingClass(),
                getAccumulator(withAnnotations(COVARIANT), withModifiers(PUBLIC), withTypeConstructors(OPTIONAL))
        );

        assertFalse(toVerify.isValid());
    }

    @Test
    public void givenValidClassAndInvalidMethod() {
        Validated<TemplateClassWithMethods> toVerify = TemplateClassWithMethodsValidator.validate(
                getValidContainingClass(),
                getAccumulator(withAnnotations(COVARIANT), withModifiers(PRIVATE), withTypeConstructors(OPTIONAL))
        );

        assertFalse(toVerify.isValid());
    }

    @Test
    public void givenInvalidClassAndInvalidMethod() {
        Validated<TemplateClassWithMethods> toVerify = TemplateClassWithMethodsValidator.validate(
                getInvalidContainingClass(),
                getAccumulator(withAnnotations(COVARIANT), withModifiers(PRIVATE), withTypeConstructors(OPTIONAL))
        );

        assertFalse(toVerify.isValid());
    }

    @Test
    public void givenValidClassAndMethodsContainingValidInitializerAndValidAccumulator() {
        ContainingClass containingClass = getValidContainingClass();

        Method initializer = getInitializer(
                withAnnotations(INITIALIZER),
                withModifiers(PUBLIC),
                withInitializedTypeConstructor(ARRAY_LIST.with(placeholder().invariant()))
        );

        Method accumulator = getAccumulator(
                withAnnotations(ACCUMULATOR),
                withModifiers(PUBLIC),
                withInputTypeConstructor(LIST.with(placeholder().covariant())),
                withPartiallyAccumulatedTypeConstructor(ARRAY_LIST.with(placeholder().covariant())),
                withAccumulatedTypeConstructor(ARRAY_LIST.with(placeholder().invariant()))
        );

        Validated<TemplateClassWithMethods> expected = Validated.valid(
                TemplateClassWithMethods.of(
                        containingClass.getTypeParameters(),
                        Optional.of(CovariantInitializer.of(initializer.getName(), ARRAY_LIST.with(placeholder().invariant()))),
                        CovariantAccumulator.of(
                                accumulator.getName(),
                                LIST.with(placeholder().covariant()),
                                ARRAY_LIST.with(placeholder().covariant()),
                                ARRAY_LIST.with(placeholder().invariant())
                        ),
                        Optional.empty()
                )
        );

        Validated<TemplateClassWithMethods> toVerify = TemplateClassWithMethodsValidator.validate(containingClass, asList(initializer, accumulator));

        assertEquals(expected, toVerify);
    }

    @Test
    public void givenValidClassAndMethodsContainingValidInitializerAndInvalidAccumulator() {
        Validated<TemplateClassWithMethods> toVerify = TemplateClassWithMethodsValidator.validate(
                getValidContainingClass(),
                asList(
                        getInitializer(withAnnotations(INITIALIZER), withModifiers(PRIVATE), withTypeConstructor(OPTIONAL)),
                        getAccumulator(withAnnotations(ACCUMULATOR), withModifiers(PUBLIC), withTypeConstructors(OPTIONAL))
                )
        );

        assertFalse(toVerify.isValid());
    }

    @Test
    public void givenValidClassAndMethodsContainingInvalidInitializerAndValidAccumulator() {
        Validated<TemplateClassWithMethods> toVerify = TemplateClassWithMethodsValidator.validate(
                getValidContainingClass(),
                asList(
                        getInitializer(withAnnotations(INITIALIZER), withModifiers(PUBLIC), withTypeConstructor(OPTIONAL)),
                        getAccumulator(withAnnotations(ACCUMULATOR), withModifiers(PRIVATE), withTypeConstructors(OPTIONAL))
                )
        );

        assertFalse(toVerify.isValid());
    }

    @Test
    public void givenInvalidClassAndMethodsContainingValidInitializerAndValidAccumulator() {
        Validated<TemplateClassWithMethods> toVerify = TemplateClassWithMethodsValidator.validate(
                getInvalidContainingClass(),
                asList(
                        getInitializer(withAnnotations(INITIALIZER), withModifiers(PUBLIC), withTypeConstructor(OPTIONAL)),
                        getAccumulator(withAnnotations(ACCUMULATOR), withModifiers(PUBLIC), withTypeConstructors(OPTIONAL))
                )
        );

        assertFalse(toVerify.isValid());
    }

    @Test
    public void givenValidClassAndMethodsContainingValidInitializerAndValidAccumulatorButWithoutSharedTypeConstructor() {
        Validated<TemplateClassWithMethods> toVerify = TemplateClassWithMethodsValidator.validate(
                getInvalidContainingClass(),
                asList(
                        getInitializer(withAnnotations(INITIALIZER), withModifiers(PUBLIC), withTypeConstructor(OPTIONAL)),
                        getAccumulator(withAnnotations(ACCUMULATOR), withModifiers(PUBLIC), withTypeConstructors(LIST))
                )
        );

        assertFalse(toVerify.isValid());
    }

    @Test
    public void givenValidClassAndMethodsContainingValidAccumulatorAndValidFinalizer() {
        ContainingClass containingClass = getValidContainingClass();

        Method accumulator = getAccumulator(
                withAnnotations(ACCUMULATOR),
                withModifiers(PUBLIC),
                withInputTypeConstructor(LIST.with(placeholder().covariant())),
                withPartiallyAccumulatedTypeConstructor(ARRAY_LIST.with(placeholder().covariant())),
                withAccumulatedTypeConstructor(ARRAY_LIST.with(placeholder().invariant()))
        );

        Method finalizer = getFinalizer(
                withAnnotations(FINALIZER),
                withModifiers(PUBLIC),
                withToFinalizeTypeConstructor(ARRAY_LIST.with(placeholder().covariant())),
                withFinalizedTypeConstructor(LIST.with(placeholder().invariant()))
        );

        Validated<TemplateClassWithMethods> expected = Validated.valid(
                TemplateClassWithMethods.of(
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

        Validated<TemplateClassWithMethods> toVerify = TemplateClassWithMethodsValidator.validate(containingClass, asList(accumulator, finalizer));

        assertEquals(expected, toVerify);
    }

    @Test
    public void givenValidClassAndMethodsContainingInvalidAccumulatorAndValidFinalizer() {
        Validated<TemplateClassWithMethods> toVerify = TemplateClassWithMethodsValidator.validate(
                getValidContainingClass(),
                asList(
                        getAccumulator(withAnnotations(ACCUMULATOR), withModifiers(PRIVATE), withTypeConstructor(LIST), withTypeConstructor(ARRAY_LIST), withTypeConstructor(ARRAY_LIST)),
                        getFinalizer(withAnnotations(FINALIZER), withModifiers(PUBLIC), withTypeConstructor(ARRAY_LIST), withTypeConstructor(LIST))
                )
        );

        assertFalse(toVerify.isValid());
    }

    @Test
    public void givenValidClassAndMethodsContainingValidAccumulatorAndInvalidFinalizer() {
        Validated<TemplateClassWithMethods> toVerify = TemplateClassWithMethodsValidator.validate(
                getValidContainingClass(),
                asList(
                        getAccumulator(withAnnotations(ACCUMULATOR), withModifiers(PUBLIC), withTypeConstructor(LIST), withTypeConstructor(ARRAY_LIST), withTypeConstructor(ARRAY_LIST)),
                        getFinalizer(withAnnotations(FINALIZER), withModifiers(PRIVATE), withTypeConstructor(ARRAY_LIST), withTypeConstructor(LIST))
                )
        );

        assertFalse(toVerify.isValid());
    }

    @Test
    public void givenInvalidClassAndMethodsContainingValidAccumulatorAndValidFinalizer() {
        Validated<TemplateClassWithMethods> toVerify = TemplateClassWithMethodsValidator.validate(
                getInvalidContainingClass(),
                asList(
                        getAccumulator(withAnnotations(ACCUMULATOR), withModifiers(PUBLIC), withTypeConstructor(LIST), withTypeConstructor(ARRAY_LIST), withTypeConstructor(ARRAY_LIST)),
                        getFinalizer(withAnnotations(FINALIZER), withModifiers(PUBLIC), withTypeConstructor(ARRAY_LIST), withTypeConstructor(LIST))
                )
        );

        assertFalse(toVerify.isValid());
    }

    @Test
    public void givenValidClassAndMethodsContainingValidAccumulatorAndValidFinalizerButWithoutSharedTypeConstructor() {
        Validated<TemplateClassWithMethods> toVerify = TemplateClassWithMethodsValidator.validate(
                getInvalidContainingClass(),
                asList(
                        getAccumulator(withAnnotations(ACCUMULATOR), withModifiers(PUBLIC), withTypeConstructor(LIST), withTypeConstructor(ARRAY_LIST), withTypeConstructor(ARRAY_LIST)),
                        getFinalizer(withAnnotations(FINALIZER), withModifiers(PUBLIC), withTypeConstructor(OPTIONAL), withTypeConstructor(LIST))
                )
        );

        assertFalse(toVerify.isValid());
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
        return Method.of(
                annotations,
                modifiers,
                asList(T.asTypeParameter()),
                Optional.of(typeConstructor.apply(T.asType())),
                "initializer",
                asList(Parameter.of(T.asType(), "value"))
        );
    }

    private Method getAccumulator(Set<FullyQualifiedName> annotations, Set<Modifier> modifiers, TypeConstructor typeConstructor) {
        return getAccumulator(annotations, modifiers, typeConstructor, typeConstructor, typeConstructor);
    }

    private Method getAccumulator(Set<FullyQualifiedName> annotations, Set<Modifier> modifiers, TypeConstructor inputTypeConstructor, TypeConstructor partiallyAccumulatedTypeConstructor, TypeConstructor accumulatedTypeConstructor) {
        return Method.of(
                annotations,
                modifiers,
                asList(T.asTypeParameter(), U.asTypeParameter(), V.asTypeParameter()),
                Optional.of(accumulatedTypeConstructor.apply(V.asType())),
                "accumulator",
                asList(
                        Parameter.of(partiallyAccumulatedTypeConstructor.apply(T.asType()), "left"),
                        Parameter.of(inputTypeConstructor.apply(U.asType()), "right"),
                        Parameter.of(BI_FUNCTION.with(T.asType().contravariant(), U.asType().contravariant(), V.asType().covariant()), "compose")
                )
        );
    }

    private Method getFinalizer(Set<FullyQualifiedName> annotations, Set<Modifier> modifiers, TypeConstructor toFinalizeTypeConstructor, TypeConstructor finalizedTypeConstructor) {
        return Method.of(
                annotations,
                modifiers,
                asList(T.asTypeParameter()),
                Optional.of(finalizedTypeConstructor.apply(T.asType())),
                "finalizer",
                asList(Parameter.of(toFinalizeTypeConstructor.apply(T.asType()), "value"))
        );
    }
}