package nl.wernerdegroot.applicatives.processor;

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

public interface VarianceProcessorTemplateTest {

    default String getResourceFileAsString(String fileName) throws IOException {
        try (InputStream is = VarianceProcessorTemplateTest.class.getResourceAsStream(fileName)) {
            if (is == null) {
                throw new NullPointerException();
            }

            try (InputStreamReader isr = new InputStreamReader(is); BufferedReader reader = new BufferedReader(isr)) {
                return reader.lines().collect(joining("\n"));
            }
        }
    }

    default String getResourceClassFileAsString(String className) throws IOException {
        String fileName = "/" + className + ".java";
        return getResourceFileAsString(fileName);
    }

    default void ensureResourceFileCompiles(String className) throws IOException {
        JavaFileObject javaFileObject = JavaFileObjects.forSourceString(className, getResourceClassFileAsString(className));
        Compilation result = Compiler.javac().compile(javaFileObject);
        boolean compiles = result.status() == Compilation.Status.SUCCESS;
        assertTrue(compiles);
    }
}
