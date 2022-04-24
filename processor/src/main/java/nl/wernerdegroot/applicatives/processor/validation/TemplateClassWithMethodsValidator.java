package nl.wernerdegroot.applicatives.processor.validation;

import nl.wernerdegroot.applicatives.processor.domain.Method;
import nl.wernerdegroot.applicatives.processor.domain.TemplateClassWithMethods;
import nl.wernerdegroot.applicatives.processor.domain.containing.ContainingClass;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static nl.wernerdegroot.applicatives.processor.CovariantBuilderProcessor.ACCUMULATOR;

public class TemplateClassWithMethodsValidator {

    public static Validated<TemplateClassWithMethods> validate(ContainingClass containingClass, Method accumulatorMethod) {
        return Validated.combine(
                TemplateClassValidator.validate(containingClass),
                MethodValidator.validate(accumulatorMethod),
                TemplateClassWithMethods::of
        );
    }

    public static Validated<TemplateClassWithMethods> validate(ContainingClass containingClass, List<Method> methods) {
        List<Method> accumulatorCandidates = methods
                .stream()
                .filter(method -> method.hasAnnotation(ACCUMULATOR))
                .collect(toList());

        if (accumulatorCandidates.size() == 0) {
            return Validated.invalid(String.format("No method in '%s' annotated with '%s'", containingClass.getClassName().raw(), ACCUMULATOR.raw()));
        } else if (accumulatorCandidates.size() > 1) {
            return Validated.invalid(String.format("More than one method in '%s' annotated with '%s'", containingClass.getClassName().raw(), ACCUMULATOR.raw()));
        }

        Method accumulator = accumulatorCandidates.iterator().next();

        return validate(containingClass, accumulator);
    }
}
