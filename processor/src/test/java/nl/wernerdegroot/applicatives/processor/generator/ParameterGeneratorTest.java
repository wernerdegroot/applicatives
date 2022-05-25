package nl.wernerdegroot.applicatives.processor.generator;

import nl.wernerdegroot.applicatives.processor.domain.Parameter;
import org.junit.jupiter.api.Test;

import static nl.wernerdegroot.applicatives.processor.domain.type.Type.STRING;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ParameterGeneratorTest {

    @Test
    public void givenParameter() {
        String expected = "java.lang.String s";
        String toVerify = ParameterGenerator.generateFrom(Parameter.of(STRING, "s"));

        assertEquals(expected, toVerify);
    }
}
