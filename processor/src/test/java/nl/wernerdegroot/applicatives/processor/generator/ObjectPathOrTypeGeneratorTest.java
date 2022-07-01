package nl.wernerdegroot.applicatives.processor.generator;

import nl.wernerdegroot.applicatives.processor.domain.TypeParameter;
import nl.wernerdegroot.applicatives.processor.domain.TypeParameterName;
import org.junit.jupiter.api.Test;

import static nl.wernerdegroot.applicatives.processor.domain.type.Type.BIG_DECIMAL;
import static nl.wernerdegroot.applicatives.processor.domain.type.Type.COMPARABLE;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ObjectPathOrTypeGeneratorTest {

    public static final TypeParameterName T = TypeParameterName.of("T");

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
    public void givenConcreteType() {
        String toVerify = toTest()
                .withType(BIG_DECIMAL)
                .getObjectPathOrTypeGenerator()
                .generate();

        String expected = "java.math.BigDecimal";

        assertEquals(expected, toVerify);
    }

    @Test
    public void givenFullyQualifiedName() {
        String toVerify = toTest()
                .withType(BIG_DECIMAL.getFullyQualifiedName())
                .getObjectPathOrTypeGenerator()
                .generate();

        String expected = "java.math.BigDecimal";

        assertEquals(expected, toVerify);
    }

    @Test
    public void givenTypeParameterName() {
        String toVerify = toTest()
                .withType(T)
                .getObjectPathOrTypeGenerator()
                .generate();

        String expected = "T";

        assertEquals(expected, toVerify);
    }

    @Test
    public void givenTypeParameter() {
        String toVerify = toTest()
                .withType(T.extending(COMPARABLE.with(T)))
                .getObjectPathOrTypeGenerator()
                .generate();

        String expected = "T";

        assertEquals(expected, toVerify);
    }

    @Test
    public void givenGenericType() {
        String toVerify = toTest()
                .withType(T.asType())
                .getObjectPathOrTypeGenerator()
                .generate();

        String expected = "T";

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
