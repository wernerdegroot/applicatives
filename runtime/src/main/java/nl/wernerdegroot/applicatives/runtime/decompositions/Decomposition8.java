package nl.wernerdegroot.applicatives.runtime.decompositions;

import nl.wernerdegroot.applicatives.runtime.*;

import java.util.function.Function;

@FunctionalInterface
public interface Decomposition8<Source, First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth> {

    static <Source, First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth> Decomposition8<Source, First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth> of(Function<? super Source, ? extends Tuple8<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth>> toTuple) {
        return new Decomposition8<Source, First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth>() {
            @Override
            public <T> T decompose(Source source, Function8<? super First, ? super Second, ? super Third, ? super Fourth, ? super Fifth, ? super Sixth, ? super Seventh, ? super Eighth, ? extends T> fn) {
                return decompose(source).apply(fn);
            }

            @Override
            public Tuple8<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth> decompose(Source source) {
                return toTuple.apply(source);
            }
        };
    }

    <T> T decompose(Source source, Function8<? super First, ? super Second, ? super Third, ? super Fourth, ? super Fifth, ? super Sixth, ? super Seventh, ? super Eighth, ? extends T> fn);

    default Tuple8<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth> decompose(Source source) {
        return decompose(source, Tuple::of);
    }
}
