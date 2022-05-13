package nl.wernerdegroot.applicatives.runtime;

@FunctionalInterface
public interface Function8<A, B, C, D, E, F, G, H, Result> {

    Result apply(A first, B second, C third, D fourth, E fifth, F sixth, G seventh, H eight);

    default Result apply(Tuple7<? extends A, ? extends B, ? extends C, ? extends D, ? extends E, ? extends F, ? extends G> tuple, H eigth) {
        return apply(tuple.getFirst(), tuple.getSecond(), tuple.getThird(), tuple.getFourth(), tuple.getFifth(), tuple.getSixth(), tuple.getSeventh(), eigth);
    }
}
