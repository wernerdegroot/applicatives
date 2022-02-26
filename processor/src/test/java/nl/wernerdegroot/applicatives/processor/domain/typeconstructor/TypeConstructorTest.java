package nl.wernerdegroot.applicatives.processor.domain.typeconstructor;

import nl.wernerdegroot.applicatives.processor.domain.FullyQualifiedName;
import nl.wernerdegroot.applicatives.processor.domain.TypeParameterName;
import nl.wernerdegroot.applicatives.processor.domain.type.ArrayType;
import nl.wernerdegroot.applicatives.processor.domain.type.GenericType;
import nl.wernerdegroot.applicatives.processor.domain.type.Type;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Consumer;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static nl.wernerdegroot.applicatives.processor.domain.BoundType.EXTENDS;
import static nl.wernerdegroot.applicatives.processor.domain.BoundType.SUPER;
import static nl.wernerdegroot.applicatives.processor.domain.type.Type.LIST;
import static nl.wernerdegroot.applicatives.processor.domain.type.Type.STRING;
import static org.junit.jupiter.api.Assertions.*;

public class TypeConstructorTest {

    public static TypeParameterName T = TypeParameterName.of("T");

    private final FullyQualifiedName ERUDITE = new FullyQualifiedName("nl.wernerdegroot.Erudite");
    private final ConcreteTypeConstructor STRING_TYPE_CONSTRUCTOR = new ConcreteTypeConstructor(FullyQualifiedName.of("java.lang.String"), emptyList());
    private final ConcreteTypeConstructor INTEGER_TYPE_CONSTRUCTOR = new ConcreteTypeConstructor(FullyQualifiedName.of("java.lang.Integer"), emptyList());

    private final PlaceholderTypeConstructor placeholder = TypeConstructor.placeholder();

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

    @Test
    public void canAcceptValueOfType() {

        // This test covers some interesting test cases that are not easily covered by
        // a test in one of the subclasses of `TypeConstructor`. These test cases check
        // if the subclasses work well together, and perform their function as expected.

        assertNotCompatible(
                LIST.of(SUPER.type(LIST.of(EXTENDS.type(placeholder)))),
                LIST.of(LIST.of(placeholder)),
                "Assign List<List<*>> to List<? super List<? extends *>>"
        );

        assertCompatible(
                LIST.of(EXTENDS.type(LIST.of(EXTENDS.type(placeholder)))),
                LIST.of(LIST.of(placeholder)),
                "Assign List<List<*>> to List<? extends List<? extends *>>"
        );

        assertNotCompatible(
                LIST.of(SUPER.type(LIST.of(SUPER.type(placeholder)))),
                LIST.of(LIST.of(placeholder)),
                "Assign List<List<*>> to List<? super List<? super *>>"
        );

        assertCompatible(
                LIST.of(SUPER.type(LIST.of(placeholder))),
                LIST.of(LIST.of(EXTENDS.type(placeholder))),
                "Assign List<List<? extends *>> to List<? super List<*>>"
        );

        assertNotCompatible(
                LIST.of(SUPER.type(LIST.of(EXTENDS.type(placeholder)))),
                LIST.of(LIST.of(SUPER.type(placeholder))),
                "Assign List<List<? super *>> to List<? super List<? extends *>>"
        );

        assertCompatible(
                LIST.of(SUPER.type(LIST.of(SUPER.type(placeholder)))),
                LIST.of(LIST.of(SUPER.type(placeholder))),
                "Assign List<List<? super *>> to List<? super List<? super *>>"
        );
    }

    private void assertNotCompatible(TypeConstructor left, TypeConstructor right, String message) {
        assertFalse(left.canAcceptValueOfType(right), message);
    }

    private void assertCompatible(TypeConstructor left, TypeConstructor right, String message) {
        assertTrue(left.canAcceptValueOfType(right), message);
    }
}
