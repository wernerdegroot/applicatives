package nl.wernerdegroot.applicatives.processor.validation;

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

public class CovariantFinalizerValidatorTest {

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
                asList(Parameter.of(ARRAY_LIST.with(T), "value"))
        );

        Validated<ValidCovariantFinalizer> expected = Validated.invalid("Method needs to return something");
        Validated<ValidCovariantFinalizer> toVerify = CovariantFinalizerValidator.validate(toValidate);

        assertEquals(expected, toVerify);
    }

    @Test
    public void validateGivenStaticMethod() {
        Method toValidate = Method.of(
                emptySet(),
                modifiers(STATIC),
                asList(T.asTypeParameter()),
                Optional.of(LIST.with(T)),
                "myFunction",
                asList(Parameter.of(ARRAY_LIST.with(T), "value"))
        );

        Validated<ValidCovariantFinalizer> expected = Validated.invalid("Method is static and cannot implement an abstract method");
        Validated<ValidCovariantFinalizer> toVerify = CovariantFinalizerValidator.validate(toValidate);

        assertEquals(expected, toVerify);
    }

    @Test
    public void validateGivenPrivateMethod() {
        Method toValidate = Method.of(
                emptySet(),
                modifiers(PRIVATE),
                asList(T.asTypeParameter()),
                Optional.of(LIST.with(T)),
                "myFunction",
                asList(Parameter.of(ARRAY_LIST.with(T), "value"))
        );

        Validated<ValidCovariantFinalizer> expected = Validated.invalid("Method is private and cannot implement an abstract method");
        Validated<ValidCovariantFinalizer> toVerify = CovariantFinalizerValidator.validate(toValidate);

        assertEquals(expected, toVerify);
    }

    @Test
    public void validateGivenMethodWithTypeParameterWithUpperBoundOtherThanObject() {
        Method toValidate = Method.of(
                emptySet(),
                modifiers(PUBLIC),
                asList(T.extending(COMPARABLE.with(T))),
                Optional.of(LIST.with(T)),
                "myFunction",
                asList(Parameter.of(ARRAY_LIST.with(T), "value"))
        );

        Validated<ValidCovariantFinalizer> expected = Validated.invalid("The type parameter needs to be unbounded");
        Validated<ValidCovariantFinalizer> toVerify = CovariantFinalizerValidator.validate(toValidate);

        assertEquals(expected, toVerify);
    }

    @Test
    public void validateGivenMethodWithLessThanOneTypeParameters() {
        Method toValidate = Method.of(
                emptySet(),
                emptySet(),
                asList(),
                Optional.of(LIST.with(STRING)),
                "myFunction",
                asList(Parameter.of(ARRAY_LIST.with(STRING), "value"))
        );

        Validated<ValidCovariantFinalizer> expected = Validated.invalid("Method requires exactly one type parameter, but found 0");
        Validated<ValidCovariantFinalizer> toVerify = CovariantFinalizerValidator.validate(toValidate);

        assertEquals(expected, toVerify);
    }

    @Test
    public void validateGivenMethodWithMoreThanOneTypeParameters() {
        Method toValidate = Method.of(
                emptySet(),
                emptySet(),
                asList(T.asTypeParameter(), U.asTypeParameter()),
                Optional.of(LIST.with(T)),
                "myFunction",
                asList(Parameter.of(ARRAY_LIST.with(T), "value"))
        );

        Validated<ValidCovariantFinalizer> expected = Validated.invalid("Method requires exactly one type parameter, but found 2");
        Validated<ValidCovariantFinalizer> toVerify = CovariantFinalizerValidator.validate(toValidate);

        assertEquals(expected, toVerify);
    }

    @Test
    public void validateGivenMethodWithLessThanOneParameter() {
        Method toValidate = Method.of(
                emptySet(),
                modifiers(PUBLIC),
                asList(T.asTypeParameter()),
                Optional.of(LIST.with(T)),
                "myFunction",
                asList()
        );

        Validated<ValidCovariantFinalizer> expected = Validated.invalid("Method requires exactly one parameter, but found 0");
        Validated<ValidCovariantFinalizer> toVerify = CovariantFinalizerValidator.validate(toValidate);

        assertEquals(expected, toVerify);
    }

    @Test
    public void validateGivenMethodWithMoreThanOneParameters() {
        Method toValidate = Method.of(
                emptySet(),
                modifiers(PUBLIC),
                asList(T.asTypeParameter()),
                Optional.of(LIST.with(T)),
                "myFunction",
                asList(Parameter.of(ARRAY_LIST.with(T), "left"), Parameter.of(ARRAY_LIST.with(T), "right"))
        );

        Validated<ValidCovariantFinalizer> expected = Validated.invalid("Method requires exactly one parameter, but found 2");
        Validated<ValidCovariantFinalizer> toVerify = CovariantFinalizerValidator.validate(toValidate);

        assertEquals(expected, toVerify);
    }

    @Test
    public void validateGivenValidMethod() {
        Method toValidate = Method.of(
                emptySet(),
                modifiers(PUBLIC),
                asList(T.asTypeParameter()),
                Optional.of(LIST.with(T)),
                "myFunction",
                asList(Parameter.of(ARRAY_LIST.with(T), "value"))
        );

        Validated<ValidCovariantFinalizer> expected = Validated.valid(ValidCovariantFinalizer.of("myFunction", ARRAY_LIST.with(T), ARRAY_LIST.asTypeConstructor(), LIST.asTypeConstructor()));
        Validated<ValidCovariantFinalizer> toVerify = CovariantFinalizerValidator.validate(toValidate);

        assertEquals(expected, toVerify);
    }

    @SafeVarargs
    private final <T> Set<T> modifiers(T... elements) {
        return Stream.of(elements).collect(toSet());
    }

}
