package nl.wernerdegroot.applicatives.processor.validation;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

public class ValidatedTest {

    @Test
    public void equals() {
        EqualsVerifier.forClass(Validated.class).verify();
    }
}
