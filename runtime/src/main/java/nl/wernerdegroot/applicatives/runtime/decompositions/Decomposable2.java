package nl.wernerdegroot.applicatives.runtime.decompositions;

import nl.wernerdegroot.applicatives.runtime.Tuple;
import nl.wernerdegroot.applicatives.runtime.Tuple2;

import java.util.function.BiFunction;

public interface Decomposable2<First, Second> {
    <T> T decomposeTo(BiFunction<? super First, ? super Second, T> fn);

    default Tuple2<First, Second> decompose() {
        return decomposeTo(Tuple::of);
    }
}
