package nl.wernerdegroot.applicatives.processor.validation;

import nl.wernerdegroot.applicatives.processor.domain.*;
import nl.wernerdegroot.applicatives.processor.domain.containing.ContainingClass;

import java.util.*;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;
import static nl.wernerdegroot.applicatives.processor.Classes.*;
import static nl.wernerdegroot.applicatives.processor.generator.TypeGenerator.generateFrom;

public class TemplateClassWithMethodsValidator {

    public static Validated<Result> validate(ContainingClass containingClass, Method method) {
        return Validated.combine(
                validateTemplateClass(containingClass),
                validateAccumulator(method),
                (templateClass, accumulator) -> templateClassWithMethods(templateClass, Optional.empty(), accumulator, Optional.empty())
        );
    }

    public static Validated<Result> validate(ContainingClass containingClass, List<Method> methods) {
        return Validated.combine(
                validateTemplateClass(containingClass),
                validateInitializer(methods),
                validateAccumulator(methods),
                validateFinalizer(methods),
                (templateClass, optionalInitializer, accumulator, optionalFinalizer) -> {
                    Set<String> messages = new HashSet<>();
                    if (optionalInitializer.isPresent()) {
                        CovariantInitializerOrFinalizerValidator.Result initializer = optionalInitializer.get();

                        if (!initializer.getToInitializeOrFinalizeTypeConstructor().canAccept(accumulator.getInputTypeConstructor())) {
                            String message = String.format("No shared type constructor between second parameter of '%s' (%s) and parameter of '%s' (%s)", accumulator.getName(), generateFrom(accumulator.getSecondParameterType()), initializer.getName(), generateFrom(initializer.getParameterType()));
                            messages.add(message);
                        }

                        if (!accumulator.getPartiallyAccumulatedTypeConstructor().canAccept(initializer.getInitializedOrFinalizedTypeConstructor())) {
                            String message = String.format("No shared type constructor between return type of '%s' (%s) and first parameter of '%s' (%s)", initializer.getName(), generateFrom(initializer.getReturnType()), accumulator.getName(), generateFrom(accumulator.getFirstParameterType()));
                            messages.add(message);
                        }
                    }

                    if (optionalFinalizer.isPresent()) {
                        CovariantInitializerOrFinalizerValidator.Result finalizer = optionalFinalizer.get();
                        if (!finalizer.getToInitializeOrFinalizeTypeConstructor().canAccept(accumulator.getAccumulatedTypeConstructor())) {
                            String message = String.format("No shared type constructor between return type of '%s' (%s) and parameter of '%s' (%s)", accumulator.getName(), generateFrom(accumulator.getReturnType()), finalizer.getName(), generateFrom(finalizer.getParameterType()));
                            messages.add(message);
                        }
                    }

                    return messages.isEmpty()
                            ? Validated.valid(templateClassWithMethods(templateClass, optionalInitializer, accumulator, optionalFinalizer))
                            : Validated.<Result>invalid(messages);
                }
        ).flatMap(Function.identity());
    }

    private static Validated<TemplateClassValidator.Result> validateTemplateClass(ContainingClass containingClass) {
        return TemplateClassValidator.validate(containingClass);
    }

    private static Validated<Optional<CovariantInitializerOrFinalizerValidator.Result>> validateInitializer(List<Method> methods) {
        List<Method> candidates = methods
                .stream()
                .filter(method -> method.hasAnnotation(INITIALIZER))
                .collect(toList());

        if (candidates.size() == 0) {
            return Validated.valid(Optional.empty());
        } else if (candidates.size() > 1) {
            return Validated.invalid(String.format("More than one method annotated with '%s'", INITIALIZER.raw()));
        } else {
            return CovariantInitializerOrFinalizerValidator.validate(candidates.iterator().next()).map(Optional::of);
        }
    }

    private static Validated<CovariantAccumulatorValidator.Result> validateAccumulator(Method method) {
        return CovariantAccumulatorValidator.validate(method);
    }

