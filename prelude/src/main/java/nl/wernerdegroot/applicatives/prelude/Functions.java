package nl.wernerdegroot.applicatives.prelude;

import nl.wernerdegroot.applicatives.runtime.Contravariant;
import nl.wernerdegroot.applicatives.runtime.Covariant;

import java.util.Collection;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collector;

public class Functions {

    private static final Results<?> RESULT_INSTANCE = new Results<>();

    @SuppressWarnings("unchecked")
    public static <P> Results<P> resultsInstance() {
        return (Results<P>) RESULT_INSTANCE;
    }

    public static <R> Parameters<R> parametersInstance(BinaryOperator<R> combineResults) {
        return new Parameters<>(combineResults);
    }

    public static class Results<P> implements ResultsOverloads<P> {

        @Override
        @Covariant
        public <A, B, C> Function<P, C> combine(Function<? super P, ? extends A> left, Function<? super P, ? extends B> right, BiFunction<? super A, ? super B, ? extends C> fn) {
            return p -> {
                A valueFromLeft = left.apply(p);
                B valueFromRight = right.apply(p);
                return fn.apply(valueFromLeft, valueFromRight);
            };
        }
    }

    public static class Parameters<R> implements ParametersOverloads<R> {

        private final BinaryOperator<R> combineResults;

        public Parameters(BinaryOperator<R> combineResults) {
            this.combineResults = combineResults;
        }

        @Override
        @Contravariant(combineMethodName = "combine")
        public <A, B, Intermediate, C> Function<C, R> combineImpl(Function<? super A, ? extends R> left, Function<? super B, ? extends R> right, Function<? super C, ? extends Intermediate> toIntermediate, Function<? super Intermediate, ? extends A> extractLeft, Function<? super Intermediate, ? extends B> extractRight) {
            return p -> {
                Intermediate intermediate = toIntermediate.apply(p);
                A fromLeft = extractLeft.apply(intermediate);
                B fromRight = extractRight.apply(intermediate);
                return combineResults.apply(left.apply(fromLeft), right.apply(fromRight));
            };
        }

        public static <P, T, A, R> Function<Collection<? extends P>, R> many(Function<? super P, ? extends T> single, Collector<T, A, R> collector) {
            return ps -> ps.stream().map(single).collect(collector);
        }
    }
}
