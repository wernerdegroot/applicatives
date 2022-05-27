package nl.wernerdegroot.applicatives.prelude;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.BiFunction;

public interface CartesianList<T> extends Iterable<T> {

    int getSize();

    static <T> CartesianList<T> singleton(T value) {
        return new Singleton<>(value);
    }

    static <T> CartesianList<T> of(List<T> elements) {
        return new Wrapper<>(elements);
    }

    static <A, B, C> CartesianList<C> of(CartesianList<A> left, List<B> right, BiFunction<? super A, ? super B, ? extends C> combinator) {
        return new Composite<>(left, right, combinator);
    }

    class Singleton<T> implements CartesianList<T> {

        private final T value;

        public Singleton(T value) {
            this.value = value;
        }

        @Override
        public Iterator<T> iterator() {
            return new Iterator<T>() {

                private boolean hasNext = true;

                @Override
                public boolean hasNext() {
                    return hasNext;
                }

                @Override
                public T next() {
                    hasNext = false;
                    return value;
                }
            };
        }

        @Override
        public int getSize() {
            return 1;
        }
    }

    class Wrapper<T> implements CartesianList<T> {
        private final List<T> wrapped;

        public Wrapper(List<T> wrapped) {
            this.wrapped = wrapped;
        }

        @Override
        public Iterator<T> iterator() {
            return wrapped.iterator();
        }

        @Override
        public int getSize() {
            return wrapped.size();
        }
    }

    class Composite<A, B, C> implements CartesianList<C> {
        private final CartesianList<A> left;
        private final List<B> right;
        private final BiFunction<? super A, ? super B, ? extends C> combinator;

        public Composite(CartesianList<A> left, List<B> right, BiFunction<? super A, ? super B, ? extends C> combinator) {
            this.left = left;
            this.right = right;
            this.combinator = combinator;
        }

        @Override
        public Iterator<C> iterator() {
            if (left.getSize() > 0 && right.size() > 0) {

                return new Iterator<C>() {

                    private boolean initialized = false;
                    private A leftValue = null;
                    private Iterator<A> leftIterator = left.iterator();
                    private Iterator<B> rightIterator = null;

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
