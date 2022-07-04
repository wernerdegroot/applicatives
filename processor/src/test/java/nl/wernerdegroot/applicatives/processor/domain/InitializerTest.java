package nl.wernerdegroot.applicatives.processor.domain;

import com.jparams.verifier.tostring.ToStringVerifier;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

public class InitializerTest {

    @Test
    public void equalsHashCodeToString() {
        EqualsVerifier.forClass(Initializer.class).verify();
        ToStringVerifier.forClass(Initializer.class).verify();
    }
}
