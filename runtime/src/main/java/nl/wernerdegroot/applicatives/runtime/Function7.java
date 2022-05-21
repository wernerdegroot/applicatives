package nl.wernerdegroot.applicatives.runtime;

@FunctionalInterface
public interface Function7<A, B, C, D, E, F, G, Result> {

    Result apply(A first, B second, C third, D fourth, E fifth, F sixth, G seventh);
}
