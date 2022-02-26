package nl.wernerdegroot.applicatives.processor.domain.typeconstructor;

import nl.wernerdegroot.applicatives.processor.domain.FullyQualifiedName;
import nl.wernerdegroot.applicatives.processor.domain.TypeParameterName;
import nl.wernerdegroot.applicatives.processor.domain.type.ConcreteType;
import nl.wernerdegroot.applicatives.processor.domain.type.WildcardType;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.emptyList;
import static nl.wernerdegroot.applicatives.processor.domain.BoundType.EXTENDS;
import static nl.wernerdegroot.applicatives.processor.domain.BoundType.SUPER;
import static org.junit.jupiter.api.Assertions.*;

public class WildcardTypeConstructorTest {

    private final TypeParameterName T = TypeParameterName.of("T");
    private final TypeParameterName U = TypeParameterName.of("U");
    private final TypeParameterName V = TypeParameterName.of("V");
    private final TypeParameterName A = TypeParameterName.of("A");
    private final TypeParameterName B = TypeParameterName.of("B");

    private final ConcreteType STRING_TYPE = new ConcreteType(FullyQualifiedName.of("java.lang.String"), emptyList());
    private final ConcreteTypeConstructor STRING_TYPE_CONSTRUCTOR = new ConcreteTypeConstructor(FullyQualifiedName.of("java.lang.String"), emptyList());
    private final ConcreteType INTEGER_TYPE = new ConcreteType(FullyQualifiedName.of("java.lang.Integer"), emptyList());
    private final ConcreteTypeConstructor INTEGER_TYPE_CONSTRUCTOR = new ConcreteTypeConstructor(FullyQualifiedName.of("java.lang.Integer"), emptyList());

    @Test
    public void of() {
        WildcardTypeConstructor expected = new WildcardTypeConstructor(SUPER, new PlaceholderTypeConstructor());
        WildcardTypeConstructor toVerify = WildcardTypeConstructor.of(SUPER, new PlaceholderTypeConstructor());

        assertEquals(expected, toVerify);
    }

    @Test
    public void replaceAllTypeParameterNamesGivenMappingThatContainsBoundTypeConstructor() {
        Map<TypeParameterName, TypeParameterName> mapping = new HashMap<>();
        mapping.put(T, A);
        mapping.put(U, B);

        WildcardTypeConstructor expected = new WildcardTypeConstructor(EXTENDS, new GenericTypeConstructor(A));
        WildcardTypeConstructor toVerify = new WildcardTypeConstructor(EXTENDS, new GenericTypeConstructor(T)).replaceAllTypeParameterNames(mapping);

        assertEquals(expected, toVerify);
    }

    @Test
    public void replaceAllTypeParameterNamesGivenMappingThatDoesNotContainBoundTypeConstructor() {
        Map<TypeParameterName, TypeParameterName> mapping = new HashMap<>();
        mapping.put(T, A);
        mapping.put(U, B);

        WildcardTypeConstructor expected = new WildcardTypeConstructor(SUPER, new GenericTypeConstructor(V));
        WildcardTypeConstructor toVerify = new WildcardTypeConstructor(SUPER, new GenericTypeConstructor(V)).replaceAllTypeParameterNames(mapping);

        assertEquals(expected, toVerify);
    }

    @Test
    public void covariantWildcardTypeConstructorCanAcceptValueOfTypeGivenEquivalentInvariantTypeConstructor() {
        assertTrue(new WildcardTypeConstructor(EXTENDS, STRING_TYPE_CONSTRUCTOR).canAcceptValueOfType(STRING_TYPE_CONSTRUCTOR));
    }

    @Test
    public void covariantWildcardTypeConstructorCanAcceptValueOfTypeGivenDifferentInvariantTypeConstructor() {
        assertFalse(new WildcardTypeConstructor(EXTENDS, STRING_TYPE_CONSTRUCTOR).canAcceptValueOfType(INTEGER_TYPE_CONSTRUCTOR));
    }

    @Test
    public void covariantWildcardTypeConstructorCanAcceptValueOfTypeGivenEquivalentCovariantTypeConstructor() {
        assertTrue(new WildcardTypeConstructor(EXTENDS, STRING_TYPE_CONSTRUCTOR).canAcceptValueOfType(new WildcardTypeConstructor(EXTENDS, STRING_TYPE_CONSTRUCTOR)));
    }

    @Test
    public void covariantWildcardTypeConstructorCanAcceptValueOfTypeGivenEquivalentContravariantTypeConstructor() {
        assertFalse(new WildcardTypeConstructor(EXTENDS, STRING_TYPE_CONSTRUCTOR).canAcceptValueOfType(new WildcardTypeConstructor(SUPER, STRING_TYPE_CONSTRUCTOR)));
    }

    @Test
    public void contravariantWildcardTypeConstructorCanAcceptValueOfTypeGivenEquivalentInvariantTypeConstructor() {
        assertTrue(new WildcardTypeConstructor(SUPER, STRING_TYPE_CONSTRUCTOR).canAcceptValueOfType(STRING_TYPE_CONSTRUCTOR));
    }

    @Test
    public void contravariantWildcardTypeConstructorCanAcceptValueOfTypeGivenDifferentInvariantTypeConstructor() {
        assertFalse(new WildcardTypeConstructor(SUPER, STRING_TYPE_CONSTRUCTOR).canAcceptValueOfType(INTEGER_TYPE_CONSTRUCTOR));
    }

    @Test
    public void contravariantWildcardTypeConstructorCanAcceptValueOfTypeGivenEquivalentCovariantTypeConstructor() {
        assertFalse(new WildcardTypeConstructor(SUPER, STRING_TYPE_CONSTRUCTOR).canAcceptValueOfType(new WildcardTypeConstructor(EXTENDS, STRING_TYPE_CONSTRUCTOR)));
    }

    @Test
    public void contravariantWildcardTypeConstructorCanAcceptValueOfTypeGivenEquivalentContravariantTypeConstructor() {
        assertTrue(new WildcardTypeConstructor(SUPER, STRING_TYPE_CONSTRUCTOR).canAcceptValueOfType(new WildcardTypeConstructor(SUPER, STRING_TYPE_CONSTRUCTOR)));
    }

    @Test
    public void applyGivenTypeConstructorWithoutPlaceholder() {
        WildcardType expected = new WildcardType(SUPER, INTEGER_TYPE);
        WildcardType toVerify = new WildcardTypeConstructor(SUPER, INTEGER_TYPE_CONSTRUCTOR).apply(STRING_TYPE);

        assertEquals(expected, toVerify);
    }

    @Test
    public void applyGivenTypeConstructorWithPlaceholder() {
        WildcardType expected = new WildcardType(EXTENDS, STRING_TYPE);
        WildcardType toVerify = new WildcardTypeConstructor(EXTENDS, new PlaceholderTypeConstructor()).apply(STRING_TYPE);

        assertEquals(expected, toVerify);
    }
}
