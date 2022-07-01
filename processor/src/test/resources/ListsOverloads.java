package nl.wernerdegroot.applicatives;

public interface ListsOverloads {

    <P1, P2, R> java.util.ArrayList<R> combineImpl(java.util.ArrayList<? extends P1> first, java.util.List<? extends P2> second, java.util.function.BiFunction<? super P1, ? super P2, ? extends R> fn);

    <R> java.util.List<R> finalize(java.util.ArrayList<? extends R> value);

    default <P1, P2, R> java.util.List<R> combine(java.util.ArrayList<? extends P1> first, java.util.List<? extends P2> second, java.util.function.BiFunction<? super P1, ? super P2, ? extends R> fn) {
        return this.finalize(this.combineImpl(first, second, fn));
    }

    default <P1, P2, P3, R> java.util.List<R> combine(java.util.ArrayList<? extends P1> first, java.util.List<? extends P2> second, java.util.List<? extends P3> third, nl.wernerdegroot.applicatives.runtime.Function3<? super P1, ? super P2, ? super P3, ? extends R> fn) {
        return this.finalize(this.combineImpl(nl.wernerdegroot.applicatives.ListsOverloads.Tuples.<P1, P2>tuple(this, first, second, 3), third, (tuple, element) -> fn.apply(tuple.getFirst(), tuple.getSecond(), element)));
    }

    default <P1, P2, R> java.util.function.BiFunction<java.util.ArrayList<? extends P1>, java.util.List<? extends P2>, java.util.List<R>> lift(java.util.function.BiFunction<? super P1, ? super P2, ? extends R> fn) {
        return (first, second) ->
                this.combine(first, second, fn);
    }

    default <P1, P2, P3, R> nl.wernerdegroot.applicatives.runtime.Function3<java.util.ArrayList<? extends P1>, java.util.List<? extends P2>, java.util.List<? extends P3>, java.util.List<R>> lift(nl.wernerdegroot.applicatives.runtime.Function3<? super P1, ? super P2, ? super P3, ? extends R> fn) {
        return (first, second, third) ->
                this.combine(first, second, third, fn);
    }

    class Tuples {

        public static <P1, P2> java.util.ArrayList<nl.wernerdegroot.applicatives.runtime.Tuple2<? extends P1, ? extends P2>> tuple(nl.wernerdegroot.applicatives.ListsOverloads self, java.util.ArrayList<? extends P1> first, java.util.List<? extends P2> second, int maxSize) {
            return self.combineImpl(first, second, nl.wernerdegroot.applicatives.runtime.FastTuple.withMaxSize(maxSize));
        }

    }

}