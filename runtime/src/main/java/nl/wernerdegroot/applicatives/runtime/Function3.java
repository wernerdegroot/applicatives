package nl.wernerdegroot.applicatives.runtime;

@FunctionalInterface
public interface Function3<A, B, C, Result> {

    Result apply(A first, B second, C third);

    default Result apply(FastTuple<? extends A, ? extends B, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?> tuple, C third) {
        return apply(tuple.getFirst(), tuple.getSecond(), third);
    }
}
