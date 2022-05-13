package nl.wernerdegroot.applicatives.runtime;

public interface Tuple3<First, Second, Third> {

    First getFirst();

    Second getSecond();

    Third getThird();

    <Fourth> Tuple4<First, Second, Third, Fourth> withFourth(Fourth fourth);

    default <R> R apply(Function3<? super First, ? super Second, ? super Third, ? extends R> fn) {
        return fn.apply(getFirst(), getSecond(), getThird());
    }
}

