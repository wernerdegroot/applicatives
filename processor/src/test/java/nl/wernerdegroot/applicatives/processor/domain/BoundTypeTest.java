package nl.wernerdegroot.applicatives.processor.domain;

import nl.wernerdegroot.applicatives.processor.domain.type.ConcreteType;
import nl.wernerdegroot.applicatives.processor.domain.type.GenericType;
import nl.wernerdegroot.applicatives.processor.domain.type.WildcardType;
import nl.wernerdegroot.applicatives.processor.domain.typeconstructor.PlaceholderTypeConstructor;
import nl.wernerdegroot.applicatives.processor.domain.typeconstructor.WildcardTypeConstructor;
import org.junit.jupiter.api.Test;

import static java.util.Collections.emptyList;
import static nl.wernerdegroot.applicatives.processor.domain.BoundType.EXTENDS;
import static nl.wernerdegroot.applicatives.processor.domain.BoundType.SUPER;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class BoundTypeTest {

    private final TypeParameterName T = new TypeParameterName("T");
    private final ConcreteType STRING_TYPE = new ConcreteType(FullyQualifiedName.of("java.lang.String"), emptyList());

    @Test
    public void toStringGivenSuperBoundType() {
        String expected = "super";
        String toVerify = SUPER.toString();

        assertEquals(expected, toVerify);
    }

    @Test
    public void toStringGivenExtendsBoundType() {
        String expected = "extends";
        String toVerify = EXTENDS.toString();

        assertEquals(expected, toVerify);
    }

    @Test
    public void typeGivenTypeParameterName() {
        WildcardType expected = new WildcardType(SUPER, new GenericType(T));
        WildcardType toVerify = SUPER.type(T);

        assertEquals(expected, toVerify);
    }

    @Test
    public void typeGivenType() {
        WildcardType expected = new WildcardType(EXTENDS, STRING_TYPE);
        WildcardType toVerify = EXTENDS.type(STRING_TYPE);

        assertEquals(expected, toVerify);
    }

    @Test
    public void typeGivenTypeConstructor() {
        WildcardTypeConstructor expected = new WildcardTypeConstructor(SUPER, new PlaceholderTypeConstructor());
        WildcardTypeConstructor toVerify = SUPER.type(new PlaceholderTypeConstructor());

        assertEquals(expected, toVerify);
    }

    @Test
    public void asTypeConstructor() {
        WildcardTypeConstructor expected = new WildcardTypeConstructor(EXTENDS, new PlaceholderTypeConstructor());
        WildcardTypeConstructor toVerify = EXTENDS.asTypeConstructor();

        assertEquals(expected, toVerify);
    }
}
