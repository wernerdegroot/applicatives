package nl.wernerdegroot.applicatives.runtime;

@FunctionalInterface
public interface Function5<A, B, C, D, E, Result> {

    Result apply(A first, B second, C third, D fourth, E fifth);

    default Result apply(Tuple4<? extends A, ? extends B, ? extends C, ? extends D> tuple, E fifth) {
        return apply(tuple.getFirst(), tuple.getSecond(), tuple.getThird(), tuple.getFourth(), fifth);
    }
}
