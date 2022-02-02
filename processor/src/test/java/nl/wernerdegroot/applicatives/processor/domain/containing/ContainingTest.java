package nl.wernerdegroot.applicatives.processor.domain.containing;

import nl.wernerdegroot.applicatives.processor.domain.*;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toSet;
import static nl.wernerdegroot.applicatives.processor.domain.Modifier.ABSTRACT;
import static nl.wernerdegroot.applicatives.processor.domain.Modifier.PUBLIC;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ContainingTest {

    @Test
    public void containingClass() {
        ContainingPackage containingPackage = new ContainingPackage(new PackageName("nl.wernerdegroot.applicatives"));
        ClassName className = new ClassName("Erudite");
        TypeParameter T = new TypeParameter(new TypeParameterName("T"), emptyList());
        TypeParameter U = new TypeParameter(new TypeParameterName("U"), emptyList());

        ContainingClass expected = new ContainingClass(
                containingPackage,
                modifiers(PUBLIC, ABSTRACT),
                className,
                asList(T, U)
        );

        ContainingClass toVerify = containingPackage
                .containingClass(
                        modifiers(PUBLIC, ABSTRACT),
                        className,
                        T,
                        U
                );

        assertEquals(expected, toVerify);
    }

    private Set<Modifier> modifiers(Modifier... modifiers) {
        return Stream.of(modifiers).collect(toSet());
    }
}
