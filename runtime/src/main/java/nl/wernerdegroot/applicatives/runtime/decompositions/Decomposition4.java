package nl.wernerdegroot.applicatives.runtime.decompositions;

import nl.wernerdegroot.applicatives.runtime.Tuple4;

@FunctionalInterface
public interface Decomposition4<Source, First, Second, Third, Fourth> {

    Tuple4<First, Second, Third, Fourth> decompose(Source source);
}
