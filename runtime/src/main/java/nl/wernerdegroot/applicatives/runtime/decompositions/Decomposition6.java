package nl.wernerdegroot.applicatives.runtime.decompositions;

import nl.wernerdegroot.applicatives.runtime.Function6;
import nl.wernerdegroot.applicatives.runtime.Tuple;
import nl.wernerdegroot.applicatives.runtime.Tuple2;
import nl.wernerdegroot.applicatives.runtime.Tuple6;

import java.util.function.BiFunction;
import java.util.function.Function;

@FunctionalInterface
public interface Decomposition6<Source, First, Second, Third, Fourth, Fifth, Sixth> {

    static <Source, First, Second, Third, Fourth, Fifth, Sixth> Decomposition6<Source, First, Second, Third, Fourth, Fifth, Sixth> of(Function<? super Source, ? extends Tuple6<First, Second, Third, Fourth, Fifth, Sixth>> toTuple) {
        return new Decomposition6<Source, First, Second, Third, Fourth, Fifth, Sixth>() {
            @Override
            public <T> T decompose(Source source, Function6<? super First, ? super Second, ? super Third, ? super Fourth, ? super Fifth, ? super Sixth, ? extends T> fn) {
                return decompose(source).apply(fn);
            }

            @Override
            public Tuple6<First, Second, Third, Fourth, Fifth, Sixth> decompose(Source source) {
                return toTuple.apply(source);
            }
        };
    }

    <T> T decompose(Source source, Function6<? super First, ? super Second, ? super Third, ? super Fourth, ? super Fifth, ? super Sixth, ? extends T> fn);

    default Tuple6<First, Second, Third, Fourth, Fifth, Sixth> decompose(Source source) {
        return decompose(source, Tuple::of);
    }
}
