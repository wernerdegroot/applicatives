package nl.wernerdegroot.applicatives.processor.domain.type;

import nl.wernerdegroot.applicatives.processor.domain.FullyQualifiedName;
import nl.wernerdegroot.applicatives.processor.domain.TypeParameterName;
import nl.wernerdegroot.applicatives.processor.domain.typeconstructor.*;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.emptyList;
import static nl.wernerdegroot.applicatives.processor.domain.type.Type.STRING;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ArrayTypeTest {

    private final TypeParameterName T = TypeParameterName.of("T");
    private final TypeParameterName U = TypeParameterName.of("U");
    private final TypeParameterName V = TypeParameterName.of("V");
    private final TypeParameterName A = TypeParameterName.of("A");
    private final TypeParameterName B = TypeParameterName.of("B");

    private final ConcreteType STRING_TYPE = new ConcreteType(FullyQualifiedName.of("java.lang.String"), emptyList());
    private final ConcreteTypeConstructor STRING_TYPE_CONSTRUCTOR = new ConcreteTypeConstructor(FullyQualifiedName.of("java.lang.String"), emptyList());

    @Test
    public void of() {
        ArrayType expected = new ArrayType(STRING);
        ArrayType toVerify = ArrayType.of(STRING);

        assertEquals(expected, toVerify);
    }

    // Test for `match` not necessary. If it compiles, it must work.

    @Test
    public void asTypeConstructor() {
        ArrayTypeConstructor expected = new ArrayTypeConstructor(STRING_TYPE_CONSTRUCTOR);
        ArrayTypeConstructor toVerify = new ArrayType(STRING_TYPE).asTypeConstructor();

        assertEquals(expected, toVerify);
    }

    @Test
    public void asTypeConstructorWithPlaceHolderForGivenNeedleThatMatchesElementType() {
        TypeConstructor expected = new ArrayTypeConstructor(new PlaceholderTypeConstructor());
        TypeConstructor toVerify = new ArrayType(new GenericType(T)).asTypeConstructorWithPlaceholderFor(T);

        assertEquals(expected, toVerify);
    }

    @Test
    public void asTypeConstructorWithPlaceHolderForGivenNeedleThatDoesNotMatchElementType() {
        TypeConstructor expected = new ArrayTypeConstructor(new GenericTypeConstructor(U));
        TypeConstructor toVerify = new ArrayType(new GenericType(U)).asTypeConstructorWithPlaceholderFor(T);

        assertEquals(expected, toVerify);
    }

    @Test
    public void replaceAllTypeParameterNamesGivenMappingThatContainsElementType() {
        Map<TypeParameterName, TypeParameterName> mapping = new HashMap<>();
        mapping.put(T, A);
        mapping.put(U, B);

        ArrayType expected = new ArrayType(new GenericType(A));
        ArrayType toVerify = new ArrayType(new GenericType(T)).replaceAllTypeParameterNames(mapping);

        assertEquals(expected, toVerify);
    }

    @Test
    public void replaceAllTypeParameterNamesGivenMappingThatDoesNotContainElementType() {
        Map<TypeParameterName, TypeParameterName> mapping = new HashMap<>();
        mapping.put(T, A);
        mapping.put(U, B);

        ArrayType expected = new ArrayType(new GenericType(V));
        ArrayType toVerify = new ArrayType(new GenericType(V)).replaceAllTypeParameterNames(mapping);

        assertEquals(expected, toVerify);
    }
}
