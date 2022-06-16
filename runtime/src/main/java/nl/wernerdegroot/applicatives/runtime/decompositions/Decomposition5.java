package nl.wernerdegroot.applicatives.runtime.decompositions;

import nl.wernerdegroot.applicatives.runtime.Function5;
import nl.wernerdegroot.applicatives.runtime.Tuple;
import nl.wernerdegroot.applicatives.runtime.Tuple5;

import java.util.function.Function;

@FunctionalInterface
public interface Decomposition5<Source, First, Second, Third, Fourth, Fifth> {

    static <Source, First, Second, Third, Fourth, Fifth> Decomposition5<Source, First, Second, Third, Fourth, Fifth> of(Function<? super Source, ? extends Tuple5<First, Second, Third, Fourth, Fifth>> toTuple) {
        return new Decomposition5<Source, First, Second, Third, Fourth, Fifth>() {
            @Override
            public <T> T decompose(Source source, Function5<? super First, ? super Second, ? super Third, ? super Fourth, ? super Fifth, ? extends T> fn) {
                return decompose(source).apply(fn);
            }

            @Override
            public Tuple5<First, Second, Third, Fourth, Fifth> decompose(Source source) {
                return toTuple.apply(source);
            }
        };
    }

    <T> T decompose(Source source, Function5<? super First, ? super Second, ? super Third, ? super Fourth, ? super Fifth, ? extends T> fn);

    default Tuple5<First, Second, Third, Fourth, Fifth> decompose(Source source) {
        return decompose(source, Tuple::of);
    }
}
