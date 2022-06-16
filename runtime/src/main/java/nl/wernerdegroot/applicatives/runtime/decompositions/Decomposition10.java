package nl.wernerdegroot.applicatives.runtime.decompositions;

import nl.wernerdegroot.applicatives.runtime.*;

import java.util.function.Function;

@FunctionalInterface
public interface Decomposition10<Source, First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth> {

    static <Source, First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth> Decomposition10<Source, First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth> of(Function<? super Source, ? extends Tuple10<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth>> toTuple) {
        return new Decomposition10<Source, First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth>() {
            @Override
            public <T> T decompose(Source source, Function10<? super First, ? super Second, ? super Third, ? super Fourth, ? super Fifth, ? super Sixth, ? super Seventh, ? super Eighth, ? super Ninth, ? super Tenth, ? extends T> fn) {
                return decompose(source).apply(fn);
            }

            @Override
            public Tuple10<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth> decompose(Source source) {
                return toTuple.apply(source);
            }
        };
    }

    <T> T decompose(Source source, Function10<? super First, ? super Second, ? super Third, ? super Fourth, ? super Fifth, ? super Sixth, ? super Seventh, ? super Eighth, ? super Ninth, ? super Tenth, ? extends T> fn);

    default Tuple10<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth> decompose(Source source) {
        return decompose(source, Tuple::of);
    }
}
