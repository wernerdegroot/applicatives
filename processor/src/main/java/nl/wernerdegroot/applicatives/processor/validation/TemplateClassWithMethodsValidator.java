package nl.wernerdegroot.applicatives.processor.validation;

import nl.wernerdegroot.applicatives.processor.domain.*;
import nl.wernerdegroot.applicatives.processor.domain.containing.ContainingClass;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static nl.wernerdegroot.applicatives.processor.CovariantBuilderProcessor.ACCUMULATOR;
import static nl.wernerdegroot.applicatives.processor.CovariantBuilderProcessor.INITIALIZER;

public class TemplateClassWithMethodsValidator {

    public static Validated<TemplateClassWithMethods> validate(ContainingClass containingClass, Method accumulatorMethod) {
        return Validated.combine(
                TemplateClassValidator.validate(containingClass),
                Validated.valid(Optional.empty()),
                CovariantAccumulatorValidator.validate(accumulatorMethod),
                TemplateClassWithMethodsValidator::templateClassWithMethods
        );
    }

    public static Validated<TemplateClassWithMethods> validate(ContainingClass containingClass, List<Method> methods) {
        return Validated.combine(
                TemplateClassValidator.validate(containingClass),
                validateInitializerMethod(methods),
                validateAccumulatorMethod(methods),
                TemplateClassWithMethodsValidator::templateClassWithMethods
        );
    }

    private static Validated<Optional<CovariantInitializer>> validateInitializerMethod(List<Method> methods) {
        List<Method> candidates = methods
                .stream()
                .filter(method -> method.hasAnnotation(INITIALIZER))
                .collect(toList());

        if (candidates.size() == 0) {
            return Validated.valid(Optional.empty());
        } else if (candidates.size() > 1) {
            return Validated.invalid(String.format("More than one method annotated with '%s'", INITIALIZER.raw()));
        } else {
            return CovariantInitializerValidator.validate(candidates.iterator().next()).map(Optional::of);
        }
    }

    private static Validated<CovariantAccumulator> validateAccumulatorMethod(List<Method> methods) {
        List<Method> candidates = methods
                .stream()
                .filter(method -> method.hasAnnotation(ACCUMULATOR))
                .collect(toList());

        if (candidates.size() == 0) {
            return Validated.invalid(String.format("No method annotated with '%s'", ACCUMULATOR.raw()));
        } else if (candidates.size() > 1) {
            return Validated.invalid(String.format("More than one method annotated with '%s'", ACCUMULATOR.raw()));
        } else {
            return CovariantAccumulatorValidator.validate(candidates.iterator().next());
        }
    }

    private static TemplateClassWithMethods templateClassWithMethods(TemplateClass templateClass, Optional<CovariantInitializer> covariantInitializer, CovariantAccumulator covariantAccumulator) {
        return TemplateClassWithMethods.of(
                templateClass.getTypeParameters(),
                covariantAccumulator.getAccumulationTypeConstructor(),
                covariantAccumulator.getPermissiveAccumulationTypeConstructor(),
                covariantAccumulator.getInputTypeConstructor(),
                covariantInitializer.map(CovariantInitializer::getName),
                covariantAccumulator.getName()
        );
    }
}
