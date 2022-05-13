package nl.wernerdegroot.applicatives.runtime;

@FunctionalInterface
public interface Function4<A, B, C, D, Result> {

    Result apply(A first, B second, C third, D fourth);

    default Result apply(Tuple3<? extends A, ? extends B, ? extends C> tuple, D fourth) {
        return apply(tuple.getFirst(), tuple.getSecond(), tuple.getThird(), fourth);
    }
}
