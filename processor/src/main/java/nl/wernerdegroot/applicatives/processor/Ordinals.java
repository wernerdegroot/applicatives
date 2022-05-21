package nl.wernerdegroot.applicatives.processor;

import java.util.List;

import static java.util.Arrays.asList;

public class Ordinals {

    public static final List<String> ORDINALS = asList(
            "first",
            "second",
            "third",
            "fourth",
            "fifth",
            "sixth",
            "seventh",
            "eighth",
            "ninth",
            "tenth",
            "eleventh",
            "twelfth",
            "thirteenth",
            "fourteenth",
            "fifteenth",
            "sixteenth",
            "seventeenth",
            "eighteenth",
            "nineteenth",
            "twentieth",
            "twentyFirst",
            "twentySecond",
            "twentyThird",
            "twentyFourth",
            "twentyFifth",
            "twentySixth"
    );

    public static String witherForIndex(int index) {
        String ordinal = ORDINALS.get(index);
        return "with" + ordinal.substring(0, 1).toUpperCase() + ordinal.substring(1);
    }

    public static String getterForIndex(int index) {
        String ordinal = ORDINALS.get(index);
        return "get" + ordinal.substring(0, 1).toUpperCase() + ordinal.substring(1);
    }
}
