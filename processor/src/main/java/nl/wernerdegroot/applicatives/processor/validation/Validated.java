package nl.wernerdegroot.applicatives.processor.validation;

import nl.wernerdegroot.applicatives.runtime.FastTuple;
import nl.wernerdegroot.applicatives.runtime.Function3;
import nl.wernerdegroot.applicatives.runtime.Function4;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;

public final class Validated<E, T> {

    private final Set<E> errorMessages;
    private final T value;
    private final boolean isValid;

    private Validated(Set<E> errorMessages, T value, boolean isValid) {
        this.errorMessages = errorMessages;
        this.value = value;
        this.isValid = isValid;
    }

    public static <E, T> Validated<E, T> valid(T value) {
        return new Validated<>(null, value, true);
    }

    public static <E, T> Validated<E, T> invalid(Set<E> errorMessages) {
        return new Validated<>(errorMessages, null, false);
    }

    @SafeVarargs
    public static <E, T> Validated<E, T> invalid(E... errorMessages) {
        return invalid(Stream.of(errorMessages).collect(toSet()));
    }

    public static <A, B, C, E> Validated<E, C> combine(Validated<E, A> left, Validated<E, B> right, BiFunction<? super A, ? super B, ? extends C> fn) {
        if (left.isValid() && right.isValid()) {
            return Validated.valid(fn.apply(left.getValue(), right.getValue()));
        } else {
            Set<E> errorMessages = new HashSet<>();

            if (!left.isValid()) {
                errorMessages.addAll(left.getErrorMessages());
            }

            if (!right.isValid()) {
                errorMessages.addAll(right.getErrorMessages());
            }

            return Validated.invalid(errorMessages);
        }
    }

    public static <A, B, C, D, E, EE> Validated<EE, E> combine(Validated<EE, A> first, Validated<EE, B> second, Validated<EE, C> third, Validated<EE, D> fourth, Function4<? super A, ? super B, ? super C, ? super D, ? extends E> fn) {
        return combine(Validated.tuple(first, second, third, 3), fourth, (tuple, element) -> fn.apply(tuple.getFirst(), tuple.getSecond(), tuple.getThird(), element));
    }

    private static <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22, P23, P24, P25, P26, E> Validated<E, FastTuple<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22, P23, P24, P25, P26>> tuple(Validated<E, P1> first, Validated<E, P2> second, int maxSize) {
        return combine(first, second, FastTuple.withMaxSize(maxSize));
    }

    private static <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22, P23, P24, P25, P26, E> Validated<E, FastTuple<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22, P23, P24, P25, P26>> tuple(Validated<E, P1> first, Validated<E, P2> second, Validated<E, P3> third, int maxSize) {
        return combine(Validated.<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22, P23, P24, P25, P26, E>tuple(first, second, maxSize), third, FastTuple::withThird);
    }

    public <U> Validated<E, U> map(Function<T, U> fn) {
        if (isValid()) {
            return Validated.valid(fn.apply(getValue()));
        } else {
            return Validated.invalid(getErrorMessages());
        }
    }

    public <U> Validated<E, U> flatMap(Function<T, Validated<E, U>> fn) {
        if (isValid()) {
            return fn.apply(getValue());
        } else {
            return Validated.invalid(getErrorMessages());
        }
    }

    public Set<E> getErrorMessages() {
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
        Validated<?, ?> validated = (Validated<?, ?>) o;
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
