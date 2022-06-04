package nl.wernerdegroot.applicatives.processor.validation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConfigValidatorTest {

    @Test
    public void givenInvalidClassName() {
        Validated<String, Void> expected = Validated.invalid("Class name '4Seasons' is not valid");
        Validated<String, Void> toVerify = ConfigValidator.validate("4Seasons", "lift", 23);

        assertEquals(expected, toVerify);
    }

    @Test
    public void givenValidClassName() {
        Validated<String, Void> expected = Validated.valid(null);
        Validated<String, Void> toVerify = ConfigValidator.validate("FourSeasons", "lift", 23);

        assertEquals(expected, toVerify);
    }

    @Test
    public void givenInvalidLiftMethodName() {
        Validated<String, Void> expected = Validated.invalid("Lift method name '22tango' is not valid");
        Validated<String, Void> toVerify = ConfigValidator.validate("Generated", "22tango", 23);

        assertEquals(expected, toVerify);
    }

    @Test
    public void givenValidLiftMethodNAme() {
        Validated<String, Void> expected = Validated.valid(null);
        Validated<String, Void> toVerify = ConfigValidator.validate("Generated", "twoToTango", 23);

        assertEquals(expected, toVerify);
    }

    @Test
    public void givenMaxArityTooSmall() {
        Validated<String, Void> expected = Validated.invalid("Maximum arity should be between 2 and 26 (but was -1)");
        Validated<String, Void> toVerify = ConfigValidator.validate("Generated", "lift", -1);

        assertEquals(expected, toVerify);
    }

    @Test
    public void givenMaxArityTooLarge() {
        Validated<String, Void> expected = Validated.invalid("Maximum arity should be between 2 and 26 (but was 99)");
        Validated<String, Void> toVerify = ConfigValidator.validate("Generated", "lift", 99);

        assertEquals(expected, toVerify);
    }

    @Test
    public void givenValidMaxArity() {
        Validated<String, Void> expected = Validated.valid(null);
        Validated<String, Void> toVerify = ConfigValidator.validate("Generated", "lift", 23);

        assertEquals(expected, toVerify);
    }
}
