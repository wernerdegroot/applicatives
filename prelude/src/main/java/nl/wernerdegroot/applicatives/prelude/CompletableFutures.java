package nl.wernerdegroot.applicatives.prelude;

import nl.wernerdegroot.applicatives.runtime.Covariant;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.stream.Collector;

public class CompletableFutures implements CompletableFuturesOverloads {

    private static final CompletableFutures INSTANCE = new CompletableFutures();

    public static CompletableFutures instance() {
        return INSTANCE;
    }

    @Override
    @Covariant(className = "CompletableFuturesOverloads")
    public <A, B, C> CompletableFuture<C> combine(CompletableFuture<? extends A> left, CompletableFuture<? extends B> right, BiFunction<? super A, ? super B, ? extends C> fn) {
        return left.thenCombine(right, fn);
    }

    public <T, A, R> Collector<CompletableFuture<? extends T>, ?, CompletableFuture<R>> collector(Collector<T, A, R> collector) {
        class Box {
            CompletableFuture<A> accumulatedFut;

            public Box() {
                this.accumulatedFut = CompletableFuture.completedFuture(collector.supplier().get());
            }

            public void addElement(CompletableFuture<? extends T> toAccumulateFut) {
                accumulatedFut = combine(accumulatedFut, toAccumulateFut, (accumulated, toAccumulate) -> {
                    collector.accumulator().accept(accumulated, toAccumulate);
                    return accumulated;
                });
            }

            public Box addBox(Box that) {
                accumulatedFut = combine(this.accumulatedFut, that.accumulatedFut, collector.combiner());
                return this;
            }

            public CompletableFuture<R> finish() {
                return accumulatedFut.thenApply(collector.finisher());
            }
        }

        return Collector.of(() -> new Box(), (box, element) -> box.addElement(element), (left, right) -> left.addBox(right), box -> box.finish());
    }
}
