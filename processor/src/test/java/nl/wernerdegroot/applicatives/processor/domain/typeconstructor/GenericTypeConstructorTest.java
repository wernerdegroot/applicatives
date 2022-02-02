package nl.wernerdegroot.applicatives.processor.domain.typeconstructor;

import nl.wernerdegroot.applicatives.processor.domain.FullyQualifiedName;
import nl.wernerdegroot.applicatives.processor.domain.TypeParameterName;
import nl.wernerdegroot.applicatives.processor.domain.type.ConcreteType;
import nl.wernerdegroot.applicatives.processor.domain.type.GenericType;
import nl.wernerdegroot.applicatives.processor.domain.type.Type;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GenericTypeConstructorTest {

    private final TypeParameterName T = TypeParameterName.of("T");
    private final TypeParameterName U = TypeParameterName.of("U");
    private final TypeParameterName V = TypeParameterName.of("V");
    private final TypeParameterName A = TypeParameterName.of("A");
    private final TypeParameterName B = TypeParameterName.of("B");

    private final TypeConstructor NEEDLE_TYPE_CONSTRUCTOR = new ConcreteTypeConstructor(new FullyQualifiedName("nl.wernerdegroot.Needle"), emptyList());
    private final Type REPLACEMENT_TYPE = new ConcreteType(new FullyQualifiedName("nl.wernerdegroot.Replacement"), emptyList());
    private final TypeConstructor REPLACEMENT_TYPE_CONSTRUCTOR = new ConcreteTypeConstructor(new FullyQualifiedName("nl.wernerdegroot.Replacement"), emptyList());

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
        GenericTypeConstructor toVerify = new GenericTypeConstructor(T).replaceAllTypeParameterNames(mapping);

        assertEquals(expected, toVerify);
    }

    @Test
    public void replaceAllTypeParameterNamesGivenMappingThatDoesNotContainTypeParameterName() {
        Map<TypeParameterName, TypeParameterName> mapping = new HashMap<>();
        mapping.put(T, A);
        mapping.put(U, B);

        GenericTypeConstructor expected = new GenericTypeConstructor(V);
        GenericTypeConstructor toVerify = new GenericTypeConstructor(V).replaceAllTypeParameterNames(mapping);

        assertEquals(expected, toVerify);
    }

    @Test
    public void replaceAllGivenNeedleThatMatchesTypeConstructorCompletely() {
        TypeConstructor expected = REPLACEMENT_TYPE_CONSTRUCTOR;
        TypeConstructor toVerify = new GenericTypeConstructor(T).replaceAll(new GenericTypeConstructor(T), REPLACEMENT_TYPE_CONSTRUCTOR);

        assertEquals(expected, toVerify);
    }

    @Test
    public void replaceAllGivenNeedleThatDoesNotMatch() {
        TypeConstructor expected = new GenericTypeConstructor(T);
        TypeConstructor toVerify = new GenericTypeConstructor(T).replaceAll(NEEDLE_TYPE_CONSTRUCTOR, REPLACEMENT_TYPE_CONSTRUCTOR);

        assertEquals(expected, toVerify);
    }

    @Test
    public void apply() {
        GenericType expected = new GenericType(T);
        GenericType toVerify = new GenericTypeConstructor(T).apply(REPLACEMENT_TYPE);

        assertEquals(expected, toVerify);
    }
}
