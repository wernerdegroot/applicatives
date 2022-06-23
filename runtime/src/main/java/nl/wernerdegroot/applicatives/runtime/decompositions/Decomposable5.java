package nl.wernerdegroot.applicatives.runtime.decompositions;

import nl.wernerdegroot.applicatives.runtime.Function5;
import nl.wernerdegroot.applicatives.runtime.Tuple;
import nl.wernerdegroot.applicatives.runtime.Tuple5;

public interface Decomposable5<First, Second, Third, Fourth, Fifth> {
    <T> T decomposeTo(Function5<? super First, ? super Second, ? super Third, ? super Fourth, ? super Fifth, ? extends T> fn);

    default Tuple5<First, Second, Third, Fourth, Fifth> decompose() {
        return decomposeTo(Tuple::of);
    }
}
