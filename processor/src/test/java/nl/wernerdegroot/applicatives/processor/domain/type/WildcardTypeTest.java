package nl.wernerdegroot.applicatives.processor.domain.type;

import nl.wernerdegroot.applicatives.processor.domain.FullyQualifiedName;
import nl.wernerdegroot.applicatives.processor.domain.TypeParameterName;
import nl.wernerdegroot.applicatives.processor.domain.typeconstructor.*;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.emptyList;
import static nl.wernerdegroot.applicatives.processor.domain.BoundType.EXTENDS;
import static nl.wernerdegroot.applicatives.processor.domain.BoundType.SUPER;
import static org.junit.jupiter.api.Assertions.*;

public class WildcardTypeTest {

    private final TypeParameterName T = TypeParameterName.of("T");
    private final TypeParameterName U = TypeParameterName.of("U");
    private final TypeParameterName V = TypeParameterName.of("V");
    private final TypeParameterName A = TypeParameterName.of("A");
    private final TypeParameterName B = TypeParameterName.of("B");

    private final ConcreteType STRING_TYPE = new ConcreteType(FullyQualifiedName.of("java.lang.String"), emptyList());
    private final ConcreteTypeConstructor STRING_TYPE_CONSTRUCTOR = new ConcreteTypeConstructor(FullyQualifiedName.of("java.lang.String"), emptyList());

    @Test
    public void of() {
        WildcardType expected = new WildcardType(SUPER, STRING_TYPE);
        WildcardType toVerify = WildcardType.of(SUPER, STRING_TYPE);

        assertEquals(expected, toVerify);
    }

    // Test for `match` not necessary. If it compiles, it must work.

    @Test
    public void asTypeConstructor() {
        WildcardTypeConstructor expected = new WildcardTypeConstructor(EXTENDS, STRING_TYPE_CONSTRUCTOR);
        WildcardTypeConstructor toVerify = new WildcardType(EXTENDS, STRING_TYPE).asTypeConstructor();

        assertEquals(expected, toVerify);
    }

    @Test
    public void asTypeConstructorWithPlaceHolderForGivenNeedleThatMatchesBoundType() {
        TypeConstructor expected = new WildcardTypeConstructor(SUPER, new PlaceholderTypeConstructor());
        TypeConstructor toVerify = new WildcardType(SUPER, new GenericType(T)).asTypeConstructorWithPlaceholderFor(T);

        assertEquals(expected, toVerify);
    }

    @Test
    public void asTypeConstructorWithPlaceHolderForGivenNeedleThatDoesNotMatchBoundType() {
        TypeConstructor expected = new WildcardTypeConstructor(EXTENDS, new GenericTypeConstructor(U));
        TypeConstructor toVerify = new WildcardType(EXTENDS, new GenericType(U)).asTypeConstructorWithPlaceholderFor(T);

        assertEquals(expected, toVerify);
    }

    @Test
    public void containsGivenNeedleThatMatchesBoundType() {
        assertTrue(new WildcardType(SUPER, new GenericType(T)).contains(T));
    }

    @Test
    public void containsGivenNeedleThatDoesNotMatchBoundType() {
        assertFalse(new WildcardType(EXTENDS, new GenericType(U)).contains(T));
    }

    @Test
    public void replaceAllTypeParameterNamesGivenMappingThatContainsBoundType() {
        Map<TypeParameterName, TypeParameterName> mapping = new HashMap<>();
        mapping.put(T, A);
        mapping.put(U, B);

        WildcardType expected = new WildcardType(SUPER, new GenericType(A));
        WildcardType toVerify = new WildcardType(SUPER, new GenericType(T)).replaceAllTypeParameterNames(mapping);

        assertEquals(expected, toVerify);
    }

    @Test
    public void replaceAllTypeParameterNamesGivenMappingThatDoesNotContainBoundType() {
        Map<TypeParameterName, TypeParameterName> mapping = new HashMap<>();
        mapping.put(T, A);
        mapping.put(U, B);

        WildcardType expected = new WildcardType(EXTENDS, new GenericType(V));
        WildcardType toVerify = new WildcardType(EXTENDS, new GenericType(V)).replaceAllTypeParameterNames(mapping);

        assertEquals(expected, toVerify);
    }
}
