package nl.wernerdegroot.applicatives.processor.generator;

import org.junit.jupiter.api.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static nl.wernerdegroot.applicatives.processor.generator.LambdaGenerator.lambda;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class LambdaGeneratorTest {

    @Test
    public void lambdaWithSingleExpressionAsBody() {
        List<String> toVerify = lambda()
                .withParameterNames("capacity", "hasPower")
                .withExpression("new Device(capacity, hasPower)")
                .lines();

        List<String> expected = asList(
                "(capacity, hasPower) ->",
                "        new Device(capacity, hasPower)"
        );

        assertEquals(expected, toVerify);
    }
}
