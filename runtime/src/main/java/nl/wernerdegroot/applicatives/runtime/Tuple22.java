package nl.wernerdegroot.applicatives.runtime;

public interface Tuple22<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond> {

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

    Thirteenth getThirteenth();

    Fourteenth getFourteenth();

    Fifteenth getFifteenth();

    Sixteenth getSixteenth();

    Seventeenth getSeventeenth();

    Eighteenth getEighteenth();

    Nineteenth getNineteenth();

    Twentieth getTwentieth();

    TwentyFirst getTwentyFirst();

    TwentySecond getTwentySecond();

    Tuple21<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst> withoutTwentySecond();

    <TwentyThird> Tuple23<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond, TwentyThird> withTwentyThird(TwentyThird twentyThird);

    default <R> R apply(Function22<? super First, ? super Second, ? super Third, ? super Fourth, ? super Fifth, ? super Sixth, ? super Seventh, ? super Eighth, ? super Ninth, ? super Tenth, ? super Eleventh, ? super Twelfth, ? super Thirteenth, ? super Fourteenth, ? super Fifteenth, ? super Sixteenth, ? super Seventeenth, ? super Eighteenth, ? super Nineteenth, ? super Twentieth, ? super TwentyFirst, ? super TwentySecond, ? extends R> fn) {
        return fn.apply(getFirst(), getSecond(), getThird(), getFourth(), getFifth(), getSixth(), getSeventh(), getEighth(), getNinth(), getTenth(), getEleventh(), getTwelfth(), getThirteenth(), getFourteenth(), getFifteenth(), getSixteenth(), getSeventeenth(), getEighteenth(), getNineteenth(), getTwentieth(), getTwentyFirst(), getTwentySecond());
    }
}

