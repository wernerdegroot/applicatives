package nl.wernerdegroot.applicatives.processor.domain.containing;

import nl.wernerdegroot.applicatives.processor.domain.*;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toSet;
import static nl.wernerdegroot.applicatives.processor.domain.Modifier.*;
import static org.junit.jupiter.api.Assertions.*;

public class ContainingClassTest {

    private final PackageName packageName = new PackageName("nl.wernerdegroot.applicatives");
    private final ContainingPackage containingPackage = new ContainingPackage(packageName);
    TypeParameter T = new TypeParameter(new TypeParameterName("T"), emptyList());
    TypeParameter U = new TypeParameter(new TypeParameterName("U"), emptyList());

    @Test
    public void of() {
        ContainingClass expected = new ContainingClass(
                containingPackage,
                modifiers(PUBLIC, ABSTRACT),
                new ClassName("Erudite"),
                asList(T, U)
        );

        ContainingClass toVerify = ContainingClass.of(
                containingPackage,
                modifiers(PUBLIC, ABSTRACT),
                new ClassName("Erudite"),
                asList(T, U)
        );

        assertEquals(expected, toVerify);
    }

    @Test
    public void withoutTypeParameters() {
        ContainingClass expected = new ContainingClass(
                containingPackage,
                modifiers(PUBLIC),
                new ClassName("Erudite"),
                emptyList()
        );

        ContainingClass toVerify = ContainingClass.of(
                packageName,
                new ClassName("Erudite")
        );

        assertEquals(expected, toVerify);
    }

    // Test for `match` not necessary. If it compiles, it must work.

    @Test
    public void getPackageName() {
        PackageName expected = packageName;
        PackageName toVerify = new ContainingClass(
                containingPackage,
                modifiers(PUBLIC, ABSTRACT),
                new ClassName("Erudite"),
                asList(T, U)
        ).getPackageName();

        assertEquals(expected, toVerify);
    }

    @Test
    public void isPackage() {
        assertFalse(
                new ContainingClass(
                        containingPackage,
                        modifiers(PUBLIC, ABSTRACT),
                        new ClassName("Erudite"),
                        asList(T, U)
                ).isPackage()
        );
    }

    @Test
    public void isClass() {
        assertTrue(
                new ContainingClass(
                        containingPackage,
                        modifiers(PUBLIC, ABSTRACT),
                        new ClassName("Erudite"),
                        asList(T, U)
                ).isClass()
        );
    }

    @Test
    public void isOuterClassGivenOuterClass() {
        assertTrue(
                new ContainingClass(
                        containingPackage,
                        modifiers(PUBLIC, ABSTRACT),
                        new ClassName("Erudite"),
                        asList(T, U)
                ).isOuterClass()
        );
    }

    @Test
    public void isOuterClassGivenStaticInnerClass() {
        assertFalse(
                new ContainingClass(
                        new ContainingClass(
                                containingPackage,
                                modifiers(PUBLIC),
                                new ClassName("Profuse"),
                                asList(T)
                        ),
                        modifiers(PRIVATE, STATIC),
                        new ClassName("Erudite"),
                        asList(U)
                ).isOuterClass()
        );
    }

    @Test
    public void isStaticInnerClassGivenOuterClass() {
        assertFalse(
                new ContainingClass(
                        containingPackage,
                        modifiers(PUBLIC, ABSTRACT),
                        new ClassName("Erudite"),
                        asList(T, U)
                ).isStaticInnerClass()
        );
    }

    @Test
    public void isStaticInnerClassGivenStaticInnerClass() {
        assertTrue(
                new ContainingClass(
                        new ContainingClass(
                                containingPackage,
                                modifiers(PUBLIC),
                                new ClassName("Profuse"),
                                asList(T)
                        ),
                        modifiers(PRIVATE, STATIC),
                        new ClassName("Erudite"),
                        asList(U)
                ).isStaticInnerClass()
        );
    }

    private Set<Modifier> modifiers(Modifier... modifiers) {
        return Stream.of(modifiers).collect(toSet());
    }
}
