package nl.wernerdegroot.applicatives.processor.validation;

import nl.wernerdegroot.applicatives.processor.domain.Method;
import nl.wernerdegroot.applicatives.processor.domain.TemplateClassWithMethods;
import nl.wernerdegroot.applicatives.processor.domain.containing.ContainingClass;

public class TemplateClassWithMethodsValidator {

    public static Validated<TemplateClassWithMethods> validate(ContainingClass containingClass, Method accumulatorMethod) {
        return Validated.combine(
                TemplateClassValidator.validate(containingClass),
                MethodValidator.validate(accumulatorMethod),
                TemplateClassWithMethods::of
        );
    }
}
