package nl.wernerdegroot.applicatives.processor.generator;

import nl.wernerdegroot.applicatives.processor.domain.TypeParameterName;
import org.junit.jupiter.api.Test;

import static nl.wernerdegroot.applicatives.processor.domain.type.Type.COMPARABLE;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TypeParametersGeneratorTest {

    @Test
    public void withoutAnyTypeParameters() {
        String toVerify = toTest().getTypeParametersGenerator().generate();
        String expected = "";
        assertEquals(expected, toVerify);
    }

    @Test
    public void withTypeParameters() {
        TypeParameterName A = TypeParameterName.of("A");
        TypeParameterName B = TypeParameterName.of("B");
        TypeParameterName C = TypeParameterName.of("C");
        String toVerify = toTest()
                .withTypeParameters(A.asTypeParameter(), B.extending(COMPARABLE.with(B)))
                .withTypeParameters(C.asTypeParameter())
                .getTypeParametersGenerator()
                .generate();
        String expected = "<A, B extends java.lang.Comparable<B>, C>";
        assertEquals(expected, toVerify);
    }

    private static ToTest toTest() {
        return new ToTest();
    }

    private static class ToTest implements TypeParametersGenerator.HasTypeParametersGenerator<ToTest> {

        private TypeParametersGenerator typeParametersGenerator = new TypeParametersGenerator();

        @Override
        public TypeParametersGenerator getTypeParametersGenerator() {
            return typeParametersGenerator;
        }

        @Override
        public ToTest getThis() {
            return this;
        }
    }
}
