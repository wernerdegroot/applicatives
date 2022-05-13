package nl.wernerdegroot.applicatives.runtime;

import java.util.function.BiFunction;

@FunctionalInterface
public interface Function2<A, B, Result> {

    static <A, B, Result> Function2<A, B, Result> fromBiFunction(BiFunction<A, B, Result> fn) {
        return fn::apply;
    }

    Result apply(A first, B second);

    default Result apply(Tuple1<? extends A> tuple, B second) {
        return apply(tuple.getFirst(), second);
    }
}
