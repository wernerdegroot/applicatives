package nl.wernerdegroot.applicatives.runtime;

@FunctionalInterface
public interface Function15<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Result> {

    Result apply(A first, B second, C third, D fourth, E fifth, F sixth, G seventh, H eight, I ninth, J tenth, K eleventh, L twelfth, M thirteenth, N fourteenth, O fifteenth);

    default Result apply(FastTuple<? extends A, ? extends B, ? extends C, ? extends D, ? extends E, ? extends F, ? extends G, ? extends H, ? extends I, ? extends J, ? extends K, ? extends L, ? extends M, ? extends N, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?> tuple, O fifteenth) {
        return apply(tuple.getFirst(), tuple.getSecond(), tuple.getThird(), tuple.getFourth(), tuple.getFifth(), tuple.getSixth(), tuple.getSeventh(), tuple.getEighth(), tuple.getNinth(), tuple.getTenth(), tuple.getEleventh(), tuple.getTwelfth(), tuple.getThirteenth(), tuple.getFourteenth(), fifteenth);
    }
}
