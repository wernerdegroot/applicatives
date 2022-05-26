package nl.wernerdegroot.applicatives.processor.validation;

import nl.wernerdegroot.applicatives.processor.domain.*;
import nl.wernerdegroot.applicatives.processor.domain.containing.ContainingClass;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toSet;
import static nl.wernerdegroot.applicatives.processor.domain.Modifier.STATIC;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TemplateClassValidatorTest {

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

        Validated<TemplateClassValidator.Result> expected = Validated.invalid("Only outer classes and static inner classes are currently supported");
        Validated<TemplateClassValidator.Result> toVerify = TemplateClassValidator.validate(toValidate);

        assertEquals(expected, toVerify);
    }

    @Test
    public void validateGivenStaticInnerClassAsContainingClass() {
        ContainingClass toValidate = PackageName.of("nl.wernerdegroot.applicatives")
                .asPackage()
                .containingClass(modifiers(), ClassName.of("Outer"), A, B)
                .containingClass(modifiers(STATIC), ClassName.of("Inner"), C, D);

        Validated<TemplateClassValidator.Result> expected = Validated.valid(TemplateClassValidator.Result.of(asList(C, D)));
        Validated<TemplateClassValidator.Result> toVerify = TemplateClassValidator.validate(toValidate);

        assertEquals(expected, toVerify);
    }

    @Test
    public void validateGivenOuterClassAsContainingClass() {
        ContainingClass toValidate = PackageName.of("nl.wernerdegroot.applicatives")
                .asPackage()
                .containingClass(modifiers(), ClassName.of("Outer"), A, B);

        Validated<TemplateClassValidator.Result> expected = Validated.valid(TemplateClassValidator.Result.of(asList(A, B)));
        Validated<TemplateClassValidator.Result> toVerify = TemplateClassValidator.validate(toValidate);

        assertEquals(expected, toVerify);
    }

    @SafeVarargs
    private final <T> Set<T> modifiers(T... elements) {
        return Stream.of(elements).collect(toSet());
    }
}
