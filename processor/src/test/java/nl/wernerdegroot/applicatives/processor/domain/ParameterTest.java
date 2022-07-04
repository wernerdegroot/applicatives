package nl.wernerdegroot.applicatives.processor.domain;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

public class ParameterTest {

    @Test
    public void equals() {
        EqualsVerifier.forClass(Parameter.class).verify();
    }
}
