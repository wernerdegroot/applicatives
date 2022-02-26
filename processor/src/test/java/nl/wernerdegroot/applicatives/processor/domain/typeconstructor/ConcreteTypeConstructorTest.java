package nl.wernerdegroot.applicatives.processor.domain.typeconstructor;

import nl.wernerdegroot.applicatives.processor.domain.FullyQualifiedName;
import nl.wernerdegroot.applicatives.processor.domain.TypeParameterName;
import nl.wernerdegroot.applicatives.processor.domain.type.ConcreteType;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static nl.wernerdegroot.applicatives.processor.domain.BoundType.EXTENDS;
import static nl.wernerdegroot.applicatives.processor.domain.BoundType.SUPER;
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
        ConcreteTypeConstructor expected = new ConcreteTypeConstructor(ERUDITE, asList(STRING_TYPE_CONSTRUCTOR, new PlaceholderTypeConstructor(), INTEGER_TYPE_CONSTRUCTOR));
        ConcreteTypeConstructor toVerify = ConcreteTypeConstructor.of(ERUDITE, asList(STRING_TYPE_CONSTRUCTOR, new PlaceholderTypeConstructor(), INTEGER_TYPE_CONSTRUCTOR));

        assertEquals(expected, toVerify);
    }

    @Test
    public void replaceAllTypeParameterNamesGivenMappingThatContainsElementTypeConstructor() {
        Map<TypeParameterName, TypeParameterName> mapping = new HashMap<>();
        mapping.put(T, A);
        mapping.put(U, B);

        ConcreteTypeConstructor expected = new ConcreteTypeConstructor(ERUDITE, asList(STRING_TYPE_CONSTRUCTOR, new GenericTypeConstructor(A), INTEGER_TYPE_CONSTRUCTOR));
        ConcreteTypeConstructor toVerify = new ConcreteTypeConstructor(ERUDITE, asList(STRING_TYPE_CONSTRUCTOR, new GenericTypeConstructor(T), INTEGER_TYPE_CONSTRUCTOR)).replaceAllTypeParameterNames(mapping);

        assertEquals(expected, toVerify);
    }

    @Test
    public void replaceAllTypeParameterNamesGivenMappingThatDoesNotContainElementTypeConstructor() {
        Map<TypeParameterName, TypeParameterName> mapping = new HashMap<>();
        mapping.put(T, A);
        mapping.put(U, B);

        ConcreteTypeConstructor expected = new ConcreteTypeConstructor(ERUDITE, asList(STRING_TYPE_CONSTRUCTOR, new GenericTypeConstructor(V), INTEGER_TYPE_CONSTRUCTOR));
        ConcreteTypeConstructor toVerify = new ConcreteTypeConstructor(ERUDITE, asList(STRING_TYPE_CONSTRUCTOR, new GenericTypeConstructor(V), INTEGER_TYPE_CONSTRUCTOR)).replaceAllTypeParameterNames(mapping);

        assertEquals(expected, toVerify);
    }

    @Test
    public void invariantConcreteTypeConstructorCanAcceptValueOfTypeGivenGenericTypeConstructor() {
        assertFalse(new ConcreteTypeConstructor(ERUDITE, asList(STRING_TYPE_CONSTRUCTOR)).canAcceptValueOfType(new GenericTypeConstructor(T)));
    }

    @Test
    public void invariantConcreteTypeConstructorCanAcceptValueOfTypeGivenSimilarInvariantConcreteTypeConstructorWithDifferentFullyQualifiedName() {
        assertFalse(new ConcreteTypeConstructor(ERUDITE, asList(STRING_TYPE_CONSTRUCTOR)).canAcceptValueOfType(new ConcreteTypeConstructor(PROFUSE, asList(STRING_TYPE_CONSTRUCTOR))));
    }

    @Test
    public void invariantConcreteTypeConstructorCanAcceptValueOfTypeGivenSimilarInvariantConcreteTypeConstructorWithDifferentTypeParameter() {
        assertFalse(new ConcreteTypeConstructor(ERUDITE, asList(STRING_TYPE_CONSTRUCTOR)).canAcceptValueOfType(new ConcreteTypeConstructor(ERUDITE, asList(INTEGER_TYPE_CONSTRUCTOR))));
    }

    @Test
    public void invariantConcreteTypeConstructorCanAcceptValueOfTypeGivenSimilarInvariantConcreteTypeConstructorWithDifferentNumberOfTypeParameters() {
        assertFalse(new ConcreteTypeConstructor(ERUDITE, asList(STRING_TYPE_CONSTRUCTOR)).canAcceptValueOfType(new ConcreteTypeConstructor(ERUDITE, asList(STRING_TYPE_CONSTRUCTOR, INTEGER_TYPE_CONSTRUCTOR))));
    }

    @Test
    public void invariantConcreteTypeConstructorCanAcceptValueOfTypeGivenEquivalentInvariantConcreteTypeConstructor() {
        assertTrue(new ConcreteTypeConstructor(ERUDITE, asList(STRING_TYPE_CONSTRUCTOR)).canAcceptValueOfType(new ConcreteTypeConstructor(ERUDITE, asList(STRING_TYPE_CONSTRUCTOR))));
    }

    @Test
    public void invariantConcreteTypeConstructorCanAcceptValueOfTypeGivenEquivalentCovariantConcreteTypeConstructor() {
        assertFalse(new ConcreteTypeConstructor(ERUDITE, asList(STRING_TYPE_CONSTRUCTOR)).canAcceptValueOfType(new ConcreteTypeConstructor(ERUDITE, asList(new WildcardTypeConstructor(EXTENDS, STRING_TYPE_CONSTRUCTOR)))));
    }

    @Test
    public void invariantConcreteTypeConstructorCanAcceptValueOfTypeGivenEquivalentContravariantConcreteTypeConstructor() {
        assertFalse(new ConcreteTypeConstructor(ERUDITE, asList(STRING_TYPE_CONSTRUCTOR)).canAcceptValueOfType(new ConcreteTypeConstructor(ERUDITE, asList(new WildcardTypeConstructor(SUPER, STRING_TYPE_CONSTRUCTOR)))));
    }

    @Test
    public void covariantConcreteTypeConstructorCanAcceptValueOfTypeGivenDifferentCovariantConcreteTypeConstructor() {
        assertFalse(new ConcreteTypeConstructor(ERUDITE, asList(new WildcardTypeConstructor(EXTENDS, STRING_TYPE_CONSTRUCTOR))).canAcceptValueOfType(new ConcreteTypeConstructor(ERUDITE, asList(new WildcardTypeConstructor(EXTENDS, INTEGER_TYPE_CONSTRUCTOR)))));
    }

    @Test
    public void covariantConcreteTypeConstructorCanAcceptValueOfTypeGivenEquivalentInvariantConcreteTypeConstructor() {
        assertTrue(new ConcreteTypeConstructor(ERUDITE, asList(new WildcardTypeConstructor(EXTENDS, STRING_TYPE_CONSTRUCTOR))).canAcceptValueOfType(new ConcreteTypeConstructor(ERUDITE, asList(STRING_TYPE_CONSTRUCTOR))));
    }

    @Test
    public void covariantConcreteTypeConstructorCanAcceptValueOfTypeGivenEquivalentCovariantConcreteTypeConstructor() {
        assertTrue(new ConcreteTypeConstructor(ERUDITE, asList(new WildcardTypeConstructor(EXTENDS, STRING_TYPE_CONSTRUCTOR))).canAcceptValueOfType(new ConcreteTypeConstructor(ERUDITE, asList(new WildcardTypeConstructor(EXTENDS, STRING_TYPE_CONSTRUCTOR)))));
    }

    @Test
    public void covariantConcreteTypeConstructorCanAcceptValueOfTypeGivenEquivalentContravariantConcreteTypeConstructor() {
        assertFalse(new ConcreteTypeConstructor(ERUDITE, asList(new WildcardTypeConstructor(EXTENDS, STRING_TYPE_CONSTRUCTOR))).canAcceptValueOfType(new ConcreteTypeConstructor(ERUDITE, asList(new WildcardTypeConstructor(SUPER, STRING_TYPE_CONSTRUCTOR)))));
    }

    @Test
    public void contravariantConcreteTypeConstructorCanAcceptValueOfTypeGivenDifferentContravariantConcreteTypeConstructor() {
        assertFalse(new ConcreteTypeConstructor(ERUDITE, asList(new WildcardTypeConstructor(SUPER, STRING_TYPE_CONSTRUCTOR))).canAcceptValueOfType(new ConcreteTypeConstructor(ERUDITE, asList(new WildcardTypeConstructor(SUPER, INTEGER_TYPE_CONSTRUCTOR)))));
    }

    @Test
    public void contravariantConcreteTypeConstructorCanAcceptValueOfTypeGivenEquivalentInvariantConcreteTypeConstructor() {
        assertTrue(new ConcreteTypeConstructor(ERUDITE, asList(new WildcardTypeConstructor(SUPER, STRING_TYPE_CONSTRUCTOR))).canAcceptValueOfType(new ConcreteTypeConstructor(ERUDITE, asList(STRING_TYPE_CONSTRUCTOR))));
    }

    @Test
    public void contravariantConcreteTypeConstructorCanAcceptValueOfTypeGivenEquivalentCovariantConcreteTypeConstructor() {
        assertFalse(new ConcreteTypeConstructor(ERUDITE, asList(new WildcardTypeConstructor(SUPER, STRING_TYPE_CONSTRUCTOR))).canAcceptValueOfType(new ConcreteTypeConstructor(ERUDITE, asList(new WildcardTypeConstructor(EXTENDS, STRING_TYPE_CONSTRUCTOR)))));
    }

    @Test
    public void contravariantConcreteTypeConstructorCanAcceptValueOfTypeGivenEquivalentContravariantConcreteTypeConstructor() {
        assertTrue(new ConcreteTypeConstructor(ERUDITE, asList(new WildcardTypeConstructor(SUPER, STRING_TYPE_CONSTRUCTOR))).canAcceptValueOfType(new ConcreteTypeConstructor(ERUDITE, asList(new WildcardTypeConstructor(SUPER, STRING_TYPE_CONSTRUCTOR)))));
    }

    @Test
    public void mixedVarianceConcreteTypeConstructorCanAcceptValueOfTypeGivenEquivalentInvariantConcreteTypeConstructor() {
        assertTrue(new ConcreteTypeConstructor(ERUDITE, asList(STRING_TYPE_CONSTRUCTOR, new WildcardTypeConstructor(EXTENDS, INTEGER_TYPE_CONSTRUCTOR), new WildcardTypeConstructor(SUPER, BOOLEAN_TYPE_CONSTRUCTOR))).canAcceptValueOfType(new ConcreteTypeConstructor(ERUDITE, asList(STRING_TYPE_CONSTRUCTOR, INTEGER_TYPE_CONSTRUCTOR, BOOLEAN_TYPE_CONSTRUCTOR))));
    }

    @Test
    public void applyGivenTypeConstructorWithoutPlaceholder() {
        ConcreteType expected = new ConcreteType(ERUDITE, asList(STRING_TYPE, INTEGER_TYPE));
        ConcreteType toVerify = new ConcreteTypeConstructor(ERUDITE, asList(STRING_TYPE_CONSTRUCTOR, INTEGER_TYPE_CONSTRUCTOR)).apply(BOOLEAN_TYPE);

        assertEquals(expected, toVerify);
    }

    @Test
    public void applyGivenTypeConstructorWithPlaceholder() {
        ConcreteType expected = new ConcreteType(ERUDITE, asList(STRING_TYPE, BOOLEAN_TYPE, INTEGER_TYPE));
        ConcreteType toVerify = new ConcreteTypeConstructor(ERUDITE, asList(STRING_TYPE_CONSTRUCTOR, new PlaceholderTypeConstructor(), INTEGER_TYPE_CONSTRUCTOR)).apply(BOOLEAN_TYPE);

        assertEquals(expected, toVerify);
    }
}
