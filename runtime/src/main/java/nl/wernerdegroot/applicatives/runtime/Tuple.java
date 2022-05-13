package nl.wernerdegroot.applicatives.runtime;

public final class Tuple {

    private Tuple() {
    }

    public static Tuple0 of() {
        return new FastTuple<>(new Object[]{});
    }

    public static <First> Tuple1<First> of(First first) {
        return new FastTuple<>(new Object[]{first});
    }

    public static <First, Second> Tuple2<First, Second> of(First first, Second second) {
        return new FastTuple<>(new Object[]{first, second});
    }

    public static <First, Second, Third> Tuple3<First, Second, Third> of(First first, Second second, Third third) {
        return new FastTuple<>(new Object[]{first, second, third});
    }

    public static <First, Second, Third, Fourth> Tuple4<First, Second, Third, Fourth> of(First first, Second second, Third third, Fourth fourth) {
        return new FastTuple<>(new Object[]{first, second, third, fourth});
    }

    public static <First, Second, Third, Fourth, Fifth> Tuple5<First, Second, Third, Fourth, Fifth> of(First first, Second second, Third third, Fourth fourth, Fifth fifth) {
        return new FastTuple<>(new Object[]{first, second, third, fourth, fifth});
    }

    public static <First, Second, Third, Fourth, Fifth, Sixth> Tuple6<First, Second, Third, Fourth, Fifth, Sixth> of(First first, Second second, Third third, Fourth fourth, Fifth fifth, Sixth sixth) {
        return new FastTuple<>(new Object[]{first, second, third, fourth, fifth, sixth});
    }

    public static <First, Second, Third, Fourth, Fifth, Sixth, Seventh> Tuple7<First, Second, Third, Fourth, Fifth, Sixth, Seventh> of(First first, Second second, Third third, Fourth fourth, Fifth fifth, Sixth sixth, Seventh seventh) {
        return new FastTuple<>(new Object[]{first, second, third, fourth, fifth, sixth, seventh});
    }

    public static <First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth> Tuple8<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth> of(First first, Second second, Third third, Fourth fourth, Fifth fifth, Sixth sixth, Seventh seventh, Eighth eighth) {
        return new FastTuple<>(new Object[]{first, second, third, fourth, fifth, sixth, seventh, eighth});
    }

    public static <First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth> Tuple9<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth> of(First first, Second second, Third third, Fourth fourth, Fifth fifth, Sixth sixth, Seventh seventh, Eighth eighth, Ninth ninth) {
        return new FastTuple<>(new Object[]{first, second, third, fourth, fifth, sixth, seventh, eighth, ninth});
    }

    public static <First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth> Tuple10<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth> of(First first, Second second, Third third, Fourth fourth, Fifth fifth, Sixth sixth, Seventh seventh, Eighth eighth, Ninth ninth, Tenth tenth) {
        return new FastTuple<>(new Object[]{first, second, third, fourth, fifth, sixth, seventh, eighth, ninth, tenth});
    }

    public static <First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh> Tuple11<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh> of(First first, Second second, Third third, Fourth fourth, Fifth fifth, Sixth sixth, Seventh seventh, Eighth eighth, Ninth ninth, Tenth tenth, Eleventh eleventh) {
        return new FastTuple<>(new Object[]{first, second, third, fourth, fifth, sixth, seventh, eighth, ninth, tenth, eleventh});
    }

    public static <First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth> Tuple12<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth> of(First first, Second second, Third third, Fourth fourth, Fifth fifth, Sixth sixth, Seventh seventh, Eighth eighth, Ninth ninth, Tenth tenth, Eleventh eleventh, Twelfth twelfth) {
        return new FastTuple<>(new Object[]{first, second, third, fourth, fifth, sixth, seventh, eighth, ninth, tenth, eleventh, twelfth});
    }

    public static <First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth> Tuple13<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth> of(First first, Second second, Third third, Fourth fourth, Fifth fifth, Sixth sixth, Seventh seventh, Eighth eighth, Ninth ninth, Tenth tenth, Eleventh eleventh, Twelfth twelfth, Thirteenth thirteenth) {
        return new FastTuple<>(new Object[]{first, second, third, fourth, fifth, sixth, seventh, eighth, ninth, tenth, eleventh, twelfth, thirteenth});
    }

    public static <First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth> Tuple14<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth> of(First first, Second second, Third third, Fourth fourth, Fifth fifth, Sixth sixth, Seventh seventh, Eighth eighth, Ninth ninth, Tenth tenth, Eleventh eleventh, Twelfth twelfth, Thirteenth thirteenth, Fourteenth fourteenth) {
        return new FastTuple<>(new Object[]{first, second, third, fourth, fifth, sixth, seventh, eighth, ninth, tenth, eleventh, twelfth, thirteenth, fourteenth});
    }

