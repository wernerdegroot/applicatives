package nl.wernerdegroot.applicatives.runtime.decompositions;

import nl.wernerdegroot.applicatives.runtime.*;

import java.util.function.Function;

@FunctionalInterface
public interface Decomposition24<Source, First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond, TwentyThird, TwentyFourth> {

    static <Source, First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond, TwentyThird, TwentyFourth> Decomposition24<Source, First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond, TwentyThird, TwentyFourth> of(Function<? super Source, ? extends Tuple24<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond, TwentyThird, TwentyFourth>> toTuple) {
        return new Decomposition24<Source, First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond, TwentyThird, TwentyFourth>() {
            @Override
            public <T> T decompose(Source source, Function24<? super First, ? super Second, ? super Third, ? super Fourth, ? super Fifth, ? super Sixth, ? super Seventh, ? super Eighth, ? super Ninth, ? super Tenth, ? super Eleventh, ? super Twelfth, ? super Thirteenth, ? super Fourteenth, ? super Fifteenth, ? super Sixteenth, ? super Seventeenth, ? super Eighteenth, ? super Nineteenth, ? super Twentieth, ? super TwentyFirst, ? super TwentySecond, ? super TwentyThird, ? super TwentyFourth, ? extends T> fn) {
                return decompose(source).apply(fn);
            }

            @Override
            public Tuple24<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond, TwentyThird, TwentyFourth> decompose(Source source) {
                return toTuple.apply(source);
            }
        };
    }

    <T> T decompose(Source source, Function24<? super First, ? super Second, ? super Third, ? super Fourth, ? super Fifth, ? super Sixth, ? super Seventh, ? super Eighth, ? super Ninth, ? super Tenth, ? super Eleventh, ? super Twelfth, ? super Thirteenth, ? super Fourteenth, ? super Fifteenth, ? super Sixteenth, ? super Seventeenth, ? super Eighteenth, ? super Nineteenth, ? super Twentieth, ? super TwentyFirst, ? super TwentySecond, ? super TwentyThird, ? super TwentyFourth, ? extends T> fn);

    default Tuple24<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond, TwentyThird, TwentyFourth> decompose(Source source) {
        return decompose(source, Tuple::of);
    }
}
