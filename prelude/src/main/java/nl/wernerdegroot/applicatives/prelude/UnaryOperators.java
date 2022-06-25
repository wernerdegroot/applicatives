package nl.wernerdegroot.applicatives.prelude;

import nl.wernerdegroot.applicatives.runtime.Invariant;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public class UnaryOperators implements UnaryOperatorsOverloads {

    private static final UnaryOperators INSTANCE = new UnaryOperators();

    public static UnaryOperators instance() {
        return INSTANCE;
    }

    @Override
    @Invariant
    public <A, B, Intermediate, C> UnaryOperator<C> combine(
            UnaryOperator<A> left,
            UnaryOperator<B> right,
            BiFunction<? super A, ? super B, ? extends C> combinator,
            Function<? super C, ? extends Intermediate> toIntermediate,
            Function<? super Intermediate, ? extends A> extractLeft,
            Function<? super Intermediate, ? extends B> extractRight) {

        return parameter -> {
            Intermediate intermediate = toIntermediate.apply(parameter);
            A fromLeft = left.apply(extractLeft.apply(intermediate));
            B fromRight = right.apply(extractRight.apply(intermediate));
            return combinator.apply(fromLeft, fromRight);
        };
    }
}
