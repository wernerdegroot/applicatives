package nl.wernerdegroot.applicatives.runtime.decompositions;

import nl.wernerdegroot.applicatives.runtime.Tuple;

import java.util.function.Function;

public class Decomposition {

    public static <Source, First, Second> Decomposition2<Source, First, Second> of(
            Function<? super Source, ? extends First> extractFirst,
            Function<? super Source, ? extends Second> extractSecond) {
        return source -> Tuple.of(extractFirst.apply(source), extractSecond.apply(source));
    }

    public static <Source, First, Second, Third> Decomposition3<Source, First, Second, Third> of(
            Function<? super Source, ? extends First> extractFirst,
            Function<? super Source, ? extends Second> extractSecond,
            Function<? super Source, ? extends Third> extractThird) {
        return source -> Tuple.of(extractFirst.apply(source), extractSecond.apply(source), extractThird.apply(source));
    }

    public static <Source, First, Second, Third, Fourth> Decomposition4<Source, First, Second, Third, Fourth> of(
            Function<? super Source, ? extends First> extractFirst,
            Function<? super Source, ? extends Second> extractSecond,
            Function<? super Source, ? extends Third> extractThird,
            Function<? super Source, ? extends Fourth> extractFourth) {
        return source -> Tuple.of(extractFirst.apply(source), extractSecond.apply(source), extractThird.apply(source), extractFourth.apply(source));
    }

    public static <Source, First, Second, Third, Fourth, Fifth> Decomposition5<Source, First, Second, Third, Fourth, Fifth> of(
            Function<? super Source, ? extends First> extractFirst,
            Function<? super Source, ? extends Second> extractSecond,
            Function<? super Source, ? extends Third> extractThird,
            Function<? super Source, ? extends Fourth> extractFourth,
            Function<? super Source, ? extends Fifth> extractFifth) {
        return source -> Tuple.of(extractFirst.apply(source), extractSecond.apply(source), extractThird.apply(source), extractFourth.apply(source), extractFifth.apply(source));
    }

    public static <Source, First, Second, Third, Fourth, Fifth, Sixth> Decomposition6<Source, First, Second, Third, Fourth, Fifth, Sixth> of(
            Function<? super Source, ? extends First> extractFirst,
            Function<? super Source, ? extends Second> extractSecond,
            Function<? super Source, ? extends Third> extractThird,
            Function<? super Source, ? extends Fourth> extractFourth,
            Function<? super Source, ? extends Fifth> extractFifth,
            Function<? super Source, ? extends Sixth> extractSixth) {
        return source -> Tuple.of(extractFirst.apply(source), extractSecond.apply(source), extractThird.apply(source), extractFourth.apply(source), extractFifth.apply(source), extractSixth.apply(source));
    }

    public static <Source, First, Second, Third, Fourth, Fifth, Sixth, Seventh> Decomposition7<Source, First, Second, Third, Fourth, Fifth, Sixth, Seventh> of(
            Function<? super Source, ? extends First> extractFirst,
            Function<? super Source, ? extends Second> extractSecond,
            Function<? super Source, ? extends Third> extractThird,
            Function<? super Source, ? extends Fourth> extractFourth,
            Function<? super Source, ? extends Fifth> extractFifth,
            Function<? super Source, ? extends Sixth> extractSixth,
            Function<? super Source, ? extends Seventh> extractSeventh) {
        return source -> Tuple.of(extractFirst.apply(source), extractSecond.apply(source), extractThird.apply(source), extractFourth.apply(source), extractFifth.apply(source), extractSixth.apply(source), extractSeventh.apply(source));
    }

    public static <Source, First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth> Decomposition8<Source, First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth> of(
            Function<? super Source, ? extends First> extractFirst,
            Function<? super Source, ? extends Second> extractSecond,
            Function<? super Source, ? extends Third> extractThird,
            Function<? super Source, ? extends Fourth> extractFourth,
            Function<? super Source, ? extends Fifth> extractFifth,
            Function<? super Source, ? extends Sixth> extractSixth,
            Function<? super Source, ? extends Seventh> extractSeventh,
            Function<? super Source, ? extends Eighth> extractEighth) {
        return source -> Tuple.of(extractFirst.apply(source), extractSecond.apply(source), extractThird.apply(source), extractFourth.apply(source), extractFifth.apply(source), extractSixth.apply(source), extractSeventh.apply(source), extractEighth.apply(source));
    }

