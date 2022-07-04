package nl.wernerdegroot.applicatives.processor.domain.typeconstructor;

import com.jparams.verifier.tostring.ToStringVerifier;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.wernerdegroot.applicatives.processor.domain.FullyQualifiedName;
import nl.wernerdegroot.applicatives.processor.domain.TypeParameterName;
import nl.wernerdegroot.applicatives.processor.domain.type.ConcreteType;
import nl.wernerdegroot.applicatives.processor.domain.type.Type;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.*;

public class PlaceholderTypeConstructorTest {

    private final TypeParameterName T = TypeParameterName.of("T");
    private final TypeParameterName U = TypeParameterName.of("U");
    private final TypeParameterName A = TypeParameterName.of("A");
    private final TypeParameterName B = TypeParameterName.of("B");

    private final ConcreteType STRING_TYPE = new ConcreteType(FullyQualifiedName.of("java.lang.String"), emptyList());
    private final ConcreteTypeConstructor STRING_TYPE_CONSTRUCTOR = new ConcreteTypeConstructor(FullyQualifiedName.of("java.lang.String"), emptyList());

    @Test
    public void replaceAllTypeParameterNames() {
        Map<TypeParameterName, TypeParameterName> mapping = new HashMap<>();
        mapping.put(T, A);
        mapping.put(U, B);

        PlaceholderTypeConstructor expected = new PlaceholderTypeConstructor();
        PlaceholderTypeConstructor toVerify = new PlaceholderTypeConstructor().replaceTypeParameterNames(mapping);

        assertEquals(expected, toVerify);
    }

    @Test
    public void referencesTypeParameterGivenPlaceholderTypeConstructor() {
        PlaceholderTypeConstructor placeholderTypeConstructor = new PlaceholderTypeConstructor();
        assertFalse(placeholderTypeConstructor.referencesTypeParameter(T));
    }

    @Test
    public void placeholderTypeConstructorCanAcceptGivenArrayTypeConstructor() {
        assertFalse(new PlaceholderTypeConstructor().canAccept(new ArrayTypeConstructor(STRING_TYPE_CONSTRUCTOR)));
    }

    @Test
    public void placeholderTypeConstructorCanAcceptGivenPlaceholderTypeConstructor() {
        assertTrue(new PlaceholderTypeConstructor().canAccept(new PlaceholderTypeConstructor()));
    }

    @Test
    public void apply() {
        Type expected = STRING_TYPE;
        Type toVerify = new PlaceholderTypeConstructor().apply(STRING_TYPE);

        assertEquals(expected, toVerify);
    }

    @Test
    public void equalsHashCodeToString() {
        EqualsVerifier.forClass(PlaceholderTypeConstructor.class).verify();
        ToStringVerifier.forClass(PlaceholderTypeConstructor.class).verify();
    }
}
