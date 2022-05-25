package nl.wernerdegroot.applicatives.processor.generator;

import nl.wernerdegroot.applicatives.processor.domain.TypeParameterName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static nl.wernerdegroot.applicatives.processor.domain.Modifier.ABSTRACT;
import static nl.wernerdegroot.applicatives.processor.domain.Modifier.PUBLIC;
import static nl.wernerdegroot.applicatives.processor.domain.type.Type.OBJECT;
import static nl.wernerdegroot.applicatives.processor.generator.ClassOrInterfaceGenerator.classOrInterface;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ClassOrInterfaceGeneratorTest {

    private final TypeParameterName A = TypeParameterName.of("A");
    private final TypeParameterName B = TypeParameterName.of("B");

    @Test
    public void givenInterfaceWithoutModifiersButWithTypeParameters() {
        List<String> toVerify = classOrInterface()
                .asInterface()
                .withName("Pair")
                .withTypeParameters(A.extending(OBJECT))
                .withTypeParameters(B)
                .withBody(
                        "A getLeft();",
                        "B getRight();"
                )
                .lines();

        List<String> expected = asList(
                "interface Pair<A, B> {",
                "",
                "    A getLeft();",
                "    B getRight();",
                "",
                "}"
        );

        assertEquals(expected, toVerify);
    }

    @Test
    public void givenClassWithModifiersButWithoutTypeParameters() {
        List<String> toVerify = classOrInterface()
                .asClass()
                .withModifiers(PUBLIC, ABSTRACT)
                .withName("Device")
                .withBody(
                        "public int capacity = 34;",
                        "public boolean hasPower = true;"
                )
                .lines();

        List<String> expected = asList(
                "public abstract class Device {",
                "",
                "    public int capacity = 34;",
                "    public boolean hasPower = true;",
                "",
                "}"
        );

        assertEquals(expected, toVerify);
    }
}
