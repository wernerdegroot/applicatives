package nl.wernerdegroot.applicatives.runtime.decompositions;

import nl.wernerdegroot.applicatives.runtime.Function3;
import nl.wernerdegroot.applicatives.runtime.Tuple;
import nl.wernerdegroot.applicatives.runtime.Tuple3;

public interface Decomposable3<First, Second, Third> {
    <T> T decomposeTo(Function3<? super First, ? super Second, ? super Third, ? extends T> fn);

    default Tuple3<First, Second, Third> decompose() {
        return decomposeTo(Tuple::of);
    }
}
