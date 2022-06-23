package nl.wernerdegroot.applicatives.runtime.decompositions;

import nl.wernerdegroot.applicatives.runtime.Tuple6;

@FunctionalInterface
public interface Decomposition6<Source, First, Second, Third, Fourth, Fifth, Sixth> {

    Tuple6<First, Second, Third, Fourth, Fifth, Sixth> decompose(Source source);
}
