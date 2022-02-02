package nl.wernerdegroot.applicatives.processor.generator;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static nl.wernerdegroot.applicatives.processor.generator.Constants.EMPTY_LINE;
import static nl.wernerdegroot.applicatives.processor.generator.Constants.INDENT;

public class BodyGenerator {

    private List<String> lines = new ArrayList<>();

    public BodyGenerator indent() {
        lines = lines
                .stream()
                .map(line -> EMPTY_LINE.equals(line) ? line : INDENT + line)
                .collect(toList());
        return this;
    }

    public List<String> lines() {
        return lines;
    }

    public interface HasBodyGenerator<This> extends HasThis<This> {

        BodyGenerator getBodyGenerator();

        default This withBody(List<String> lines) {
            lines.forEach(getBodyGenerator().lines::add);
            return getThis();
        }

        default This withBody(String... lines) {
            return withBody(asList(lines));
        }
    }
}
