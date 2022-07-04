package nl.wernerdegroot.applicatives.processor.validation;

import com.jparams.verifier.tostring.ToStringVerifier;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.wernerdegroot.applicatives.processor.domain.*;
import nl.wernerdegroot.applicatives.processor.domain.containing.ContainingClass;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toSet;
import static nl.wernerdegroot.applicatives.processor.domain.Modifier.STATIC;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ClassValidatorTest {

    private final TypeParameter A = TypeParameterName.of("A").asTypeParameter();
    private final TypeParameter B = TypeParameterName.of("B").asTypeParameter();
    private final TypeParameter C = TypeParameterName.of("C").asTypeParameter();
    private final TypeParameter D = TypeParameterName.of("D").asTypeParameter();

    @Test
    public void validateGivenNonStaticInnerClassAsContainingClass() {
        ContainingClass toValidate = PackageName.of("nl.wernerdegroot.applicatives")
                .asPackage()
                .containingClass(modifiers(), ClassName.of("Outer"), A, B)
                .containingClass(modifiers(), ClassName.of("Inner"), C, D);

        Validated<String, ClassValidator.Result> expected = Validated.invalid("Only outer classes and static inner classes are currently supported");
        Validated<String, ClassValidator.Result> toVerify = ClassValidator.validate(toValidate);

        assertEquals(expected, toVerify);
    }

    @Test
    public void validateGivenStaticInnerClassAsContainingClass() {
        ContainingClass toValidate = PackageName.of("nl.wernerdegroot.applicatives")
                .asPackage()
                .containingClass(modifiers(), ClassName.of("Outer"), A, B)
                .containingClass(modifiers(STATIC), ClassName.of("Inner"), C, D);

        Validated<String, ClassValidator.Result> expected = Validated.valid(ClassValidator.Result.of(asList(C, D)));
        Validated<String, ClassValidator.Result> toVerify = ClassValidator.validate(toValidate);

        assertEquals(expected, toVerify);
    }

    @Test
    public void validateGivenOuterClassAsContainingClass() {
        ContainingClass toValidate = PackageName.of("nl.wernerdegroot.applicatives")
                .asPackage()
                .containingClass(modifiers(), ClassName.of("Outer"), A, B);

        Validated<String, ClassValidator.Result> expected = Validated.valid(ClassValidator.Result.of(asList(A, B)));
        Validated<String, ClassValidator.Result> toVerify = ClassValidator.validate(toValidate);

        assertEquals(expected, toVerify);
    }

    @Test
    public void resultEqualsHashCodeToString() {
        EqualsVerifier.forClass(ClassValidator.Result.class).verify();
        ToStringVerifier.forClass(ClassValidator.Result.class).verify();
    }

    @SafeVarargs
    private final <T> Set<T> modifiers(T... elements) {
        return Stream.of(elements).collect(toSet());
    }
}
