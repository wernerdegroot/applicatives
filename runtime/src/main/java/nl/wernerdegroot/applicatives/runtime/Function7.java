package nl.wernerdegroot.applicatives.runtime;

@FunctionalInterface
public interface Function7<A, B, C, D, E, F, G, Result> {

    Result apply(A first, B second, C third, D fourth, E fifth, F sixth, G seventh);

    default Result apply(FastTuple<? extends A, ? extends B, ? extends C, ? extends D, ? extends E, ? extends F, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?> tuple, G seventh) {
        return apply(tuple.getFirst(), tuple.getSecond(), tuple.getThird(), tuple.getFourth(), tuple.getFifth(), tuple.getSixth(), seventh);
    }
}
