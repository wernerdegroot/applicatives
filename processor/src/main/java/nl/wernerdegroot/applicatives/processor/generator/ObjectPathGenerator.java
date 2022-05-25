package nl.wernerdegroot.applicatives.processor.generator;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static nl.wernerdegroot.applicatives.processor.generator.Constants.PERIOD;

public class ObjectPathGenerator {

    private List<String> components = new ArrayList<>();

    public String generate() {
        return String.join(PERIOD, components);
    }

    public interface HasObjectPathGenerator<This> extends HasThis<This> {

        ObjectPathGenerator getObjectPathGenerator();

        default This withObjectPath(List<String> components) {
            getObjectPathGenerator().components.addAll(components);
            return getThis();
        }

        default This withObjectPath(String... components) {
            return withObjectPath(asList(components));
        }
    }
}
