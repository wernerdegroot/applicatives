package nl.wernerdegroot.applicatives.runtime.decompositions;

import nl.wernerdegroot.applicatives.runtime.Tuple9;

@FunctionalInterface
public interface Decomposition9<Source, First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth> {

    Tuple9<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth> decompose(Source source);
}
