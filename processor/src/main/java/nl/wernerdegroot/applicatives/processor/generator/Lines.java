package nl.wernerdegroot.applicatives.processor.generator;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import static java.util.Arrays.asList;
import static nl.wernerdegroot.applicatives.processor.generator.Constants.EMPTY_LINE;

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

    public static <E> Collector<E, ?, List<String>> collector(Function<E, List<String>> toLines) {
        return Collector.of(
                ArrayList::new,
                (lines, element) -> {
                    if (!lines.isEmpty()) {
                        lines.add(EMPTY_LINE);
                    }
                    lines.addAll(toLines.apply(element));
                },
                (left, right) -> {
                    if (!left.isEmpty() && !right.isEmpty()) {
                        left.add(EMPTY_LINE);
                    }
                    left.addAll(right);
                    return left;
                }
        );
    }

    @Override
    public String get(int index) {
        return lines.get(index);
    }

    @Override
    public int size() {
        return lines.size();
    }

    @Override
    public String set(int index, String element) {
        return lines.set(index, element);
    }

    @Override
    public void add(int index, String element) {
        lines.add(index, element);
    }

    @Override
    public String remove(int index) {
        return lines.remove(index);
    }

    public Lines append(List<String> toAppend) {
        lines.addAll(toAppend);
        return this;
    }

    public Lines append(String... lines) {
        return append(asList(lines));
    }
}
