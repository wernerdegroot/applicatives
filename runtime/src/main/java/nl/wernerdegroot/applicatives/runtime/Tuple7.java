package nl.wernerdegroot.applicatives.runtime;

interface Tuple7<First, Second, Third, Fourth, Fifth, Sixth, Seventh> {

    First getFirst();

    Second getSecond();

    Third getThird();

    Fourth getFourth();

    Fifth getFifth();

    Sixth getSixth();

    Seventh getSeventh();

    <Eighth> Tuple8<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth> withEighth(Eighth eighth);

    default <R> R apply(Function7<? super First, ? super Second, ? super Third, ? super Fourth, ? super Fifth, ? super Sixth, ? super Seventh, ? extends R> fn) {
        return fn.apply(getFirst(), getSecond(), getThird(), getFourth(), getFifth(), getSixth(), getSeventh());
    }
}

