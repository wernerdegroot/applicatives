package nl.wernerdegroot.applicatives.processor.validation;

import nl.wernerdegroot.applicatives.processor.domain.CovariantAccumulator;
import nl.wernerdegroot.applicatives.processor.domain.CovariantFinalizer;
import nl.wernerdegroot.applicatives.processor.domain.CovariantInitializer;
import nl.wernerdegroot.applicatives.processor.domain.Method;
import nl.wernerdegroot.applicatives.processor.domain.containing.ContainingClass;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;
import static nl.wernerdegroot.applicatives.processor.Classes.*;
import static nl.wernerdegroot.applicatives.processor.generator.TypeGenerator.generateFrom;

public class TemplateClassWithMethodsValidator {

    public static Validated<TemplateClassWithMethods> validate(ContainingClass containingClass, Method method) {
        return Validated.combine(
                validateTemplateClass(containingClass),
                validateAccumulator(method),
                (templateClass, accumulator) -> templateClassWithMethods(templateClass, Optional.empty(), accumulator, Optional.empty())
        );
    }

    public static Validated<TemplateClassWithMethods> validate(ContainingClass containingClass, List<Method> methods) {
        return Validated.combine(
                validateTemplateClass(containingClass),
                validateInitializer(methods),
                validateAccumulator(methods),
                validateFinalizer(methods),
                (templateClass, optionalInitializer, accumulator, optionalFinalizer) -> {
                    if (optionalInitializer.isPresent()) {
                        ValidCovariantInitializer initializer = optionalInitializer.get();
                        if (!accumulator.getPartiallyAccumulatedTypeConstructor().canAccept(initializer.getInitializedTypeConstructor())) {
                            String message = String.format("No shared type constructor between return type of '%s' (%s) and first parameter of '%s' (%s)", initializer.getName(), generateFrom(initializer.getReturnType()), accumulator.getName(), generateFrom(accumulator.getFirstParameterType()));
                            return Validated.<TemplateClassWithMethods>invalid(message);
                        }
                    }

                    if (optionalFinalizer.isPresent()) {
                        ValidCovariantFinalizer finalizer = optionalFinalizer.get();
                        if (!finalizer.getToFinalizeTypeConstructor().canAccept(accumulator.getAccumulatedTypeConstructor())) {
                            String message = String.format("No shared type constructor between return type of '%s' (%s) and parameter of '%s' (%s)", accumulator.getName(), generateFrom(accumulator.getFirstParameterType()), finalizer.getName(), generateFrom(finalizer.getParameterType()));
                            return Validated.<TemplateClassWithMethods>invalid(message);
                        }
                    }

                    return Validated.valid(templateClassWithMethods(templateClass, optionalInitializer, accumulator, optionalFinalizer));
                }
        ).flatMap(Function.identity());
    }

    private static Validated<TemplateClass> validateTemplateClass(ContainingClass containingClass) {
        return TemplateClassValidator.validate(containingClass);
    }

    private static Validated<Optional<ValidCovariantInitializer>> validateInitializer(List<Method> methods) {
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

    private static Validated<ValidCovariantAccumulator> validateAccumulator(Method method) {
        return CovariantAccumulatorValidator.validate(method);
    }

    private static Validated<ValidCovariantAccumulator> validateAccumulator(List<Method> methods) {
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

    private static Validated<Optional<ValidCovariantFinalizer>> validateFinalizer(List<Method> methods) {
        List<Method> candidates = methods
                .stream()
                .filter(method -> method.hasAnnotation(FINALIZER))
                .collect(toList());

        if (candidates.size() == 0) {
            return Validated.valid(Optional.empty());
        } else if (candidates.size() > 1) {
            return Validated.invalid(String.format("More than one method annotated with '%s'", FINALIZER.raw()));
        } else {
            return CovariantFinalizerValidator.validate(candidates.iterator().next()).map(Optional::of);
        }
    }

    private static CovariantInitializer toDomain(ValidCovariantInitializer initializer) {
        return CovariantInitializer.of(initializer.getName(), initializer.getInitializedTypeConstructor());
    }

    private static CovariantAccumulator toDomain(ValidCovariantAccumulator accumulator) {
        return CovariantAccumulator.of(accumulator.getName(), accumulator.getInputTypeConstructor(), accumulator.getPartiallyAccumulatedTypeConstructor(), accumulator.getAccumulatedTypeConstructor());
    }

    private static CovariantFinalizer toDomain(ValidCovariantFinalizer finalizer) {
        return CovariantFinalizer.of(finalizer.getName(), finalizer.getToFinalizeTypeConstructor(), finalizer.getFinalizedTypeConstructor());
    }

    private static TemplateClassWithMethods templateClassWithMethods(TemplateClass templateClass, Optional<ValidCovariantInitializer> optionalInitializer, ValidCovariantAccumulator accumulator, Optional<ValidCovariantFinalizer> optionalFinalizer) {
        return TemplateClassWithMethods.of(
                templateClass.getTypeParameters(),
                optionalInitializer.map(TemplateClassWithMethodsValidator::toDomain),
                TemplateClassWithMethodsValidator.toDomain(accumulator),
                optionalFinalizer.map(TemplateClassWithMethodsValidator::toDomain)
        );
    }
}
