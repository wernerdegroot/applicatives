package nl.wernerdegroot.applicatives.prelude;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

public class Person {

    private final String firstName;
    private final Optional<String> infix;
    private final String lastName;
    private final LocalDate birthDate;

    public Person(String firstName, Optional<String> infix, String lastName, LocalDate birthDate) {
        this.firstName = firstName;
        this.infix = infix;
        this.lastName = lastName;
        this.birthDate = birthDate;
    }

    public String getFirstName() {
        return firstName;
    }

    public Optional<String> getInfix() {
        return infix;
    }

    public String getLastName() {
        return lastName;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return getFirstName().equals(person.getFirstName()) && getInfix().equals(person.getInfix()) && getLastName().equals(person.getLastName()) && getBirthDate().equals(person.getBirthDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFirstName(), getInfix(), getLastName(), getBirthDate());
    }

    @Override
    public String toString() {
        return "Person{" +
                "firstName='" + firstName + '\'' +
                ", infix=" + infix +
                ", lastName='" + lastName + '\'' +
                ", birthDate=" + birthDate +
                '}';
    }
}
