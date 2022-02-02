package nl.wernerdegroot.applicatives.processor.generator;

import org.junit.jupiter.api.Test;

import static nl.wernerdegroot.applicatives.processor.domain.Modifier.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ModifiersGeneratorTest {

    @Test
    public void givenManyModifiers() {
        String toVerify = toTest()
                .withModifiers(PUBLIC, STATIC, ABSTRACT)
                .withModifiers(PRIVATE, DEFAULT)
                .getModifiersGenerator()
                .generate();

        String expected = "public private abstract default static";

        assertEquals(expected, toVerify);
    }

    private static ToTest toTest() {
        return new ToTest();
    }

    private static class ToTest implements ModifiersGenerator.HasModifiersGenerator<ToTest> {

        private ModifiersGenerator modifiersGenerator = new ModifiersGenerator();

        @Override
        public ModifiersGenerator getModifiersGenerator() {
            return modifiersGenerator;
        }

        @Override
        public ToTest getThis() {
            return this;
        }
    }

}
