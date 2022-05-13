package nl.wernerdegroot.applicatives.runtime;

@FunctionalInterface
public interface Function26<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W, X, Y, Z, Result> {

    Result apply(A first, B second, C third, D fourth, E fifth, F sixth, G seventh, H eight, I ninth, J tenth, K eleventh, L twelfth, M thirteenth, N fourteenth, O fifteenth, P sixteenth, Q seventeenth, R eighteenth, S nineteenth, T twentieth, U twentyFirst, V twentySecond, W twentyThird, X twentyFourth, Y twentyFifth, Z twentySixth);

    default Result apply(Tuple25<? extends A, ? extends B, ? extends C, ? extends D, ? extends E, ? extends F, ? extends G, ? extends H, ? extends I, ? extends J, ? extends K, ? extends L, ? extends M, ? extends N, ? extends O, ? extends P, ? extends Q, ? extends R, ? extends S, ? extends T, ? extends U, ? extends V, ? extends W, ? extends X, ? extends Y> tuple, Z twentySixth) {
        return apply(tuple.getFirst(), tuple.getSecond(), tuple.getThird(), tuple.getFourth(), tuple.getFifth(), tuple.getSixth(), tuple.getSeventh(), tuple.getEighth(), tuple.getNinth(), tuple.getTenth(), tuple.getEleventh(), tuple.getTwelfth(), tuple.getThirteenth(), tuple.getFourteenth(), tuple.getFifteenth(), tuple.getSixteenth(), tuple.getSeventeenth(), tuple.getEighteenth(), tuple.getNineteenth(), tuple.getTwentieth(), tuple.getTwentyFirst(), tuple.getTwentySecond(), tuple.getTwentyThird(), tuple.getTwentyFourth(), tuple.getTwentyFifth(), twentySixth);
    }
}
