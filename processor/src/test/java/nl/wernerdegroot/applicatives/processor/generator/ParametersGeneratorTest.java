package nl.wernerdegroot.applicatives.processor.generator;

import nl.wernerdegroot.applicatives.processor.domain.Parameter;
import nl.wernerdegroot.applicatives.processor.domain.TypeParameterName;
import org.junit.jupiter.api.Test;

import static nl.wernerdegroot.applicatives.processor.domain.type.Type.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ParametersGeneratorTest {

    private final TypeParameterName T = TypeParameterName.of("T");
    private final TypeParameterName U = TypeParameterName.of("U");

    @Test
    public void givenParameters() {
        String toVerify = toTest()
                .withParameter(STRING, "s")
                .withParameters(Parameter.of(BIG_DECIMAL, "bd"), Parameter.of(INTEGER, "i"))
                .withParameterTypes(OPTIONAL.of(T), OPTIONAL.of(U))
                .andParameterNames("t", "u")
                .getParametersGenerator()
                .generate();

        String expected = "(java.lang.String s, java.math.BigDecimal bd, java.lang.Integer i, java.util.Optional<T> t, java.util.Optional<U> u)";

        assertEquals(expected, toVerify);
    }

    public static ToTest toTest() {
        return new ToTest();
    }

    public static class ToTest implements ParametersGenerator.HasParametersGenerator<ToTest> {

        private ParametersGenerator parametersGenerator = new ParametersGenerator();

        @Override
        public ParametersGenerator getParametersGenerator() {
            return parametersGenerator;
        }

        @Override
        public ToTest getThis() {
            return this;
        }
    }
}
