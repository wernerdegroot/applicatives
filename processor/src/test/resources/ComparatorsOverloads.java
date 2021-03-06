package nl.wernerdegroot.applicatives;

public interface ComparatorsOverloads {

    <P1, P2, Intermediate, R> java.util.Comparator<R> combineImpl(java.util.Comparator<? super P1> first, java.util.Comparator<? super P2> second, java.util.function.Function<? super R, ? extends Intermediate> toIntermediate, java.util.function.Function<? super Intermediate, ? extends P1> extractLeft, java.util.function.Function<? super Intermediate, ? extends P2> extractRight);

    default <P1, P2, R> java.util.Comparator<R> combine(java.util.Comparator<? super P1> first, java.util.Comparator<? super P2> second, nl.wernerdegroot.applicatives.runtime.decompositions.Decomposition2<? super R, ? extends P1, ? extends P2> decomposition) {
        return this.combineImpl(first, second, decomposition::decompose, nl.wernerdegroot.applicatives.runtime.Tuple2::getFirst, nl.wernerdegroot.applicatives.runtime.Tuple2::getSecond);
    }

    default <P1, P2, R extends nl.wernerdegroot.applicatives.runtime.decompositions.Decomposable2<P1, P2>> java.util.Comparator<R> combine(java.util.Comparator<? super P1> first, java.util.Comparator<? super P2> second) {
        return this.<P1, P2, R>combine(first, second, R::decompose);
    }

    default <P1, P2, P3, R> java.util.Comparator<R> combine(java.util.Comparator<? super P1> first, java.util.Comparator<? super P2> second, java.util.Comparator<? super P3> third, nl.wernerdegroot.applicatives.runtime.decompositions.Decomposition3<? super R, ? extends P1, ? extends P2, ? extends P3> decomposition) {
        return this.combineImpl(nl.wernerdegroot.applicatives.ComparatorsOverloads.Tuples.<P1, P2>tuple(this, first, second), third, decomposition::decompose, nl.wernerdegroot.applicatives.runtime.Tuple3::withoutThird, nl.wernerdegroot.applicatives.runtime.Tuple3::getThird);
    }

    default <P1, P2, P3, R extends nl.wernerdegroot.applicatives.runtime.decompositions.Decomposable3<P1, P2, P3>> java.util.Comparator<R> combine(java.util.Comparator<? super P1> first, java.util.Comparator<? super P2> second, java.util.Comparator<? super P3> third) {
        return this.<P1, P2, P3, R>combine(first, second, third, R::decompose);
    }

    default <P1, P2, P3, P4, R> java.util.Comparator<R> combine(java.util.Comparator<? super P1> first, java.util.Comparator<? super P2> second, java.util.Comparator<? super P3> third, java.util.Comparator<? super P4> fourth, nl.wernerdegroot.applicatives.runtime.decompositions.Decomposition4<? super R, ? extends P1, ? extends P2, ? extends P3, ? extends P4> decomposition) {
        return this.combineImpl(nl.wernerdegroot.applicatives.ComparatorsOverloads.Tuples.<P1, P2, P3>tuple(this, first, second, third), fourth, decomposition::decompose, nl.wernerdegroot.applicatives.runtime.Tuple4::withoutFourth, nl.wernerdegroot.applicatives.runtime.Tuple4::getFourth);
    }

    default <P1, P2, P3, P4, R extends nl.wernerdegroot.applicatives.runtime.decompositions.Decomposable4<P1, P2, P3, P4>> java.util.Comparator<R> combine(java.util.Comparator<? super P1> first, java.util.Comparator<? super P2> second, java.util.Comparator<? super P3> third, java.util.Comparator<? super P4> fourth) {
        return this.<P1, P2, P3, P4, R>combine(first, second, third, fourth, R::decompose);
    }

    default <P1, P2, R> java.util.function.BiFunction<java.util.Comparator<? super P1>, java.util.Comparator<? super P2>, java.util.Comparator<R>> lift(nl.wernerdegroot.applicatives.runtime.decompositions.Decomposition2<? super R, ? extends P1, ? extends P2> decomposition) {
        return (first, second) ->
                this.combine(first, second, decomposition);
    }

    default <P1, P2, P3, R> nl.wernerdegroot.applicatives.runtime.Function3<java.util.Comparator<? super P1>, java.util.Comparator<? super P2>, java.util.Comparator<? super P3>, java.util.Comparator<R>> lift(nl.wernerdegroot.applicatives.runtime.decompositions.Decomposition3<? super R, ? extends P1, ? extends P2, ? extends P3> decomposition) {
        return (first, second, third) ->
                this.combine(first, second, third, decomposition);
    }

    default <P1, P2, P3, P4, R> nl.wernerdegroot.applicatives.runtime.Function4<java.util.Comparator<? super P1>, java.util.Comparator<? super P2>, java.util.Comparator<? super P3>, java.util.Comparator<? super P4>, java.util.Comparator<R>> lift(nl.wernerdegroot.applicatives.runtime.decompositions.Decomposition4<? super R, ? extends P1, ? extends P2, ? extends P3, ? extends P4> decomposition) {
        return (first, second, third, fourth) ->
                this.combine(first, second, third, fourth, decomposition);
    }

    class Tuples {

        public static <P1, P2> java.util.Comparator<nl.wernerdegroot.applicatives.runtime.Tuple2<? extends P1, ? extends P2>> tuple(nl.wernerdegroot.applicatives.ComparatorsOverloads self, java.util.Comparator<? super P1> first, java.util.Comparator<? super P2> second) {
            return self.combineImpl(first, second, java.util.function.Function.identity(), nl.wernerdegroot.applicatives.runtime.Tuple2::getFirst, nl.wernerdegroot.applicatives.runtime.Tuple2::getSecond);
        }

        public static <P1, P2, P3> java.util.Comparator<nl.wernerdegroot.applicatives.runtime.Tuple3<? extends P1, ? extends P2, ? extends P3>> tuple(nl.wernerdegroot.applicatives.ComparatorsOverloads self, java.util.Comparator<? super P1> first, java.util.Comparator<? super P2> second, java.util.Comparator<? super P3> third) {
            return self.combineImpl(nl.wernerdegroot.applicatives.ComparatorsOverloads.Tuples.<P1, P2>tuple(self, first, second), third, java.util.function.Function.identity(), nl.wernerdegroot.applicatives.runtime.Tuple3::withoutThird, nl.wernerdegroot.applicatives.runtime.Tuple3::getThird);
        }

    }

}