package nl.wernerdegroot.applicatives.runtime;

public interface Tuple12<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth> {

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

    Eleventh getEleventh();

    Twelfth getTwelfth();

    Tuple11<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh> withoutTwelfth();

    <Thirteenth> Tuple13<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth> withThirteenth(Thirteenth thirteenth);

    default <R> R apply(Function12<? super First, ? super Second, ? super Third, ? super Fourth, ? super Fifth, ? super Sixth, ? super Seventh, ? super Eighth, ? super Ninth, ? super Tenth, ? super Eleventh, ? super Twelfth, ? extends R> fn) {
        return fn.apply(getFirst(), getSecond(), getThird(), getFourth(), getFifth(), getSixth(), getSeventh(), getEighth(), getNinth(), getTenth(), getEleventh(), getTwelfth());
    }
}

