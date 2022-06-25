package nl.wernerdegroot.applicatives.processor.generator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static java.util.stream.Collectors.joining;

public interface GeneratorTest {

    default String getResourceFileAsString(String fileName) throws IOException {
        try (InputStream is = CovariantGeneratorTest.class.getResourceAsStream(fileName)) {
            if (is == null) {
                throw new NullPointerException();
            }

            try (InputStreamReader isr = new InputStreamReader(is); BufferedReader reader = new BufferedReader(isr)) {
                return reader.lines().collect(joining("\n"));
            }
        }
    }
}
