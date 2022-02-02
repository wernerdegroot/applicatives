package nl.wernerdegroot.applicatives.processor.generator;

import org.junit.jupiter.api.Test;

import static nl.wernerdegroot.applicatives.processor.domain.type.Type.BIG_DECIMAL;
import static nl.wernerdegroot.applicatives.processor.domain.type.Type.CHAR_SEQUENCE;
import static nl.wernerdegroot.applicatives.processor.generator.MethodCallGenerator.methodCall;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MethodCallGeneratorTest {

    @Test
    public void givenInstanceMethodAndExplicitTypeParameters() {
        String toVerify = methodCall()
                .withObjectPath("objects")
                .withTypeArguments(CHAR_SEQUENCE)
                .withMethodName("map")
                .withArguments("java.lang.Object::toString")
                .generate();

        String expected = "objects.<java.lang.CharSequence>map(java.lang.Object::toString)";

        assertEquals(expected, toVerify);
    }

    @Test
    public void givenStaticMethodAndImplicitTypeParameters() {
        String toVerify = methodCall()
                .withType(BIG_DECIMAL)
                .withMethodName("valueOf")
                .withArguments("10")
                .generate();

        String expected = "java.math.BigDecimal.valueOf(10)";

        assertEquals(expected, toVerify);
    }
}
