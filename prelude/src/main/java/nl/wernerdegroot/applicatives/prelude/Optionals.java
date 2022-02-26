package nl.wernerdegroot.applicatives.prelude;

import nl.wernerdegroot.applicatives.runtime.Covariant;

import java.util.Optional;
import java.util.function.BiFunction;

public class Optionals implements OptionalsApplicative {

    @Override
    @Covariant(className = "OptionalsApplicative")
    public <A, B, C> Optional<C> combine(Optional<? extends A> left, Optional<? extends B> right, BiFunction<? super A, ? super B, ? extends C> fn) {
        if (left.isPresent() && right.isPresent()) {
            return Optional.of(fn.apply(left.get(), right.get()));
        } else {
            return Optional.empty();
        }
    }
}
