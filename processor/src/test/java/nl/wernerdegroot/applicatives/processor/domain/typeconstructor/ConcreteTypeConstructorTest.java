package nl.wernerdegroot.applicatives.processor.domain.typeconstructor;

import nl.wernerdegroot.applicatives.processor.domain.FullyQualifiedName;
import nl.wernerdegroot.applicatives.processor.domain.TypeParameterName;
import nl.wernerdegroot.applicatives.processor.domain.type.ConcreteType;
import nl.wernerdegroot.applicatives.processor.domain.type.Type;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static nl.wernerdegroot.applicatives.processor.domain.typeconstructor.TypeConstructor.placeholder;
import static org.junit.jupiter.api.Assertions.*;

public class ConcreteTypeConstructorTest {

    private final TypeParameterName T = new TypeParameterName("T");
    private final TypeParameterName U = new TypeParameterName("U");
    private final TypeParameterName V = new TypeParameterName("V");
    private final TypeParameterName A = new TypeParameterName("A");
    private final TypeParameterName B = new TypeParameterName("B");

    private final FullyQualifiedName ERUDITE = new FullyQualifiedName("nl.wernerdegroot.Erudite");
    private final FullyQualifiedName PROFUSE = new FullyQualifiedName("nl.wernerdegroot.Profuse");
    private final ConcreteType STRING_TYPE = new ConcreteType(FullyQualifiedName.of("java.lang.String"), emptyList());
    private final ConcreteTypeConstructor STRING_TYPE_CONSTRUCTOR = new ConcreteTypeConstructor(FullyQualifiedName.of("java.lang.String"), emptyList());
    private final ConcreteType INTEGER_TYPE = new ConcreteType(FullyQualifiedName.of("java.lang.Integer"), emptyList());
    private final ConcreteTypeConstructor INTEGER_TYPE_CONSTRUCTOR = new ConcreteTypeConstructor(FullyQualifiedName.of("java.lang.Integer"), emptyList());
    private final ConcreteType BOOLEAN_TYPE = new ConcreteType(FullyQualifiedName.of("java.lang.Boolean"), emptyList());
    private final ConcreteTypeConstructor BOOLEAN_TYPE_CONSTRUCTOR = new ConcreteTypeConstructor(FullyQualifiedName.of("java.lang.Boolean"), emptyList());

    @Test
    public void of() {
        ConcreteTypeConstructor expected = new ConcreteTypeConstructor(
                ERUDITE,
                asList(
                        STRING_TYPE_CONSTRUCTOR.invariant(),
                        TypeConstructor.placeholder().covariant(),
                        INTEGER_TYPE_CONSTRUCTOR.contravariant()
                )
        );

        ConcreteTypeConstructor toVerify = ConcreteTypeConstructor.of(
                ERUDITE,
                asList(
                        STRING_TYPE_CONSTRUCTOR.invariant(),
                        TypeConstructor.placeholder().covariant(),
                        INTEGER_TYPE_CONSTRUCTOR.contravariant()
                )
        );

        assertEquals(expected, toVerify);
    }

    @Test
    public void replaceAllTypeParameterNamesGivenMappingThatContainsElementTypeConstructor() {
        Map<TypeParameterName, TypeParameterName> mapping = new HashMap<>();
        mapping.put(T, A);
        mapping.put(U, B);

        ConcreteTypeConstructor expected = new ConcreteTypeConstructor(
                ERUDITE,
                asList(
                        STRING_TYPE_CONSTRUCTOR.invariant(),
                        TypeConstructor.generic(A).covariant(),
                        INTEGER_TYPE_CONSTRUCTOR.contravariant()
                )
        );

        ConcreteTypeConstructor toVerify = new ConcreteTypeConstructor(
                ERUDITE,
                asList(
                        STRING_TYPE_CONSTRUCTOR.invariant(),
                        TypeConstructor.generic(T).covariant(),
                        INTEGER_TYPE_CONSTRUCTOR.contravariant()
                )
        ).replaceTypeParameterNames(mapping);

        assertEquals(expected, toVerify);
    }

    @Test
    public void replaceAllTypeParameterNamesGivenMappingThatDoesNotContainElementTypeConstructor() {
        Map<TypeParameterName, TypeParameterName> mapping = new HashMap<>();
        mapping.put(T, A);
        mapping.put(U, B);

        ConcreteTypeConstructor expected = new ConcreteTypeConstructor(
                ERUDITE,
                asList(
                        STRING_TYPE_CONSTRUCTOR.invariant(),
                        TypeConstructor.generic(V).covariant(),
                        INTEGER_TYPE_CONSTRUCTOR.contravariant()
                )
        );

        ConcreteTypeConstructor toVerify = new ConcreteTypeConstructor(
                ERUDITE,
                asList(
                        STRING_TYPE_CONSTRUCTOR.invariant(),
                        TypeConstructor.generic(V).covariant(),
                        INTEGER_TYPE_CONSTRUCTOR.contravariant()
                )
        ).replaceTypeParameterNames(mapping);

        assertEquals(expected, toVerify);
    }