    private static Validated<CovariantAccumulatorValidator.Result> validateAccumulator(List<Method> methods) {
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

    private static Validated<Optional<CovariantInitializerOrFinalizerValidator.Result>> validateFinalizer(List<Method> methods) {
        List<Method> candidates = methods
                .stream()
                .filter(method -> method.hasAnnotation(FINALIZER))
                .collect(toList());

        if (candidates.size() == 0) {
            return Validated.valid(Optional.empty());
        } else if (candidates.size() > 1) {
            return Validated.invalid(String.format("More than one method annotated with '%s'", FINALIZER.raw()));
        } else {
            return CovariantInitializerOrFinalizerValidator.validate(candidates.iterator().next()).map(Optional::of);
        }
    }

    private static CovariantInitializer toCovariantInitializer(CovariantInitializerOrFinalizerValidator.Result initializer) {
        return CovariantInitializer.of(initializer.getName(), initializer.getToInitializeOrFinalizeTypeConstructor(), initializer.getInitializedOrFinalizedTypeConstructor());
    }

    private static CovariantAccumulator toCovariantAccumulator(CovariantAccumulatorValidator.Result accumulator) {
        return CovariantAccumulator.of(accumulator.getName(), accumulator.getInputTypeConstructor(), accumulator.getPartiallyAccumulatedTypeConstructor(), accumulator.getAccumulatedTypeConstructor());
    }

    private static CovariantFinalizer toCovariantFinalizer(CovariantInitializerOrFinalizerValidator.Result finalizer) {
        return CovariantFinalizer.of(finalizer.getName(), finalizer.getToInitializeOrFinalizeTypeConstructor(), finalizer.getInitializedOrFinalizedTypeConstructor());
    }

    private static Result templateClassWithMethods(TemplateClassValidator.Result templateClass, Optional<CovariantInitializerOrFinalizerValidator.Result> optionalInitializer, CovariantAccumulatorValidator.Result accumulator, Optional<CovariantInitializerOrFinalizerValidator.Result> optionalFinalizer) {
        return Result.of(
                templateClass.getTypeParameters(),
                optionalInitializer.map(TemplateClassWithMethodsValidator::toCovariantInitializer),
                TemplateClassWithMethodsValidator.toCovariantAccumulator(accumulator),
                optionalFinalizer.map(TemplateClassWithMethodsValidator::toCovariantFinalizer)
        );
    }

    public static final class Result implements HasReplaceableTypeParameterNames<Result> {

        private final List<TypeParameter> classTypeParameters;
        private final Optional<CovariantInitializer> optionalInitializer;
        private final CovariantAccumulator accumulator;
        private final Optional<CovariantFinalizer> optionalFinalizer;

        public Result(List<TypeParameter> classTypeParameters, Optional<CovariantInitializer> optionalInitializer, CovariantAccumulator accumulator, Optional<CovariantFinalizer> optionalFinalizer) {
            this.classTypeParameters = classTypeParameters;
            this.optionalInitializer = optionalInitializer;
            this.accumulator = accumulator;
            this.optionalFinalizer = optionalFinalizer;
        }

        public static Result of(List<TypeParameter> classTypeParameters, Optional<CovariantInitializer> optionalInitializer, CovariantAccumulator accumulator, Optional<CovariantFinalizer> optionalFinalizer) {
            return new Result(classTypeParameters, optionalInitializer, accumulator, optionalFinalizer);
        }

        @Override
        public Result replaceTypeParameterNames(Map<TypeParameterName, TypeParameterName> replacements) {
            return Result.of(
                    classTypeParameters.stream().map(r -> r.replaceAllTypeParameterNames(replacements)).collect(toList()),
                    optionalInitializer.map(initializer -> initializer.replaceTypeParameterNames(replacements)),
                    accumulator.replaceTypeParameterNames(replacements),
                    optionalFinalizer.map(finalizer -> finalizer.replaceTypeParameterNames(replacements))
            );
        }

        public List<TypeParameter> getClassTypeParameters() {
            return classTypeParameters;
        }

        public Optional<CovariantInitializer> getOptionalInitializer() {
            return optionalInitializer;
        }

        public CovariantAccumulator getAccumulator() {
            return accumulator;
        }

        public Optional<CovariantFinalizer> getOptionalFinalizer() {
            return optionalFinalizer;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Result result = (Result) o;
            return Objects.equals(getClassTypeParameters(), result.getClassTypeParameters()) && Objects.equals(getOptionalInitializer(), result.getOptionalInitializer()) && Objects.equals(getAccumulator(), result.getAccumulator()) && Objects.equals(getOptionalFinalizer(), result.getOptionalFinalizer());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getClassTypeParameters(), getOptionalInitializer(), getAccumulator(), getOptionalFinalizer());
        }

        @Override
        public String toString() {
            return "Result{" +
                    "classTypeParameters=" + classTypeParameters +
                    ", optionalInitializer=" + optionalInitializer +
                    ", accumulator=" + accumulator +
                    ", optionalFinalizer=" + optionalFinalizer +
                    '}';
        }
    }
}