    public static <Source, First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth> Decomposition9<Source, First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth> of(
            Function<? super Source, ? extends First> extractFirst,
            Function<? super Source, ? extends Second> extractSecond,
            Function<? super Source, ? extends Third> extractThird,
            Function<? super Source, ? extends Fourth> extractFourth,
            Function<? super Source, ? extends Fifth> extractFifth,
            Function<? super Source, ? extends Sixth> extractSixth,
            Function<? super Source, ? extends Seventh> extractSeventh,
            Function<? super Source, ? extends Eighth> extractEighth,
            Function<? super Source, ? extends Ninth> extractNinth) {
        return source -> Tuple.of(extractFirst.apply(source), extractSecond.apply(source), extractThird.apply(source), extractFourth.apply(source), extractFifth.apply(source), extractSixth.apply(source), extractSeventh.apply(source), extractEighth.apply(source), extractNinth.apply(source));
    }

    public static <Source, First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth> Decomposition10<Source, First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth> of(
            Function<? super Source, ? extends First> extractFirst,
            Function<? super Source, ? extends Second> extractSecond,
            Function<? super Source, ? extends Third> extractThird,
            Function<? super Source, ? extends Fourth> extractFourth,
            Function<? super Source, ? extends Fifth> extractFifth,
            Function<? super Source, ? extends Sixth> extractSixth,
            Function<? super Source, ? extends Seventh> extractSeventh,
            Function<? super Source, ? extends Eighth> extractEighth,
            Function<? super Source, ? extends Ninth> extractNinth,
            Function<? super Source, ? extends Tenth> extractTenth) {
        return source -> Tuple.of(extractFirst.apply(source), extractSecond.apply(source), extractThird.apply(source), extractFourth.apply(source), extractFifth.apply(source), extractSixth.apply(source), extractSeventh.apply(source), extractEighth.apply(source), extractNinth.apply(source), extractTenth.apply(source));
    }

    public static <Source, First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh> Decomposition11<Source, First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh> of(
            Function<? super Source, ? extends First> extractFirst,
            Function<? super Source, ? extends Second> extractSecond,
            Function<? super Source, ? extends Third> extractThird,
            Function<? super Source, ? extends Fourth> extractFourth,
            Function<? super Source, ? extends Fifth> extractFifth,
            Function<? super Source, ? extends Sixth> extractSixth,
            Function<? super Source, ? extends Seventh> extractSeventh,
            Function<? super Source, ? extends Eighth> extractEighth,
            Function<? super Source, ? extends Ninth> extractNinth,
            Function<? super Source, ? extends Tenth> extractTenth,
            Function<? super Source, ? extends Eleventh> extractEleventh) {
        return source -> Tuple.of(extractFirst.apply(source), extractSecond.apply(source), extractThird.apply(source), extractFourth.apply(source), extractFifth.apply(source), extractSixth.apply(source), extractSeventh.apply(source), extractEighth.apply(source), extractNinth.apply(source), extractTenth.apply(source), extractEleventh.apply(source));
    }

    public static <Source, First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth> Decomposition12<Source, First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth> of(
            Function<? super Source, ? extends First> extractFirst,
            Function<? super Source, ? extends Second> extractSecond,
            Function<? super Source, ? extends Third> extractThird,
            Function<? super Source, ? extends Fourth> extractFourth,
            Function<? super Source, ? extends Fifth> extractFifth,
            Function<? super Source, ? extends Sixth> extractSixth,
            Function<? super Source, ? extends Seventh> extractSeventh,
            Function<? super Source, ? extends Eighth> extractEighth,
            Function<? super Source, ? extends Ninth> extractNinth,
            Function<? super Source, ? extends Tenth> extractTenth,
            Function<? super Source, ? extends Eleventh> extractEleventh,
            Function<? super Source, ? extends Twelfth> extractTwelfth) {
        return source -> Tuple.of(extractFirst.apply(source), extractSecond.apply(source), extractThird.apply(source), extractFourth.apply(source), extractFifth.apply(source), extractSixth.apply(source), extractSeventh.apply(source), extractEighth.apply(source), extractNinth.apply(source), extractTenth.apply(source), extractEleventh.apply(source), extractTwelfth.apply(source));
    }

    public static <Source, First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth> Decomposition13<Source, First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth> of(
            Function<? super Source, ? extends First> extractFirst,
            Function<? super Source, ? extends Second> extractSecond,
            Function<? super Source, ? extends Third> extractThird,
            Function<? super Source, ? extends Fourth> extractFourth,
            Function<? super Source, ? extends Fifth> extractFifth,
            Function<? super Source, ? extends Sixth> extractSixth,
            Function<? super Source, ? extends Seventh> extractSeventh,
            Function<? super Source, ? extends Eighth> extractEighth,
            Function<? super Source, ? extends Ninth> extractNinth,
            Function<? super Source, ? extends Tenth> extractTenth,
            Function<? super Source, ? extends Eleventh> extractEleventh,
            Function<? super Source, ? extends Twelfth> extractTwelfth,
            Function<? super Source, ? extends Thirteenth> extractThirteenth) {
        return source -> Tuple.of(extractFirst.apply(source), extractSecond.apply(source), extractThird.apply(source), extractFourth.apply(source), extractFifth.apply(source), extractSixth.apply(source), extractSeventh.apply(source), extractEighth.apply(source), extractNinth.apply(source), extractTenth.apply(source), extractEleventh.apply(source), extractTwelfth.apply(source), extractThirteenth.apply(source));
    }