    @Test
    public void referencesTypeParameterGivenConcreteTypeConstructorThatDoesNotReferenceTypeParameter() {
        ConcreteTypeConstructor concreteTypeConstructor = new ConcreteTypeConstructor(
                ERUDITE,
                asList(new GenericTypeConstructor(U).covariant())
        );
        assertFalse(concreteTypeConstructor.referencesTypeParameter(T));
    }

    @Test
    public void referencesTypeParameterGivenConcreteTypeConstructorThatReferencesTypeParameter() {
        ConcreteTypeConstructor concreteTypeConstructor = new ConcreteTypeConstructor(
                ERUDITE,
                asList(new GenericTypeConstructor(T).covariant())
        );
        assertTrue(concreteTypeConstructor.referencesTypeParameter(T));
    }


    @Test
    public void invariantConcreteTypeConstructorCanAcceptGivenGenericTypeConstructor() {
        ConcreteTypeConstructor target = TypeConstructor.concrete(ERUDITE, STRING_TYPE_CONSTRUCTOR.invariant());
        GenericTypeConstructor source = TypeConstructor.generic(T);

        assertFalse(target.canAccept(source));
    }

    @Test
    public void invariantConcreteTypeConstructorCanAcceptGivenSimilarInvariantConcreteTypeConstructorWithDifferentFullyQualifiedName() {
        ConcreteTypeConstructor target = TypeConstructor.concrete(ERUDITE, STRING_TYPE_CONSTRUCTOR.invariant());
        ConcreteTypeConstructor source = TypeConstructor.concrete(PROFUSE, STRING_TYPE_CONSTRUCTOR.invariant());

        assertFalse(target.canAccept(source));
    }

    @Test
    public void invariantConcreteTypeConstructorCanAcceptGivenSimilarInvariantConcreteTypeConstructorWithDifferentTypeParameter() {
        ConcreteTypeConstructor target = TypeConstructor.concrete(ERUDITE, STRING_TYPE_CONSTRUCTOR.invariant());
        ConcreteTypeConstructor source = TypeConstructor.concrete(ERUDITE, INTEGER_TYPE_CONSTRUCTOR.invariant());

        assertFalse(target.canAccept(source));
    }

    @Test
    public void invariantConcreteTypeConstructorCanAcceptGivenSimilarInvariantConcreteTypeConstructorWithDifferentNumberOfTypeParameters() {
        ConcreteTypeConstructor target = TypeConstructor.concrete(ERUDITE, STRING_TYPE_CONSTRUCTOR.invariant());
        ConcreteTypeConstructor source = TypeConstructor.concrete(ERUDITE, STRING_TYPE_CONSTRUCTOR.invariant(), INTEGER_TYPE_CONSTRUCTOR.covariant());

        assertFalse(target.canAccept(source));
    }

    @Test
    public void invariantConcreteTypeConstructorCanAcceptGivenEquivalentInvariantConcreteTypeConstructor() {
        ConcreteTypeConstructor target = TypeConstructor.concrete(ERUDITE, STRING_TYPE_CONSTRUCTOR.invariant());
        ConcreteTypeConstructor source = TypeConstructor.concrete(ERUDITE, STRING_TYPE_CONSTRUCTOR.invariant());

        assertTrue(target.canAccept(source));
    }

    @Test
    public void invariantConcreteTypeConstructorCanAcceptGivenEquivalentCovariantConcreteTypeConstructor() {
        ConcreteTypeConstructor target = TypeConstructor.concrete(ERUDITE, STRING_TYPE_CONSTRUCTOR.invariant());
        ConcreteTypeConstructor source = TypeConstructor.concrete(ERUDITE, STRING_TYPE_CONSTRUCTOR.covariant());

        assertFalse(target.canAccept(source));
    }

    @Test
    public void invariantConcreteTypeConstructorCanAcceptGivenEquivalentContravariantConcreteTypeConstructor() {
        ConcreteTypeConstructor target = TypeConstructor.concrete(ERUDITE, STRING_TYPE_CONSTRUCTOR.invariant());
        ConcreteTypeConstructor source = TypeConstructor.concrete(ERUDITE, STRING_TYPE_CONSTRUCTOR.contravariant());

        assertFalse(target.canAccept(source));
    }

    @Test
    public void covariantConcreteTypeConstructorCanAcceptGivenDifferentCovariantConcreteTypeConstructor() {
        ConcreteTypeConstructor target = TypeConstructor.concrete(ERUDITE, STRING_TYPE_CONSTRUCTOR.covariant());
        ConcreteTypeConstructor source = TypeConstructor.concrete(ERUDITE, INTEGER_TYPE_CONSTRUCTOR.covariant());

        assertFalse(target.canAccept(source));
    }

    @Test
    public void covariantConcreteTypeConstructorCanAcceptGivenEquivalentInvariantConcreteTypeConstructor() {
        ConcreteTypeConstructor target = TypeConstructor.concrete(ERUDITE, STRING_TYPE_CONSTRUCTOR.covariant());
        ConcreteTypeConstructor source = TypeConstructor.concrete(ERUDITE, STRING_TYPE_CONSTRUCTOR.invariant());

        assertTrue(target.canAccept(source));
    }

