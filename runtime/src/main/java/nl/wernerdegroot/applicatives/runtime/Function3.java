package nl.wernerdegroot.applicatives.runtime;

@FunctionalInterface
public interface Function3<A, B, C, Result> {

    Result apply(A first, B second, C third);
}
