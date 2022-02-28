package nl.wernerdegroot.applicatives.processor.domain.typeconstructor;

import nl.wernerdegroot.applicatives.processor.domain.FullyQualifiedName;
import nl.wernerdegroot.applicatives.processor.domain.TypeParameterName;
import nl.wernerdegroot.applicatives.processor.domain.type.ArrayType;
import nl.wernerdegroot.applicatives.processor.domain.type.ConcreteType;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.emptyList;
import static nl.wernerdegroot.applicatives.processor.domain.BoundType.EXTENDS;
import static nl.wernerdegroot.applicatives.processor.domain.BoundType.SUPER;
import static org.junit.jupiter.api.Assertions.*;

public class ArrayTypeConstructorTest {

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
        ArrayTypeConstructor expected = new ArrayTypeConstructor(new PlaceholderTypeConstructor());
        ArrayTypeConstructor toVerify = ArrayTypeConstructor.of(new PlaceholderTypeConstructor());

        assertEquals(expected, toVerify);
    }

    @Test
    public void replaceAllTypeParameterNamesGivenMappingThatContainsElementTypeConstructor() {
        Map<TypeParameterName, TypeParameterName> mapping = new HashMap<>();
        mapping.put(T, A);
        mapping.put(U, B);

        ArrayTypeConstructor expected = new ArrayTypeConstructor(new GenericTypeConstructor(A));
        ArrayTypeConstructor toVerify = new ArrayTypeConstructor(new GenericTypeConstructor(T)).replaceAllTypeParameterNames(mapping);

        assertEquals(expected, toVerify);
    }

    @Test
    public void replaceAllTypeParameterNamesGivenMappingThatDoesNotContainElementTypeConstructor() {
        Map<TypeParameterName, TypeParameterName> mapping = new HashMap<>();
        mapping.put(T, A);
        mapping.put(U, B);

        ArrayTypeConstructor expected = new ArrayTypeConstructor(new GenericTypeConstructor(V));
        ArrayTypeConstructor toVerify = new ArrayTypeConstructor(new GenericTypeConstructor(V)).replaceAllTypeParameterNames(mapping);

        assertEquals(expected, toVerify);
    }

    @Test
    public void invariantArrayTypeConstructorCanAcceptGivenConcreteTypeConstructor() {
        assertFalse(new ArrayTypeConstructor(STRING_TYPE_CONSTRUCTOR).canAccept(STRING_TYPE_CONSTRUCTOR));
    }

    @Test
    public void invariantArrayTypeConstructorCanAcceptGivenDifferentInvariantArrayTypeConstructor() {
        assertFalse(new ArrayTypeConstructor(STRING_TYPE_CONSTRUCTOR).canAccept(new ArrayTypeConstructor(INTEGER_TYPE_CONSTRUCTOR)));
    }

    @Test
    public void invariantArrayTypeConstructorCanAcceptGivenEquivalentInvariantArrayTypeConstructor() {
        assertTrue(new ArrayTypeConstructor(STRING_TYPE_CONSTRUCTOR).canAccept(new ArrayTypeConstructor(STRING_TYPE_CONSTRUCTOR)));
    }

    @Test
    public void invariantArrayTypeConstructorCanAcceptGivenEquivalentCovariantArrayTypeConstructor() {
        assertFalse(new ArrayTypeConstructor(STRING_TYPE_CONSTRUCTOR).canAccept(new ArrayTypeConstructor(new WildcardTypeConstructor(EXTENDS, STRING_TYPE_CONSTRUCTOR))));
    }

    @Test
    public void invariantArrayTypeConstructorCanAcceptGivenEquivalentContravariantArrayTypeConstructor() {
        assertFalse(new ArrayTypeConstructor(STRING_TYPE_CONSTRUCTOR).canAccept(new ArrayTypeConstructor(new WildcardTypeConstructor(SUPER, STRING_TYPE_CONSTRUCTOR))));
    }

    @Test
    public void covariantArrayTypeConstructorCanAcceptGivenDifferentCovariantArrayTypeConstructor() {
        assertFalse(new ArrayTypeConstructor(new WildcardTypeConstructor(EXTENDS, STRING_TYPE_CONSTRUCTOR)).canAccept(new ArrayTypeConstructor(new WildcardTypeConstructor(EXTENDS, INTEGER_TYPE_CONSTRUCTOR))));
    }

    @Test
    public void covariantArrayTypeConstructorCanAcceptGivenEquivalentInvariantArrayTypeConstructor() {
        assertTrue(new ArrayTypeConstructor(new WildcardTypeConstructor(EXTENDS, STRING_TYPE_CONSTRUCTOR)).canAccept(new ArrayTypeConstructor(STRING_TYPE_CONSTRUCTOR)));
    }

    @Test
    public void covariantArrayTypeConstructorCanAcceptGivenEquivalentCovariantArrayTypeConstructor() {
        assertTrue(new ArrayTypeConstructor(new WildcardTypeConstructor(EXTENDS, STRING_TYPE_CONSTRUCTOR)).canAccept(new ArrayTypeConstructor(new WildcardTypeConstructor(EXTENDS, STRING_TYPE_CONSTRUCTOR))));
    }

    @Test
    public void covariantArrayTypeConstructorCanAcceptGivenEquivalentContravariantArrayTypeConstructor() {
        assertFalse(new ArrayTypeConstructor(new WildcardTypeConstructor(EXTENDS, STRING_TYPE_CONSTRUCTOR)).canAccept(new ArrayTypeConstructor(new WildcardTypeConstructor(SUPER, STRING_TYPE_CONSTRUCTOR))));
    }

    @Test
    public void contravariantArrayTypeConstructorCanAcceptGivenDifferentContravariantArrayTypeConstructor() {
        assertFalse(new ArrayTypeConstructor(new WildcardTypeConstructor(SUPER, STRING_TYPE_CONSTRUCTOR)).canAccept(new ArrayTypeConstructor(new WildcardTypeConstructor(SUPER, INTEGER_TYPE_CONSTRUCTOR))));
    }

    @Test
    public void contravariantArrayTypeConstructorCanAcceptGivenEquivalentInvariantArrayTypeConstructor() {
        assertTrue(new ArrayTypeConstructor(new WildcardTypeConstructor(SUPER, STRING_TYPE_CONSTRUCTOR)).canAccept(new ArrayTypeConstructor(STRING_TYPE_CONSTRUCTOR)));
    }

    @Test
    public void contravariantArrayTypeConstructorCanAcceptGivenEquivalentCovariantArrayTypeConstructor() {
        assertFalse(new ArrayTypeConstructor(new WildcardTypeConstructor(SUPER, STRING_TYPE_CONSTRUCTOR)).canAccept(new ArrayTypeConstructor(new WildcardTypeConstructor(EXTENDS, STRING_TYPE_CONSTRUCTOR))));
    }

    @Test
    public void contravariantArrayTypeConstructorCanAcceptGivenEquivalentContravariantArrayTypeConstructor() {
        assertTrue(new ArrayTypeConstructor(new WildcardTypeConstructor(SUPER, STRING_TYPE_CONSTRUCTOR)).canAccept(new ArrayTypeConstructor(new WildcardTypeConstructor(SUPER, STRING_TYPE_CONSTRUCTOR))));
    }

    @Test
    public void applyGivenTypeConstructorWithoutPlaceholder() {
        ArrayType expected = new ArrayType(STRING_TYPE);
        ArrayType toVerify = new ArrayTypeConstructor(STRING_TYPE_CONSTRUCTOR).apply(INTEGER_TYPE);

        assertEquals(expected, toVerify);
    }

    @Test
    public void applyGivenTypeConstructorWithPlaceholder() {
        ArrayType expected = new ArrayType(STRING_TYPE);
        ArrayType toVerify = new ArrayTypeConstructor(new PlaceholderTypeConstructor()).apply(STRING_TYPE);

        assertEquals(expected, toVerify);
    }
}
