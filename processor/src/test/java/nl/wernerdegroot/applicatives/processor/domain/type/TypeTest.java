package nl.wernerdegroot.applicatives.processor.domain.type;

import nl.wernerdegroot.applicatives.processor.domain.FullyQualifiedName;
import nl.wernerdegroot.applicatives.processor.domain.Parameter;
import nl.wernerdegroot.applicatives.processor.domain.TypeParameter;
import nl.wernerdegroot.applicatives.processor.domain.TypeParameterName;
import nl.wernerdegroot.applicatives.processor.domain.typeconstructor.ConcreteTypeConstructor;
import nl.wernerdegroot.applicatives.processor.domain.typeconstructor.TypeConstructor;
import org.junit.jupiter.api.Test;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.*;

public class TypeTest {

    public static TypeParameterName A = TypeParameterName.of("A");
    public static TypeParameterName B = TypeParameterName.of("B");
    public static TypeParameterName T = TypeParameterName.of("T");
    public static GenericType T_TYPE = new GenericType(T);
    TypeParameter T_TYPE_PARAMETER = new TypeParameter(T, emptyList());
    public static TypeParameterName U = TypeParameterName.of("U");
    TypeParameter U_TYPE_PARAMETER = new TypeParameter(U, emptyList());
    public static TypeParameterName V = TypeParameterName.of("V");
    TypeParameter V_TYPE_PARAMETER = new TypeParameter(V, emptyList());
    public static TypeParameterName W = TypeParameterName.of("W");
    public static GenericType W_TYPE = new GenericType(W);

    private final FullyQualifiedName ERUDITE = new FullyQualifiedName("nl.wernerdegroot.Erudite");
    private final ConcreteType OBJECT_TYPE = new ConcreteType(FullyQualifiedName.of("java.lang.Object"), emptyList());
    private final ConcreteType STRING_TYPE = new ConcreteType(FullyQualifiedName.of("java.lang.String"), emptyList());
    private final ConcreteTypeConstructor STRING_TYPE_CONSTRUCTOR = new ConcreteTypeConstructor(FullyQualifiedName.of("java.lang.String"), emptyList());
    private final ConcreteType INTEGER_TYPE = new ConcreteType(FullyQualifiedName.of("java.lang.Integer"), emptyList());
    private final ConcreteTypeConstructor INTEGER_TYPE_CONSTRUCTOR = new ConcreteTypeConstructor(FullyQualifiedName.of("java.lang.Integer"), emptyList());

    @Test
    public void using() {
        Type expected = Type.concrete(ERUDITE, STRING_TYPE.invariant(), T_TYPE.covariant(), INTEGER_TYPE.contravariant());
        Type toVerify = Type.generic(T).using(TypeConstructor.concrete(ERUDITE, STRING_TYPE_CONSTRUCTOR.invariant(), TypeConstructor.placeholder().covariant(), INTEGER_TYPE_CONSTRUCTOR.contravariant()));

        assertEquals(expected, toVerify);
    }

    @Test
    public void containsGivenManyTypeParameterNamesWithOneThatMatches() {
        assertTrue(Type.concrete(ERUDITE, STRING_TYPE.invariant(), T_TYPE.covariant(), INTEGER_TYPE.contravariant()).contains(T, U, V));
    }

    @Test
    public void containsGivenManyTypeParameterNamesWithNoneThatMatches() {
        assertFalse(Type.concrete(ERUDITE, STRING_TYPE.invariant(), W_TYPE.covariant(), INTEGER_TYPE.contravariant()).contains(T, U, V));
    }

    @Test
    public void containsGivenManyTypeParametersWithOneThatMatches() {
        assertTrue(Type.concrete(ERUDITE, STRING_TYPE.invariant(), T_TYPE.covariant(), INTEGER_TYPE.contravariant()).contains(T_TYPE_PARAMETER, U_TYPE_PARAMETER, V_TYPE_PARAMETER));
    }

    @Test
    public void containsGivenManyTypeParametersWithNoneThatMatches() {
        assertFalse(Type.concrete(ERUDITE, STRING_TYPE.invariant(), W_TYPE.covariant(), INTEGER_TYPE.contravariant()).contains(T_TYPE_PARAMETER, U_TYPE_PARAMETER, V_TYPE_PARAMETER));
    }

    @Test
    public void array() {
        ArrayType expected = new ArrayType(STRING_TYPE);
        ArrayType toVerify = STRING_TYPE.array();

        assertEquals(expected, toVerify);
    }

    @Test
    public void withName() {
        Parameter expected = new Parameter(STRING_TYPE, "s");
        Parameter toVerify = STRING_TYPE.withName("s");

        assertEquals(expected, toVerify);
    }

    @Test
    public void generic() {
        GenericType expected = new GenericType(T);
        GenericType toVerify = Type.generic(T);

        assertEquals(expected, toVerify);
    }

    @Test
    public void concreteGivenFullyQualifiedNameAndTypeArguments() {
        ConcreteType expected = new ConcreteType(ERUDITE, asList(STRING_TYPE.invariant(), T_TYPE.covariant(), INTEGER_TYPE.contravariant()));
        ConcreteType toVerify = Type.concrete(ERUDITE, STRING_TYPE.invariant(), T_TYPE.covariant(), INTEGER_TYPE.contravariant());

        assertEquals(expected, toVerify);
    }

    @Test
    public void concreteGivenFullyQualifiedName() {
        ConcreteType expected = new ConcreteType(ERUDITE, emptyList());
        ConcreteType toVerify = Type.concrete(ERUDITE);

        assertEquals(expected, toVerify);
    }

    @Test
    public void arrayGivenElementType() {
        ArrayType expected = new ArrayType(STRING_TYPE);
        ArrayType toVerify = Type.array(STRING_TYPE);

        assertEquals(expected, toVerify);
    }
}
