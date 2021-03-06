package nl.wernerdegroot.applicatives;

public interface OptionalsOverloads {

    <P1, P2, R> java.util.Optional<R> combine(java.util.Optional<? extends P1> first, java.util.Optional<? extends P2> second, java.util.function.BiFunction<? super P1, ? super P2, ? extends R> fn);

    default <P1, P2, R> java.util.function.BiFunction<java.util.Optional<? extends P1>, java.util.Optional<? extends P2>, java.util.Optional<R>> lift(java.util.function.BiFunction<? super P1, ? super P2, ? extends R> fn) {
        return (first, second) ->
                this.combine(first, second, fn);
    }

    class Tuples {


    }

}