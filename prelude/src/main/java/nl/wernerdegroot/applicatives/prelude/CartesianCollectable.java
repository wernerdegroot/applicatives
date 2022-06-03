package nl.wernerdegroot.applicatives.prelude;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collector;

import static java.util.Collections.singletonList;

public interface CartesianCollectable<T> {

    static <T, A, R> Collector<? super Collection<? extends T>, ?, List<R>> collector(Collector<T, A, R> collector) {
        class Box {
            CartesianCollectable<T> value;

            public Box() {
                value = null;
            }

            public Box(CartesianCollectable<T> value) {
                this.value = value;
            }

            public void add(Collection<? extends T> collection) {
                if (value == null) {
                    value = new Wrapper<>(collection);
                } else {
                    value = new Composite<>(value, collection);
                }
            }

            public Box combine(Box that) {
                this.value = this.value.combine(that.value);
                return this;
            }

            public List<R> collect() {
                return value.collect(collector);
            }
        }

        return Collector.of(Box::new, Box::add, Box::combine, Box::collect);
    }

    int getSize();

    <A, R> void add(List<A> acc, int times, Collector<T, A, R> collector);

    <A, R> List<R> collect(Collector<T, A, R> collector);

    default CartesianCollectable<T> add(Collection<? extends T> that) {
        return new Composite<>(this, that);
    }

    default CartesianCollectable<T> combine(CartesianCollectable<T> that) {
        return that.combineInverse(this);
    }

    CartesianCollectable<T> combineInverse(CartesianCollectable<T> that);

    class Wrapper<T> implements CartesianCollectable<T> {

        private final Collection<? extends T> wrapped;

        public Wrapper(Collection<? extends T> wrapped) {
            this.wrapped = wrapped;
        }

        @Override
        public int getSize() {
            return wrapped.size();
        }

        @Override
        public <A, R> void add(List<A> acc, int times, Collector<T, A, R> collector) {
            int index = 0;
            for (T fromWrapped : wrapped) {
                for (int i = 0; i < times; ++i) {
                    collector.accumulator().accept(acc.get(index++), fromWrapped);
                }
            }
        }

        @Override
        public <A, R> List<R> collect(Collector<T, A, R> collector) {
            return singletonList(wrapped.stream().collect(collector));
        }

        @Override
        public CartesianCollectable<T> combineInverse(CartesianCollectable<T> that) {
            return that.add(wrapped);
        }
    }

    class Composite<T> implements CartesianCollectable<T> {
        private final CartesianCollectable<T> left;
        private final Collection<? extends T> right;

        public Composite(CartesianCollectable<T> left, Collection<? extends T> right) {
            this.left = left;
            this.right = right;
        }

        @Override
        public int getSize() {
            return left.getSize() * right.size();
        }

        @Override
        public <A, R> void add(List<A> acc, int times, Collector<T, A, R> collector) {
            int leftSize = left.getSize();
            int rightSize = right.size();
            left.add(acc, times * rightSize, collector);
            int index = 0;
            for (int i = 0; i < leftSize; ++i) {
                for (T fromRight : right) {
                    for (int j = 0; j < times; ++j) {
                        collector.accumulator().accept(acc.get(index++), fromRight);
                    }
                }
            }
        }

        @Override
        public <A, R> List<R> collect(Collector<T, A, R> collector) {
            int size = getSize();

            // Initialize:
            List<A> acc = new ArrayList<>(size);
            for (int i = 0; i < size; ++i) {
                acc.add(collector.supplier().get());
            }

            // Accumulate:
            add(acc, 1, collector);

            // Finish:
            List<R> result = new ArrayList<>(size);
            for (int i = 0; i < size; ++i) {
                result.add(collector.finisher().apply(acc.get(i)));
            }

            return result;
        }

        @Override
        public CartesianCollectable<T> combineInverse(CartesianCollectable<T> that) {
            return that.combine(left).add(right);
        }
    }
}
