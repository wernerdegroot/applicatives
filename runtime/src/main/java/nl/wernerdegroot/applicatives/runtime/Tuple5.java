package nl.wernerdegroot.applicatives.runtime;

public interface Tuple5<First, Second, Third, Fourth, Fifth> {

    First getFirst();

    Second getSecond();

    Third getThird();

    Fourth getFourth();

    Fifth getFifth();

    <Sixth> Tuple6<First, Second, Third, Fourth, Fifth, Sixth> withSixth(Sixth sixth);

    default <R> R apply(Function5<? super First, ? super Second, ? super Third, ? super Fourth, ? super Fifth, ? extends R> fn) {
        return fn.apply(getFirst(), getSecond(), getThird(), getFourth(), getFifth());
    }
}

