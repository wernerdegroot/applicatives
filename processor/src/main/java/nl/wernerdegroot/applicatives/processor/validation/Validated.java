package nl.wernerdegroot.applicatives.processor.validation;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import static java.util.Collections.emptySet;
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

    public static <A, B, C> Validated<C> combine(Validated<A> left, Validated<B> right, BiFunction<? super A, ? super B, ? extends C> fn) {
        if (left.isValid() && right.isValid()) {
            return Validated.valid(fn.apply(left.getValue(), right.getValue()));
        } else {
            Set<String> errorMessages = new HashSet<>();

            if (!left.isValid()) {
                errorMessages.addAll(left.getErrorMessages());
            }

            if (!right.isValid()) {
                errorMessages.addAll(right.getErrorMessages());
            }

            return Validated.invalid(errorMessages);
        }
    }

    public Set<String> getErrorMessages() {
        if (isValid) {
            throw new NoSuchElementException();
        }

        return errorMessages;
    }

    public T getValue() {
        if (!isValid) {
            throw new NoSuchElementException();
        }

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
        return isValid == validated.isValid && Objects.equals(errorMessages, validated.errorMessages) && Objects.equals(value, validated.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(errorMessages, value, isValid);
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
