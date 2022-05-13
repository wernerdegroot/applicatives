package nl.wernerdegroot.applicatives.runtime;

public interface Tuple18<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth> {

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

    <Nineteenth> Tuple19<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth> withNineteenth(Nineteenth nineteenth);

    default <R> R apply(Function18<? super First, ? super Second, ? super Third, ? super Fourth, ? super Fifth, ? super Sixth, ? super Seventh, ? super Eighth, ? super Ninth, ? super Tenth, ? super Eleventh, ? super Twelfth, ? super Thirteenth, ? super Fourteenth, ? super Fifteenth, ? super Sixteenth, ? super Seventeenth, ? super Eighteenth, ? extends R> fn) {
        return fn.apply(getFirst(), getSecond(), getThird(), getFourth(), getFifth(), getSixth(), getSeventh(), getEighth(), getNinth(), getTenth(), getEleventh(), getTwelfth(), getThirteenth(), getFourteenth(), getFifteenth(), getSixteenth(), getSeventeenth(), getEighteenth());
    }
}

