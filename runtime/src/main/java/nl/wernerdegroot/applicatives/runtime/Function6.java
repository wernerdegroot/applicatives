package nl.wernerdegroot.applicatives.runtime;

@FunctionalInterface
public interface Function6<A, B, C, D, E, F, Result> {

    Result apply(A first, B second, C third, D fourth, E fifth, F sixth);

    default Result apply(Tuple5<? extends A, ? extends B, ? extends C, ? extends D, ? extends E> tuple, F sixth) {
        return apply(tuple.getFirst(), tuple.getSecond(), tuple.getThird(), tuple.getFourth(), tuple.getFifth(), sixth);
    }
}
