package nl.wernerdegroot.applicatives.runtime.decompositions;

import nl.wernerdegroot.applicatives.runtime.Function8;

public interface Decomposable8<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth> {
    <T> T decomposeTo(Function8<? super First, ? super Second, ? super Third, ? super Fourth, ? super Fifth, ? super Sixth, ? super Seventh, ? super Eighth, ? extends T> fn);
}
