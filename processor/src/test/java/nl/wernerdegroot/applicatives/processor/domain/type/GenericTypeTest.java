package nl.wernerdegroot.applicatives.processor.domain.type;

import com.jparams.verifier.tostring.ToStringVerifier;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.wernerdegroot.applicatives.processor.domain.TypeParameterName;
import nl.wernerdegroot.applicatives.processor.domain.typeconstructor.GenericTypeConstructor;
import nl.wernerdegroot.applicatives.processor.domain.typeconstructor.PlaceholderTypeConstructor;
import nl.wernerdegroot.applicatives.processor.domain.typeconstructor.TypeConstructor;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class GenericTypeTest {

    private final TypeParameterName T = new TypeParameterName("T");
    private final TypeParameterName U = new TypeParameterName("U");
    private final TypeParameterName V = new TypeParameterName("V");
    private final TypeParameterName A = new TypeParameterName("A");
    private final TypeParameterName B = new TypeParameterName("B");

    @Test
    public void of() {
        GenericType expected = new GenericType(T);
        GenericType toVerify = GenericType.of(T);

        assertEquals(expected, toVerify);
    }

    // Test for `match` not necessary. If it compiles, it must work.

    @Test
    public void asTypeConstructor() {
        GenericTypeConstructor expected = new GenericTypeConstructor(T);
        GenericTypeConstructor toVerify = new GenericType(T).asTypeConstructor();

        assertEquals(expected, toVerify);
    }

    @Test
    public void asTypeConstructorWithPlaceholderForGivenNeedleThatMatches() {
        TypeConstructor expected = new PlaceholderTypeConstructor();
        TypeConstructor toVerify = new GenericType(T).asTypeConstructorWithPlaceholderFor(T);

        assertEquals(expected, toVerify);
    }

    @Test
    public void asTypeConstructorWithPlaceholderForGivenNeedleThatDoesNotMatch() {
        TypeConstructor expected = new GenericTypeConstructor(U);
        TypeConstructor toVerify = new GenericType(U).asTypeConstructorWithPlaceholderFor(T);

        assertEquals(expected, toVerify);
    }

    @Test
    public void replaceAllTypeParameterNamesGivenMappingThatContainsTypeParameterName() {
        Map<TypeParameterName, TypeParameterName> mapping = new HashMap<>();
        mapping.put(T, A);
        mapping.put(U, B);

        GenericType expected = new GenericType(A);
        GenericType toVerify = new GenericType(T).replaceAllTypeParameterNames(mapping);

        assertEquals(expected, toVerify);
    }

    @Test
    public void replaceAllTypeParameterNamesGivenMappingThatDoesNotContainTypeParameterName() {
        Map<TypeParameterName, TypeParameterName> mapping = new HashMap<>();
        mapping.put(T, A);
        mapping.put(U, B);

        GenericType expected = new GenericType(V);
        GenericType toVerify = new GenericType(V).replaceAllTypeParameterNames(mapping);

        assertEquals(expected, toVerify);
    }

    @Test
    public void equalsHashCodeToString() {
        EqualsVerifier.forClass(GenericType.class).verify();
        ToStringVerifier.forClass(GenericType.class).verify();
    }
}
