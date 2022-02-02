package nl.wernerdegroot.applicatives.processor.generator;

import nl.wernerdegroot.applicatives.processor.domain.Modifier;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.asList;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.joining;
import static nl.wernerdegroot.applicatives.processor.generator.Constants.SPACE;

public class ModifiersGenerator {

    private Set<Modifier> modifiers = new HashSet<>();

    public boolean isEmpty() {
        return modifiers.isEmpty();
    }

    public String generate() {
        return modifiers
                .stream()
                .sorted(comparing(Modifier::ordinal))
                .map(Modifier::toString)
                .collect(joining(SPACE));
    }

    public interface HasModifiersGenerator<This> extends HasThis<This> {

        ModifiersGenerator getModifiersGenerator();

        default This withModifiers(Collection<Modifier> modifiers) {
            getModifiersGenerator().modifiers.addAll(modifiers);
            return getThis();
        }

        default This withModifiers(Modifier... modifiers) {
            return this.withModifiers(asList(modifiers));
        }
    }
}
