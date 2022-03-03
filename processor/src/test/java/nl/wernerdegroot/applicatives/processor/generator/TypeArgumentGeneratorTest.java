package nl.wernerdegroot.applicatives.processor.generator;

import org.junit.jupiter.api.Test;

import static nl.wernerdegroot.applicatives.processor.domain.type.Type.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TypeArgumentGeneratorTest {

    @Test
    public void generateGivenInvariant() {
        String expected = "java.lang.String";
        String toVerify = new TypeArgumentGenerator(STRING.invariant()).generate();

        assertEquals(expected, toVerify);
    }

    @Test
    public void generateGivenCovariant() {
        String expected = "? extends java.lang.Integer";
        String toVerify = new TypeArgumentGenerator(INTEGER.covariant()).generate();

        assertEquals(expected, toVerify);
    }

    @Test
    public void generateGivenContravariant() {
        String expected = "? super java.lang.Boolean";
        String toVerify = new TypeArgumentGenerator(BOOLEAN.contravariant()).generate();

        assertEquals(expected, toVerify);
    }
}
