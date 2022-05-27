package nl.wernerdegroot.applicatives.prelude;

import java.util.Iterator;
import java.util.List;
import java.util.function.BiFunction;

public interface ZipList<T> extends Iterable<T> {

    int getSize();

    static <T> ZipList<T> singleton(T value) {
        return new Singleton<>(value);
    }

    static <T> ZipList<T> of(List<T> elements) {
        return new Wrapper<>(elements);
    }

    static <A, B, C> ZipList<C> of(ZipList<A> left, List<B> right, BiFunction<? super A, ? super B, ? extends C> combinator) {
        return new Composite<>(left, right, combinator);
    }


    class Singleton<T> implements ZipList<T> {
        private final T value;

        public Singleton(T value) {
            this.value = value;
        }

        @Override
        public int getSize() {
            return Integer.MAX_VALUE;
        }

        @Override
        public Iterator<T> iterator() {
            return new Iterator<T>() {

                @Override
                public boolean hasNext() {
                    return true;
                }

                @Override
                public T next() {
                    return value;
                }
            };
        }
    }

    class Wrapper<T> implements ZipList<T> {
        private final List<T> wrapped;

        public Wrapper(List<T> wrapped) {
            this.wrapped = wrapped;
        }

        @Override
        public int getSize() {
            return wrapped.size();
        }

        @Override
        public Iterator<T> iterator() {
            return wrapped.iterator();
        }
    }

    class Composite<A, B, C> implements ZipList<C> {
        private final ZipList<A> left;
        private final List<B> right;
        private final BiFunction<? super A, ? super B, ? extends C> combinator;

        public Composite(ZipList<A> left, List<B> right, BiFunction<? super A, ? super B, ? extends C> combinator) {
            this.left = left;
            this.right = right;
            this.combinator = combinator;
        }

        @Override
        public int getSize() {
            return Math.min(left.getSize(), right.size());
        }

        @Override
        public Iterator<C> iterator() {
            return new Iterator<C>() {

                private final Iterator<A> leftIterator = left.iterator();
                private final Iterator<B> rightIterator = right.iterator();

                @Override
                public boolean hasNext() {
                    return leftIterator.hasNext() && rightIterator.hasNext();
                }

                @Override
                public C next() {
                    return combinator.apply(leftIterator.next(), rightIterator.next());
                }
            };
        }
    }
}
