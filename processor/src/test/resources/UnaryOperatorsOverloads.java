package nl.wernerdegroot.applicatives;

public interface UnaryOperatorsOverloads {

    <P1, P2, Intermediate, R> java.util.function.UnaryOperator<R> combineImpl(java.util.function.UnaryOperator<P1> first, java.util.function.UnaryOperator<P2> second, java.util.function.BiFunction<? super P1, ? super P2, ? extends R> fn, java.util.function.Function<? super R, ? extends Intermediate> toIntermediate, java.util.function.Function<? super Intermediate, ? extends P1> extractLeft, java.util.function.Function<? super Intermediate, ? extends P2> extractRight);

    default <P1, P2, R> java.util.function.UnaryOperator<R> combine(java.util.function.UnaryOperator<P1> first, java.util.function.UnaryOperator<P2> second, java.util.function.BiFunction<? super P1, ? super P2, ? extends R> fn, nl.wernerdegroot.applicatives.runtime.decompositions.Decomposition2<? super R, ? extends P1, ? extends P2> decomposition) {
        return this.combineImpl(first, second, fn, decomposition::decompose, nl.wernerdegroot.applicatives.runtime.Tuple2::getFirst, nl.wernerdegroot.applicatives.runtime.Tuple2::getSecond);
    }

    default <P1, P2, R extends nl.wernerdegroot.applicatives.runtime.decompositions.Decomposable2<P1, P2>> java.util.function.UnaryOperator<R> combine(java.util.function.UnaryOperator<P1> first, java.util.function.UnaryOperator<P2> second, java.util.function.BiFunction<? super P1, ? super P2, ? extends R> fn) {
        return this.<P1, P2, R>combine(first, second, fn, R::decompose);
    }

    default <P1, P2, P3, R> java.util.function.UnaryOperator<R> combine(java.util.function.UnaryOperator<P1> first, java.util.function.UnaryOperator<P2> second, java.util.function.UnaryOperator<P3> third, nl.wernerdegroot.applicatives.runtime.Function3<? super P1, ? super P2, ? super P3, ? extends R> fn, nl.wernerdegroot.applicatives.runtime.decompositions.Decomposition3<? super R, ? extends P1, ? extends P2, ? extends P3> decomposition) {
        return this.combineImpl(nl.wernerdegroot.applicatives.UnaryOperatorsOverloads.Tuples.<P1, P2>tuple(this, first, second, 3), third, (tuple, element) -> fn.apply(tuple.getFirst(), tuple.getSecond(), element), decomposition::decompose, nl.wernerdegroot.applicatives.runtime.Tuple3::withoutThird, nl.wernerdegroot.applicatives.runtime.Tuple3::getThird);
    }

    default <P1, P2, P3, R extends nl.wernerdegroot.applicatives.runtime.decompositions.Decomposable3<P1, P2, P3>> java.util.function.UnaryOperator<R> combine(java.util.function.UnaryOperator<P1> first, java.util.function.UnaryOperator<P2> second, java.util.function.UnaryOperator<P3> third, nl.wernerdegroot.applicatives.runtime.Function3<? super P1, ? super P2, ? super P3, ? extends R> fn) {
        return this.<P1, P2, P3, R>combine(first, second, third, fn, R::decompose);
    }

    default <P1, P2, P3, P4, R> java.util.function.UnaryOperator<R> combine(java.util.function.UnaryOperator<P1> first, java.util.function.UnaryOperator<P2> second, java.util.function.UnaryOperator<P3> third, java.util.function.UnaryOperator<P4> fourth, nl.wernerdegroot.applicatives.runtime.Function4<? super P1, ? super P2, ? super P3, ? super P4, ? extends R> fn, nl.wernerdegroot.applicatives.runtime.decompositions.Decomposition4<? super R, ? extends P1, ? extends P2, ? extends P3, ? extends P4> decomposition) {
        return this.combineImpl(nl.wernerdegroot.applicatives.UnaryOperatorsOverloads.Tuples.<P1, P2, P3>tuple(this, first, second, third, 4), fourth, (tuple, element) -> fn.apply(tuple.getFirst(), tuple.getSecond(), tuple.getThird(), element), decomposition::decompose, nl.wernerdegroot.applicatives.runtime.Tuple4::withoutFourth, nl.wernerdegroot.applicatives.runtime.Tuple4::getFourth);
    }

