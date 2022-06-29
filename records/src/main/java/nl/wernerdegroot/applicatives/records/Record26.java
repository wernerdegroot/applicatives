package nl.wernerdegroot.applicatives.records;

import nl.wernerdegroot.applicatives.runtime.Function26;
import nl.wernerdegroot.applicatives.runtime.decompositions.Decomposable26;

public interface Record26<R extends Record & Record26<R, First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond, TwentyThird, TwentyFourth, TwentyFifth, TwentySixth>, First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond, TwentyThird, TwentyFourth, TwentyFifth, TwentySixth> extends Decomposable26<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond, TwentyThird, TwentyFourth, TwentyFifth, TwentySixth> {

    @Override
    default <T> T decomposeTo(Function26<? super First, ? super Second, ? super Third, ? super Fourth, ? super Fifth, ? super Sixth, ? super Seventh, ? super Eighth, ? super Ninth, ? super Tenth, ? super Eleventh, ? super Twelfth, ? super Thirteenth, ? super Fourteenth, ? super Fifteenth, ? super Sixteenth, ? super Seventeenth, ? super Eighteenth, ? super Nineteenth, ? super Twentieth, ? super TwentyFirst, ? super TwentySecond, ? super TwentyThird, ? super TwentyFourth, ? super TwentyFifth, ? super TwentySixth, ? extends T> fn) {
        var components = this.getClass().getRecordComponents();
        try {
            return fn.apply(
                    (First) components[0].getAccessor().invoke(this),
                    (Second) components[1].getAccessor().invoke(this),
                    (Third) components[2].getAccessor().invoke(this),
                    (Fourth) components[3].getAccessor().invoke(this),
                    (Fifth) components[4].getAccessor().invoke(this),
                    (Sixth) components[5].getAccessor().invoke(this),
                    (Seventh) components[6].getAccessor().invoke(this),
                    (Eighth) components[7].getAccessor().invoke(this),
                    (Ninth) components[8].getAccessor().invoke(this),
                    (Tenth) components[9].getAccessor().invoke(this),
                    (Eleventh) components[10].getAccessor().invoke(this),
                    (Twelfth) components[11].getAccessor().invoke(this),
                    (Thirteenth) components[12].getAccessor().invoke(this),
                    (Fourteenth) components[13].getAccessor().invoke(this),
                    (Fifteenth) components[14].getAccessor().invoke(this),
                    (Sixteenth) components[15].getAccessor().invoke(this),
                    (Seventeenth) components[16].getAccessor().invoke(this),
                    (Eighteenth) components[17].getAccessor().invoke(this),
                    (Nineteenth) components[18].getAccessor().invoke(this),
                    (Twentieth) components[19].getAccessor().invoke(this),
                    (TwentyFirst) components[20].getAccessor().invoke(this),
                    (TwentySecond) components[21].getAccessor().invoke(this),
                    (TwentyThird) components[22].getAccessor().invoke(this),
                    (TwentyFourth) components[23].getAccessor().invoke(this),
                    (TwentyFifth) components[24].getAccessor().invoke(this),
                    (TwentySixth) components[25].getAccessor().invoke(this)
            );
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}
