package nl.wernerdegroot.applicatives.prelude;

import nl.wernerdegroot.applicatives.runtime.Accumulator;
import nl.wernerdegroot.applicatives.runtime.Covariant;
import nl.wernerdegroot.applicatives.runtime.Finalizer;
import nl.wernerdegroot.applicatives.runtime.Initializer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collector;

@Covariant.Builder
public class Lists implements ListsOverloads {

    private static final Lists INSTANCE = new Lists();

    public static Lists instance() {
        return INSTANCE;
    }

    @Override
    @Initializer
    public <A> CartesianIterable<A> initialize(List<? extends A> value) {
        return CartesianIterable.of(value);
    }

    @Override
    @Accumulator
    public <A, B, C> CartesianIterable<C> combine(CartesianIterable<? extends A> left, List<? extends B> right, BiFunction<? super A, ? super B, ? extends C> fn) {
        return CartesianIterable.of(left, right, fn);
    }

    @Override
    @Finalizer
    public <A> List<A> finalize(CartesianIterable<? extends A> toFinalize) {
        ArrayList<A> finalized = new ArrayList<>(toFinalize.getSize());
        Iterator<? extends A> iterator = toFinalize.iterator();
        while (iterator.hasNext()) {
            finalized.add(iterator.next());
        }
        return finalized;
    }

    public <T, A, R> Collector<? super List<T>, ?, List<R>> collector(Collector<T, A, R> collector) {
        return CartesianCollectable.collector(collector);
    }
}
