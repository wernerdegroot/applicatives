package nl.wernerdegroot.applicatives.prelude;

import java.util.Iterator;
import java.util.List;
import java.util.function.BiFunction;

public interface ZipList<T> {

    int getSize();

    Iterator<? extends T> iterator();

    static <T> ZipList<T> of(List<? extends T> elements) {
        return new Wrapper<>(elements);
    }

    static <A, B, C> ZipList<C> of(ZipList<A> left, List<B> right, BiFunction<? super A, ? super B, ? extends C> combinator) {
        return new Composite<>(left, right, combinator);
    }

    class Wrapper<T> implements ZipList<T> {
        private final List<? extends T> wrapped;

        public Wrapper(List<? extends T> wrapped) {
            this.wrapped = wrapped;
        }

        @Override
        public int getSize() {
            return wrapped.size();
        }

        @Override
        public Iterator<? extends T> iterator() {
            return wrapped.iterator();
        }
    }

    class Composite<A, B, C> implements ZipList<C> {
        private final ZipList<? extends A> left;
        private final List<? extends B> right;
        private final BiFunction<? super A, ? super B, ? extends C> combinator;

        public Composite(ZipList<? extends A> left, List<? extends B> right, BiFunction<? super A, ? super B, ? extends C> combinator) {
            this.left = left;
            this.right = right;
            this.combinator = combinator;
        }

        @Override
        public int getSize() {
            return Math.min(left.getSize(), right.size());
        }

        @Override
        public Iterator<? extends C> iterator() {
            return new Iterator<C>() {

                private final Iterator<? extends A> leftIterator = left.iterator();
                private final Iterator<? extends B> rightIterator = right.iterator();

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
