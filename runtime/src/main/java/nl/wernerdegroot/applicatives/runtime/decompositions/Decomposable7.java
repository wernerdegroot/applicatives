package nl.wernerdegroot.applicatives.runtime.decompositions;

import nl.wernerdegroot.applicatives.runtime.Function7;
import nl.wernerdegroot.applicatives.runtime.Tuple;
import nl.wernerdegroot.applicatives.runtime.Tuple7;

public interface Decomposable7<First, Second, Third, Fourth, Fifth, Sixth, Seventh> {
    <T> T decomposeTo(Function7<? super First, ? super Second, ? super Third, ? super Fourth, ? super Fifth, ? super Sixth, ? super Seventh, ? extends T> fn);

    default Tuple7<First, Second, Third, Fourth, Fifth, Sixth, Seventh> decompose() {
        return decomposeTo(Tuple::of);
    }
}
