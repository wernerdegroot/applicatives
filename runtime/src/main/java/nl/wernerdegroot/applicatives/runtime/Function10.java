package nl.wernerdegroot.applicatives.runtime;

@FunctionalInterface
public interface Function10<A, B, C, D, E, F, G, H, I, J, Result> {

    Result apply(A first, B second, C third, D fourth, E fifth, F sixth, G seventh, H eight, I ninth, J tenth);

    default Result apply(Tuple9<? extends A, ? extends B, ? extends C, ? extends D, ? extends E, ? extends F, ? extends G, ? extends H, ? extends I> tuple, J tenth) {
        return apply(tuple.getFirst(), tuple.getSecond(), tuple.getThird(), tuple.getFourth(), tuple.getFifth(), tuple.getSixth(), tuple.getSeventh(), tuple.getEighth(), tuple.getNinth(), tenth);
    }
}
