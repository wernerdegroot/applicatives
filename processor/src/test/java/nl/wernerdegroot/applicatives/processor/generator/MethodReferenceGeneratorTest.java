package nl.wernerdegroot.applicatives.processor.generator;

import org.junit.jupiter.api.Test;

import static nl.wernerdegroot.applicatives.processor.domain.type.Type.STRING;
import static nl.wernerdegroot.applicatives.processor.generator.MethodReferenceGenerator.methodReference;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MethodReferenceGeneratorTest {

    @Test
    public void givenInstanceMethod() {
        String toVerify = methodReference()
                .withObjectPath("this")
                .withMethodName("toString")
                .generate();

        String expected = "this::toString";

        assertEquals(expected, toVerify);
    }

    @Test
    public void givenStaticMethod() {
        String toVerify = methodReference()
                .withType(STRING)
                .withMethodName("toString")
                .generate();

        String expected = "java.lang.String::toString";

        assertEquals(expected, toVerify);
    }
}
