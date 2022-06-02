package nl.wernerdegroot.applicatives.prelude;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

public interface MergeMap<K, V> {

    Set<K> getKeys();

    V getValue(K key);

    static <K, V> MergeMap<K, V> of(Map<K, ? extends V> wrapped) {
        return new Wrapper<>(wrapped);
    }

    static <K, A, B, C> MergeMap<K, C> of(MergeMap<K, ? extends A> left, Map<K, ? extends B> right, BiFunction<? super A, ? super B, ? extends C> combinator) {
        return new Composite<>(left, right, combinator);
    }

    class Wrapper<K, V> implements MergeMap<K, V> {

        private final Map<K, ? extends V> wrapped;

        public Wrapper(Map<K, ? extends V> wrapped) {
            this.wrapped = wrapped;
        }

        @Override
        public Set<K> getKeys() {
            return new HashSet<>(wrapped.keySet());
        }

        @Override
        public V getValue(K key) {
            return wrapped.get(key);
        }
    }

    class Composite<K, A, B, C> implements MergeMap<K, C> {
        private final MergeMap<K, ? extends A> left;
        private final Map<K, ? extends B> right;
        private final BiFunction<? super A, ? super B, ? extends C> combinator;

        public Composite(MergeMap<K, ? extends A> left, Map<K, ? extends B> right, BiFunction<? super A, ? super B, ? extends C> combinator) {
            this.left = left;
            this.right = right;
            this.combinator = combinator;
        }

        @Override
        public Set<K> getKeys() {
            Set<K> keys = left.getKeys();
            keys.retainAll(right.keySet());
            return keys;
        }

        @Override
        public C getValue(K key) {
            return combinator.apply(left.getValue(key), right.get(key));
        }
    }
}
