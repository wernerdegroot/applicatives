package nl.wernerdegroot.applicatives.processor.domain;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

public class AccumulatorTest {

    @Test
    public void equals() {
        EqualsVerifier.forClass(Accumulator.class).verify();
    }
}
