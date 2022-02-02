package nl.wernerdegroot.applicatives.processor.domain.type;

import nl.wernerdegroot.applicatives.processor.domain.FullyQualifiedName;
import nl.wernerdegroot.applicatives.processor.domain.TypeParameterName;
import nl.wernerdegroot.applicatives.processor.domain.typeconstructor.ConcreteTypeConstructor;
import nl.wernerdegroot.applicatives.processor.domain.typeconstructor.GenericTypeConstructor;
import nl.wernerdegroot.applicatives.processor.domain.typeconstructor.PlaceholderTypeConstructor;
import nl.wernerdegroot.applicatives.processor.domain.typeconstructor.TypeConstructor;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.*;

public class ConcreteTypeTest {

    private final TypeParameterName T = new TypeParameterName("T");
    private final GenericType T_TYPE = new GenericType(T);
    private final GenericTypeConstructor T_TYPE_CONSTRUCTOR = new GenericTypeConstructor(T);
    private final TypeParameterName U = new TypeParameterName("U");
    private final GenericType U_TYPE = new GenericType(U);
    private final GenericTypeConstructor U_TYPE_CONSTRUCTOR = new GenericTypeConstructor(U);
    private final TypeParameterName V = new TypeParameterName("V");
    private final GenericType V_TYPE = new GenericType(V);
    private final TypeParameterName A = new TypeParameterName("A");
    private final GenericType A_TYPE = new GenericType(A);
    private final TypeParameterName B = new TypeParameterName("B");

    private final FullyQualifiedName ERUDITE = new FullyQualifiedName("nl.wernerdegroot.Erudite");
    private final ConcreteType STRING_TYPE = new ConcreteType(FullyQualifiedName.of("java.lang.String"), emptyList());
    private final ConcreteTypeConstructor STRING_TYPE_CONSTRUCTOR = new ConcreteTypeConstructor(FullyQualifiedName.of("java.lang.String"), emptyList());
    private final ConcreteType INTEGER_TYPE = new ConcreteType(FullyQualifiedName.of("java.lang.Integer"), emptyList());
    private final ConcreteTypeConstructor INTEGER_TYPE_CONSTRUCTOR = new ConcreteTypeConstructor(FullyQualifiedName.of("java.lang.Integer"), emptyList());

    @Test
    public void of() {
        ConcreteType expected = new ConcreteType(ERUDITE, asList(STRING_TYPE, T_TYPE, INTEGER_TYPE));
        ConcreteType toVerify = ConcreteType.of(ERUDITE, asList(STRING_TYPE, T_TYPE, INTEGER_TYPE));

        assertEquals(expected, toVerify);
    }

    // Test for `match` not necessary. If it compiles, it must work.

    @Test
    public void asTypeConstructor() {
        ConcreteTypeConstructor expected = new ConcreteTypeConstructor(ERUDITE, asList(STRING_TYPE_CONSTRUCTOR, T_TYPE_CONSTRUCTOR, INTEGER_TYPE_CONSTRUCTOR));
        ConcreteTypeConstructor toVerify = new ConcreteType(ERUDITE, asList(STRING_TYPE, T_TYPE, INTEGER_TYPE)).asTypeConstructor();

        assertEquals(expected, toVerify);
    }

    @Test
    public void asTypeConstructorWithPlaceHolderForGivenNeedleThatMatchesOneOfTheTypeArguments() {
        TypeConstructor expected = new ConcreteTypeConstructor(ERUDITE, asList(STRING_TYPE_CONSTRUCTOR, new PlaceholderTypeConstructor(), INTEGER_TYPE_CONSTRUCTOR));
        TypeConstructor toVerify = new ConcreteType(ERUDITE, asList(STRING_TYPE, T_TYPE, INTEGER_TYPE)).asTypeConstructorWithPlaceholderFor(T);

        assertEquals(expected, toVerify);
    }

    @Test
    public void asTypeConstructorWithPlaceHolderForGivenNeedleThatDoesNotMatchAnyOfTheTypeArguments() {
        TypeConstructor expected = new ConcreteTypeConstructor(ERUDITE, asList(STRING_TYPE_CONSTRUCTOR, U_TYPE_CONSTRUCTOR, INTEGER_TYPE_CONSTRUCTOR));
        TypeConstructor toVerify = new ConcreteType(ERUDITE, asList(STRING_TYPE, U_TYPE, INTEGER_TYPE)).asTypeConstructorWithPlaceholderFor(T);

        assertEquals(expected, toVerify);
    }

    @Test
    public void containsGivenNeedleThatMatchesOneOfTheTypeArguments() {
        assertTrue(new ConcreteType(ERUDITE, asList(STRING_TYPE, T_TYPE, INTEGER_TYPE)).contains(T));
    }

    @Test
    public void containsGivenNeedleThatDoesNotMatchAnyOfTheTypeArguments() {
        assertFalse(new ConcreteType(ERUDITE, asList(STRING_TYPE, U_TYPE, INTEGER_TYPE)).contains(T));
    }

    @Test
    public void replaceAllTypeParameterNamesGivenMappingThatContainsOneOfTheTypeArguments() {
        Map<TypeParameterName, TypeParameterName> mapping = new HashMap<>();
        mapping.put(T, A);
        mapping.put(U, B);

        ConcreteType expected = new ConcreteType(ERUDITE, asList(STRING_TYPE, A_TYPE, INTEGER_TYPE));
        ConcreteType toVerify = new ConcreteType(ERUDITE, asList(STRING_TYPE, T_TYPE, INTEGER_TYPE)).replaceAllTypeParameterNames(mapping);

        assertEquals(expected, toVerify);
    }

    @Test
    public void replaceAllTypeParameterNamesGivenMappingThatDoesNotContainAnyOfTheTypeArguments() {
        Map<TypeParameterName, TypeParameterName> mapping = new HashMap<>();
        mapping.put(T, A);
        mapping.put(U, B);

        ConcreteType expected = new ConcreteType(ERUDITE, asList(STRING_TYPE, V_TYPE, INTEGER_TYPE));
        ConcreteType toVerify = new ConcreteType(ERUDITE, asList(STRING_TYPE, V_TYPE, INTEGER_TYPE)).replaceAllTypeParameterNames(mapping);

        assertEquals(expected, toVerify);
    }
}