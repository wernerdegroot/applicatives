package nl.wernerdegroot.applicatives.processor.generator;

import nl.wernerdegroot.applicatives.processor.domain.ClassName;
import nl.wernerdegroot.applicatives.processor.domain.Modifier;
import nl.wernerdegroot.applicatives.processor.domain.PackageName;
import nl.wernerdegroot.applicatives.processor.domain.TypeParameterName;
import nl.wernerdegroot.applicatives.processor.domain.containing.ContainingClass;
import nl.wernerdegroot.applicatives.processor.domain.containing.ContainingPackage;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toSet;
import static nl.wernerdegroot.applicatives.processor.Utils.modifiers;
import static nl.wernerdegroot.applicatives.processor.domain.Modifier.*;
import static nl.wernerdegroot.applicatives.processor.domain.type.Type.SERIALIZABLE;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ContainingClassGeneratorTest {

    private final TypeParameterName A = TypeParameterName.of("A");
    private final TypeParameterName B = TypeParameterName.of("B");
    private final TypeParameterName C = TypeParameterName.of("C");

    @Test
    public void givenNestedClass() {
        ContainingClass containingClass = ContainingClass.of(
                ContainingClass.of(
                        ContainingClass.of(
                                ContainingPackage.of(PackageName.of("nl.wernerdegroot")),
                                modifiers(PUBLIC),
                                ClassName.of("OuterClass"),
                                asList(A.asTypeParameter())
                        ),
                        modifiers(PRIVATE, STATIC),
                        ClassName.of("StaticInnerClass"),
                        asList(B.asTypeParameter(), C.extending(SERIALIZABLE))
                ),
                modifiers(),
                ClassName.of("InnerClass"),
                emptyList()
        );

        String expected = "nl.wernerdegroot.OuterClass<A>.StaticInnerClass<B, C extends java.io.Serializable>.InnerClass";
        String toVerify = ContainingClassGenerator.generateFrom(containingClass);

        assertEquals(expected, toVerify);
    }
}
