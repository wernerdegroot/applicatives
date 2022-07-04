package nl.wernerdegroot.applicatives.processor.domain;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

public class TypeParameterTest {

    @Test
    public void equals() {
        EqualsVerifier.forClass(TypeParameter.class).verify();
    }
}
