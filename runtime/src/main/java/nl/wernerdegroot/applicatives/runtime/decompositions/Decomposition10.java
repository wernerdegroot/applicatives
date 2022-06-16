package nl.wernerdegroot.applicatives.runtime.decompositions;

import nl.wernerdegroot.applicatives.runtime.Tuple10;

@FunctionalInterface
public interface Decomposition10<Source, First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth> {

    Tuple10<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth> decompose(Source source);
}
