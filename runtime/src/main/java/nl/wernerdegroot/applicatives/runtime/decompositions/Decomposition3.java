package nl.wernerdegroot.applicatives.runtime.decompositions;

import nl.wernerdegroot.applicatives.runtime.Function3;
import nl.wernerdegroot.applicatives.runtime.Tuple;
import nl.wernerdegroot.applicatives.runtime.Tuple3;

import java.util.function.Function;

@FunctionalInterface
public interface Decomposition3<Source, First, Second, Third> {

    static <Source, First, Second, Third> Decomposition3<Source, First, Second, Third> of(Function<? super Source, ? extends Tuple3<First, Second, Third>> toTuple) {
        return new Decomposition3<Source, First, Second, Third>() {
            @Override
            public <T> T decompose(Source source, Function3<? super First, ? super Second, ? super Third, ? extends T> fn) {
                return decompose(source).apply(fn);
            }

            @Override
            public Tuple3<First, Second, Third> decompose(Source source) {
                return toTuple.apply(source);
            }
        };
    }

    <T> T decompose(Source source, Function3<? super First, ? super Second, ? super Third, ? extends T> fn);

    default Tuple3<First, Second, Third> decompose(Source source) {
        return decompose(source, Tuple::of);
    }
}
