package nl.wernerdegroot.applicatives.processor.validation;

import nl.wernerdegroot.applicatives.processor.domain.*;
import nl.wernerdegroot.applicatives.processor.domain.containing.ContainingClass;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;
import static nl.wernerdegroot.applicatives.processor.Classes.ACCUMULATOR;
import static nl.wernerdegroot.applicatives.processor.Classes.INITIALIZER;
import static nl.wernerdegroot.applicatives.processor.generator.TypeGenerator.generateFrom;

public class TemplateClassWithMethodsValidator {

    public static Validated<TemplateClassWithMethods> validate(ContainingClass containingClass, Method method) {
        return Validated.combine(
                validateTemplateClass(containingClass),
                validateAccumulator(method),
                (templateClass, accumulator) -> {
                    return templateClassWithMethods(templateClass, Optional.empty(), accumulator);
                }
        );
    }

    public static Validated<TemplateClassWithMethods> validate(ContainingClass containingClass, List<Method> methods) {
        return Validated.combine(
                validateTemplateClass(containingClass),
                validateInitializer(methods),
                validateAccumulator(methods),
                (templateClass, optionalInitializer, accumulator) -> {
                    if (optionalInitializer.isPresent()) {
                        CovariantInitializer initializer = optionalInitializer.get();
                        if (!accumulator.getPermissiveAccumulationTypeConstructor().canAccept(initializer.getPermissiveAccumulationTypeConstructor())) {
                            String message = String.format("No shared type constructor between return type of '%s' (%s) and first parameter of '%s' (%s)", accumulator.getName(), generateFrom(accumulator.getFirstParameterType()), initializer.getName(), generateFrom(initializer.getReturnType()));
                            return Validated.<TemplateClassWithMethods>invalid(message);
                        }
                    }

                    return Validated.valid(templateClassWithMethods(templateClass, optionalInitializer, accumulator));
                }
        ).flatMap(Function.identity());
    }

    private static Validated<TemplateClass> validateTemplateClass(ContainingClass containingClass) {
        return TemplateClassValidator.validate(containingClass);
    }

    private static Validated<Optional<CovariantInitializer>> validateInitializer(List<Method> methods) {
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

    private static Validated<CovariantAccumulator> validateAccumulator(Method method) {
        return CovariantAccumulatorValidator.validate(method);
    }

    private static Validated<CovariantAccumulator> validateAccumulator(List<Method> methods) {
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
