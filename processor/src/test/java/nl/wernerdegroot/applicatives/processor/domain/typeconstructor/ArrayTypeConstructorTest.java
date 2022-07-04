package nl.wernerdegroot.applicatives.processor.domain.typeconstructor;

import com.jparams.verifier.tostring.ToStringVerifier;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.wernerdegroot.applicatives.processor.domain.FullyQualifiedName;
import nl.wernerdegroot.applicatives.processor.domain.TypeParameterName;
import nl.wernerdegroot.applicatives.processor.domain.type.ArrayType;
import nl.wernerdegroot.applicatives.processor.domain.type.ConcreteType;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.emptyList;
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
        ArrayTypeConstructor toVerify = new ArrayTypeConstructor(new GenericTypeConstructor(T)).replaceTypeParameterNames(mapping);

        assertEquals(expected, toVerify);
    }

    @Test
    public void replaceAllTypeParameterNamesGivenMappingThatDoesNotContainElementTypeConstructor() {
        Map<TypeParameterName, TypeParameterName> mapping = new HashMap<>();
        mapping.put(T, A);
        mapping.put(U, B);

        ArrayTypeConstructor expected = new ArrayTypeConstructor(new GenericTypeConstructor(V));
        ArrayTypeConstructor toVerify = new ArrayTypeConstructor(new GenericTypeConstructor(V)).replaceTypeParameterNames(mapping);

        assertEquals(expected, toVerify);
    }

    @Test
    public void referencesTypeParameterGivenArrayTypeConstructorThatDoesNotReferenceTypeParameter() {
        ArrayTypeConstructor arrayTypeConstructor = new ArrayTypeConstructor(new GenericTypeConstructor(U));
        assertFalse(arrayTypeConstructor.referencesTypeParameter(T));
    }

    @Test
    public void referencesTypeParameterGivenArrayTypeConstructorThatReferencesTypeParameter() {
        ArrayTypeConstructor arrayTypeConstructor = new ArrayTypeConstructor(new GenericTypeConstructor(T));
        assertTrue(arrayTypeConstructor.referencesTypeParameter(T));
    }

    @Test
    public void canAcceptGivenConcreteTypeConstructor() {
        assertFalse(new ArrayTypeConstructor(STRING_TYPE_CONSTRUCTOR).canAccept(STRING_TYPE_CONSTRUCTOR));
    }

    @Test
    public void canAcceptGivenDifferentArrayTypeConstructor() {
        assertFalse(new ArrayTypeConstructor(STRING_TYPE_CONSTRUCTOR).canAccept(new ArrayTypeConstructor(INTEGER_TYPE_CONSTRUCTOR)));
    }

    @Test
    public void canAcceptGivenEquivalentArrayTypeConstructor() {
        assertTrue(new ArrayTypeConstructor(STRING_TYPE_CONSTRUCTOR).canAccept(new ArrayTypeConstructor(STRING_TYPE_CONSTRUCTOR)));
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

    @Test
    public void equalsHashCodeToString() {
        EqualsVerifier.forClass(ArrayTypeConstructor.class).verify();
        ToStringVerifier.forClass(ArrayTypeConstructor.class).verify();
    }
}