    default <P1, P2, P3, P4, R extends nl.wernerdegroot.applicatives.runtime.decompositions.Decomposable4<P1, P2, P3, P4>> java.util.function.UnaryOperator<R> combine(java.util.function.UnaryOperator<P1> first, java.util.function.UnaryOperator<P2> second, java.util.function.UnaryOperator<P3> third, java.util.function.UnaryOperator<P4> fourth, nl.wernerdegroot.applicatives.runtime.Function4<? super P1, ? super P2, ? super P3, ? super P4, ? extends R> fn) {
        return this.<P1, P2, P3, P4, R>combine(first, second, third, fourth, fn, R::decompose);
    }

    default <P1, P2, R> java.util.function.BiFunction<java.util.function.UnaryOperator<P1>, java.util.function.UnaryOperator<P2>, java.util.function.UnaryOperator<R>> lift(java.util.function.BiFunction<? super P1, ? super P2, ? extends R> fn, nl.wernerdegroot.applicatives.runtime.decompositions.Decomposition2<? super R, ? extends P1, ? extends P2> decomposition) {
        return (first, second) ->
                this.combine(first, second, fn, decomposition);
    }

    default <P1, P2, P3, R> nl.wernerdegroot.applicatives.runtime.Function3<java.util.function.UnaryOperator<P1>, java.util.function.UnaryOperator<P2>, java.util.function.UnaryOperator<P3>, java.util.function.UnaryOperator<R>> lift(nl.wernerdegroot.applicatives.runtime.Function3<? super P1, ? super P2, ? super P3, ? extends R> fn, nl.wernerdegroot.applicatives.runtime.decompositions.Decomposition3<? super R, ? extends P1, ? extends P2, ? extends P3> decomposition) {
        return (first, second, third) ->
                this.combine(first, second, third, fn, decomposition);
    }

    default <P1, P2, P3, P4, R> nl.wernerdegroot.applicatives.runtime.Function4<java.util.function.UnaryOperator<P1>, java.util.function.UnaryOperator<P2>, java.util.function.UnaryOperator<P3>, java.util.function.UnaryOperator<P4>, java.util.function.UnaryOperator<R>> lift(nl.wernerdegroot.applicatives.runtime.Function4<? super P1, ? super P2, ? super P3, ? super P4, ? extends R> fn, nl.wernerdegroot.applicatives.runtime.decompositions.Decomposition4<? super R, ? extends P1, ? extends P2, ? extends P3, ? extends P4> decomposition) {
        return (first, second, third, fourth) ->
                this.combine(first, second, third, fourth, fn, decomposition);
    }

    class Tuples {

        public static <P1, P2> java.util.function.UnaryOperator<nl.wernerdegroot.applicatives.runtime.Tuple2<? extends P1, ? extends P2>> tuple(nl.wernerdegroot.applicatives.UnaryOperatorsOverloads self, java.util.function.UnaryOperator<P1> first, java.util.function.UnaryOperator<P2> second, int maxSize) {
            return self.combineImpl(first, second, nl.wernerdegroot.applicatives.runtime.FastTuple.withMaxSize(maxSize), java.util.function.Function.identity(), nl.wernerdegroot.applicatives.runtime.Tuple2::getFirst, nl.wernerdegroot.applicatives.runtime.Tuple2::getSecond);
        }

        public static <P1, P2, P3> java.util.function.UnaryOperator<nl.wernerdegroot.applicatives.runtime.Tuple3<? extends P1, ? extends P2, ? extends P3>> tuple(nl.wernerdegroot.applicatives.UnaryOperatorsOverloads self, java.util.function.UnaryOperator<P1> first, java.util.function.UnaryOperator<P2> second, java.util.function.UnaryOperator<P3> third, int maxSize) {
            return self.<nl.wernerdegroot.applicatives.runtime.Tuple2<? extends P1, ? extends P2>, P3, nl.wernerdegroot.applicatives.runtime.Tuple3<? extends P1, ? extends P2, ? extends P3>, nl.wernerdegroot.applicatives.runtime.Tuple3<? extends P1, ? extends P2, ? extends P3>>combineImpl(nl.wernerdegroot.applicatives.UnaryOperatorsOverloads.Tuples.<P1, P2>tuple(self, first, second, maxSize), third, nl.wernerdegroot.applicatives.runtime.Tuple2::withThird, java.util.function.Function.identity(), nl.wernerdegroot.applicatives.runtime.Tuple3::withoutThird, nl.wernerdegroot.applicatives.runtime.Tuple3::getThird);
        }

    }

}