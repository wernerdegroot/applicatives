package nl.wernerdegroot.applicatives.runtime.decompositions;

import nl.wernerdegroot.applicatives.runtime.Function10;

public interface Decomposable10<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth> {
    <T> T decomposeTo(Function10<? super First, ? super Second, ? super Third, ? super Fourth, ? super Fifth, ? super Sixth, ? super Seventh, ? super Eighth, ? super Ninth, ? super Tenth, ? extends T> fn);
}
