package nl.wernerdegroot.applicatives.processor.generator;

import nl.wernerdegroot.applicatives.processor.domain.FullyQualifiedName;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
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

        default This withObjectPath(FullyQualifiedName... components) {
            return withObjectPath(Stream.of(components).map(FullyQualifiedName::raw).collect(toList()));
        }
    }
}
