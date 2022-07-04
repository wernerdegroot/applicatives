package nl.wernerdegroot.applicatives;

public interface ResultsOverloads<P> {

    <P1, P2, R> java.util.function.Function<P, R> combineImpl(java.util.function.Function<P, P1> first, java.util.function.Function<P, P2> second, java.util.function.BiFunction<? super P1, ? super P2, ? extends R> fn);

    default <P1, P2, R> java.util.function.Function<P, R> combine(java.util.function.Function<P, P1> first, java.util.function.Function<P, P2> second, java.util.function.BiFunction<? super P1, ? super P2, ? extends R> fn) {
        return this.combineImpl(first, second, fn);
    }

    default <P1, P2, P3, R> java.util.function.Function<P, R> combine(java.util.function.Function<P, P1> first, java.util.function.Function<P, P2> second, java.util.function.Function<P, P3> third, nl.wernerdegroot.applicatives.runtime.Function3<? super P1, ? super P2, ? super P3, ? extends R> fn) {
        return this.combineImpl(nl.wernerdegroot.applicatives.ResultsOverloads.Tuples.<P1, P2, P>tuple(this, first, second, 3), third, (tuple, element) -> fn.apply(tuple.getFirst(), tuple.getSecond(), element));
    }

    default <P1, P2, P3, P4, R> java.util.function.Function<P, R> combine(java.util.function.Function<P, P1> first, java.util.function.Function<P, P2> second, java.util.function.Function<P, P3> third, java.util.function.Function<P, P4> fourth, nl.wernerdegroot.applicatives.runtime.Function4<? super P1, ? super P2, ? super P3, ? super P4, ? extends R> fn) {
        return this.combineImpl(nl.wernerdegroot.applicatives.ResultsOverloads.Tuples.<P1, P2, P3, P>tuple(this, first, second, third, 4), fourth, (tuple, element) -> fn.apply(tuple.getFirst(), tuple.getSecond(), tuple.getThird(), element));
    }

    default <P1, P2, R> java.util.function.BiFunction<java.util.function.Function<P, P1>, java.util.function.Function<P, P2>, java.util.function.Function<P, R>> lift(java.util.function.BiFunction<? super P1, ? super P2, ? extends R> fn) {
        return (first, second) ->
                this.combine(first, second, fn);
    }

    default <P1, P2, P3, R> nl.wernerdegroot.applicatives.runtime.Function3<java.util.function.Function<P, P1>, java.util.function.Function<P, P2>, java.util.function.Function<P, P3>, java.util.function.Function<P, R>> lift(nl.wernerdegroot.applicatives.runtime.Function3<? super P1, ? super P2, ? super P3, ? extends R> fn) {
        return (first, second, third) ->
                this.combine(first, second, third, fn);
    }

    default <P1, P2, P3, P4, R> nl.wernerdegroot.applicatives.runtime.Function4<java.util.function.Function<P, P1>, java.util.function.Function<P, P2>, java.util.function.Function<P, P3>, java.util.function.Function<P, P4>, java.util.function.Function<P, R>> lift(nl.wernerdegroot.applicatives.runtime.Function4<? super P1, ? super P2, ? super P3, ? super P4, ? extends R> fn) {
        return (first, second, third, fourth) ->
                this.combine(first, second, third, fourth, fn);
    }

    class Tuples {

        public static <P1, P2, P> java.util.function.Function<P, nl.wernerdegroot.applicatives.runtime.Tuple2<? extends P1, ? extends P2>> tuple(nl.wernerdegroot.applicatives.ResultsOverloads<P> self, java.util.function.Function<P, P1> first, java.util.function.Function<P, P2> second, int maxSize) {
            return self.<P1, P2, nl.wernerdegroot.applicatives.runtime.Tuple2<? extends P1, ? extends P2>>combineImpl(first, second, nl.wernerdegroot.applicatives.runtime.FastTuple.withMaxSize(maxSize));
        }

        public static <P1, P2, P3, P> java.util.function.Function<P, nl.wernerdegroot.applicatives.runtime.Tuple3<? extends P1, ? extends P2, ? extends P3>> tuple(nl.wernerdegroot.applicatives.ResultsOverloads<P> self, java.util.function.Function<P, P1> first, java.util.function.Function<P, P2> second, java.util.function.Function<P, P3> third, int maxSize) {
            return self.<nl.wernerdegroot.applicatives.runtime.Tuple2<? extends P1, ? extends P2>, P3, nl.wernerdegroot.applicatives.runtime.Tuple3<? extends P1, ? extends P2, ? extends P3>>combineImpl(nl.wernerdegroot.applicatives.ResultsOverloads.Tuples.<P1, P2, P>tuple(self, first, second, maxSize), third, nl.wernerdegroot.applicatives.runtime.Tuple2::withThird);
        }

    }

}