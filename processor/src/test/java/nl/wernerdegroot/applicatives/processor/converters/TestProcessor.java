package nl.wernerdegroot.applicatives.processor.converters;

import com.google.auto.service.AutoService;
import com.google.testing.compile.Compiler;
import com.google.testing.compile.JavaFileObjects;
import org.junit.jupiter.api.Assertions;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@SupportedAnnotationTypes(TestProcessor.ANNOTATION_CLASS_NAME)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class TestProcessor extends AbstractProcessor {

    public static final String ANNOTATION_CLASS_NAME = "nl.wernerdegroot.applicatives.processor.converters.TestAnnotation";

    static {
        try {
            Class.forName(ANNOTATION_CLASS_NAME);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(String.format("Can't find annotation class %s", ANNOTATION_CLASS_NAME), e);
        }
    }

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
                Assertions.fail(String.format("No elements annotated with %s", ANNOTATION_CLASS_NAME));
            } else {
                annotatedElements.forEach(consumer);
            }
        }
        return false;
    }

    public boolean isCalled() {
        return called;
    }

    public static void doTest(String subject, Consumer<Element> consumer) {
        try {
            Path sourceFilePath = Paths.get("src/test/java/nl/wernerdegroot/applicatives/processor/converters/subjects/" + subject + ".java").toAbsolutePath();
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
