package nl.wernerdegroot.applicatives.processor.domain;

import com.jparams.verifier.tostring.ToStringVerifier;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ClassNameTest {

    @Test
    public void of() {
        ClassName expected = new ClassName("String");
        ClassName toVerify = ClassName.of("String");

        assertEquals(expected, toVerify);
    }

    @Test
    public void raw() {
        String expected = "String";
        String toVerify = new ClassName("String").raw();

        assertEquals(expected, toVerify);
    }

    @Test
    public void equalsHashCodeToString() {
        EqualsVerifier.forClass(ClassName.class).verify();
        ToStringVerifier.forClass(ClassName.class).verify();
    }
}