    public static <Source, First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth> Decomposition14<Source, First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth> of(
            Function<? super Source, ? extends First> extractFirst,
            Function<? super Source, ? extends Second> extractSecond,
            Function<? super Source, ? extends Third> extractThird,
            Function<? super Source, ? extends Fourth> extractFourth,
            Function<? super Source, ? extends Fifth> extractFifth,
            Function<? super Source, ? extends Sixth> extractSixth,
            Function<? super Source, ? extends Seventh> extractSeventh,
            Function<? super Source, ? extends Eighth> extractEighth,
            Function<? super Source, ? extends Ninth> extractNinth,
            Function<? super Source, ? extends Tenth> extractTenth,
            Function<? super Source, ? extends Eleventh> extractEleventh,
            Function<? super Source, ? extends Twelfth> extractTwelfth,
            Function<? super Source, ? extends Thirteenth> extractThirteenth,
            Function<? super Source, ? extends Fourteenth> extractFourteenth) {
        return source -> Tuple.of(extractFirst.apply(source), extractSecond.apply(source), extractThird.apply(source), extractFourth.apply(source), extractFifth.apply(source), extractSixth.apply(source), extractSeventh.apply(source), extractEighth.apply(source), extractNinth.apply(source), extractTenth.apply(source), extractEleventh.apply(source), extractTwelfth.apply(source), extractThirteenth.apply(source), extractFourteenth.apply(source));
    }

    public static <Source, First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth> Decomposition15<Source, First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth> of(
            Function<? super Source, ? extends First> extractFirst,
            Function<? super Source, ? extends Second> extractSecond,
            Function<? super Source, ? extends Third> extractThird,
            Function<? super Source, ? extends Fourth> extractFourth,
            Function<? super Source, ? extends Fifth> extractFifth,
            Function<? super Source, ? extends Sixth> extractSixth,
            Function<? super Source, ? extends Seventh> extractSeventh,
            Function<? super Source, ? extends Eighth> extractEighth,
            Function<? super Source, ? extends Ninth> extractNinth,
            Function<? super Source, ? extends Tenth> extractTenth,
            Function<? super Source, ? extends Eleventh> extractEleventh,
            Function<? super Source, ? extends Twelfth> extractTwelfth,
            Function<? super Source, ? extends Thirteenth> extractThirteenth,
            Function<? super Source, ? extends Fourteenth> extractFourteenth,
            Function<? super Source, ? extends Fifteenth> extractFifteenth) {
        return source -> Tuple.of(extractFirst.apply(source), extractSecond.apply(source), extractThird.apply(source), extractFourth.apply(source), extractFifth.apply(source), extractSixth.apply(source), extractSeventh.apply(source), extractEighth.apply(source), extractNinth.apply(source), extractTenth.apply(source), extractEleventh.apply(source), extractTwelfth.apply(source), extractThirteenth.apply(source), extractFourteenth.apply(source), extractFifteenth.apply(source));
    }

    public static <Source, First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth> Decomposition16<Source, First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth> of(
            Function<? super Source, ? extends First> extractFirst,
            Function<? super Source, ? extends Second> extractSecond,
            Function<? super Source, ? extends Third> extractThird,
            Function<? super Source, ? extends Fourth> extractFourth,
            Function<? super Source, ? extends Fifth> extractFifth,
            Function<? super Source, ? extends Sixth> extractSixth,
            Function<? super Source, ? extends Seventh> extractSeventh,
            Function<? super Source, ? extends Eighth> extractEighth,
            Function<? super Source, ? extends Ninth> extractNinth,
            Function<? super Source, ? extends Tenth> extractTenth,
            Function<? super Source, ? extends Eleventh> extractEleventh,
            Function<? super Source, ? extends Twelfth> extractTwelfth,
            Function<? super Source, ? extends Thirteenth> extractThirteenth,
            Function<? super Source, ? extends Fourteenth> extractFourteenth,
            Function<? super Source, ? extends Fifteenth> extractFifteenth,
            Function<? super Source, ? extends Sixteenth> extractSixteenth) {
        return source -> Tuple.of(extractFirst.apply(source), extractSecond.apply(source), extractThird.apply(source), extractFourth.apply(source), extractFifth.apply(source), extractSixth.apply(source), extractSeventh.apply(source), extractEighth.apply(source), extractNinth.apply(source), extractTenth.apply(source), extractEleventh.apply(source), extractTwelfth.apply(source), extractThirteenth.apply(source), extractFourteenth.apply(source), extractFifteenth.apply(source), extractSixteenth.apply(source));
    }

