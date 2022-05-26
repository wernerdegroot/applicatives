package nl.wernerdegroot.applicatives.processor.validation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConfigValidatorTest {

    @Test
    public void givenInvalidClassName() {
        Validated<Void> expected = Validated.invalid("Class name '4Seasons' is not valid");
        Validated<Void> toVerify = ConfigValidator.validate("4Seasons", "lift", 23);

        assertEquals(expected, toVerify);
    }

    @Test
    public void givenValidClassName() {
        Validated<Void> expected = Validated.valid(null);
        Validated<Void> toVerify = ConfigValidator.validate("FourSeasons", "lift", 23);

        assertEquals(expected, toVerify);
    }

    @Test
    public void givenInvalidLiftMethodName() {
        Validated<Void> expected = Validated.invalid("Lift method name '22tango' is not valid");
        Validated<Void> toVerify = ConfigValidator.validate("Generated", "22tango", 23);

        assertEquals(expected, toVerify);
    }

    @Test
    public void givenValidLiftMethodNAme() {
        Validated<Void> expected = Validated.valid(null);
        Validated<Void> toVerify = ConfigValidator.validate("Generated", "twoToTango", 23);

        assertEquals(expected, toVerify);
    }

    @Test
    public void givenMaxArityTooSmall() {
        Validated<Void> expected = Validated.invalid("Maximum arity should be between 2 and 26 (but was -1)");
        Validated<Void> toVerify = ConfigValidator.validate("Generated", "lift", -1);

        assertEquals(expected, toVerify);
    }

    @Test
    public void givenMaxArityTooLarge() {
        Validated<Void> expected = Validated.invalid("Maximum arity should be between 2 and 26 (but was 99)");
        Validated<Void> toVerify = ConfigValidator.validate("Generated", "lift", 99);

        assertEquals(expected, toVerify);
    }

    @Test
    public void givenValidMaxArity() {
        Validated<Void> expected = Validated.valid(null);
        Validated<Void> toVerify = ConfigValidator.validate("Generated", "lift", 23);

        assertEquals(expected, toVerify);
    }
}
