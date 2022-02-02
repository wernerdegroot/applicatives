package nl.wernerdegroot.applicatives.processor.domain.typeconstructor;

import nl.wernerdegroot.applicatives.processor.domain.FullyQualifiedName;
import nl.wernerdegroot.applicatives.processor.domain.TypeParameterName;
import nl.wernerdegroot.applicatives.processor.domain.type.ArrayType;
import nl.wernerdegroot.applicatives.processor.domain.type.GenericType;
import nl.wernerdegroot.applicatives.processor.domain.type.Type;
import org.junit.jupiter.api.Test;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static nl.wernerdegroot.applicatives.processor.domain.BoundType.SUPER;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TypeConstructorTest {

    public static TypeParameterName T = TypeParameterName.of("T");

    private final FullyQualifiedName ERUDITE = new FullyQualifiedName("nl.wernerdegroot.Erudite");
    private final ConcreteTypeConstructor STRING_TYPE_CONSTRUCTOR = new ConcreteTypeConstructor(FullyQualifiedName.of("java.lang.String"), emptyList());
    private final ConcreteTypeConstructor INTEGER_TYPE_CONSTRUCTOR = new ConcreteTypeConstructor(FullyQualifiedName.of("java.lang.Integer"), emptyList());

    @Test
    public void apply() {
        Type expected = new ArrayType(new GenericType(T));
        Type toVerify = new ArrayTypeConstructor(new PlaceholderTypeConstructor()).apply(T);

        assertEquals(expected, toVerify);
    }

    @Test
    public void array() {
        ArrayTypeConstructor expected = new ArrayTypeConstructor(new PlaceholderTypeConstructor());
        ArrayTypeConstructor toVerify = new PlaceholderTypeConstructor().array();

        assertEquals(expected, toVerify);
    }

    @Test
    public void generic() {
        GenericTypeConstructor expected = new GenericTypeConstructor(T);
        GenericTypeConstructor toVerify = TypeConstructor.generic(T);

        assertEquals(expected, toVerify);
    }

    @Test
    public void concreteGivenFullyQualifiedNameAndListOfTypeArguments() {
        ConcreteTypeConstructor expected = new ConcreteTypeConstructor(ERUDITE, asList(STRING_TYPE_CONSTRUCTOR, new PlaceholderTypeConstructor(), INTEGER_TYPE_CONSTRUCTOR));
        ConcreteTypeConstructor toVerify = TypeConstructor.concrete(ERUDITE, asList(STRING_TYPE_CONSTRUCTOR, new PlaceholderTypeConstructor(), INTEGER_TYPE_CONSTRUCTOR));

        assertEquals(expected, toVerify);
    }

    @Test
    public void concreteGivenFullyQualifiedNameAndTypeArguments() {
        ConcreteTypeConstructor expected = new ConcreteTypeConstructor(ERUDITE, asList(STRING_TYPE_CONSTRUCTOR, new PlaceholderTypeConstructor(), INTEGER_TYPE_CONSTRUCTOR));
        ConcreteTypeConstructor toVerify = TypeConstructor.concrete(ERUDITE, STRING_TYPE_CONSTRUCTOR, new PlaceholderTypeConstructor(), INTEGER_TYPE_CONSTRUCTOR);

        assertEquals(expected, toVerify);
    }

    @Test
    public void concreteGivenFullyQualifiedName() {
        ConcreteTypeConstructor expected = new ConcreteTypeConstructor(ERUDITE, emptyList());
        ConcreteTypeConstructor toVerify = TypeConstructor.concrete(ERUDITE);

        assertEquals(expected, toVerify);
    }

    @Test
    public void wildcard() {
        WildcardTypeConstructor expected = new WildcardTypeConstructor(SUPER, STRING_TYPE_CONSTRUCTOR);
        WildcardTypeConstructor toVerify = TypeConstructor.wildcard(SUPER, STRING_TYPE_CONSTRUCTOR);

        assertEquals(expected, toVerify);
    }

    @Test
    public void arrayGivenElementTypeConstructor() {
        ArrayTypeConstructor expected = new ArrayTypeConstructor(STRING_TYPE_CONSTRUCTOR);
        ArrayTypeConstructor toVerify = TypeConstructor.array(STRING_TYPE_CONSTRUCTOR);

        assertEquals(expected, toVerify);
    }

    @Test
    public void placeholder() {
        PlaceholderTypeConstructor expected = new PlaceholderTypeConstructor();
        PlaceholderTypeConstructor toVerify = TypeConstructor.placeholder();

        assertEquals(expected, toVerify);
    }
}
