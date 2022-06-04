package nl.wernerdegroot.applicatives.prelude;

import nl.wernerdegroot.applicatives.runtime.Covariant;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collector;

public class Optionals implements OptionalsOverloads {

    private static final Optionals INSTANCE = new Optionals();

    public static Optionals instance() {
        return INSTANCE;
    }

    @Override
    @Covariant(className = "OptionalsOverloads")
    public <A, B, C> Optional<C> combine(Optional<? extends A> left, Optional<? extends B> right, BiFunction<? super A, ? super B, ? extends C> fn) {
        if (left.isPresent() && right.isPresent()) {
            return Optional.of(fn.apply(left.get(), right.get()));
        } else {
            return Optional.empty();
        }
    }

    public <T, A, R> Collector<Optional<? extends T>, ?, Optional<R>> collector(Collector<T, A, R> collector) {
        class Box {
            Optional<A> accumulatedOpt;

            public Box() {
                this.accumulatedOpt = Optional.of(collector.supplier().get());
            }

            public void addElement(Optional<? extends T> toAccumulateOpt) {
                accumulatedOpt = combine(accumulatedOpt, toAccumulateOpt, (accumulated, toAccumulate) -> {
                    collector.accumulator().accept(accumulated, toAccumulate);
                    return accumulated;
                });
            }

            public Box addBox(Box that) {
                accumulatedOpt = combine(this.accumulatedOpt, that.accumulatedOpt, collector.combiner());
                return this;
            }

            public Optional<R> finish() {
                return accumulatedOpt.map(collector.finisher());
            }
        }

        return Collector.of(() -> new Box(), (box, element) -> box.addElement(element), (left, right) -> left.addBox(right), box -> box.finish());
    }
}