    @Test
    public void covariantConcreteTypeConstructorCanAcceptGivenEquivalentCovariantConcreteTypeConstructor() {
        ConcreteTypeConstructor target = TypeConstructor.concrete(ERUDITE, STRING_TYPE_CONSTRUCTOR.covariant());
        ConcreteTypeConstructor source = TypeConstructor.concrete(ERUDITE, STRING_TYPE_CONSTRUCTOR.covariant());

        assertTrue(target.canAccept(source));
    }

    @Test
    public void covariantConcreteTypeConstructorCanAcceptGivenEquivalentContravariantConcreteTypeConstructor() {
        ConcreteTypeConstructor target = TypeConstructor.concrete(ERUDITE, STRING_TYPE_CONSTRUCTOR.covariant());
        ConcreteTypeConstructor source = TypeConstructor.concrete(ERUDITE, STRING_TYPE_CONSTRUCTOR.contravariant());

        assertFalse(target.canAccept(source));
    }

    @Test
    public void contravariantConcreteTypeConstructorCanAcceptGivenDifferentContravariantConcreteTypeConstructor() {
        ConcreteTypeConstructor target = TypeConstructor.concrete(ERUDITE, STRING_TYPE_CONSTRUCTOR.contravariant());
        ConcreteTypeConstructor source = TypeConstructor.concrete(ERUDITE, INTEGER_TYPE_CONSTRUCTOR.contravariant());

        assertFalse(target.canAccept(source));
    }

    @Test
    public void contravariantConcreteTypeConstructorCanAcceptGivenEquivalentInvariantConcreteTypeConstructor() {
        ConcreteTypeConstructor target = TypeConstructor.concrete(ERUDITE, STRING_TYPE_CONSTRUCTOR.contravariant());
        ConcreteTypeConstructor source = TypeConstructor.concrete(ERUDITE, STRING_TYPE_CONSTRUCTOR.invariant());

        assertTrue(target.canAccept(source));
    }

    @Test
    public void contravariantConcreteTypeConstructorCanAcceptGivenEquivalentCovariantConcreteTypeConstructor() {
        ConcreteTypeConstructor target = TypeConstructor.concrete(ERUDITE, STRING_TYPE_CONSTRUCTOR.contravariant());
        ConcreteTypeConstructor source = TypeConstructor.concrete(ERUDITE, STRING_TYPE_CONSTRUCTOR.covariant());

        assertFalse(target.canAccept(source));
    }

    @Test
    public void contravariantConcreteTypeConstructorCanAcceptGivenEquivalentContravariantConcreteTypeConstructor() {
        ConcreteTypeConstructor target = TypeConstructor.concrete(ERUDITE, STRING_TYPE_CONSTRUCTOR.contravariant());
        ConcreteTypeConstructor source = TypeConstructor.concrete(ERUDITE, STRING_TYPE_CONSTRUCTOR.contravariant());

        assertTrue(target.canAccept(source));
    }

    @Test
    public void mixedVarianceConcreteTypeConstructorCanAcceptGivenEquivalentInvariantConcreteTypeConstructor() {
        ConcreteTypeConstructor target = TypeConstructor.concrete(ERUDITE, STRING_TYPE_CONSTRUCTOR.invariant(), INTEGER_TYPE_CONSTRUCTOR.covariant(), BOOLEAN_TYPE_CONSTRUCTOR.contravariant());
        ConcreteTypeConstructor source = TypeConstructor.concrete(ERUDITE, STRING_TYPE_CONSTRUCTOR.invariant(), INTEGER_TYPE_CONSTRUCTOR.invariant(), BOOLEAN_TYPE_CONSTRUCTOR.invariant());

        assertTrue(target.canAccept(source));
    }

    @Test
    public void applyGivenTypeConstructorWithoutPlaceholder() {
        ConcreteType expected = Type.concrete(ERUDITE, STRING_TYPE.invariant(), BOOLEAN_TYPE.contravariant(), INTEGER_TYPE.covariant());
        ConcreteType toVerify = TypeConstructor.concrete(ERUDITE, STRING_TYPE_CONSTRUCTOR.invariant(), BOOLEAN_TYPE_CONSTRUCTOR.contravariant(), INTEGER_TYPE_CONSTRUCTOR.covariant()).apply(BOOLEAN_TYPE);

        assertEquals(expected, toVerify);
    }

    @Test
    public void applyGivenTypeConstructorWithPlaceholder() {
        ConcreteType expected = Type.concrete(ERUDITE, STRING_TYPE.invariant(), BOOLEAN_TYPE.covariant(), INTEGER_TYPE.contravariant());
        ConcreteType toVerify = TypeConstructor.concrete(ERUDITE, STRING_TYPE_CONSTRUCTOR.invariant(), placeholder().covariant(), INTEGER_TYPE_CONSTRUCTOR.contravariant()).apply(BOOLEAN_TYPE);

        assertEquals(expected, toVerify);
    }
}
