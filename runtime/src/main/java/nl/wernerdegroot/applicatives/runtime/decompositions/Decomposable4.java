package nl.wernerdegroot.applicatives.runtime.decompositions;

import nl.wernerdegroot.applicatives.runtime.Function4;
import nl.wernerdegroot.applicatives.runtime.Tuple;
import nl.wernerdegroot.applicatives.runtime.Tuple4;

public interface Decomposable4<First, Second, Third, Fourth> {
    <T> T decomposeTo(Function4<? super First, ? super Second, ? super Third, ? super Fourth, ? extends T> fn);

    default Tuple4<First, Second, Third, Fourth> decompose() {
        return decomposeTo(Tuple::of);
    }
}
