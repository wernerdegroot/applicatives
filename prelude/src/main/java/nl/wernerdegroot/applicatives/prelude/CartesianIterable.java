package nl.wernerdegroot.applicatives.prelude;

import java.util.*;
import java.util.function.BiFunction;

public interface CartesianIterable<T> {

    int getSize();

    Iterator<? extends T> iterator();

    static <T> CartesianIterable<T> of(Collection<? extends T> elements) {
        return new Wrapper<>(elements);
    }

    static <A, B, C> CartesianIterable<C> of(CartesianIterable<? extends A> left, Collection<? extends B> right, BiFunction<? super A, ? super B, ? extends C> combinator) {
        return new Composite<>(left, right, combinator);
    }

    class Wrapper<T> implements CartesianIterable<T> {
        private final Collection<? extends T> wrapped;

        public Wrapper(Collection<? extends T> wrapped) {
            this.wrapped = wrapped;
        }

        public Iterator<? extends T> iterator() {
            return wrapped.iterator();
        }

        @Override
        public int getSize() {
            return wrapped.size();
        }
    }

    class Composite<A, B, C> implements CartesianIterable<C> {
        private final CartesianIterable<? extends A> left;
        private final Collection<? extends B> right;
        private final BiFunction<? super A, ? super B, ? extends C> combinator;

        public Composite(CartesianIterable<? extends A> left, Collection<? extends B> right, BiFunction<? super A, ? super B, ? extends C> combinator) {
            this.left = left;
            this.right = right;
            this.combinator = combinator;
        }

        @Override
        public Iterator<? extends C> iterator() {
            if (left.getSize() > 0 && right.size() > 0) {

                return new Iterator<C>() {

                    private boolean initialized = false;
                    private A leftValue = null;
                    private Iterator<? extends A> leftIterator = left.iterator();
                    private Iterator<? extends B> rightIterator = null;

                    @Override
                    public boolean hasNext() {
                        return leftIterator.hasNext() || rightIterator.hasNext();
                    }

                    @Override
                    public C next() {
                        if (!initialized || !rightIterator.hasNext()) {
                            leftValue = leftIterator.next();
                            rightIterator = right.iterator();
                            initialized = true;
                        }
                        B rightValue = rightIterator.next();
                        return combinator.apply(leftValue, rightValue);
                    }
                };
            } else {
                return new Iterator<C>() {
                    @Override
                    public boolean hasNext() {
                        return false;
                    }

                    @Override
                    public C next() {
                        throw new NoSuchElementException();
                    }
                };
            }
        }

        @Override
        public int getSize() {
            return left.getSize() * right.size();
        }
    }
}
