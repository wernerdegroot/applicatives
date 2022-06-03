package nl.wernerdegroot.applicatives.prelude;

import nl.wernerdegroot.applicatives.runtime.Accumulator;
import nl.wernerdegroot.applicatives.runtime.Covariant;
import nl.wernerdegroot.applicatives.runtime.Finalizer;
import nl.wernerdegroot.applicatives.runtime.Initializer;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Covariant.Builder(className = "SetsOverloads")
public class Sets implements SetsOverloads {

    private static final Sets INSTANCE = new Sets();

    public static Sets instance() {
        return INSTANCE;
    }

    @Override
    @Initializer
    public <A> CartesianIterable<A> initialize(Set<? extends A> value) {
        return CartesianIterable.of(value);
    }

    @Override
    @Accumulator
    public <A, B, C> CartesianIterable<C> combine(CartesianIterable<? extends A> left, Set<? extends B> right, BiFunction<? super A, ? super B, ? extends C> fn) {
        return CartesianIterable.of(left, right, fn);
    }

    @Override
    @Finalizer
    public <A> Set<A> finalize(CartesianIterable<? extends A> toFinalize) {
        Set<A> finalized = new HashSet<>(toFinalize.getSize());
        Iterator<? extends A> iterator = toFinalize.iterator();
        while (iterator.hasNext()) {
            finalized.add(iterator.next());
        }
        return finalized;
    }

    public <T, A, R> Collector<? super Set<T>, ?, Set<R>> collector(Collector<T, A, R> collector) {
        return Collectors.collectingAndThen(CartesianCollectable.collector(collector), HashSet::new);
    }
}
