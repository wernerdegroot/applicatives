package nl.wernerdegroot.applicatives.processor.generator;

import org.junit.jupiter.api.Test;

import static nl.wernerdegroot.applicatives.processor.domain.type.Type.BIG_DECIMAL;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ObjectPathOrTypeGeneratorTest {

    @Test
    public void givenObjectPath() {
        String toVerify = toTest()
                .withObjectPath("this")
                .withObjectPath("property")
                .withObjectPath("current")
                .getObjectPathOrTypeGenerator()
                .generate();

        String expected = "this.property.current";

        assertEquals(expected, toVerify);
    }

    @Test
    public void givenType() {
        String toVerify = toTest()
                .withType(BIG_DECIMAL)
                .getObjectPathOrTypeGenerator()
                .generate();

        String expected = "java.math.BigDecimal";

        assertEquals(expected, toVerify);
    }

    private static ToTest toTest() {
        return new ToTest();
    }

    private static class ToTest implements ObjectPathOrTypeGenerator.HasObjectPathOrTypeGenerator<ToTest> {

        private ObjectPathOrTypeGenerator objectPathOrTypeGenerator = new ObjectPathOrTypeGenerator();

        @Override
        public ObjectPathOrTypeGenerator getObjectPathOrTypeGenerator() {
            return objectPathOrTypeGenerator;
        }

        @Override
        public ToTest getThis() {
            return this;
        }
    }
}
