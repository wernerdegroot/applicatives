package nl.wernerdegroot.applicatives.processor.validation;

import nl.wernerdegroot.applicatives.processor.domain.AccumulatorMethod;
import nl.wernerdegroot.applicatives.processor.domain.Method;
import nl.wernerdegroot.applicatives.processor.domain.TemplateClass;
import nl.wernerdegroot.applicatives.processor.domain.TemplateClassWithMethods;
import nl.wernerdegroot.applicatives.processor.domain.containing.ContainingClass;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static nl.wernerdegroot.applicatives.processor.CovariantBuilderProcessor.ACCUMULATOR;

public class TemplateClassWithMethodsValidator {

    public static Validated<TemplateClassWithMethods> validate(ContainingClass containingClass, Method accumulatorMethod) {
        return Validated.combine(
                TemplateClassValidator.validate(containingClass),
                MethodValidator.validate(accumulatorMethod),
                TemplateClassWithMethodsValidator::templateClassWithMethods
        );
    }

    public static Validated<TemplateClassWithMethods> validate(ContainingClass containingClass, List<Method> methods) {
        return Validated.combine(
                TemplateClassValidator.validate(containingClass),
                validateAccumulatorMethod(methods),
                TemplateClassWithMethodsValidator::templateClassWithMethods
        );
    }

    private static Validated<AccumulatorMethod> validateAccumulatorMethod(List<Method> methods) {
        List<Method> accumulatorCandidates = methods
                .stream()
                .filter(method -> method.hasAnnotation(ACCUMULATOR))
                .collect(toList());

        if (accumulatorCandidates.size() == 0) {
            return Validated.invalid(String.format("No method annotated with '%s'", ACCUMULATOR.raw()));
        } else if (accumulatorCandidates.size() > 1) {
            return Validated.invalid(String.format("More than one method annotated with '%s'", ACCUMULATOR.raw()));
        } else {
            return MethodValidator.validate(accumulatorCandidates.iterator().next());
        }
    }

    private static TemplateClassWithMethods templateClassWithMethods(TemplateClass templateClass, AccumulatorMethod accumulatorMethod) {
        return TemplateClassWithMethods.of(
                templateClass.getTypeParameters(),
                accumulatorMethod.getAccumulationTypeConstructor(),
                accumulatorMethod.getPermissiveAccumulationTypeConstructor(),
                accumulatorMethod.getInputTypeConstructor(),
                Optional.empty(),
                accumulatorMethod.getName()
        );
    }
}
