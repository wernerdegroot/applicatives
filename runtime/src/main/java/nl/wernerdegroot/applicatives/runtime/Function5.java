package nl.wernerdegroot.applicatives.runtime;

@FunctionalInterface
public interface Function5<A, B, C, D, E, Result> {

    Result apply(A first, B second, C third, D fourth, E fifth);
}
