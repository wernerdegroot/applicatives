package nl.wernerdegroot.applicatives.processor.converters;

import nl.wernerdegroot.applicatives.processor.domain.FullyQualifiedName;
import nl.wernerdegroot.applicatives.processor.domain.Method;
import org.junit.jupiter.api.Test;

import javax.lang.model.element.TypeElement;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Collections.*;
import static nl.wernerdegroot.applicatives.processor.converters.TestProcessor.doTest;
import static nl.wernerdegroot.applicatives.processor.domain.Modifier.PUBLIC;
import static nl.wernerdegroot.applicatives.processor.domain.Modifier.STATIC;
import static nl.wernerdegroot.applicatives.processor.domain.type.Type.STRING;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@TestAnnotation
public class EnclosedMethodConverterTest {

    public static FullyQualifiedName DEPRECATED = FullyQualifiedName.of(Deprecated.class.getCanonicalName());
    public static FullyQualifiedName SUPPRESS_WARNINGS = FullyQualifiedName.of(SuppressWarnings.class.getCanonicalName());

    @Test
    public void givenManyMethods() {
        doTest("ClassWithManyMethods", element -> {
            if (!(element instanceof TypeElement)) {
                fail();
            } else {
                TypeElement typeElement = (TypeElement) element;

                List<Method> toVerify = EnclosedMethodsConverter.toDomain(typeElement, DEPRECATED, SUPPRESS_WARNINGS);
                List<Method> expected = asList(
                        Method.of(
                                singleton(DEPRECATED),
                                singleton(STATIC),
                                emptyList(),
                                Optional.empty(),
                                "deprecated",
                                emptyList()
                        ),
                        Method.of(
                                singleton(SUPPRESS_WARNINGS),
                                singleton(PUBLIC),
                                emptyList(),
                                Optional.of(STRING.array()),
                                "suppressWarnings",
                                singletonList(STRING.withName("more"))
                        )
                );

                assertEquals(expected, toVerify);
            }
        });
    }
}
