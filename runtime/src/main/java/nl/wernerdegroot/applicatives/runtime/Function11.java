package nl.wernerdegroot.applicatives.runtime;

@FunctionalInterface
public interface Function11<A, B, C, D, E, F, G, H, I, J, K, Result> {

    Result apply(A first, B second, C third, D fourth, E fifth, F sixth, G seventh, H eight, I ninth, J tenth, K eleventh);

    default Result apply(FastTuple<? extends A, ? extends B, ? extends C, ? extends D, ? extends E, ? extends F, ? extends G, ? extends H, ? extends I, ? extends J, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?> tuple, K eleventh) {
        return apply(tuple.getFirst(), tuple.getSecond(), tuple.getThird(), tuple.getFourth(), tuple.getFifth(), tuple.getSixth(), tuple.getSeventh(), tuple.getEighth(), tuple.getNinth(), tuple.getTenth(), eleventh);
    }
}
