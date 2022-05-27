package nl.wernerdegroot.applicatives.prelude;

import nl.wernerdegroot.applicatives.runtime.Tuple;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OptionalsTest {

    @Test
    public void combineGivenNonEmptyOptionals() {
        Optional<String> optionalFirstName = Optional.of("Jack");
        Optional<Optional<String>> optionalInfix = Optional.of(Optional.empty());
        Optional<String> optionalLastName = Optional.of("Bauer");
        Optional<LocalDate> optionalBirthDate = Optional.of(LocalDate.of(1966, 2, 18));

        Optional<Person> toVerify = Optionals.instance().combine(
                optionalFirstName,
                optionalInfix,
                optionalLastName,
                optionalBirthDate,
                Person::new
        );

        Optional<Person> expected = Optional.of(new Person("Jack", Optional.empty(), "Bauer", LocalDate.of(1966, 2, 18)));

        assertEquals(expected, toVerify);
    }

    @Test
    public void combineGivenSingleEmptyOptional() {
        Optional<String> optionalFirstName = Optional.of("Jack");
        Optional<Optional<String>> optionalInfix = Optional.empty();
        Optional<String> optionalLastName = Optional.of("Bauer");
        Optional<LocalDate> optionalBirthDate = Optional.of(LocalDate.of(1966, 2, 18));

        Optional<Person> toVerify = Optionals.instance().combine(
                optionalFirstName,
                optionalInfix,
                optionalLastName,
                optionalBirthDate,
                Person::new
        );

        Optional<Person> expected = Optional.empty();

        assertEquals(expected, toVerify);
    }

    @Test
    public void liftGivenNonEmptyOptionals() {
        Optional<String> optionalFirstName = Optional.of("Jack");
        Optional<Optional<String>> optionalInfix = Optional.of(Optional.empty());
        Optional<String> optionalLastName = Optional.of("Bauer");
        Optional<LocalDate> optionalBirthDate = Optional.of(LocalDate.of(1966, 2, 18));

        Optional<Person> toVerify = Tuple.of(
                optionalFirstName,
                optionalInfix,
                optionalLastName,
                optionalBirthDate
        ).apply(Optionals.instance().lift(Person::new));

        Optional<Person> expected = Optional.of(new Person("Jack", Optional.empty(), "Bauer", LocalDate.of(1966, 2, 18)));

        assertEquals(expected, toVerify);
    }

    @Test
    public void liftGivenSingleEmptyOptional() {
        Optional<String> optionalFirstName = Optional.of("Jack");
        Optional<Optional<String>> optionalInfix = Optional.empty();
        Optional<String> optionalLastName = Optional.of("Bauer");
        Optional<LocalDate> optionalBirthDate = Optional.of(LocalDate.of(1966, 2, 18));

        Optional<Person> toVerify = Tuple.of(
                optionalFirstName,
                optionalInfix,
                optionalLastName,
                optionalBirthDate
        ).apply(Optionals.instance().lift(Person::new));

        Optional<Person> expected = Optional.empty();

        assertEquals(expected, toVerify);
    }
}
