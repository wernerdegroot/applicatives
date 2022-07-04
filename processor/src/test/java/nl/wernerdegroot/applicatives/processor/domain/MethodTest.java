package nl.wernerdegroot.applicatives.processor.domain;

import com.jparams.verifier.tostring.ToStringVerifier;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

public class MethodTest {
    @Test
    public void equalsHashCodeToString() {
        EqualsVerifier.forClass(Method.class).verify();
        ToStringVerifier.forClass(Method.class).verify();
    }
}
