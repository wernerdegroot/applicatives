package nl.wernerdegroot.applicatives.prelude;

import nl.wernerdegroot.applicatives.runtime.Covariant;

import java.util.function.BiFunction;
import java.util.function.Function;

public class Functions<P> implements FunctionsOverloads<P> {

    private static final Functions<?> INSTANCE = new Functions<>();

    @SuppressWarnings("unchecked")
    public static <P> Functions<P> instance() {
        return (Functions<P>) INSTANCE;
    }

    @Covariant(className = "FunctionsOverloads")
    public <A, B, C> Function<P, C> combine(Function<? super P, ? extends A> left, Function<? super P, ? extends B> right, BiFunction<? super A, ? super B, ? extends C> fn) {
        return p -> {
            A valueFromLeft = left.apply(p);
            B valueFromRight = right.apply(p);
            return fn.apply(valueFromLeft, valueFromRight);
        };
    }
}
