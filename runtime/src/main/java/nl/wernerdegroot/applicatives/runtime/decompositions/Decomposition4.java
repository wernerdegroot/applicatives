package nl.wernerdegroot.applicatives.runtime.decompositions;

import nl.wernerdegroot.applicatives.runtime.Function4;
import nl.wernerdegroot.applicatives.runtime.Tuple;
import nl.wernerdegroot.applicatives.runtime.Tuple4;

import java.util.function.Function;

@FunctionalInterface
public interface Decomposition4<Source, First, Second, Third, Fourth> {

    static <Source, First, Second, Third, Fourth> Decomposition4<Source, First, Second, Third, Fourth> of(Function<? super Source, ? extends Tuple4<First, Second, Third, Fourth>> toTuple) {
        return new Decomposition4<Source, First, Second, Third, Fourth>() {
            @Override
            public <T> T decompose(Source source, Function4<? super First, ? super Second, ? super Third, ? super Fourth, ? extends T> fn) {
                return decompose(source).apply(fn);
            }

            @Override
            public Tuple4<First, Second, Third, Fourth> decompose(Source source) {
                return toTuple.apply(source);
            }
        };
    }

    <T> T decompose(Source source, Function4<? super First, ? super Second, ? super Third, ? super Fourth, ? extends T> fn);

    default Tuple4<First, Second, Third, Fourth> decompose(Source source) {
        return decompose(source, Tuple::of);
    }
}
