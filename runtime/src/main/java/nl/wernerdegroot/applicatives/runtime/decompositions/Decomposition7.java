package nl.wernerdegroot.applicatives.runtime.decompositions;

import nl.wernerdegroot.applicatives.runtime.Tuple7;

@FunctionalInterface
public interface Decomposition7<Source, First, Second, Third, Fourth, Fifth, Sixth, Seventh> {

    Tuple7<First, Second, Third, Fourth, Fifth, Sixth, Seventh> decompose(Source source);
}
