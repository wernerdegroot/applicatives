package nl.wernerdegroot.applicatives.processor.domain;

import com.jparams.verifier.tostring.ToStringVerifier;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

public class AccumulatorTest {

    @Test
    public void equalsHashCodeToString() {
        EqualsVerifier.forClass(Accumulator.class).verify();
        ToStringVerifier.forClass(Accumulator.class).verify();
    }
}
