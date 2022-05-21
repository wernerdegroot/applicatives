package nl.wernerdegroot.applicatives.runtime;

@FunctionalInterface
public interface Function6<A, B, C, D, E, F, Result> {

    Result apply(A first, B second, C third, D fourth, E fifth, F sixth);
}
