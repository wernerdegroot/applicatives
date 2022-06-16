package nl.wernerdegroot.applicatives.runtime.decompositions;

import nl.wernerdegroot.applicatives.runtime.Function4;

public interface Decomposable4<First, Second, Third, Fourth> {
    <T> T decomposeTo(Function4<? super First, ? super Second, ? super Third, ? super Fourth, ? extends T> fn);
}
