package nl.wernerdegroot.applicatives.processor.converters;

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
import static nl.wernerdegroot.applicatives.processor.converters.TestProcessor.doTest;
import static nl.wernerdegroot.applicatives.processor.domain.Modifier.*;
import static nl.wernerdegroot.applicatives.processor.domain.type.Type.SERIALIZABLE;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ClassConverterTest {

    private final TypeParameterName A = TypeParameterName.of("A");
    private final TypeParameterName B = TypeParameterName.of("B");
    private final TypeParameterName C = TypeParameterName.of("C");

    @Test
    public void toDomain() {
        doTest("BunchOfNestedClasses", element -> {

            ContainingClass expected = ContainingClass.of(
                    ContainingClass.of(
                            ContainingClass.of(
                                    ContainingPackage.of(PackageName.of("nl.wernerdegroot.applicatives.processor.converters.subjects")),
                                    modifiers(PUBLIC),
                                    ClassName.of("BunchOfNestedClasses"),
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

            ContainingClass toVerify = ContainingClassConverter.toDomain(element);

            assertEquals(expected, toVerify);
        });
    }

    private Set<Modifier> modifiers(Modifier... modifiers) {
        return Stream.of(modifiers).collect(toSet());
    }
}
