package nl.wernerdegroot.applicatives.processor.converters;

import nl.wernerdegroot.applicatives.processor.domain.*;
import nl.wernerdegroot.applicatives.processor.domain.type.TypeArgument;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toSet;
import static nl.wernerdegroot.applicatives.processor.converters.TestProcessor.doTest;
import static nl.wernerdegroot.applicatives.processor.domain.Modifier.*;
import static nl.wernerdegroot.applicatives.processor.domain.type.Type.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

// This class also indirectly tests all the other converters.
public class MethodConverterTest {

    private final TypeParameterName D = TypeParameterName.of("D");
    private final TypeParameterName E = TypeParameterName.of("E");

    @Test
    public void shouldExtractAnnotationsCorrectly() {
        doTest("ComplexMethod", element -> {
            Method method = MethodConverter.toDomain(element);
            assertEquals(
                    Stream.of(
                            FullyQualifiedName.of("nl.wernerdegroot.applicatives.processor.converters.TestAnnotation"),
                            FullyQualifiedName.of("java.lang.Deprecated"),
                            FullyQualifiedName.of("java.lang.SuppressWarnings")
                    ).collect(toSet()),
                    method.getAnnotations()
            );
        });
    }

    @Test
    public void shouldExtractModifiersCorrectly() {
        doTest("ComplexMethod", element -> {
            Method method = MethodConverter.toDomain(element);
            assertEquals(
                    modifiers(
                            PUBLIC,
                            STRICTFP,
                            SYNCHRONIZED
                    ),
                    method.getModifiers()
            );
        });
    }

    @Test
    public void shouldExtractTypeParametersCorrectly() {
        doTest("ComplexMethod", element -> {
            Method method = MethodConverter.toDomain(element);
            assertEquals(
                    asList(
                            D.extending(OBJECT),
                            E.extending(NUMBER, COMPARABLE.with(D))
                    ),
                    method.getTypeParameters()
            );
        });
    }

    @Test
    public void shouldExtractReturnTypeCorrectly() {
        doTest("ComplexMethod", element -> {
            Method method = MethodConverter.toDomain(element);
            assertEquals(Optional.empty(), method.getReturnType());
        });
    }

    @Test
    public void shouldExtractNameCorrectly() {
        doTest("ComplexMethod", element -> {
            Method method = MethodConverter.toDomain(element);
            assertEquals("someMethod", method.getName());
        });
    }

    @Test
    public void shouldExtractParametersCorrectly() {
        FullyQualifiedName StaticInnerClass = FullyQualifiedName.of("nl.wernerdegroot.applicatives.processor.converters.subjects.ComplexMethod.StaticInnerClass");

        doTest("ComplexMethod", element -> {
            Method method = MethodConverter.toDomain(element);
            assertEquals(
                    asList(
                            Parameter.of(INT, "primitive"),
                            Parameter.of(BOOLEAN, "object"),
                            Parameter.of(CHAR.array().array(), "twoDimensionalPrimitiveArray"),
                            Parameter.of(LIST.with(TypeArgument.wildcard()), "listOfWildcardsWithoutUpperOrLowerBound"),
                            Parameter.of(SET.with(SERIALIZABLE.covariant()), "setOfWildcardsWithUpperBound"),
                            Parameter.of(COLLECTION.with(NUMBER.contravariant()), "collectionOfWildcardsWithLowerBound"),
                            Parameter.of(StaticInnerClass.with(E.asType().invariant(), THREAD.invariant()), "nestedClasses"),
                            Parameter.of(STRING.array(), "varArgs")
                    ),
                    method.getParameters()
            );
        });
    }

    private Set<Modifier> modifiers(Modifier... modifiers) {
        return Stream.of(modifiers).collect(toSet());
    }
}
