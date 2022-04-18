package nl.wernerdegroot.applicatives.processor.validation;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;

public class Validated<T> {

    private final Set<String> errorMessages;
    private final T value;
    private final boolean isValid;

    private Validated(Set<String> errorMessages, T value, boolean isValid) {
        this.errorMessages = errorMessages;
        this.value = value;
        this.isValid = isValid;
    }

    public static <T> Validated<T> valid(T value) {
        return new Validated<>(null, value, true);
    }

    public static <T> Validated<T> invalid(Set<String> errorMessages) {
        return new Validated<>(errorMessages, null, false);
    }

    public static <T> Validated<T> invalid(String... errorMessages) {
        return invalid(Stream.of(errorMessages).collect(toSet()));
    }

    public Set<String> getErrorMessages() {
        return errorMessages;
    }

    public T getValue() {
        return value;
    }

    public boolean isValid() {
        return isValid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Validated<?> validated = (Validated<?>) o;
        return isValid() == validated.isValid() && Objects.equals(getErrorMessages(), validated.getErrorMessages()) && Objects.equals(getValue(), validated.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getErrorMessages(), getValue(), isValid());
    }

    @Override
    public String toString() {
        return "Validated{" +
                "errorMessages=" + errorMessages +
                ", value=" + value +
                ", isValid=" + isValid +
                '}';
    }
}
