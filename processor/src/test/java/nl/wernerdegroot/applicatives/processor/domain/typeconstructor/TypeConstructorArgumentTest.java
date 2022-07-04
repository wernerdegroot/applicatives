package nl.wernerdegroot.applicatives.processor.domain.typeconstructor;

import com.jparams.verifier.tostring.ToStringVerifier;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

public class TypeConstructorArgumentTest {

    @Test
    public void equalsHashCodeToString() {
        EqualsVerifier.forClass(TypeConstructorArgument.class).verify();
        ToStringVerifier.forClass(TypeConstructorArgument.class).verify();
    }
}
