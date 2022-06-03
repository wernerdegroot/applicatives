package nl.wernerdegroot.applicatives.processor.validation;

import nl.wernerdegroot.applicatives.processor.domain.TypeParameter;
import nl.wernerdegroot.applicatives.processor.domain.containing.ContainingClass;

import java.util.List;
import java.util.Objects;

public class TemplateClassValidator {

    public static Validated<String, Result> validate(ContainingClass containingClass) {

        // We check whether we're dealing with an outer class or a static inner class.
        // Why is this required? Consider the following scenario:
        //
        //   class Outer<A extends B, B> {
        //     class Inner<B> {
        //       ...
        //     }
        //   }
        //
        // It is impossible to collapse these three type parameters of these two classes into a
        // single list of type parameters without carefully renaming some of them to avoid shadowing.
        // Instead of going through all that effort for this extreme edge-case, I'm just going avoid
        // it completely. If we'd like to be more sophisticated we could try:
        //
        //  * To support only a single class with type parameters in the hierarchy
        //  * Only support multiple classes with type parameters if their names don't conflict
        //  * Only support conflicts if the type parameter that is shadowed can be removed
        //    completely (isn't used as upper bound for any of the other type parameters)
        if (!containingClass.isOuterClass() && !containingClass.isStaticInnerClass()) {
            return Validated.invalid("Only outer classes and static inner classes are currently supported");
        }
        List<TypeParameter> typeParameters = containingClass.getTypeParameters();

        return Validated.valid(Result.of(typeParameters));
    }

    public static class Result {
        private final List<TypeParameter> typeParameters;

        public Result(List<TypeParameter> typeParameters) {
            this.typeParameters = typeParameters;
        }

        public static Result of(List<TypeParameter> typeParameters) {
            return new Result(typeParameters);
        }

        public List<TypeParameter> getTypeParameters() {
            return typeParameters;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Result that = (Result) o;
            return getTypeParameters().equals(that.getTypeParameters());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getTypeParameters());
        }

        @Override
        public String toString() {
            return "Result{" +
                    "typeParameters=" + typeParameters +
                    '}';
        }
    }
}
