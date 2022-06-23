package nl.wernerdegroot.applicatives.prelude;

import nl.wernerdegroot.applicatives.runtime.Accumulator;
import nl.wernerdegroot.applicatives.runtime.Contravariant;
import nl.wernerdegroot.applicatives.runtime.Finalizer;
import nl.wernerdegroot.applicatives.runtime.Initializer;

import java.util.function.Function;

// Added to the test package to make sure `@Covariant.Builder` compiles neatly.
@Contravariant.Builder(combineMethodName = "combine")
public class FunctionsReturningString implements FunctionsReturningStringOverloads {

    @Initializer
    public <A> Function<A, StringBuilder> initialize(Function<? super A, ? extends String> value) {
        return parameter -> new StringBuilder(value.apply(parameter));
    }

    @Accumulator
    public <A, B, Intermediate, C> Function<C, StringBuilder> combineImpl(Function<? super A, ? extends StringBuilder> left, Function<? super B, ? extends String> right, Function<? super C, ? extends Intermediate> toIntermediate, Function<? super Intermediate, ? extends A> extractLeft, Function<? super Intermediate, ? extends B> extractRight) {
        return parameter -> {
            Intermediate intermediate = toIntermediate.apply(parameter);
            StringBuilder fromLeft = left.apply(extractLeft.apply(intermediate));
            String fromRight = right.apply(extractRight.apply(intermediate));
            return fromLeft.append(fromRight);
        };
    }

    @Finalizer
    public <A> Function<A, String> finalize(Function<? super A, ? extends StringBuilder> value) {
        return parameter -> value.apply(parameter).toString();
    }
}
