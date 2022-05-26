package nl.wernerdegroot.applicatives.processor.validation;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.wernerdegroot.applicatives.processor.domain.Method;
import nl.wernerdegroot.applicatives.processor.domain.Parameter;
import nl.wernerdegroot.applicatives.processor.domain.TypeParameterName;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.toSet;
import static nl.wernerdegroot.applicatives.processor.domain.Modifier.*;
import static nl.wernerdegroot.applicatives.processor.domain.type.Type.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CovariantInitializerValidatorTest {

    private final TypeParameterName T = TypeParameterName.of("T");
    private final TypeParameterName U = TypeParameterName.of("U");

    @Test
    public void validateGivenMethodThatDoesNotReturnAnything() {
        Method toValidate = Method.of(
                emptySet(),
                modifiers(PUBLIC),
                asList(T.asTypeParameter()),
                Optional.empty(),
                "myFunction",
                asList(Parameter.of(T.asType(), "value"))
        );

        Validated<CovariantInitializerValidator.Result> expected = Validated.invalid("Method needs to return something");
        Validated<CovariantInitializerValidator.Result> toVerify = CovariantInitializerValidator.validate(toValidate);

        assertEquals(expected, toVerify);
    }

    @Test
    public void validateGivenStaticMethod() {
        Method toValidate = Method.of(
                emptySet(),
                modifiers(STATIC),
                asList(T.asTypeParameter()),
                Optional.of(OPTIONAL.with(T)),
                "myFunction",
                asList(Parameter.of(T.asType(), "value"))
        );

        Validated<CovariantInitializerValidator.Result> expected = Validated.invalid("Method is static and cannot implement an abstract method");
        Validated<CovariantInitializerValidator.Result> toVerify = CovariantInitializerValidator.validate(toValidate);

        assertEquals(expected, toVerify);
    }

    @Test
    public void validateGivenPrivateMethod() {
        Method toValidate = Method.of(
                emptySet(),
                modifiers(PRIVATE),
                asList(T.asTypeParameter()),
                Optional.of(OPTIONAL.with(T)),
                "myFunction",
                asList(Parameter.of(T.asType(), "value"))
        );

        Validated<CovariantInitializerValidator.Result> expected = Validated.invalid("Method is private and cannot implement an abstract method");
        Validated<CovariantInitializerValidator.Result> toVerify = CovariantInitializerValidator.validate(toValidate);

        assertEquals(expected, toVerify);
    }

    @Test
    public void validateGivenMethodWithTypeParameterWithUpperBoundOtherThanObject() {
        Method toValidate = Method.of(
                emptySet(),
                modifiers(PUBLIC),
                asList(T.extending(COMPARABLE.with(T))),
                Optional.of(OPTIONAL.with(T)),
                "myFunction",
                asList(Parameter.of(T.asType(), "value"))
        );

        Validated<CovariantInitializerValidator.Result> expected = Validated.invalid("The type parameters need to be unbounded");
        Validated<CovariantInitializerValidator.Result> toVerify = CovariantInitializerValidator.validate(toValidate);

        assertEquals(expected, toVerify);
    }

    @Test
    public void validateGivenMethodWithLessThanOneTypeParameters() {
        Method toValidate = Method.of(
                emptySet(),
                emptySet(),
                asList(),
                Optional.of(OPTIONAL.with(T)),
                "myFunction",
                asList(Parameter.of(T.asType(), "value"))
        );

        Validated<CovariantInitializerValidator.Result> expected = Validated.invalid("Method requires exactly 1 type parameters, but found 0");
        Validated<CovariantInitializerValidator.Result> toVerify = CovariantInitializerValidator.validate(toValidate);

        assertEquals(expected, toVerify);
    }

    @Test
    public void validateGivenMethodWithMoreThanOneTypeParameters() {
        Method toValidate = Method.of(
                emptySet(),
                emptySet(),
                asList(T.asTypeParameter(), U.asTypeParameter()),
                Optional.of(OPTIONAL.with(T)),
                "myFunction",
                asList(Parameter.of(T.asType(), "value"))
        );

        Validated<CovariantInitializerValidator.Result> expected = Validated.invalid("Method requires exactly 1 type parameters, but found 2");
        Validated<CovariantInitializerValidator.Result> toVerify = CovariantInitializerValidator.validate(toValidate);

        assertEquals(expected, toVerify);
    }

    @Test
    public void validateGivenMethodWithLessThanOneParameters() {
        Method toValidate = Method.of(
                emptySet(),
                modifiers(PUBLIC),
                asList(T.asTypeParameter()),
                Optional.of(OPTIONAL.with(T)),
                "myFunction",
                asList()
        );

        Validated<CovariantInitializerValidator.Result> expected = Validated.invalid("Method requires exactly 1 parameters, but found 0");
        Validated<CovariantInitializerValidator.Result> toVerify = CovariantInitializerValidator.validate(toValidate);

        assertEquals(expected, toVerify);
    }

    @Test
    public void validateGivenMethodWithMoreThanOneParameters() {
        Method toValidate = Method.of(
                emptySet(),
                modifiers(PUBLIC),
                asList(T.asTypeParameter()),
                Optional.of(OPTIONAL.with(T)),
                "myFunction",
                asList(Parameter.of(T.asType(), "left"), Parameter.of(T.asType(), "right"))
        );

        Validated<CovariantInitializerValidator.Result> expected = Validated.invalid("Method requires exactly 1 parameters, but found 2");
        Validated<CovariantInitializerValidator.Result> toVerify = CovariantInitializerValidator.validate(toValidate);

        assertEquals(expected, toVerify);
    }

    @Test
    public void validateGivenMethodWithWrongParameter() {
        Method toValidate = Method.of(
                emptySet(),
                modifiers(PUBLIC),
                asList(T.asTypeParameter()),
                Optional.of(OPTIONAL.with(T)),
                "myFunction",
                asList(Parameter.of(STRING, "value"))
        );

        Validated<CovariantInitializerValidator.Result> expected = Validated.invalid("Expected parameter to be T but was java.lang.String");
        Validated<CovariantInitializerValidator.Result> toVerify = CovariantInitializerValidator.validate(toValidate);

        assertEquals(expected, toVerify);
    }

    @Test
    public void validateGivenValidMethod() {
        Method toValidate = Method.of(
                emptySet(),
                modifiers(PUBLIC),
                asList(T.asTypeParameter()),
                Optional.of(OPTIONAL.with(T)),
                "myFunction",
                asList(Parameter.of(T.asType(), "value"))
        );

        Validated<CovariantInitializerValidator.Result> expected = Validated.valid(CovariantInitializerValidator.Result.of("myFunction", OPTIONAL.asTypeConstructor(), OPTIONAL.with(T)));
        Validated<CovariantInitializerValidator.Result> toVerify = CovariantInitializerValidator.validate(toValidate);

        assertEquals(expected, toVerify);
    }

    @Test
    public void resultEquals() {
        EqualsVerifier.forClass(CovariantInitializerValidator.Result.class).verify();
    }

    @SafeVarargs
    private final <T> Set<T> modifiers(T... elements) {
        return Stream.of(elements).collect(toSet());
    }

}
