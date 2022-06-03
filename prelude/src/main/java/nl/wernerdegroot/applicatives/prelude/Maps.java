package nl.wernerdegroot.applicatives.prelude;

import nl.wernerdegroot.applicatives.runtime.Accumulator;
import nl.wernerdegroot.applicatives.runtime.Covariant;
import nl.wernerdegroot.applicatives.runtime.Finalizer;
import nl.wernerdegroot.applicatives.runtime.Initializer;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

@Covariant.Builder(className = "MapsOverloads")
public class Maps<K> implements MapsOverloads<K> {

    private static final Maps<?> INSTANCE = new Maps<>();

    @SuppressWarnings("unchecked")
    public static <K> Maps<K> instance() {
        return (Maps<K>) INSTANCE;
    }

    @Override
    @Initializer
    public <A> MergeMap<K, A> initialize(Map<K, ? extends A> value) {
        return MergeMap.of(value);
    }

    @Override
    @Accumulator
    public <A, B, C> MergeMap<K, C> compose(MergeMap<K, ? extends A> left, Map<K, ? extends B> right, BiFunction<? super A, ? super B, ? extends C> fn) {
        return MergeMap.of(left, right, fn);
    }

    @Override
    @Finalizer
    public <A> Map<K, A> finalize(MergeMap<K, ? extends A> value) {
        Set<K> keys = value.getKeys();
        Map<K, A> result = new HashMap<>(keys.size());
        keys.forEach(key -> result.put(key, value.getValue(key)));
        return result;
    }
}
