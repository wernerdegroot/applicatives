package nl.wernerdegroot.applicatives.runtime.decompositions;

import nl.wernerdegroot.applicatives.runtime.Tuple3;

@FunctionalInterface
public interface Decomposition3<Source, First, Second, Third> {

    Tuple3<First, Second, Third> decompose(Source source);
}
