package nl.wernerdegroot.applicatives.processor.generator;

import nl.wernerdegroot.applicatives.processor.domain.TypeParameterName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static nl.wernerdegroot.applicatives.processor.domain.Modifier.*;
import static nl.wernerdegroot.applicatives.processor.domain.type.Type.*;
import static nl.wernerdegroot.applicatives.processor.generator.MethodGenerator.method;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MethodGeneratorTest {

    private final TypeParameterName T = TypeParameterName.of("T");
    private final TypeParameterName U = TypeParameterName.of("U");

    @Test
    public void givenAbstractMethodWithReturnType() {
        List<String> toVerify = method()
                .withModifiers(PUBLIC, ABSTRACT)
                .withTypeParameters(T.extending(OBJECT), U.asTypeParameter())
                .withReturnType(OPTIONAL.with(U))
                .withName("map")
                .withParameter(OPTIONAL.with(T), "optional")
                .withParameter(FUNCTION.with(T, U), "fn")
                .lines();

        List<String> expected = singletonList(
                "public abstract <T, U> java.util.Optional<U> map(java.util.Optional<T> optional, java.util.function.Function<T, U> fn);"
        );

        assertEquals(expected, toVerify);
    }

    @Test
    public void givenConcreteMethod() {
        List<String> toVerify = method()
                .withModifiers(PRIVATE, STATIC)
                .withName("notify")
                .withBody("System.out.println(\"Consider yourself notified!\");")
                .lines();

        List<String> expected = asList(
                "private static void notify() {",
                "    System.out.println(\"Consider yourself notified!\");",
                "}"
        );

        assertEquals(expected, toVerify);
    }

    @Test
    public void givenConcreteMethodWithTwoLineBody() {
        List<String> toVerify = method()
                .withModifiers(PUBLIC)
                .withReturnType(STRING)
                .withName("toString")
                .withBody("String result = \"Helpful description\";")
                .withReturnStatement("result")
                .lines();

        List<String> expected = asList(
                "public java.lang.String toString() {",
                "    String result = \"Helpful description\";",
                "    return result;",
                "}"
        );

        assertEquals(expected, toVerify);
    }
}