    public static <Source, First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth> Decomposition17<Source, First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth> of(
            Function<? super Source, ? extends First> extractFirst,
            Function<? super Source, ? extends Second> extractSecond,
            Function<? super Source, ? extends Third> extractThird,
            Function<? super Source, ? extends Fourth> extractFourth,
            Function<? super Source, ? extends Fifth> extractFifth,
            Function<? super Source, ? extends Sixth> extractSixth,
            Function<? super Source, ? extends Seventh> extractSeventh,
            Function<? super Source, ? extends Eighth> extractEighth,
            Function<? super Source, ? extends Ninth> extractNinth,
            Function<? super Source, ? extends Tenth> extractTenth,
            Function<? super Source, ? extends Eleventh> extractEleventh,
            Function<? super Source, ? extends Twelfth> extractTwelfth,
            Function<? super Source, ? extends Thirteenth> extractThirteenth,
            Function<? super Source, ? extends Fourteenth> extractFourteenth,
            Function<? super Source, ? extends Fifteenth> extractFifteenth,
            Function<? super Source, ? extends Sixteenth> extractSixteenth,
            Function<? super Source, ? extends Seventeenth> extractSeventeenth) {
        return source -> Tuple.of(extractFirst.apply(source), extractSecond.apply(source), extractThird.apply(source), extractFourth.apply(source), extractFifth.apply(source), extractSixth.apply(source), extractSeventh.apply(source), extractEighth.apply(source), extractNinth.apply(source), extractTenth.apply(source), extractEleventh.apply(source), extractTwelfth.apply(source), extractThirteenth.apply(source), extractFourteenth.apply(source), extractFifteenth.apply(source), extractSixteenth.apply(source), extractSeventeenth.apply(source));
    }

    public static <Source, First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth> Decomposition18<Source, First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth> of(
            Function<? super Source, ? extends First> extractFirst,
            Function<? super Source, ? extends Second> extractSecond,
            Function<? super Source, ? extends Third> extractThird,
            Function<? super Source, ? extends Fourth> extractFourth,
            Function<? super Source, ? extends Fifth> extractFifth,
            Function<? super Source, ? extends Sixth> extractSixth,
            Function<? super Source, ? extends Seventh> extractSeventh,
            Function<? super Source, ? extends Eighth> extractEighth,
            Function<? super Source, ? extends Ninth> extractNinth,
            Function<? super Source, ? extends Tenth> extractTenth,
            Function<? super Source, ? extends Eleventh> extractEleventh,
            Function<? super Source, ? extends Twelfth> extractTwelfth,
            Function<? super Source, ? extends Thirteenth> extractThirteenth,
            Function<? super Source, ? extends Fourteenth> extractFourteenth,
            Function<? super Source, ? extends Fifteenth> extractFifteenth,
            Function<? super Source, ? extends Sixteenth> extractSixteenth,
            Function<? super Source, ? extends Seventeenth> extractSeventeenth,
            Function<? super Source, ? extends Eighteenth> extractEighteenth) {
        return source -> Tuple.of(extractFirst.apply(source), extractSecond.apply(source), extractThird.apply(source), extractFourth.apply(source), extractFifth.apply(source), extractSixth.apply(source), extractSeventh.apply(source), extractEighth.apply(source), extractNinth.apply(source), extractTenth.apply(source), extractEleventh.apply(source), extractTwelfth.apply(source), extractThirteenth.apply(source), extractFourteenth.apply(source), extractFifteenth.apply(source), extractSixteenth.apply(source), extractSeventeenth.apply(source), extractEighteenth.apply(source));
    }

