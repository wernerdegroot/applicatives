package nl.wernerdegroot.applicatives;

public interface CompletableFuturesOverloads {

    <P1, P2, R> java.util.concurrent.CompletableFuture<R> combineImpl(java.util.concurrent.CompletableFuture<? extends P1> first, java.util.concurrent.CompletableFuture<? extends P2> second, java.util.function.BiFunction<? super P1, ? super P2, ? extends R> fn);

    default <P1, P2, R> java.util.concurrent.CompletableFuture<R> combine(java.util.concurrent.CompletableFuture<? extends P1> first, java.util.concurrent.CompletableFuture<? extends P2> second, java.util.function.BiFunction<? super P1, ? super P2, ? extends R> fn) {
        return this.combineImpl(first, second, fn);
    }

    default <P1, P2, R> java.util.function.BiFunction<java.util.concurrent.CompletableFuture<? extends P1>, java.util.concurrent.CompletableFuture<? extends P2>, java.util.concurrent.CompletableFuture<R>> lift(java.util.function.BiFunction<? super P1, ? super P2, ? extends R> fn) {
        return (first, second) ->
                this.combine(first, second, fn);
    }

    class Tuples {


    }

}