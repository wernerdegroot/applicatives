package nl.wernerdegroot.applicatives.processor.domain.typeconstructor;

import nl.wernerdegroot.applicatives.processor.domain.FullyQualifiedName;
import nl.wernerdegroot.applicatives.processor.domain.TypeParameterName;
import nl.wernerdegroot.applicatives.processor.domain.type.ConcreteType;
import nl.wernerdegroot.applicatives.processor.domain.type.Type;
import nl.wernerdegroot.applicatives.processor.domain.type.WildcardType;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.emptyList;
import static nl.wernerdegroot.applicatives.processor.domain.BoundType.EXTENDS;
import static nl.wernerdegroot.applicatives.processor.domain.BoundType.SUPER;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class WildcardTypeConstructorTest {

    private final TypeParameterName T = TypeParameterName.of("T");
    private final TypeParameterName U = TypeParameterName.of("U");
    private final TypeParameterName V = TypeParameterName.of("V");
    private final TypeParameterName A = TypeParameterName.of("A");
    private final TypeParameterName B = TypeParameterName.of("B");

    private final ConcreteType STRING_TYPE = new ConcreteType(FullyQualifiedName.of("java.lang.String"), emptyList());
    private final ConcreteTypeConstructor STRING_TYPE_CONSTRUCTOR = new ConcreteTypeConstructor(FullyQualifiedName.of("java.lang.String"), emptyList());

    private final TypeConstructor NEEDLE_TYPE_CONSTRUCTOR = new ConcreteTypeConstructor(new FullyQualifiedName("nl.wernerdegroot.Needle"), emptyList());
    private final Type REPLACEMENT_TYPE = new ConcreteType(new FullyQualifiedName("nl.wernerdegroot.Replacement"), emptyList());
    private final TypeConstructor REPLACEMENT_TYPE_CONSTRUCTOR = new ConcreteTypeConstructor(new FullyQualifiedName("nl.wernerdegroot.Replacement"), emptyList());

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
    public void replaceAllGivenNeedleThatMatchesTypeConstructorCompletely() {
        TypeConstructor expected = REPLACEMENT_TYPE_CONSTRUCTOR;
        TypeConstructor toVerify = new WildcardTypeConstructor(EXTENDS, new GenericTypeConstructor(T)).replaceAll(new WildcardTypeConstructor(EXTENDS, new GenericTypeConstructor(T)), REPLACEMENT_TYPE_CONSTRUCTOR);

        assertEquals(expected, toVerify);
    }

    @Test
    public void replaceAllGivenNeedleThatMatchesBoundTypeConstructor() {
        TypeConstructor expected = new WildcardTypeConstructor(SUPER, REPLACEMENT_TYPE_CONSTRUCTOR);
        TypeConstructor toVerify = new WildcardTypeConstructor(SUPER, new GenericTypeConstructor(T)).replaceAll(new GenericTypeConstructor(T), REPLACEMENT_TYPE_CONSTRUCTOR);

        assertEquals(expected, toVerify);
    }

    @Test
    public void replaceAllGivenNeedleThatDoesNotMatch() {
        TypeConstructor expected = new WildcardTypeConstructor(EXTENDS, new GenericTypeConstructor(T));
        TypeConstructor toVerify = new WildcardTypeConstructor(EXTENDS, new GenericTypeConstructor(T)).replaceAll(NEEDLE_TYPE_CONSTRUCTOR, REPLACEMENT_TYPE_CONSTRUCTOR);

        assertEquals(expected, toVerify);
    }

    @Test
    public void applyGivenTypeConstructorWithoutPlaceholder() {
        WildcardType expected = new WildcardType(SUPER, STRING_TYPE);
        WildcardType toVerify = new WildcardTypeConstructor(SUPER, STRING_TYPE_CONSTRUCTOR).apply(REPLACEMENT_TYPE);

        assertEquals(expected, toVerify);
    }

    @Test
    public void applyGivenTypeConstructorWithPlaceholder() {
        WildcardType expected = new WildcardType(EXTENDS, REPLACEMENT_TYPE);
        WildcardType toVerify = new WildcardTypeConstructor(EXTENDS, new PlaceholderTypeConstructor()).apply(REPLACEMENT_TYPE);

        assertEquals(expected, toVerify);
    }
}
