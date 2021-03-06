package nl.wernerdegroot.applicatives.runtime.decompositions;

import nl.wernerdegroot.applicatives.runtime.Function11;
import nl.wernerdegroot.applicatives.runtime.Tuple;
import nl.wernerdegroot.applicatives.runtime.Tuple11;

public interface Decomposable11<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh> {
    <T> T decomposeTo(Function11<? super First, ? super Second, ? super Third, ? super Fourth, ? super Fifth, ? super Sixth, ? super Seventh, ? super Eighth, ? super Ninth, ? super Tenth, ? super Eleventh, ? extends T> fn);

    default Tuple11<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh> decompose() {
        return decomposeTo(Tuple::of);
    }
}
