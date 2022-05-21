package nl.wernerdegroot.applicatives.runtime;

@FunctionalInterface
public interface Function4<A, B, C, D, Result> {

    Result apply(A first, B second, C third, D fourth);
}
