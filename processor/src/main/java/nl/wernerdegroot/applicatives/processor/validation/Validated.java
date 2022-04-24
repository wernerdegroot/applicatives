package nl.wernerdegroot.applicatives.processor.validation;

import nl.wernerdegroot.applicatives.runtime.FastTuple;
import nl.wernerdegroot.applicatives.runtime.Function3;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
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

    public static <A, B, C, D> Validated<D> combine(Validated<A> first, Validated<B> second, Validated<C> third, Function3<? super A, ? super B, ? super C, ? extends D> fn) {
        return combine(Validated.tuple(first, second, 3), third, fn::apply);
    }

    private static <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22, P23, P24, P25, P26> Validated<FastTuple<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22, P23, P24, P25, P26>> tuple(Validated<P1> first, Validated<P2> second, int maxSize) {
        return combine(first, second, FastTuple.withMaxSize(maxSize));
    }

    public <U> Validated<U> map(Function<T, U> fn) {
        if (isValid()) {
            return Validated.valid(fn.apply(getValue()));
        } else {
            return Validated.invalid(getErrorMessages());
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