    public static <Source, First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth> Decomposition19<Source, First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth> of(
            Function<? super Source, ? extends First> extractFirst,
            Function<? super Source, ? extends Second> extractSecond,
            Function<? super Source, ? extends Third> extractThird,
            Function<? super Source, ? extends Fourth> extractFourth,
            Function<? super Source, ? extends Fifth> extractFifth,
            Function<? super Source, ? extends Sixth> extractSixth,
            Function<? super Source, ? extends Seventh> extractSeventh,
            Function<? super Source, ? extends Eighth> extractEighth,
            Function<? super Source, ? extends Ninth> extractNinth,
            Function<? super Source, ? extends Tenth> extractTenth,
            Function<? super Source, ? extends Eleventh> extractEleventh,
            Function<? super Source, ? extends Twelfth> extractTwelfth,
            Function<? super Source, ? extends Thirteenth> extractThirteenth,
            Function<? super Source, ? extends Fourteenth> extractFourteenth,
            Function<? super Source, ? extends Fifteenth> extractFifteenth,
            Function<? super Source, ? extends Sixteenth> extractSixteenth,
            Function<? super Source, ? extends Seventeenth> extractSeventeenth,
            Function<? super Source, ? extends Eighteenth> extractEighteenth,
            Function<? super Source, ? extends Nineteenth> extractNineteenth) {
        return source -> Tuple.of(extractFirst.apply(source), extractSecond.apply(source), extractThird.apply(source), extractFourth.apply(source), extractFifth.apply(source), extractSixth.apply(source), extractSeventh.apply(source), extractEighth.apply(source), extractNinth.apply(source), extractTenth.apply(source), extractEleventh.apply(source), extractTwelfth.apply(source), extractThirteenth.apply(source), extractFourteenth.apply(source), extractFifteenth.apply(source), extractSixteenth.apply(source), extractSeventeenth.apply(source), extractEighteenth.apply(source), extractNineteenth.apply(source));
    }

    public static <Source, First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth> Decomposition20<Source, First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth> of(
            Function<? super Source, ? extends First> extractFirst,
            Function<? super Source, ? extends Second> extractSecond,
            Function<? super Source, ? extends Third> extractThird,
            Function<? super Source, ? extends Fourth> extractFourth,
            Function<? super Source, ? extends Fifth> extractFifth,
            Function<? super Source, ? extends Sixth> extractSixth,
            Function<? super Source, ? extends Seventh> extractSeventh,
            Function<? super Source, ? extends Eighth> extractEighth,
            Function<? super Source, ? extends Ninth> extractNinth,
            Function<? super Source, ? extends Tenth> extractTenth,
            Function<? super Source, ? extends Eleventh> extractEleventh,
            Function<? super Source, ? extends Twelfth> extractTwelfth,
            Function<? super Source, ? extends Thirteenth> extractThirteenth,
            Function<? super Source, ? extends Fourteenth> extractFourteenth,
            Function<? super Source, ? extends Fifteenth> extractFifteenth,
            Function<? super Source, ? extends Sixteenth> extractSixteenth,
            Function<? super Source, ? extends Seventeenth> extractSeventeenth,
            Function<? super Source, ? extends Eighteenth> extractEighteenth,
            Function<? super Source, ? extends Nineteenth> extractNineteenth,
            Function<? super Source, ? extends Twentieth> extractTwentieth) {
        return source -> Tuple.of(extractFirst.apply(source), extractSecond.apply(source), extractThird.apply(source), extractFourth.apply(source), extractFifth.apply(source), extractSixth.apply(source), extractSeventh.apply(source), extractEighth.apply(source), extractNinth.apply(source), extractTenth.apply(source), extractEleventh.apply(source), extractTwelfth.apply(source), extractThirteenth.apply(source), extractFourteenth.apply(source), extractFifteenth.apply(source), extractSixteenth.apply(source), extractSeventeenth.apply(source), extractEighteenth.apply(source), extractNineteenth.apply(source), extractTwentieth.apply(source));
    }

    public static <Source, First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst> Decomposition21<Source, First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst> of(
            Function<? super Source, ? extends First> extractFirst,
            Function<? super Source, ? extends Second> extractSecond,
            Function<? super Source, ? extends Third> extractThird,
            Function<? super Source, ? extends Fourth> extractFourth,
            Function<? super Source, ? extends Fifth> extractFifth,
            Function<? super Source, ? extends Sixth> extractSixth,
            Function<? super Source, ? extends Seventh> extractSeventh,
            Function<? super Source, ? extends Eighth> extractEighth,
            Function<? super Source, ? extends Ninth> extractNinth,
            Function<? super Source, ? extends Tenth> extractTenth,
            Function<? super Source, ? extends Eleventh> extractEleventh,
            Function<? super Source, ? extends Twelfth> extractTwelfth,
            Function<? super Source, ? extends Thirteenth> extractThirteenth,
            Function<? super Source, ? extends Fourteenth> extractFourteenth,
            Function<? super Source, ? extends Fifteenth> extractFifteenth,
            Function<? super Source, ? extends Sixteenth> extractSixteenth,
            Function<? super Source, ? extends Seventeenth> extractSeventeenth,
            Function<? super Source, ? extends Eighteenth> extractEighteenth,
            Function<? super Source, ? extends Nineteenth> extractNineteenth,
            Function<? super Source, ? extends Twentieth> extractTwentieth,
            Function<? super Source, ? extends TwentyFirst> extractTwentyFirst) {
        return source -> Tuple.of(extractFirst.apply(source), extractSecond.apply(source), extractThird.apply(source), extractFourth.apply(source), extractFifth.apply(source), extractSixth.apply(source), extractSeventh.apply(source), extractEighth.apply(source), extractNinth.apply(source), extractTenth.apply(source), extractEleventh.apply(source), extractTwelfth.apply(source), extractThirteenth.apply(source), extractFourteenth.apply(source), extractFifteenth.apply(source), extractSixteenth.apply(source), extractSeventeenth.apply(source), extractEighteenth.apply(source), extractNineteenth.apply(source), extractTwentieth.apply(source), extractTwentyFirst.apply(source));
    }

