package nl.wernerdegroot.applicatives.processor.converters;

import com.google.auto.service.AutoService;
import com.google.testing.compile.Compiler;
import com.google.testing.compile.JavaFileObjects;
import nl.wernerdegroot.applicatives.processor.domain.*;
import nl.wernerdegroot.applicatives.processor.domain.containing.ContainingClass;
import nl.wernerdegroot.applicatives.processor.domain.type.TypeArgument;
import org.junit.jupiter.api.Test;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toSet;
import static nl.wernerdegroot.applicatives.processor.domain.Modifier.*;
import static nl.wernerdegroot.applicatives.processor.domain.type.Type.*;
import static org.junit.jupiter.api.Assertions.*;

// This class also indirectly tests all the other converters.
public class MethodConverterTest {

    private final TypeParameterName A = TypeParameterName.of("A");
    private final TypeParameterName B = TypeParameterName.of("B");
    private final TypeParameterName C = TypeParameterName.of("C");

    @Test
    public void shouldExtractModifiersCorrectly() {
        doTest(element -> {
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
    public void shouldExtractGenericsCorrectly() {
        doTest(element -> {
            Method method = MethodConverter.toDomain(element);
            assertEquals(
                    asList(
                            A.extending(OBJECT),
                            B.extending(NUMBER, COMPARABLE.with(A))
                    ),
                    method.getTypeParameters()
            );
        });
    }

    @Test
    public void shouldExtractReturnTypeCorrectly() {
        doTest(element -> {
            Method method = MethodConverter.toDomain(element);
            assertEquals(Optional.empty(), method.getReturnType());
        });
    }

    @Test
    public void shouldExtractNameCorrectly() {
        doTest(element -> {
            Method method = MethodConverter.toDomain(element);
            assertEquals("someMethod", method.getName());
        });
    }

    @Test
    public void shouldExtractParametersCorrectly() {
        FullyQualifiedName StaticInnerClass = FullyQualifiedName.of("nl.wernerdegroot.applicatives.processor.converters.TestClass.StaticInnerClass");

        doTest(element -> {
            Method method = MethodConverter.toDomain(element);
            assertEquals(
                    asList(
                            Parameter.of(INT, "primitive"),
                            Parameter.of(BOOLEAN, "object"),
                            Parameter.of(CHAR.array().array(), "twoDimensionalPrimitiveArray"),
                            Parameter.of(LIST.with(TypeArgument.wildcard()), "listOfWildcardsWithoutUpperOrLowerBound"),
                            Parameter.of(SET.with(SERIALIZABLE.covariant()), "setOfWildcardsWithUpperBound"),
                            Parameter.of(COLLECTION.with(NUMBER.contravariant()), "collectionOfWildcardsWithLowerBound"),
                            Parameter.of(StaticInnerClass.with(B.asType().invariant(), THREAD.invariant()), "nestedClasses")
                    ),
                    method.getParameters()
            );
        });
    }

    @Test
    public void shouldExtractContainingClassCorrectly() {
        doTest(element -> {
            Method method = MethodConverter.toDomain(element);
            ContainingClass expected = PackageName.of("nl.wernerdegroot.applicatives.processor.converters")
                    .asPackage()
                    .containingClass(
                            modifiers(PUBLIC),
                            ClassName.of("TestClass"),
                            C.asTypeParameter()
                    )
                    .containingClass(
                            modifiers(PUBLIC, STATIC),
                            ClassName.of("StaticInnerClass"),
                            B.asTypeParameter(),
                            C.extending(RUNNABLE)
                    )
                    .containingClass(
                            modifiers(PUBLIC),
                            ClassName.of("InnerClass"),
                            A.asTypeParameter(),
                            C.extending(SERIALIZABLE)
                    );

            assertEquals(expected, method.getContainingClass());
        });
    }

    private Set<Modifier> modifiers(Modifier... modifiers) {
        return Stream.of(modifiers).collect(toSet());
    }

    private static final String ANNOTATION_CLASS_NAME = "nl.wernerdegroot.applicatives.processor.converters.TestAnnotation";

    {
        try {
            Class.forName(ANNOTATION_CLASS_NAME);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(String.format("Can't find annotation class %s", ANNOTATION_CLASS_NAME), e);
        }
    }

    @SupportedAnnotationTypes(ANNOTATION_CLASS_NAME)
    @SupportedSourceVersion(SourceVersion.RELEASE_8)
    @AutoService(Processor.class)
    private static class TestProcessor extends AbstractProcessor {
        private final Consumer<Element> consumer;
        private boolean called = false;

        public TestProcessor(Consumer<Element> consumer) {
            this.consumer = consumer;
        }

        @Override
        public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
            called = true;
            for (TypeElement annotation : annotations) {
                Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(annotation);
                if (annotatedElements.isEmpty()) {
                    fail(String.format("No elements annotated with %s", ANNOTATION_CLASS_NAME));
                } else {
                    annotatedElements.forEach(consumer);
                }
            }
            return false;
        }

        public boolean isCalled() {
            return called;
        }
    }

    private void doTest(Consumer<Element> consumer) {
        try {
            Path sourceFilePath = Paths.get("src/test/java/nl/wernerdegroot/applicatives/processor/converters/TestClass.java").toAbsolutePath();
            if (!sourceFilePath.toFile().exists()) {
                fail(String.format("Can't find source file %s", sourceFilePath));
            }
            JavaFileObject file = JavaFileObjects.forResource(sourceFilePath.toUri().toURL());
            TestProcessor testProcessor = new TestProcessor(consumer);
            Compiler.javac().withProcessors(testProcessor).compile(file);
            assertTrue(testProcessor.isCalled());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
