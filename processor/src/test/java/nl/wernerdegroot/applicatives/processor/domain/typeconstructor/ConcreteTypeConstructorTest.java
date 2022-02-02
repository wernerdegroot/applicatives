package nl.wernerdegroot.applicatives.processor.domain.typeconstructor;

import nl.wernerdegroot.applicatives.processor.domain.FullyQualifiedName;
import nl.wernerdegroot.applicatives.processor.domain.TypeParameterName;
import nl.wernerdegroot.applicatives.processor.domain.type.ConcreteType;
import nl.wernerdegroot.applicatives.processor.domain.type.Type;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConcreteTypeConstructorTest {

    private final TypeParameterName T = new TypeParameterName("T");
    private final TypeParameterName U = new TypeParameterName("U");
    private final TypeParameterName V = new TypeParameterName("V");
    private final TypeParameterName A = new TypeParameterName("A");
    private final TypeParameterName B = new TypeParameterName("B");

    private final FullyQualifiedName ERUDITE = new FullyQualifiedName("nl.wernerdegroot.Erudite");
    private final ConcreteType STRING_TYPE = new ConcreteType(FullyQualifiedName.of("java.lang.String"), emptyList());
    private final ConcreteTypeConstructor STRING_TYPE_CONSTRUCTOR = new ConcreteTypeConstructor(FullyQualifiedName.of("java.lang.String"), emptyList());
    private final ConcreteType INTEGER_TYPE = new ConcreteType(FullyQualifiedName.of("java.lang.Integer"), emptyList());
    private final ConcreteTypeConstructor INTEGER_TYPE_CONSTRUCTOR = new ConcreteTypeConstructor(FullyQualifiedName.of("java.lang.Integer"), emptyList());

    private final TypeConstructor NEEDLE_TYPE_CONSTRUCTOR = new ConcreteTypeConstructor(new FullyQualifiedName("nl.wernerdegroot.Needle"), emptyList());
    private final Type REPLACEMENT_TYPE = new ConcreteType(new FullyQualifiedName("nl.wernerdegroot.Replacement"), emptyList());
    private final TypeConstructor REPLACEMENT_TYPE_CONSTRUCTOR = new ConcreteTypeConstructor(new FullyQualifiedName("nl.wernerdegroot.Replacement"), emptyList());

    @Test
    public void of() {
        ConcreteTypeConstructor expected = new ConcreteTypeConstructor(ERUDITE, asList(STRING_TYPE_CONSTRUCTOR, new PlaceholderTypeConstructor(), INTEGER_TYPE_CONSTRUCTOR));
        ConcreteTypeConstructor toVerify = ConcreteTypeConstructor.of(ERUDITE, asList(STRING_TYPE_CONSTRUCTOR, new PlaceholderTypeConstructor(), INTEGER_TYPE_CONSTRUCTOR));

        assertEquals(expected, toVerify);
    }

    @Test
    public void replaceAllTypeParameterNamesGivenMappingThatContainsElementTypeConstructor() {
        Map<TypeParameterName, TypeParameterName> mapping = new HashMap<>();
        mapping.put(T, A);
        mapping.put(U, B);

        ConcreteTypeConstructor expected = new ConcreteTypeConstructor(ERUDITE, asList(STRING_TYPE_CONSTRUCTOR, new GenericTypeConstructor(A), INTEGER_TYPE_CONSTRUCTOR));
        ConcreteTypeConstructor toVerify = new ConcreteTypeConstructor(ERUDITE, asList(STRING_TYPE_CONSTRUCTOR, new GenericTypeConstructor(T), INTEGER_TYPE_CONSTRUCTOR)).replaceAllTypeParameterNames(mapping);

        assertEquals(expected, toVerify);
    }

    @Test
    public void replaceAllTypeParameterNamesGivenMappingThatDoesNotContainElementTypeConstructor() {
        Map<TypeParameterName, TypeParameterName> mapping = new HashMap<>();
        mapping.put(T, A);
        mapping.put(U, B);

        ConcreteTypeConstructor expected = new ConcreteTypeConstructor(ERUDITE, asList(STRING_TYPE_CONSTRUCTOR, new GenericTypeConstructor(V), INTEGER_TYPE_CONSTRUCTOR));
        ConcreteTypeConstructor toVerify = new ConcreteTypeConstructor(ERUDITE, asList(STRING_TYPE_CONSTRUCTOR, new GenericTypeConstructor(V), INTEGER_TYPE_CONSTRUCTOR)).replaceAllTypeParameterNames(mapping);

        assertEquals(expected, toVerify);
    }

    @Test
    public void replaceAllGivenNeedleThatMatchesTypeConstructorCompletely() {
        TypeConstructor expected = REPLACEMENT_TYPE_CONSTRUCTOR;
        TypeConstructor toVerify = new ConcreteTypeConstructor(ERUDITE, asList(STRING_TYPE_CONSTRUCTOR, new GenericTypeConstructor(T), INTEGER_TYPE_CONSTRUCTOR)).replaceAll(new ConcreteTypeConstructor(ERUDITE, asList(STRING_TYPE_CONSTRUCTOR, new GenericTypeConstructor(T), INTEGER_TYPE_CONSTRUCTOR)), REPLACEMENT_TYPE_CONSTRUCTOR);

        assertEquals(expected, toVerify);
    }

    @Test
    public void replaceAllGivenNeedleThatMatchesOneOfTheTypeParameters() {
        TypeConstructor expected = new ConcreteTypeConstructor(ERUDITE, asList(STRING_TYPE_CONSTRUCTOR, REPLACEMENT_TYPE_CONSTRUCTOR, INTEGER_TYPE_CONSTRUCTOR));
        TypeConstructor toVerify = new ConcreteTypeConstructor(ERUDITE, asList(STRING_TYPE_CONSTRUCTOR, new GenericTypeConstructor(T), INTEGER_TYPE_CONSTRUCTOR)).replaceAll(new GenericTypeConstructor(T), REPLACEMENT_TYPE_CONSTRUCTOR);

        assertEquals(expected, toVerify);
    }

    @Test
    public void replaceAllGivenNeedleThatDoesNotMatch() {
        TypeConstructor expected = new ConcreteTypeConstructor(ERUDITE, asList(STRING_TYPE_CONSTRUCTOR, new GenericTypeConstructor(T), INTEGER_TYPE_CONSTRUCTOR));
        TypeConstructor toVerify = new ConcreteTypeConstructor(ERUDITE, asList(STRING_TYPE_CONSTRUCTOR, new GenericTypeConstructor(T), INTEGER_TYPE_CONSTRUCTOR)).replaceAll(NEEDLE_TYPE_CONSTRUCTOR, REPLACEMENT_TYPE_CONSTRUCTOR);

        assertEquals(expected, toVerify);
    }

    @Test
    public void applyGivenTypeConstructorWithoutPlaceholder() {
        ConcreteType expected = new ConcreteType(ERUDITE, asList(STRING_TYPE, INTEGER_TYPE));
        ConcreteType toVerify = new ConcreteTypeConstructor(ERUDITE, asList(STRING_TYPE_CONSTRUCTOR, INTEGER_TYPE_CONSTRUCTOR)).apply(REPLACEMENT_TYPE);

        assertEquals(expected, toVerify);
    }

    @Test
    public void applyGivenTypeConstructorWithPlaceholder() {
        ConcreteType expected = new ConcreteType(ERUDITE, asList(STRING_TYPE, REPLACEMENT_TYPE, INTEGER_TYPE));
        ConcreteType toVerify = new ConcreteTypeConstructor(ERUDITE, asList(STRING_TYPE_CONSTRUCTOR, new PlaceholderTypeConstructor(), INTEGER_TYPE_CONSTRUCTOR)).apply(REPLACEMENT_TYPE);

        assertEquals(expected, toVerify);
    }
}
