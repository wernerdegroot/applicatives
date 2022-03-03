package nl.wernerdegroot.applicatives.processor.generator;

import nl.wernerdegroot.applicatives.processor.domain.TypeParameterName;
import nl.wernerdegroot.applicatives.processor.domain.type.Type;
import org.junit.jupiter.api.Test;

import static nl.wernerdegroot.applicatives.processor.domain.type.Type.*;
import static nl.wernerdegroot.applicatives.processor.generator.TypeGenerator.type;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TypeGeneratorTest {

    private final TypeParameterName T = TypeParameterName.of("T");

    @Test
    public void givenGenericType() {
        Type type = T.asType();
        String toVerify = type(type).generate();
        String expected = "T";
        assertEquals(expected, toVerify);
    }

    @Test
    public void givenConcreteTypeWithoutTypeParameters() {
        Type type = STRING;
        String toVerify = type(type).generate();
        String expected = "java.lang.String";
        assertEquals(expected, toVerify);
    }

    @Test
    public void givenConcreteTypeWithTypeParameters() {
        Type type = FUNCTION.with(
                STRING.contravariant(),
                OBJECT.covariant()
        );
        String toVerify = type(type).generate();
        String expected = "java.util.function.Function<? super java.lang.String, ? extends java.lang.Object>";
        assertEquals(expected, toVerify);
    }

    @Test
    public void givenArrayType() {
        Type type = OBJECT.array();
        String toVerify = type(type).generate();
        String expected = "java.lang.Object[]";
        assertEquals(expected, toVerify);
    }
}
