package nl.wernerdegroot.applicatives.processor.converters.subjects;

import nl.wernerdegroot.applicatives.processor.converters.TestAnnotation;

@TestAnnotation
public class ClassWithManyMethods implements InterfaceWithManyMethods {

    @Deprecated
    static void deprecated() {

    }

    @Override
    public String override(int multiplier) {
        return "Override times " + multiplier + "!";
    }

    public int noAnnotations() {
        return 42;
    }

    @SuppressWarnings({"some", "warnings"})
    public String[] suppressWarnings(String more) {
        return new String[]{more, "warnings"};
    }
}
