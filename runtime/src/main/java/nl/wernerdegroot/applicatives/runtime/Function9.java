package nl.wernerdegroot.applicatives.runtime;

@FunctionalInterface
public interface Function9<A, B, C, D, E, F, G, H, I, Result> {

    Result apply(A first, B second, C third, D fourth, E fifth, F sixth, G seventh, H eight, I ninth);

    default Result apply(Tuple8<? extends A, ? extends B, ? extends C, ? extends D, ? extends E, ? extends F, ? extends G, ? extends H> tuple, I ninth) {
        return apply(tuple.getFirst(), tuple.getSecond(), tuple.getThird(), tuple.getFourth(), tuple.getFifth(), tuple.getSixth(), tuple.getSeventh(), tuple.getEighth(), ninth);
    }
}
