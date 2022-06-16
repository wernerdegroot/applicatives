package nl.wernerdegroot.applicatives.runtime.decompositions;

import nl.wernerdegroot.applicatives.runtime.Tuple;
import nl.wernerdegroot.applicatives.runtime.Tuple2;

import java.util.function.BiFunction;
import java.util.function.Function;

@FunctionalInterface
public interface Decomposition2<Source, First, Second> {

    static <Source, First, Second> Decomposition2<Source, First, Second> of(Function<? super Source, ? extends Tuple2<First, Second>> toTuple) {
        return new Decomposition2<Source, First, Second>() {
            @Override
            public <T> T decompose(Source source, BiFunction<? super First, ? super Second, ? extends T> fn) {
                return decompose(source).apply(fn);
            }

            @Override
            public Tuple2<First, Second> decompose(Source source) {
                return toTuple.apply(source);
            }
        };
    }

    <T> T decompose(Source source, BiFunction<? super First, ? super Second, ? extends T> fn);

    default Tuple2<First, Second> decompose(Source source) {
        return decompose(source, Tuple::of);
    }
}
