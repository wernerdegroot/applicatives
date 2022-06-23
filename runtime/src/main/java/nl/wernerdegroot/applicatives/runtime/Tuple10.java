package nl.wernerdegroot.applicatives.runtime;

public interface Tuple10<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth> {

    First getFirst();

    Second getSecond();

    Third getThird();

    Fourth getFourth();

    Fifth getFifth();

    Sixth getSixth();

    Seventh getSeventh();

    Eighth getEighth();

    Ninth getNinth();

    Tenth getTenth();

    Tuple9<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth> withoutTenth();

    <Eleventh> Tuple11<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh> withEleventh(Eleventh eleventh);

    default <R> R apply(Function10<? super First, ? super Second, ? super Third, ? super Fourth, ? super Fifth, ? super Sixth, ? super Seventh, ? super Eighth, ? super Ninth, ? super Tenth, ? extends R> fn) {
        return fn.apply(getFirst(), getSecond(), getThird(), getFourth(), getFifth(), getSixth(), getSeventh(), getEighth(), getNinth(), getTenth());
    }
}

