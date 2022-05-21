package nl.wernerdegroot.applicatives.runtime;

@FunctionalInterface
public interface Function11<A, B, C, D, E, F, G, H, I, J, K, Result> {

    Result apply(A first, B second, C third, D fourth, E fifth, F sixth, G seventh, H eight, I ninth, J tenth, K eleventh);
}
