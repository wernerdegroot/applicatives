package nl.wernerdegroot.applicatives.runtime;

@FunctionalInterface
public interface Function8<A, B, C, D, E, F, G, H, Result> {

    Result apply(A first, B second, C third, D fourth, E fifth, F sixth, G seventh, H eight);
}
