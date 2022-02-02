package nl.wernerdegroot.applicatives.processor.generator;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ObjectPathGeneratorTest {

    @Test
    public void givenObjectPath() {
        String toVerify = toTest()
                .withObjectPath("this", "value")
                .withObjectPath("selection")
                .withObjectPath("current")
                .getObjectPathGenerator()
                .generate();

        String expected = "this.value.selection.current";

        assertEquals(expected, toVerify);
    }

    private static ToTest toTest() {
        return new ToTest();
    }

    private static class ToTest implements ObjectPathGenerator.HasObjectPathGenerator<ToTest> {

        private ObjectPathGenerator objectPathGenerator = new ObjectPathGenerator();

        @Override
        public ObjectPathGenerator getObjectPathGenerator() {
            return objectPathGenerator;
        }

        @Override
        public ToTest getThis() {
            return this;
        }
    }
}
