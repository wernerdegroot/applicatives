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
    public void givenAbstractMethod() {
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
                .withTypeParameters(T.extending(OBJECT), U.asTypeParameter())
                .withReturnType(OPTIONAL.with(U))
                .withName("map")
                .withParameter(OPTIONAL.with(T), "optional")
                .withParameter(FUNCTION.with(T, U), "fn")
                .withBody("java.util.Optional<U> result = optional.map(fn);")
                .withReturnStatement("result")
                .lines();

        List<String> expected = asList(
                "private static <T, U> java.util.Optional<U> map(java.util.Optional<T> optional, java.util.function.Function<T, U> fn) {",
                "    java.util.Optional<U> result = optional.map(fn);",
                "    return result;",
                "}"
        );

        assertEquals(expected, toVerify);
    }
}
