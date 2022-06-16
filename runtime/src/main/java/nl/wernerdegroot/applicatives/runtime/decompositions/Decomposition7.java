package nl.wernerdegroot.applicatives.runtime.decompositions;

import nl.wernerdegroot.applicatives.runtime.*;

import java.util.function.Function;

@FunctionalInterface
public interface Decomposition7<Source, First, Second, Third, Fourth, Fifth, Sixth, Seventh> {

    static <Source, First, Second, Third, Fourth, Fifth, Sixth, Seventh> Decomposition7<Source, First, Second, Third, Fourth, Fifth, Sixth, Seventh> of(Function<? super Source, ? extends Tuple7<First, Second, Third, Fourth, Fifth, Sixth, Seventh>> toTuple) {
        return new Decomposition7<Source, First, Second, Third, Fourth, Fifth, Sixth, Seventh>() {
            @Override
            public <T> T decompose(Source source, Function7<? super First, ? super Second, ? super Third, ? super Fourth, ? super Fifth, ? super Sixth, ? super Seventh, ? extends T> fn) {
                return decompose(source).apply(fn);
            }

            @Override
            public Tuple7<First, Second, Third, Fourth, Fifth, Sixth, Seventh> decompose(Source source) {
                return toTuple.apply(source);
            }
        };
    }

    <T> T decompose(Source source, Function7<? super First, ? super Second, ? super Third, ? super Fourth, ? super Fifth, ? super Sixth, ? super Seventh, ? extends T> fn);

    default Tuple7<First, Second, Third, Fourth, Fifth, Sixth, Seventh> decompose(Source source) {
        return decompose(source, Tuple::of);
    }
}
