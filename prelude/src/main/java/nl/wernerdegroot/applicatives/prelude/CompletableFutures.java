package nl.wernerdegroot.applicatives.prelude;

import nl.wernerdegroot.applicatives.runtime.Covariant;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;

public class CompletableFutures implements CompletableFuturesApplicative {

    private static final CompletableFutures INSTANCE = new CompletableFutures();

    public static CompletableFutures instance() {
        return INSTANCE;
    }

    @Override
    @Covariant(className = "CompletableFuturesApplicative")
    public <A, B, C>CompletableFuture<C> combine(CompletableFuture<? extends A> left, CompletableFuture<? extends B> right, BiFunction<? super A, ? super B, ? extends C> fn) {
        return left.thenCombine(right, fn);
    }
}