    public static <First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth> Tuple15<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth> of(First first, Second second, Third third, Fourth fourth, Fifth fifth, Sixth sixth, Seventh seventh, Eighth eighth, Ninth ninth, Tenth tenth, Eleventh eleventh, Twelfth twelfth, Thirteenth thirteenth, Fourteenth fourteenth, Fifteenth fifteenth) {
        return new FastTuple<>(new Object[]{first, second, third, fourth, fifth, sixth, seventh, eighth, ninth, tenth, eleventh, twelfth, thirteenth, fourteenth, fifteenth});
    }

    public static <First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth> Tuple16<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth> of(First first, Second second, Third third, Fourth fourth, Fifth fifth, Sixth sixth, Seventh seventh, Eighth eighth, Ninth ninth, Tenth tenth, Eleventh eleventh, Twelfth twelfth, Thirteenth thirteenth, Fourteenth fourteenth, Fifteenth fifteenth, Sixteenth sixteenth) {
        return new FastTuple<>(new Object[]{first, second, third, fourth, fifth, sixth, seventh, eighth, ninth, tenth, eleventh, twelfth, thirteenth, fourteenth, fifteenth, sixteenth});
    }

    public static <First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth> Tuple17<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth> of(First first, Second second, Third third, Fourth fourth, Fifth fifth, Sixth sixth, Seventh seventh, Eighth eighth, Ninth ninth, Tenth tenth, Eleventh eleventh, Twelfth twelfth, Thirteenth thirteenth, Fourteenth fourteenth, Fifteenth fifteenth, Sixteenth sixteenth, Seventeenth seventeenth) {
        return new FastTuple<>(new Object[]{first, second, third, fourth, fifth, sixth, seventh, eighth, ninth, tenth, eleventh, twelfth, thirteenth, fourteenth, fifteenth, sixteenth, seventeenth});
    }

    public static <First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth> Tuple18<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth> of(First first, Second second, Third third, Fourth fourth, Fifth fifth, Sixth sixth, Seventh seventh, Eighth eighth, Ninth ninth, Tenth tenth, Eleventh eleventh, Twelfth twelfth, Thirteenth thirteenth, Fourteenth fourteenth, Fifteenth fifteenth, Sixteenth sixteenth, Seventeenth seventeenth, Eighteenth eighteenth) {
        return new FastTuple<>(new Object[]{first, second, third, fourth, fifth, sixth, seventh, eighth, ninth, tenth, eleventh, twelfth, thirteenth, fourteenth, fifteenth, sixteenth, seventeenth, eighteenth});
    }

    public static <First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth> Tuple19<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth> of(First first, Second second, Third third, Fourth fourth, Fifth fifth, Sixth sixth, Seventh seventh, Eighth eighth, Ninth ninth, Tenth tenth, Eleventh eleventh, Twelfth twelfth, Thirteenth thirteenth, Fourteenth fourteenth, Fifteenth fifteenth, Sixteenth sixteenth, Seventeenth seventeenth, Eighteenth eighteenth, Nineteenth nineteenth) {
        return new FastTuple<>(new Object[]{first, second, third, fourth, fifth, sixth, seventh, eighth, ninth, tenth, eleventh, twelfth, thirteenth, fourteenth, fifteenth, sixteenth, seventeenth, eighteenth, nineteenth});
    }

    public static <First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth> Tuple20<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth> of(First first, Second second, Third third, Fourth fourth, Fifth fifth, Sixth sixth, Seventh seventh, Eighth eighth, Ninth ninth, Tenth tenth, Eleventh eleventh, Twelfth twelfth, Thirteenth thirteenth, Fourteenth fourteenth, Fifteenth fifteenth, Sixteenth sixteenth, Seventeenth seventeenth, Eighteenth eighteenth, Nineteenth nineteenth, Twentieth twentieth) {
        return new FastTuple<>(new Object[]{first, second, third, fourth, fifth, sixth, seventh, eighth, ninth, tenth, eleventh, twelfth, thirteenth, fourteenth, fifteenth, sixteenth, seventeenth, eighteenth, nineteenth, twentieth});
    }

    public static <First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst> Tuple21<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst> of(First first, Second second, Third third, Fourth fourth, Fifth fifth, Sixth sixth, Seventh seventh, Eighth eighth, Ninth ninth, Tenth tenth, Eleventh eleventh, Twelfth twelfth, Thirteenth thirteenth, Fourteenth fourteenth, Fifteenth fifteenth, Sixteenth sixteenth, Seventeenth seventeenth, Eighteenth eighteenth, Nineteenth nineteenth, Twentieth twentieth, TwentyFirst twentyFirst) {
        return new FastTuple<>(new Object[]{first, second, third, fourth, fifth, sixth, seventh, eighth, ninth, tenth, eleventh, twelfth, thirteenth, fourteenth, fifteenth, sixteenth, seventeenth, eighteenth, nineteenth, twentieth, twentyFirst});
    }

