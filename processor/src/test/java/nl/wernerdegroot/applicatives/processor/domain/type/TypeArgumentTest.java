package nl.wernerdegroot.applicatives.processor.domain.type;

import com.jparams.verifier.tostring.ToStringVerifier;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

public class TypeArgumentTest {

    @Test
    public void equalsHashCodeToString() {
        EqualsVerifier.forClass(TypeArgument.class).verify();
        ToStringVerifier.forClass(TypeArgument.class).verify();
    }
}
