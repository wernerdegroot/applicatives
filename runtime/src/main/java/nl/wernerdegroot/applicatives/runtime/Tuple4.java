package nl.wernerdegroot.applicatives.runtime;

public interface Tuple4<First, Second, Third, Fourth> {

    First getFirst();

    Second getSecond();

    Third getThird();

    Fourth getFourth();

    <Fifth> Tuple5<First, Second, Third, Fourth, Fifth> withFifth(Fifth fifth);

    default <R> R apply(Function4<? super First, ? super Second, ? super Third, ? super Fourth, ? extends R> fn) {
        return fn.apply(getFirst(), getSecond(), getThird(), getFourth());
    }
}

