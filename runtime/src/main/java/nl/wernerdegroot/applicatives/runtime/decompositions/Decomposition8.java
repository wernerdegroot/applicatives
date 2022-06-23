package nl.wernerdegroot.applicatives.runtime.decompositions;

import nl.wernerdegroot.applicatives.runtime.Tuple8;

@FunctionalInterface
public interface Decomposition8<Source, First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth> {

    Tuple8<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth> decompose(Source source);
}
