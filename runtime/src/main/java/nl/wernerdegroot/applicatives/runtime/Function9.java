package nl.wernerdegroot.applicatives.runtime;

@FunctionalInterface
public interface Function9<A, B, C, D, E, F, G, H, I, Result> {

    Result apply(A first, B second, C third, D fourth, E fifth, F sixth, G seventh, H eight, I ninth);
}
