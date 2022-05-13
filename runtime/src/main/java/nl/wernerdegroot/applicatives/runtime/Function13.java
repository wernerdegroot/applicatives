package nl.wernerdegroot.applicatives.runtime;

@FunctionalInterface
public interface Function13<A, B, C, D, E, F, G, H, I, J, K, L, M, Result> {

    Result apply(A first, B second, C third, D fourth, E fifth, F sixth, G seventh, H eight, I ninth, J tenth, K eleventh, L twelfth, M thirteenth);

    default Result apply(Tuple12<? extends A, ? extends B, ? extends C, ? extends D, ? extends E, ? extends F, ? extends G, ? extends H, ? extends I, ? extends J, ? extends K, ? extends L> tuple, M thirteenth) {
        return apply(tuple.getFirst(), tuple.getSecond(), tuple.getThird(), tuple.getFourth(), tuple.getFifth(), tuple.getSixth(), tuple.getSeventh(), tuple.getEighth(), tuple.getNinth(), tuple.getTenth(), tuple.getEleventh(), tuple.getTwelfth(), thirteenth);
    }
}
