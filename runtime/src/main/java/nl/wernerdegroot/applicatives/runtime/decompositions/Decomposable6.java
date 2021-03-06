package nl.wernerdegroot.applicatives.runtime.decompositions;

import nl.wernerdegroot.applicatives.runtime.Function6;
import nl.wernerdegroot.applicatives.runtime.Tuple;
import nl.wernerdegroot.applicatives.runtime.Tuple6;

public interface Decomposable6<First, Second, Third, Fourth, Fifth, Sixth> {
    <T> T decomposeTo(Function6<? super First, ? super Second, ? super Third, ? super Fourth, ? super Fifth, ? super Sixth, ? extends T> fn);

    default Tuple6<First, Second, Third, Fourth, Fifth, Sixth> decompose() {
        return decomposeTo(Tuple::of);
    }
}
