package nl.wernerdegroot.applicatives.runtime;

public interface Tuple8<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth> {

    First getFirst();

    Second getSecond();

    Third getThird();

    Fourth getFourth();

    Fifth getFifth();

    Sixth getSixth();

    Seventh getSeventh();

    Eighth getEighth();

    <Ninth> Tuple9<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth> withNinth(Ninth ninth);

    default <R> R apply(Function8<? super First, ? super Second, ? super Third, ? super Fourth, ? super Fifth, ? super Sixth, ? super Seventh, ? super Eighth, ? extends R> fn) {
        return fn.apply(getFirst(), getSecond(), getThird(), getFourth(), getFifth(), getSixth(), getSeventh(), getEighth());
    }
}

