package nl.wernerdegroot.applicatives.processor.validation;

import nl.wernerdegroot.applicatives.processor.domain.*;
import nl.wernerdegroot.applicatives.processor.domain.containing.ContainingClass;
import nl.wernerdegroot.applicatives.processor.logging.Log;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;

public class ContravariantValidator {

    public static Validated<Log, Result> validate(ContainingClass containingClass, Method method) {
        return Validated.combine(
                validateTemplateClass(containingClass),
                validateAccumulator(method),
                (templateClass, accumulator) -> templateClassWithMethods(templateClass, accumulator)
        );
    }

    private static Validated<Log, ClassValidator.Result> validateTemplateClass(ContainingClass containingClass) {
        return ClassValidator.validate(containingClass)
                .fold(invalidFor("Class '%s'", containingClass.getFullyQualifiedName().raw()), valid());
    }

    private static Validated<Log, ContravariantAccumulatorValidator.Result> validateAccumulator(Method method) {
        return ContravariantAccumulatorValidator.validate(method)
                .fold(invalidFor("Method '%s'", method.getName()), valid());
    }

    private static ContravariantAccumulator toContravariantAccumulator(ContravariantAccumulatorValidator.Result accumulator) {
        return ContravariantAccumulator.of(accumulator.getName(), accumulator.getInputTypeConstructor(), accumulator.getPartiallyAccumulatedTypeConstructor(), accumulator.getAccumulatedTypeConstructor());
    }

    private static Result templateClassWithMethods(ClassValidator.Result templateClass, ContravariantAccumulatorValidator.Result accumulator) {
        return Result.of(
                templateClass.getTypeParameters(),
                ContravariantValidator.toContravariantAccumulator(accumulator)
        );
    }

    public static final class Result implements HasReplaceableTypeParameterNames<Result> {

        private final List<TypeParameter> classTypeParameters;
        private final ContravariantAccumulator accumulator;

        public Result(List<TypeParameter> classTypeParameters, ContravariantAccumulator accumulator) {
            this.classTypeParameters = classTypeParameters;
            this.accumulator = accumulator;
        }

        public static Result of(List<TypeParameter> classTypeParameters, ContravariantAccumulator accumulator) {
            return new Result(classTypeParameters, accumulator);
        }

        @Override
        public Result replaceTypeParameterNames(Map<TypeParameterName, TypeParameterName> replacements) {
            return Result.of(
                    classTypeParameters.stream().map(r -> r.replaceAllTypeParameterNames(replacements)).collect(toList()),
                    accumulator.replaceTypeParameterNames(replacements)
            );
        }

        public List<TypeParameter> getClassTypeParameters() {
            return classTypeParameters;
        }

        public ContravariantAccumulator getAccumulator() {
            return accumulator;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Result result = (Result) o;
            return Objects.equals(getClassTypeParameters(), result.getClassTypeParameters()) && Objects.equals(getAccumulator(), result.getAccumulator());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getClassTypeParameters(), getAccumulator());
        }

        @Override
        public String toString() {
            return "Result{" +
                    "classTypeParameters=" + classTypeParameters +
                    ", accumulator=" + accumulator +
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
