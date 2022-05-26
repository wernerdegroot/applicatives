package nl.wernerdegroot.applicatives.processor.domain;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

public class CovariantFinalizerTest {

    @Test
    public void equals() {
        EqualsVerifier.forClass(CovariantFinalizer.class).verify();
    }
}
