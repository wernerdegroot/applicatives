package nl.wernerdegroot.applicatives.runtime;

public interface Tuple26<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond, TwentyThird, TwentyFourth, TwentyFifth, TwentySixth> {

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

    TwentyThird getTwentyThird();

    TwentyFourth getTwentyFourth();

    TwentyFifth getTwentyFifth();

    TwentySixth getTwentySixth();

    Tuple25<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond, TwentyThird, TwentyFourth, TwentyFifth> withoutTwentySixth();

    default <R> R apply(Function26<? super First, ? super Second, ? super Third, ? super Fourth, ? super Fifth, ? super Sixth, ? super Seventh, ? super Eighth, ? super Ninth, ? super Tenth, ? super Eleventh, ? super Twelfth, ? super Thirteenth, ? super Fourteenth, ? super Fifteenth, ? super Sixteenth, ? super Seventeenth, ? super Eighteenth, ? super Nineteenth, ? super Twentieth, ? super TwentyFirst, ? super TwentySecond, ? super TwentyThird, ? super TwentyFourth, ? super TwentyFifth, ? super TwentySixth, ? extends R> fn) {
        return fn.apply(getFirst(), getSecond(), getThird(), getFourth(), getFifth(), getSixth(), getSeventh(), getEighth(), getNinth(), getTenth(), getEleventh(), getTwelfth(), getThirteenth(), getFourteenth(), getFifteenth(), getSixteenth(), getSeventeenth(), getEighteenth(), getNineteenth(), getTwentieth(), getTwentyFirst(), getTwentySecond(), getTwentyThird(), getTwentyFourth(), getTwentyFifth(), getTwentySixth());
    }
}