    public static <Source, First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond> Decomposition22<Source, First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond> of(
            Function<? super Source, ? extends First> extractFirst,
            Function<? super Source, ? extends Second> extractSecond,
            Function<? super Source, ? extends Third> extractThird,
            Function<? super Source, ? extends Fourth> extractFourth,
            Function<? super Source, ? extends Fifth> extractFifth,
            Function<? super Source, ? extends Sixth> extractSixth,
            Function<? super Source, ? extends Seventh> extractSeventh,
            Function<? super Source, ? extends Eighth> extractEighth,
            Function<? super Source, ? extends Ninth> extractNinth,
            Function<? super Source, ? extends Tenth> extractTenth,
            Function<? super Source, ? extends Eleventh> extractEleventh,
            Function<? super Source, ? extends Twelfth> extractTwelfth,
            Function<? super Source, ? extends Thirteenth> extractThirteenth,
            Function<? super Source, ? extends Fourteenth> extractFourteenth,
            Function<? super Source, ? extends Fifteenth> extractFifteenth,
            Function<? super Source, ? extends Sixteenth> extractSixteenth,
            Function<? super Source, ? extends Seventeenth> extractSeventeenth,
            Function<? super Source, ? extends Eighteenth> extractEighteenth,
            Function<? super Source, ? extends Nineteenth> extractNineteenth,
            Function<? super Source, ? extends Twentieth> extractTwentieth,
            Function<? super Source, ? extends TwentyFirst> extractTwentyFirst,
            Function<? super Source, ? extends TwentySecond> extractTwentySecond) {
        return source -> Tuple.of(extractFirst.apply(source), extractSecond.apply(source), extractThird.apply(source), extractFourth.apply(source), extractFifth.apply(source), extractSixth.apply(source), extractSeventh.apply(source), extractEighth.apply(source), extractNinth.apply(source), extractTenth.apply(source), extractEleventh.apply(source), extractTwelfth.apply(source), extractThirteenth.apply(source), extractFourteenth.apply(source), extractFifteenth.apply(source), extractSixteenth.apply(source), extractSeventeenth.apply(source), extractEighteenth.apply(source), extractNineteenth.apply(source), extractTwentieth.apply(source), extractTwentyFirst.apply(source), extractTwentySecond.apply(source));
    }

    public static <Source, First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond, TwentyThird> Decomposition23<Source, First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond, TwentyThird> of(
            Function<? super Source, ? extends First> extractFirst,
            Function<? super Source, ? extends Second> extractSecond,
            Function<? super Source, ? extends Third> extractThird,
            Function<? super Source, ? extends Fourth> extractFourth,
            Function<? super Source, ? extends Fifth> extractFifth,
            Function<? super Source, ? extends Sixth> extractSixth,
            Function<? super Source, ? extends Seventh> extractSeventh,
            Function<? super Source, ? extends Eighth> extractEighth,
            Function<? super Source, ? extends Ninth> extractNinth,
            Function<? super Source, ? extends Tenth> extractTenth,
            Function<? super Source, ? extends Eleventh> extractEleventh,
            Function<? super Source, ? extends Twelfth> extractTwelfth,
            Function<? super Source, ? extends Thirteenth> extractThirteenth,
            Function<? super Source, ? extends Fourteenth> extractFourteenth,
            Function<? super Source, ? extends Fifteenth> extractFifteenth,
            Function<? super Source, ? extends Sixteenth> extractSixteenth,
            Function<? super Source, ? extends Seventeenth> extractSeventeenth,
            Function<? super Source, ? extends Eighteenth> extractEighteenth,
            Function<? super Source, ? extends Nineteenth> extractNineteenth,
            Function<? super Source, ? extends Twentieth> extractTwentieth,
            Function<? super Source, ? extends TwentyFirst> extractTwentyFirst,
            Function<? super Source, ? extends TwentySecond> extractTwentySecond,
            Function<? super Source, ? extends TwentyThird> extractTwentyThird) {
        return source -> Tuple.of(extractFirst.apply(source), extractSecond.apply(source), extractThird.apply(source), extractFourth.apply(source), extractFifth.apply(source), extractSixth.apply(source), extractSeventh.apply(source), extractEighth.apply(source), extractNinth.apply(source), extractTenth.apply(source), extractEleventh.apply(source), extractTwelfth.apply(source), extractThirteenth.apply(source), extractFourteenth.apply(source), extractFifteenth.apply(source), extractSixteenth.apply(source), extractSeventeenth.apply(source), extractEighteenth.apply(source), extractNineteenth.apply(source), extractTwentieth.apply(source), extractTwentyFirst.apply(source), extractTwentySecond.apply(source), extractTwentyThird.apply(source));
    }

