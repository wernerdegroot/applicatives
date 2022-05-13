package nl.wernerdegroot.applicatives.runtime;

import java.util.function.BiFunction;

public interface Tuple2<First, Second> {

    First getFirst();

    Second getSecond();

    <Third> Tuple3<First, Second, Third> withThird(Third third);

    default <R> R apply(BiFunction<? super First, ? super Second, ? extends R> fn) {
        return fn.apply(getFirst(), getSecond());
    }
}

