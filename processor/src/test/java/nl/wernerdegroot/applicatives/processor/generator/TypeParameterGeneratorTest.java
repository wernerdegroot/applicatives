package nl.wernerdegroot.applicatives.processor.generator;

import nl.wernerdegroot.applicatives.processor.domain.TypeParameter;
import nl.wernerdegroot.applicatives.processor.domain.TypeParameterName;
import org.junit.jupiter.api.Test;

import static nl.wernerdegroot.applicatives.processor.domain.type.Type.COMPARABLE;
import static nl.wernerdegroot.applicatives.processor.domain.type.Type.SERIALIZABLE;
import static nl.wernerdegroot.applicatives.processor.generator.TypeParameterGenerator.typeParameter;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TypeParameterGeneratorTest {

    private final TypeParameterName T = TypeParameterName.of("T");

    @Test
    public void withoutUpperBounds() {
        TypeParameter typeParameter = T.asTypeParameter();
        String toVerify = typeParameter(typeParameter).generate();
        String expected = "T";
        assertEquals(expected, toVerify);
    }

    @Test
    public void withUpperBound() {
        TypeParameter typeParameter = T.extending(COMPARABLE.with(T));
        String toVerify = typeParameter(typeParameter).generate();
        String expected = "T extends java.lang.Comparable<T>";
        assertEquals(expected, toVerify);
    }

    @Test
    public void withMultipleUpperBounds() {
        TypeParameter typeParameter = T.extending(COMPARABLE.with(T), SERIALIZABLE);
        String toVerify = typeParameter(typeParameter).generate();
        String expected = "T extends java.lang.Comparable<T> & java.io.Serializable";
        assertEquals(expected, toVerify);
    }
}