    public static <Source, First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond, TwentyThird, TwentyFourth> Decomposition24<Source, First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond, TwentyThird, TwentyFourth> of(
            Function<? super Source, ? extends First> extractFirst,
            Function<? super Source, ? extends Second> extractSecond,
            Function<? super Source, ? extends Third> extractThird,
            Function<? super Source, ? extends Fourth> extractFourth,
            Function<? super Source, ? extends Fifth> extractFifth,
            Function<? super Source, ? extends Sixth> extractSixth,
            Function<? super Source, ? extends Seventh> extractSeventh,
            Function<? super Source, ? extends Eighth> extractEighth,
            Function<? super Source, ? extends Ninth> extractNinth,
            Function<? super Source, ? extends Tenth> extractTenth,
            Function<? super Source, ? extends Eleventh> extractEleventh,
            Function<? super Source, ? extends Twelfth> extractTwelfth,
            Function<? super Source, ? extends Thirteenth> extractThirteenth,
            Function<? super Source, ? extends Fourteenth> extractFourteenth,
            Function<? super Source, ? extends Fifteenth> extractFifteenth,
            Function<? super Source, ? extends Sixteenth> extractSixteenth,
            Function<? super Source, ? extends Seventeenth> extractSeventeenth,
            Function<? super Source, ? extends Eighteenth> extractEighteenth,
            Function<? super Source, ? extends Nineteenth> extractNineteenth,
            Function<? super Source, ? extends Twentieth> extractTwentieth,
            Function<? super Source, ? extends TwentyFirst> extractTwentyFirst,
            Function<? super Source, ? extends TwentySecond> extractTwentySecond,
            Function<? super Source, ? extends TwentyThird> extractTwentyThird,
            Function<? super Source, ? extends TwentyFourth> extractTwentyFourth) {
        return source -> Tuple.of(extractFirst.apply(source), extractSecond.apply(source), extractThird.apply(source), extractFourth.apply(source), extractFifth.apply(source), extractSixth.apply(source), extractSeventh.apply(source), extractEighth.apply(source), extractNinth.apply(source), extractTenth.apply(source), extractEleventh.apply(source), extractTwelfth.apply(source), extractThirteenth.apply(source), extractFourteenth.apply(source), extractFifteenth.apply(source), extractSixteenth.apply(source), extractSeventeenth.apply(source), extractEighteenth.apply(source), extractNineteenth.apply(source), extractTwentieth.apply(source), extractTwentyFirst.apply(source), extractTwentySecond.apply(source), extractTwentyThird.apply(source), extractTwentyFourth.apply(source));
    }