    public static <First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond> Tuple22<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond> of(First first, Second second, Third third, Fourth fourth, Fifth fifth, Sixth sixth, Seventh seventh, Eighth eighth, Ninth ninth, Tenth tenth, Eleventh eleventh, Twelfth twelfth, Thirteenth thirteenth, Fourteenth fourteenth, Fifteenth fifteenth, Sixteenth sixteenth, Seventeenth seventeenth, Eighteenth eighteenth, Nineteenth nineteenth, Twentieth twentieth, TwentyFirst twentyFirst, TwentySecond twentySecond) {
        return new FastTuple<>(new Object[]{first, second, third, fourth, fifth, sixth, seventh, eighth, ninth, tenth, eleventh, twelfth, thirteenth, fourteenth, fifteenth, sixteenth, seventeenth, eighteenth, nineteenth, twentieth, twentyFirst, twentySecond});
    }

    public static <First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond, TwentyThird> Tuple23<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond, TwentyThird> of(First first, Second second, Third third, Fourth fourth, Fifth fifth, Sixth sixth, Seventh seventh, Eighth eighth, Ninth ninth, Tenth tenth, Eleventh eleventh, Twelfth twelfth, Thirteenth thirteenth, Fourteenth fourteenth, Fifteenth fifteenth, Sixteenth sixteenth, Seventeenth seventeenth, Eighteenth eighteenth, Nineteenth nineteenth, Twentieth twentieth, TwentyFirst twentyFirst, TwentySecond twentySecond, TwentyThird twentyThird) {
        return new FastTuple<>(new Object[]{first, second, third, fourth, fifth, sixth, seventh, eighth, ninth, tenth, eleventh, twelfth, thirteenth, fourteenth, fifteenth, sixteenth, seventeenth, eighteenth, nineteenth, twentieth, twentyFirst, twentySecond, twentyThird});
    }

    public static <First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond, TwentyThird, TwentyFourth> Tuple24<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond, TwentyThird, TwentyFourth> of(First first, Second second, Third third, Fourth fourth, Fifth fifth, Sixth sixth, Seventh seventh, Eighth eighth, Ninth ninth, Tenth tenth, Eleventh eleventh, Twelfth twelfth, Thirteenth thirteenth, Fourteenth fourteenth, Fifteenth fifteenth, Sixteenth sixteenth, Seventeenth seventeenth, Eighteenth eighteenth, Nineteenth nineteenth, Twentieth twentieth, TwentyFirst twentyFirst, TwentySecond twentySecond, TwentyThird twentyThird, TwentyFourth twentyFourth) {
        return new FastTuple<>(new Object[]{first, second, third, fourth, fifth, sixth, seventh, eighth, ninth, tenth, eleventh, twelfth, thirteenth, fourteenth, fifteenth, sixteenth, seventeenth, eighteenth, nineteenth, twentieth, twentyFirst, twentySecond, twentyThird, twentyFourth});
    }

    public static <First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond, TwentyThird, TwentyFourth, TwentyFifth> Tuple25<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond, TwentyThird, TwentyFourth, TwentyFifth> of(First first, Second second, Third third, Fourth fourth, Fifth fifth, Sixth sixth, Seventh seventh, Eighth eighth, Ninth ninth, Tenth tenth, Eleventh eleventh, Twelfth twelfth, Thirteenth thirteenth, Fourteenth fourteenth, Fifteenth fifteenth, Sixteenth sixteenth, Seventeenth seventeenth, Eighteenth eighteenth, Nineteenth nineteenth, Twentieth twentieth, TwentyFirst twentyFirst, TwentySecond twentySecond, TwentyThird twentyThird, TwentyFourth twentyFourth, TwentyFifth twentyFifth) {
        return new FastTuple<>(new Object[]{first, second, third, fourth, fifth, sixth, seventh, eighth, ninth, tenth, eleventh, twelfth, thirteenth, fourteenth, fifteenth, sixteenth, seventeenth, eighteenth, nineteenth, twentieth, twentyFirst, twentySecond, twentyThird, twentyFourth, twentyFifth});
    }

    public static <First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond, TwentyThird, TwentyFourth, TwentyFifth, TwentySixth> Tuple26<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond, TwentyThird, TwentyFourth, TwentyFifth, TwentySixth> of(First first, Second second, Third third, Fourth fourth, Fifth fifth, Sixth sixth, Seventh seventh, Eighth eighth, Ninth ninth, Tenth tenth, Eleventh eleventh, Twelfth twelfth, Thirteenth thirteenth, Fourteenth fourteenth, Fifteenth fifteenth, Sixteenth sixteenth, Seventeenth seventeenth, Eighteenth eighteenth, Nineteenth nineteenth, Twentieth twentieth, TwentyFirst twentyFirst, TwentySecond twentySecond, TwentyThird twentyThird, TwentyFourth twentyFourth, TwentyFifth twentyFifth, TwentySixth twentySixth) {
        return new FastTuple<>(new Object[]{first, second, third, fourth, fifth, sixth, seventh, eighth, ninth, tenth, eleventh, twelfth, thirteenth, fourteenth, fifteenth, sixteenth, seventeenth, eighteenth, nineteenth, twentieth, twentyFirst, twentySecond, twentyThird, twentyFourth, twentyFifth, twentySixth});
    }
}
