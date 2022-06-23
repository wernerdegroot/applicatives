package nl.wernerdegroot.applicatives.runtime;

public interface Tuple6<First, Second, Third, Fourth, Fifth, Sixth> {

    First getFirst();

    Second getSecond();

    Third getThird();

    Fourth getFourth();

    Fifth getFifth();

    Sixth getSixth();

    Tuple5<First, Second, Third, Fourth, Fifth> withoutSixth();

    <Seventh> Tuple7<First, Second, Third, Fourth, Fifth, Sixth, Seventh> withSeventh(Seventh seventh);

    default <R> R apply(Function6<? super First, ? super Second, ? super Third, ? super Fourth, ? super Fifth, ? super Sixth, ? extends R> fn) {
        return fn.apply(getFirst(), getSecond(), getThird(), getFourth(), getFifth(), getSixth());
    }
}

