package nl.wernerdegroot.applicatives.processor.generator;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.asList;

public class Lines extends AbstractList<String> {

    private final List<String> lines = new ArrayList<>();

    public Lines() {
    }

    public Lines(List<String> initialLines) {
        lines.addAll(initialLines);
    }

    public static Lines of(List<String> initialLines) {
        return new Lines(initialLines);
    }

    public static Lines lines() {
        return new Lines();
    }

    @Override
    public String get(int index) {
        return lines.get(index);
    }

    @Override
    public int size() {
        return lines.size();
    }

    public Lines append(List<String> toAppend) {
        lines.addAll(toAppend);
        return this;
    }

    public Lines append(String... lines) {
        return append(asList(lines));
    }
}
