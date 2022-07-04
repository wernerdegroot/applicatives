package nl.wernerdegroot.applicatives.processor.validation;

import com.jparams.verifier.tostring.ToStringVerifier;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ValidatedTest {

    @Test
    public void equalsHashCodeToString() {
        EqualsVerifier.forClass(Validated.class).verify();
        ToStringVerifier.forClass(Validated.class).verify();
    }

    @Test
    public void getValueGivenInvalid() {
        Validated<String, Void> invalid = Validated.invalid("pretty", "bad");
        assertThrows(NoSuchElementException.class, () -> {
            invalid.getValue();
        });
    }

    @Test
    public void getErrorMessagesGivenValid() {
        Validated<Void, String> valid = Validated.valid("OK");
        assertThrows(NoSuchElementException.class, () -> {
            valid.getErrorMessages();
        });
    }
}
