package lambdas;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.util.Arrays.asList;

class CoolThing {
    public static void doCoolThings() {
        System.out.println("Very cool");
    }
}

public class Test {

    static int i = 0;


    public static void main(String[] args) {
        Integer x = 43;
        giveMeBooles(true, true);
    }

    public static void giveMeBooles(Boolean... i) {

    }

    public static void giveMeBooles(boolean... i) {

    }
}
