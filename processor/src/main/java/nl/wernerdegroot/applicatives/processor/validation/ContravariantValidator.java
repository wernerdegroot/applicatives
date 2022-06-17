package nl.wernerdegroot.applicatives.processor.validation;

import nl.wernerdegroot.applicatives.processor.domain.*;
import nl.wernerdegroot.applicatives.processor.domain.containing.ContainingClass;
import nl.wernerdegroot.applicatives.processor.logging.Log;

import java.util.*;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;
import static nl.wernerdegroot.applicatives.processor.Classes.*;
import static nl.wernerdegroot.applicatives.processor.Classes.ACCUMULATOR_FULLY_QUALIFIED_NAME;
import static nl.wernerdegroot.applicatives.processor.generator.TypeGenerator.generateFrom;

public class ContravariantValidator {

    public static Validated<Log, Result> validate(ContainingClass containingClass, Method method) {
        return Validated.combine(
                validateTemplateClass(containingClass),
                validateAccumulator(method),
                (templateClass, accumulator) -> templateClassWithMethods(templateClass, Optional.empty(), accumulator, Optional.empty())
        );
    }

    public static Validated<Log, ContravariantValidator.Result> validate(ContainingClass containingClass, List<Method> methods) {
        return Validated.combine(
                validateTemplateClass(containingClass),
                validateInitializer(methods),
                validateAccumulator(methods),
                validateFinalizer(methods),
                (templateClass, optionalInitializer, accumulator, optionalFinalizer) -> {
                    Set<Log> messages = new HashSet<>();
                    if (optionalInitializer.isPresent()) {
                        CovariantInitializerOrFinalizerValidator.Result initializer = optionalInitializer.get();

                        if (!initializer.getToInitializeOrFinalizeTypeConstructor().canAccept(accumulator.getInputTypeConstructor())) {
                            Log message = Log.of("No shared type constructor between second parameter of '%s' (%s) and parameter of '%s' (%s)", accumulator.getName(), generateFrom(accumulator.getSecondParameterType()), initializer.getName(), generateFrom(initializer.getParameterType()));
                            messages.add(message);
                        }

                        if (!accumulator.getPartiallyAccumulatedTypeConstructor().canAccept(initializer.getInitializedOrFinalizedTypeConstructor())) {
                            Log message = Log.of("No shared type constructor between return type of '%s' (%s) and first parameter of '%s' (%s)", initializer.getName(), generateFrom(initializer.getReturnType()), accumulator.getName(), generateFrom(accumulator.getFirstParameterType()));
                            messages.add(message);
                        }
                    }

                    if (optionalFinalizer.isPresent()) {
                        CovariantInitializerOrFinalizerValidator.Result finalizer = optionalFinalizer.get();
                        if (!finalizer.getToInitializeOrFinalizeTypeConstructor().canAccept(accumulator.getAccumulatedTypeConstructor())) {
                            Log message = Log.of("No shared type constructor between return type of '%s' (%s) and parameter of '%s' (%s)", accumulator.getName(), generateFrom(accumulator.getReturnType()), finalizer.getName(), generateFrom(finalizer.getParameterType()));
                            messages.add(message);
                        }
                    }

                    return messages.isEmpty()
                            ? Validated.<Log, ContravariantValidator.Result>valid(templateClassWithMethods(templateClass, optionalInitializer, accumulator, optionalFinalizer))
                            : Validated.<Log, ContravariantValidator.Result>invalid(messages);
                }
        ).flatMap(Function.identity());
    }

    private static Validated<Log, ClassValidator.Result> validateTemplateClass(ContainingClass containingClass) {
        return ClassValidator.validate(containingClass)
                .fold(invalidFor("Class '%s'", containingClass.getFullyQualifiedName().raw()), valid());
    }

    private static Validated<Log, Optional<CovariantInitializerOrFinalizerValidator.Result>> validateInitializer(List<Method> methods) {
        List<Method> candidates = methods
                .stream()
                .filter(method -> method.hasAnnotation(INITIALIZER_FULLY_QUALIFIED_NAME))
                .collect(toList());

        if (candidates.size() == 0) {
            return Validated.valid(Optional.empty());
        } else if (candidates.size() > 1) {
            return Validated.invalid(Log.of("More than one method annotated with '%s'", INITIALIZER_FULLY_QUALIFIED_NAME.raw()));
        } else {
            Method initializer = candidates.iterator().next();
            return CovariantInitializerOrFinalizerValidator.validate(initializer)
                    .map(Optional::of)
                    .fold(invalidFor("Method '%s'", initializer.getName()), valid());
        }
    }

    private static Validated<Log, ContravariantAccumulatorValidator.Result> validateAccumulator(Method method) {
        return ContravariantAccumulatorValidator.validate(method)
                .fold(invalidFor("Method '%s'", method.getName()), valid());
    }

