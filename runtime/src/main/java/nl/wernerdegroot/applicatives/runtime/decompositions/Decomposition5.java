package nl.wernerdegroot.applicatives.runtime.decompositions;

import nl.wernerdegroot.applicatives.runtime.Tuple5;

@FunctionalInterface
public interface Decomposition5<Source, First, Second, Third, Fourth, Fifth> {

    Tuple5<First, Second, Third, Fourth, Fifth> decompose(Source source);
}
