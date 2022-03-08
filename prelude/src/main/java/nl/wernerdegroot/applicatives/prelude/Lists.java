package nl.wernerdegroot.applicatives.prelude;

import nl.wernerdegroot.applicatives.runtime.Covariant;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

/**
 * When combining <strong>more</strong> than two lists, it is strongly advised
 * to use {@link nl.wernerdegroot.applicatives.prelude.Streams Streams} instead
 * to avoid many intermediate lists that need to be created and then garbage
 * collected.
 */
public class Lists implements ListsApplicative {

    // The fact that we are returning an `ArrayList` (implementation detail)
    // is a temporary situation while we allow the left type constructor and
    // the right type constructor to diverge (work in progress).
    @Override
    @Covariant(className = "ListsApplicative")
    public <A, B, C> ArrayList<C> combine(ArrayList<? extends A> left, List<? extends B> right, BiFunction<? super A, ? super B, ? extends C> fn) {
        ArrayList<C> result = new ArrayList<>(left.size() * right.size());
        for (A elementFromLeft : left) {
            for (B elementFromRight : right) {
                result.add(fn.apply(elementFromLeft, elementFromRight));
            }
        }
        return result;
    }
}
