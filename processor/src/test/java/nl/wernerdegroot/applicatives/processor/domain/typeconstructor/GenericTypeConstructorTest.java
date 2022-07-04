package nl.wernerdegroot.applicatives.processor.domain.typeconstructor;

import com.jparams.verifier.tostring.ToStringVerifier;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.wernerdegroot.applicatives.processor.domain.FullyQualifiedName;
import nl.wernerdegroot.applicatives.processor.domain.TypeParameterName;
import nl.wernerdegroot.applicatives.processor.domain.type.ConcreteType;
import nl.wernerdegroot.applicatives.processor.domain.type.GenericType;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.*;

public class GenericTypeConstructorTest {

    private final TypeParameterName T = TypeParameterName.of("T");
    private final TypeParameterName U = TypeParameterName.of("U");
    private final TypeParameterName V = TypeParameterName.of("V");
    private final TypeParameterName A = TypeParameterName.of("A");
    private final TypeParameterName B = TypeParameterName.of("B");

    private final ConcreteType STRING_TYPE = new ConcreteType(new FullyQualifiedName("java.lang.String"), emptyList());

    @Test
    public void of() {
        GenericTypeConstructor expected = new GenericTypeConstructor(T);
        GenericTypeConstructor toVerify = GenericTypeConstructor.of(T);

        assertEquals(expected, toVerify);
    }

    @Test
    public void replaceAllTypeParameterNamesGivenMappingThatContainsTypeParameterName() {
        Map<TypeParameterName, TypeParameterName> mapping = new HashMap<>();
        mapping.put(T, A);
        mapping.put(U, B);

        GenericTypeConstructor expected = new GenericTypeConstructor(A);
        GenericTypeConstructor toVerify = new GenericTypeConstructor(T).replaceTypeParameterNames(mapping);

        assertEquals(expected, toVerify);
    }

    @Test
    public void replaceAllTypeParameterNamesGivenMappingThatDoesNotContainTypeParameterName() {
        Map<TypeParameterName, TypeParameterName> mapping = new HashMap<>();
        mapping.put(T, A);
        mapping.put(U, B);

        GenericTypeConstructor expected = new GenericTypeConstructor(V);
        GenericTypeConstructor toVerify = new GenericTypeConstructor(V).replaceTypeParameterNames(mapping);

        assertEquals(expected, toVerify);
    }

    @Test
    public void referencesTypeParameterGivenGenericTypeConstructorThatDoesNotReferenceTypeParameter() {
        GenericTypeConstructor genericTypeConstructor = new GenericTypeConstructor(U);
        assertFalse(genericTypeConstructor.referencesTypeParameter(T));
    }

    @Test
    public void referencesTypeParameterGivenGenericTypeConstructorThatReferencesTypeParameter() {
        GenericTypeConstructor genericTypeConstructor = new GenericTypeConstructor(T);
        assertTrue(genericTypeConstructor.referencesTypeParameter(T));
    }

    @Test
    public void genericTypeConstructorCanAcceptGivenPlaceholderTypeConstructor() {
        assertFalse(new GenericTypeConstructor(T).canAccept(new PlaceholderTypeConstructor()));
    }

    @Test
    public void genericTypeConstructorCanAcceptGivenEquivalentGenericTypeConstructor() {
        assertTrue(new GenericTypeConstructor(T).canAccept(new GenericTypeConstructor(T)));
    }

    @Test
    public void genericTypeConstructorCanAcceptGivenDifferentGenericTypeConstructor() {
        assertFalse(new GenericTypeConstructor(T).canAccept(new GenericTypeConstructor(U)));
    }

    @Test
    public void apply() {
        GenericType expected = new GenericType(T);
        GenericType toVerify = new GenericTypeConstructor(T).apply(STRING_TYPE);

        assertEquals(expected, toVerify);
    }

    @Test
    public void equalsHashCodeToString() {
        EqualsVerifier.forClass(GenericTypeConstructor.class).verify();
        ToStringVerifier.forClass(GenericTypeConstructor.class).verify();
    }
}