    private static Validated<Log, ContravariantAccumulatorValidator.Result> validateAccumulator(List<Method> methods) {
        List<Method> candidates = methods
                .stream()
                .filter(method -> method.hasAnnotation(ACCUMULATOR_FULLY_QUALIFIED_NAME))
                .collect(toList());

        if (candidates.size() == 0) {
            return Validated.invalid(Log.of("No method annotated with '%s'", ACCUMULATOR_FULLY_QUALIFIED_NAME.raw()));
        } else if (candidates.size() > 1) {
            return Validated.invalid(Log.of("More than one method annotated with '%s'", ACCUMULATOR_FULLY_QUALIFIED_NAME.raw()));
        } else {
            Method accumulator = candidates.iterator().next();
            return ContravariantAccumulatorValidator.validate(accumulator)
                    .fold(invalidFor("Method '%s'", accumulator.getName()), valid());
        }
    }

    private static Validated<Log, Optional<CovariantInitializerOrFinalizerValidator.Result>> validateFinalizer(List<Method> methods) {
        List<Method> candidates = methods
                .stream()
                .filter(method -> method.hasAnnotation(FINALIZER_FULLY_QUALIFIED_NAME))
                .collect(toList());

        if (candidates.size() == 0) {
            return Validated.valid(Optional.empty());
        } else if (candidates.size() > 1) {
            return Validated.invalid(Log.of("More than one method annotated with '%s'", FINALIZER_FULLY_QUALIFIED_NAME.raw()));
        } else {
            Method finalizer = candidates.iterator().next();
            return CovariantInitializerOrFinalizerValidator.validate(candidates.iterator().next())
                    .map(Optional::of)
                    .fold(invalidFor("Method '%s'", finalizer.getName()), valid());
        }
    }

    private static CovariantInitializer toCovariantInitializer(CovariantInitializerOrFinalizerValidator.Result initializer) {
        return CovariantInitializer.of(initializer.getName(), initializer.getToInitializeOrFinalizeTypeConstructor(), initializer.getInitializedOrFinalizedTypeConstructor());
    }

    private static ContravariantAccumulator toContravariantAccumulator(ContravariantAccumulatorValidator.Result accumulator) {
        return ContravariantAccumulator.of(accumulator.getName(), accumulator.getInputTypeConstructor(), accumulator.getPartiallyAccumulatedTypeConstructor(), accumulator.getAccumulatedTypeConstructor());
    }

    private static CovariantFinalizer toCovariantFinalizer(CovariantInitializerOrFinalizerValidator.Result finalizer) {
        return CovariantFinalizer.of(finalizer.getName(), finalizer.getToInitializeOrFinalizeTypeConstructor(), finalizer.getInitializedOrFinalizedTypeConstructor());
    }

    private static Result templateClassWithMethods(ClassValidator.Result templateClass, Optional<CovariantInitializerOrFinalizerValidator.Result> optionalInitializer, ContravariantAccumulatorValidator.Result accumulator, Optional<CovariantInitializerOrFinalizerValidator.Result> optionalFinalizer) {
        return Result.of(
                templateClass.getTypeParameters(),
                optionalInitializer.map(ContravariantValidator::toCovariantInitializer),
                ContravariantValidator.toContravariantAccumulator(accumulator),
                optionalFinalizer.map(ContravariantValidator::toCovariantFinalizer)
        );
    }

    public static final class Result implements HasReplaceableTypeParameterNames<Result> {

        private final List<TypeParameter> classTypeParameters;
        private final Optional<CovariantInitializer> optionalInitializer;
        private final ContravariantAccumulator accumulator;
        private final Optional<CovariantFinalizer> optionalFinalizer;

        public Result(List<TypeParameter> classTypeParameters, Optional<CovariantInitializer> optionalInitializer, ContravariantAccumulator accumulator, Optional<CovariantFinalizer> optionalFinalizer) {
            this.classTypeParameters = classTypeParameters;
            this.optionalInitializer = optionalInitializer;
            this.accumulator = accumulator;
            this.optionalFinalizer = optionalFinalizer;
        }

        public static Result of(List<TypeParameter> classTypeParameters, Optional<CovariantInitializer> optionalInitializer, ContravariantAccumulator accumulator, Optional<CovariantFinalizer> optionalFinalizer) {
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

        public ContravariantAccumulator getAccumulator() {
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

    private static <E, T> Function<T, Validated<E, T>> valid() {
        return Validated::valid;
    }

    private static <T> Function<Set<String>, Validated<Log, T>> invalidFor(String description, Object... arguments) {
        return messages -> Validated.invalid(Log.of(String.format(description, arguments)).withDetails(messages));
    }
}
