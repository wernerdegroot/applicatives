package nl.wernerdegroot.applicatives.processor.domain.typeconstructor;

import nl.wernerdegroot.applicatives.processor.domain.FullyQualifiedName;
import nl.wernerdegroot.applicatives.processor.domain.TypeParameterName;
import nl.wernerdegroot.applicatives.processor.domain.type.ArrayType;
import nl.wernerdegroot.applicatives.processor.domain.type.ConcreteType;
import nl.wernerdegroot.applicatives.processor.domain.type.Type;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ArrayTypeConstructorTest {

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
    public void replaceAllGivenNeedleThatMatchesTypeConstructorCompletely() {
        TypeConstructor expected = REPLACEMENT_TYPE_CONSTRUCTOR;
        TypeConstructor toVerify = new ArrayTypeConstructor(new GenericTypeConstructor(T)).replaceAll(new ArrayTypeConstructor(new GenericTypeConstructor(T)), REPLACEMENT_TYPE_CONSTRUCTOR);

        assertEquals(expected, toVerify);
    }

    @Test
    public void replaceAllGivenNeedleThatMatchesElementTypeConstructor() {
        TypeConstructor expected = new ArrayTypeConstructor(REPLACEMENT_TYPE_CONSTRUCTOR);
        TypeConstructor toVerify = new ArrayTypeConstructor(new GenericTypeConstructor(T)).replaceAll(new GenericTypeConstructor(T), REPLACEMENT_TYPE_CONSTRUCTOR);

        assertEquals(expected, toVerify);
    }

    @Test
    public void replaceAllGivenNeedleThatDoesNotMatch() {
        TypeConstructor expected = new ArrayTypeConstructor(new GenericTypeConstructor(T));
        TypeConstructor toVerify = new ArrayTypeConstructor(new GenericTypeConstructor(T)).replaceAll(NEEDLE_TYPE_CONSTRUCTOR, REPLACEMENT_TYPE_CONSTRUCTOR);

        assertEquals(expected, toVerify);
    }

    @Test
    public void applyGivenTypeConstructorWithoutPlaceholder() {
        ArrayType expected = new ArrayType(STRING_TYPE);
        ArrayType toVerify = new ArrayTypeConstructor(STRING_TYPE_CONSTRUCTOR).apply(REPLACEMENT_TYPE);

        assertEquals(expected, toVerify);
    }

    @Test
    public void applyGivenTypeConstructorWithPlaceholder() {
        ArrayType expected = new ArrayType(REPLACEMENT_TYPE);
        ArrayType toVerify = new ArrayTypeConstructor(new PlaceholderTypeConstructor()).apply(REPLACEMENT_TYPE);

        assertEquals(expected, toVerify);
    }
}
