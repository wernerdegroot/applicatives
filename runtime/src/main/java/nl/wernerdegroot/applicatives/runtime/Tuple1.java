package nl.wernerdegroot.applicatives.runtime;

import java.util.function.Function;

public interface Tuple1<First> {

    First getFirst();

    <Second> Tuple2<First, Second> withSecond(Second second);

    default <R> R apply(Function<? super First, ? extends R> fn) {
        return fn.apply(getFirst());
    }
}

