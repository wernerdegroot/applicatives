package nl.wernerdegroot.applicatives.runtime.decompositions;

import nl.wernerdegroot.applicatives.runtime.*;

import java.util.function.Function;

@FunctionalInterface
public interface Decomposition9<Source, First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth> {

    static <Source, First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth> Decomposition9<Source, First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth> of(Function<? super Source, ? extends Tuple9<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth>> toTuple) {
        return new Decomposition9<Source, First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth>() {
            @Override
            public <T> T decompose(Source source, Function9<? super First, ? super Second, ? super Third, ? super Fourth, ? super Fifth, ? super Sixth, ? super Seventh, ? super Eighth, ? super Ninth, ? extends T> fn) {
                return decompose(source).apply(fn);
            }

            @Override
            public Tuple9<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth> decompose(Source source) {
                return toTuple.apply(source);
            }
        };
    }

    <T> T decompose(Source source, Function9<? super First, ? super Second, ? super Third, ? super Fourth, ? super Fifth, ? super Sixth, ? super Seventh, ? super Eighth, ? super Ninth, ? extends T> fn);

    default Tuple9<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth> decompose(Source source) {
        return decompose(source, Tuple::of);
    }
}
