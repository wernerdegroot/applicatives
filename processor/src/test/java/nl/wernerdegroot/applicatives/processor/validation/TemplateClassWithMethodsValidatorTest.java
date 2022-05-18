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
import static org.junit.jupiter.api.Assertions.*;

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
                withAccumulationTypeConstructor(ARRAY_LIST.with(placeholder().invariant())),
                withPermissiveAccumulationTypeConstructor(ARRAY_LIST.with(placeholder().covariant())),
                withInputTypeConstructor(LIST.with(placeholder().covariant()))
        );

        Validated<TemplateClassWithMethods> expected = Validated.valid(
                TemplateClassWithMethods.of(
                        containingClass.getTypeParameters(),
                        ARRAY_LIST.with(placeholder().invariant()),
                        ARRAY_LIST.with(placeholder().covariant()),
                        LIST.with(placeholder().covariant()),
                        Optional.empty(),
                        Optional.empty(),
                        accumulator.getName(),
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
        Validated<TemplateClassWithMethods> toVerify = TemplateClassWithMethodsValidator.validate(
                getValidContainingClass(),
                asList(
                        getInitializer(withAnnotations(INITIALIZER), withModifiers(PUBLIC), withTypeConstructor(OPTIONAL)),
                        getAccumulator(withAnnotations(ACCUMULATOR), withModifiers(PUBLIC), withTypeConstructors(OPTIONAL))
                )
        );

        assertTrue(toVerify.isValid());
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
        Validated<TemplateClassWithMethods> toVerify = TemplateClassWithMethodsValidator.validate(
                getValidContainingClass(),
                asList(
                        getAccumulator(withAnnotations(ACCUMULATOR), withModifiers(PUBLIC), withTypeConstructor(ARRAY_LIST), withTypeConstructor(ARRAY_LIST), withTypeConstructor(LIST)),
                        getFinalizer(withAnnotations(FINALIZER), withModifiers(PUBLIC), withTypeConstructor(ARRAY_LIST), withTypeConstructor(LIST))
                )
        );

        assertTrue(toVerify.isValid());
    }

    @Test
    public void givenValidClassAndMethodsContainingInvalidAccumulatorAndValidFinalizer() {
        Validated<TemplateClassWithMethods> toVerify = TemplateClassWithMethodsValidator.validate(
                getValidContainingClass(),
                asList(
                        getAccumulator(withAnnotations(ACCUMULATOR), withModifiers(PRIVATE), withTypeConstructor(ARRAY_LIST), withTypeConstructor(ARRAY_LIST), withTypeConstructor(LIST)),
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
                        getAccumulator(withAnnotations(ACCUMULATOR), withModifiers(PUBLIC), withTypeConstructor(ARRAY_LIST), withTypeConstructor(ARRAY_LIST), withTypeConstructor(LIST)),
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
                        getAccumulator(withAnnotations(ACCUMULATOR), withModifiers(PUBLIC), withTypeConstructor(ARRAY_LIST), withTypeConstructor(ARRAY_LIST), withTypeConstructor(LIST)),
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
                        getAccumulator(withAnnotations(ACCUMULATOR), withModifiers(PUBLIC), withTypeConstructor(ARRAY_LIST), withTypeConstructor(ARRAY_LIST), withTypeConstructor(LIST)),
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

    private TypeConstructor withAccumulationTypeConstructor(TypeConstructor typeConstructor) {
        return typeConstructor;
    }

    private TypeConstructor withPermissiveAccumulationTypeConstructor(TypeConstructor typeConstructor) {
        return typeConstructor;
    }

    private TypeConstructor withInputTypeConstructor(TypeConstructor typeConstructor) {
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

    private Method getAccumulator(Set<FullyQualifiedName> annotations, Set<Modifier> modifiers, TypeConstructor accumulationTypeConstructor, TypeConstructor permissiveAccumulationTypeConstructor, TypeConstructor inputTypeConstructor) {
        return Method.of(
                annotations,
                modifiers,
                asList(T.asTypeParameter(), U.asTypeParameter(), V.asTypeParameter()),
                Optional.of(accumulationTypeConstructor.apply(V.asType())),
                "accumulator",
                asList(
                        Parameter.of(permissiveAccumulationTypeConstructor.apply(T.asType()), "left"),
                        Parameter.of(inputTypeConstructor.apply(U.asType()), "right"),
                        Parameter.of(BI_FUNCTION.with(T.asType().contravariant(), U.asType().contravariant(), V.asType().covariant()), "compose")
                )
        );
    }

    private Method getFinalizer(Set<FullyQualifiedName> annotations, Set<Modifier> modifiers, TypeConstructor permissiveAccumulationTypeConstructor, TypeConstructor resultTypeConstructor) {
        return Method.of(
                annotations,
                modifiers,
                asList(T.asTypeParameter()),
                Optional.of(resultTypeConstructor.apply(T.asType())),
                "finalizer",
                asList(Parameter.of(permissiveAccumulationTypeConstructor.apply(T.asType()), "value"))
        );
    }
}