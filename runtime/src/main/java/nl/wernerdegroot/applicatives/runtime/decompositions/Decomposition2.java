package nl.wernerdegroot.applicatives.runtime.decompositions;

import nl.wernerdegroot.applicatives.runtime.Tuple2;

@FunctionalInterface
public interface Decomposition2<Source, First, Second> {

    Tuple2<First, Second> decompose(Source source);
}