    public static <Source, First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond, TwentyThird, TwentyFourth, TwentyFifth> Decomposition25<Source, First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond, TwentyThird, TwentyFourth, TwentyFifth> of(
            Function<? super Source, ? extends First> extractFirst,
            Function<? super Source, ? extends Second> extractSecond,
            Function<? super Source, ? extends Third> extractThird,
            Function<? super Source, ? extends Fourth> extractFourth,
            Function<? super Source, ? extends Fifth> extractFifth,
            Function<? super Source, ? extends Sixth> extractSixth,
            Function<? super Source, ? extends Seventh> extractSeventh,
            Function<? super Source, ? extends Eighth> extractEighth,
            Function<? super Source, ? extends Ninth> extractNinth,
            Function<? super Source, ? extends Tenth> extractTenth,
            Function<? super Source, ? extends Eleventh> extractEleventh,
            Function<? super Source, ? extends Twelfth> extractTwelfth,
            Function<? super Source, ? extends Thirteenth> extractThirteenth,
            Function<? super Source, ? extends Fourteenth> extractFourteenth,
            Function<? super Source, ? extends Fifteenth> extractFifteenth,
            Function<? super Source, ? extends Sixteenth> extractSixteenth,
            Function<? super Source, ? extends Seventeenth> extractSeventeenth,
            Function<? super Source, ? extends Eighteenth> extractEighteenth,
            Function<? super Source, ? extends Nineteenth> extractNineteenth,
            Function<? super Source, ? extends Twentieth> extractTwentieth,
            Function<? super Source, ? extends TwentyFirst> extractTwentyFirst,
            Function<? super Source, ? extends TwentySecond> extractTwentySecond,
            Function<? super Source, ? extends TwentyThird> extractTwentyThird,
            Function<? super Source, ? extends TwentyFourth> extractTwentyFourth,
            Function<? super Source, ? extends TwentyFifth> extractTwentyFifth) {
        return source -> Tuple.of(extractFirst.apply(source), extractSecond.apply(source), extractThird.apply(source), extractFourth.apply(source), extractFifth.apply(source), extractSixth.apply(source), extractSeventh.apply(source), extractEighth.apply(source), extractNinth.apply(source), extractTenth.apply(source), extractEleventh.apply(source), extractTwelfth.apply(source), extractThirteenth.apply(source), extractFourteenth.apply(source), extractFifteenth.apply(source), extractSixteenth.apply(source), extractSeventeenth.apply(source), extractEighteenth.apply(source), extractNineteenth.apply(source), extractTwentieth.apply(source), extractTwentyFirst.apply(source), extractTwentySecond.apply(source), extractTwentyThird.apply(source), extractTwentyFourth.apply(source), extractTwentyFifth.apply(source));
    }

    public static <Source, First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond, TwentyThird, TwentyFourth, TwentyFifth, TwentySixth> Decomposition26<Source, First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond, TwentyThird, TwentyFourth, TwentyFifth, TwentySixth> of(
            Function<? super Source, ? extends First> extractFirst,
            Function<? super Source, ? extends Second> extractSecond,
            Function<? super Source, ? extends Third> extractThird,
            Function<? super Source, ? extends Fourth> extractFourth,
            Function<? super Source, ? extends Fifth> extractFifth,
            Function<? super Source, ? extends Sixth> extractSixth,
            Function<? super Source, ? extends Seventh> extractSeventh,
            Function<? super Source, ? extends Eighth> extractEighth,
            Function<? super Source, ? extends Ninth> extractNinth,
            Function<? super Source, ? extends Tenth> extractTenth,
            Function<? super Source, ? extends Eleventh> extractEleventh,
            Function<? super Source, ? extends Twelfth> extractTwelfth,
            Function<? super Source, ? extends Thirteenth> extractThirteenth,
            Function<? super Source, ? extends Fourteenth> extractFourteenth,
            Function<? super Source, ? extends Fifteenth> extractFifteenth,
            Function<? super Source, ? extends Sixteenth> extractSixteenth,
            Function<? super Source, ? extends Seventeenth> extractSeventeenth,
            Function<? super Source, ? extends Eighteenth> extractEighteenth,
            Function<? super Source, ? extends Nineteenth> extractNineteenth,
            Function<? super Source, ? extends Twentieth> extractTwentieth,
            Function<? super Source, ? extends TwentyFirst> extractTwentyFirst,
            Function<? super Source, ? extends TwentySecond> extractTwentySecond,
            Function<? super Source, ? extends TwentyThird> extractTwentyThird,
            Function<? super Source, ? extends TwentyFourth> extractTwentyFourth,
            Function<? super Source, ? extends TwentyFifth> extractTwentyFifth,
            Function<? super Source, ? extends TwentySixth> extractTwentySixth) {
        return source -> Tuple.of(extractFirst.apply(source), extractSecond.apply(source), extractThird.apply(source), extractFourth.apply(source), extractFifth.apply(source), extractSixth.apply(source), extractSeventh.apply(source), extractEighth.apply(source), extractNinth.apply(source), extractTenth.apply(source), extractEleventh.apply(source), extractTwelfth.apply(source), extractThirteenth.apply(source), extractFourteenth.apply(source), extractFifteenth.apply(source), extractSixteenth.apply(source), extractSeventeenth.apply(source), extractEighteenth.apply(source), extractNineteenth.apply(source), extractTwentieth.apply(source), extractTwentyFirst.apply(source), extractTwentySecond.apply(source), extractTwentyThird.apply(source), extractTwentyFourth.apply(source), extractTwentyFifth.apply(source), extractTwentySixth.apply(source));
    }
}
