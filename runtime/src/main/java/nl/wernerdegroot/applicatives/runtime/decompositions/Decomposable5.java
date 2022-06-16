package nl.wernerdegroot.applicatives.runtime.decompositions;

import nl.wernerdegroot.applicatives.runtime.Function5;

public interface Decomposable5<First, Second, Third, Fourth, Fifth> {
    <T> T decomposeTo(Function5<? super First, ? super Second, ? super Third, ? super Fourth, ? super Fifth, ? extends T> fn);
}
