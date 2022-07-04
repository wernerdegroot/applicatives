package nl.wernerdegroot.applicatives.processor.validation;

import com.jparams.verifier.tostring.ToStringVerifier;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

public class ParameterAndTypeParametersValidatorTest {

    @Test
    public void resultEqualsHashCodeToString() {
        EqualsVerifier.forClass(ParametersAndTypeParametersValidator.Result.class).verify();
        ToStringVerifier.forClass(ParametersAndTypeParametersValidator.Result.class).verify();
    }
}
