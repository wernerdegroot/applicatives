package nl.wernerdegroot.applicatives.processor.domain;

import com.jparams.verifier.tostring.ToStringVerifier;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

public class FinalizerTest {

    @Test
    public void equalsHashCodeToString() {
        EqualsVerifier.forClass(Finalizer.class).verify();
        ToStringVerifier.forClass(Finalizer.class).verify();
    }
}
