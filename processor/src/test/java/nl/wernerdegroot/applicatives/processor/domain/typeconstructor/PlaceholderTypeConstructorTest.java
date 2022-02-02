package nl.wernerdegroot.applicatives.processor.domain.typeconstructor;

import nl.wernerdegroot.applicatives.processor.domain.FullyQualifiedName;
import nl.wernerdegroot.applicatives.processor.domain.TypeParameterName;
import nl.wernerdegroot.applicatives.processor.domain.type.ConcreteType;
import nl.wernerdegroot.applicatives.processor.domain.type.Type;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PlaceholderTypeConstructorTest {

    private final TypeParameterName T = TypeParameterName.of("T");
    private final TypeParameterName U = TypeParameterName.of("U");
    private final TypeParameterName V = TypeParameterName.of("V");
    private final TypeParameterName A = TypeParameterName.of("A");
    private final TypeParameterName B = TypeParameterName.of("B");

    private final TypeConstructor NEEDLE_TYPE_CONSTRUCTOR = new ConcreteTypeConstructor(new FullyQualifiedName("nl.wernerdegroot.Needle"), emptyList());
    private final Type REPLACEMENT_TYPE = new ConcreteType(new FullyQualifiedName("nl.wernerdegroot.Replacement"), emptyList());
    private final TypeConstructor REPLACEMENT_TYPE_CONSTRUCTOR = new ConcreteTypeConstructor(new FullyQualifiedName("nl.wernerdegroot.Replacement"), emptyList());

    @Test
    public void replaceAllTypeParameterNames() {
        Map<TypeParameterName, TypeParameterName> mapping = new HashMap<>();
        mapping.put(T, A);
        mapping.put(U, B);

        PlaceholderTypeConstructor expected = new PlaceholderTypeConstructor();
        PlaceholderTypeConstructor toVerify = new PlaceholderTypeConstructor().replaceAllTypeParameterNames(mapping);

        assertEquals(expected, toVerify);
    }

    @Test
    public void replaceAllGivenNeedleThatMatchesTypeConstructorCompletely() {
        TypeConstructor expected = REPLACEMENT_TYPE_CONSTRUCTOR;
        TypeConstructor toVerify = new PlaceholderTypeConstructor().replaceAll(new PlaceholderTypeConstructor(), REPLACEMENT_TYPE_CONSTRUCTOR);

        assertEquals(expected, toVerify);
    }

    @Test
    public void replaceAllGivenNeedleThatDoesNotMatch() {
        TypeConstructor expected = new PlaceholderTypeConstructor();
        TypeConstructor toVerify = new PlaceholderTypeConstructor().replaceAll(NEEDLE_TYPE_CONSTRUCTOR, REPLACEMENT_TYPE_CONSTRUCTOR);

        assertEquals(expected, toVerify);
    }

    @Test
    public void apply() {
        Type expected = REPLACEMENT_TYPE;
        Type toVerify = new PlaceholderTypeConstructor().apply(REPLACEMENT_TYPE);

        assertEquals(expected, toVerify);
    }
}
