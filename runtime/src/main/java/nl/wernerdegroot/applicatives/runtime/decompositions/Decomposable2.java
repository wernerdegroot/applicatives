package nl.wernerdegroot.applicatives.runtime.decompositions;

import java.util.function.BiFunction;

public interface Decomposable2<First, Second> {
    <T> T decomposeTo(BiFunction<? super First, ? super Second, T> fn);
}
