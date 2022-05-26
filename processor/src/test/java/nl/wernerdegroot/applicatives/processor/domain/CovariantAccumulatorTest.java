package nl.wernerdegroot.applicatives.processor.domain;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

public class CovariantAccumulatorTest {

    @Test
    public void equals() {
        EqualsVerifier.forClass(CovariantAccumulator.class).verify();
    }
}
