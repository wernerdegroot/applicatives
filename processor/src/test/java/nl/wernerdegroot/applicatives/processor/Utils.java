package nl.wernerdegroot.applicatives.processor;

import nl.wernerdegroot.applicatives.processor.domain.Modifier;

import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;

public class Utils {

    public static Set<Modifier> modifiers(Modifier... modifiers) {
        return Stream.of(modifiers).collect(toSet());
    }
}
