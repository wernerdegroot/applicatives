package nl.wernerdegroot.applicatives.runtime.decompositions;

import nl.wernerdegroot.applicatives.runtime.Function9;

public interface Decomposable9<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth> {
    <T> T decomposeTo(Function9<? super First, ? super Second, ? super Third, ? super Fourth, ? super Fifth, ? super Sixth, ? super Seventh, ? super Eighth, ? super Ninth, ? extends T> fn);
}
