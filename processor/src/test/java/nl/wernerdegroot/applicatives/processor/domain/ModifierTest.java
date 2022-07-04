package nl.wernerdegroot.applicatives.processor.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ModifierTest {

    @Test
    public void givenInvalidValue() {
        assertThrows(IllegalArgumentException.class, () -> {
            Modifier.fromString("sanitized");
        });
    }
}
