package nl.wernerdegroot.applicatives.prelude;

import nl.wernerdegroot.applicatives.runtime.Accumulator;
import nl.wernerdegroot.applicatives.runtime.Covariant;
import nl.wernerdegroot.applicatives.runtime.Finalizer;
import nl.wernerdegroot.applicatives.runtime.Initializer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiFunction;

/**
 * When combining <strong>more</strong> than two lists, it is strongly advised
 * to use {@link nl.wernerdegroot.applicatives.prelude.Streams Streams} instead
 * to avoid many intermediate lists that need to be created and then garbage
 * collected.
 */
@Covariant.Builder(className = "ListsApplicative")
public class Lists implements ListsApplicative {

    private static final Lists INSTANCE = new Lists();

    public static Lists instance() {
        return INSTANCE;
    }

    @Override
    @Initializer
    public <A> CartesianList<A> initialize(List<? extends A> value) {
        return CartesianList.of(value);
    }

    @Override
    @Accumulator
    public <A, B, C> CartesianList<C> combine(CartesianList<? extends A> left, List<? extends B> right, BiFunction<? super A, ? super B, ? extends C> fn) {
        return CartesianList.of(left, right, fn);
    }

    @Override
    @Finalizer
    public <A> List<A> finalize(CartesianList<? extends A> toFinalize) {
        ArrayList<A> finalized = new ArrayList<>(toFinalize.getSize());
        Iterator<? extends A> iterator = toFinalize.iterator();
        while (iterator.hasNext()) {
            finalized.add(iterator.next());
        }
        return finalized;
    }
}
