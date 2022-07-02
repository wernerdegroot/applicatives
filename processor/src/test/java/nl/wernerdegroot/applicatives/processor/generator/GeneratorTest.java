package nl.wernerdegroot.applicatives.processor.generator;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.Compiler;
import com.google.testing.compile.JavaFileObjects;

import javax.tools.JavaFileObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static java.util.stream.Collectors.joining;
import static org.junit.jupiter.api.Assertions.assertTrue;

public interface GeneratorTest {

    default String getResourceFileAsString(String className) throws IOException {
        String fileName = "/" + className + ".java";
        try (InputStream is = CovariantGeneratorTest.class.getResourceAsStream(fileName)) {
            if (is == null) {
                throw new NullPointerException();
            }

            try (InputStreamReader isr = new InputStreamReader(is); BufferedReader reader = new BufferedReader(isr)) {
                return reader.lines().collect(joining("\n"));
            }
        }
    }

    default void ensureResourceFileCompiles(String className) throws IOException {
        JavaFileObject javaFileObject = JavaFileObjects.forSourceString(className, getResourceFileAsString(className));
        Compilation result = Compiler.javac().compile(javaFileObject);
        boolean compiles = result.status() == Compilation.Status.SUCCESS;
        assertTrue(compiles);
    }
}
