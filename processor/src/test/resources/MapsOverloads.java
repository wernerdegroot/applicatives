package nl.wernerdegroot.applicatives;

public interface MapsOverloads<K extends java.lang.Comparable<? super K>> {

    <R> java.util.TreeMap<? super K, ? super R> initialize(java.util.Map<? super K, ? extends R> value);

    <P1, P2, R> java.util.TreeMap<? super K, R> combineImpl(java.util.TreeMap<? super K, ? extends P1> first, java.util.Map<? super K, ? extends P2> second, java.util.function.BiFunction<? super P1, ? super P2, ? extends R> fn);

    default <P1, P2, R> java.util.TreeMap<? super K, R> combine(java.util.Map<? super K, ? extends P1> first, java.util.Map<? super K, ? extends P2> second, java.util.function.BiFunction<? super P1, ? super P2, ? extends R> fn) {
        return this.combineImpl(this.initialize(first), second, fn);
    }

    default <P1, P2, P3, R> java.util.TreeMap<? super K, R> combine(java.util.Map<? super K, ? extends P1> first, java.util.Map<? super K, ? extends P2> second, java.util.Map<? super K, ? extends P3> third, nl.wernerdegroot.applicatives.runtime.Function3<? super P1, ? super P2, ? super P3, ? extends R> fn) {
        return this.combineImpl(nl.wernerdegroot.applicatives.MapsOverloads.Tuples.<P1, P2, K>tuple(this, first, second, 3), third, (tuple, element) -> fn.apply(tuple.getFirst(), tuple.getSecond(), element));
    }

    default <P1, P2, R> java.util.function.BiFunction<java.util.Map<? super K, ? extends P1>, java.util.Map<? super K, ? extends P2>, java.util.TreeMap<? super K, R>> lift(java.util.function.BiFunction<? super P1, ? super P2, ? extends R> fn) {
        return (first, second) ->
                this.combine(first, second, fn);
    }

    default <P1, P2, P3, R> nl.wernerdegroot.applicatives.runtime.Function3<java.util.Map<? super K, ? extends P1>, java.util.Map<? super K, ? extends P2>, java.util.Map<? super K, ? extends P3>, java.util.TreeMap<? super K, R>> lift(nl.wernerdegroot.applicatives.runtime.Function3<? super P1, ? super P2, ? super P3, ? extends R> fn) {
        return (first, second, third) ->
                this.combine(first, second, third, fn);
    }

    class Tuples {

        public static <P1, P2, K extends java.lang.Comparable<? super K>> java.util.TreeMap<? super K, nl.wernerdegroot.applicatives.runtime.Tuple2<? extends P1, ? extends P2>> tuple(nl.wernerdegroot.applicatives.MapsOverloads<K> self, java.util.Map<? super K, ? extends P1> first, java.util.Map<? super K, ? extends P2> second, int maxSize) {
            return self.combineImpl(self.initialize(first), second, nl.wernerdegroot.applicatives.runtime.FastTuple.withMaxSize(maxSize));
        }

    }

}