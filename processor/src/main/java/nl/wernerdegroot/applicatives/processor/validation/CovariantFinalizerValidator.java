package nl.wernerdegroot.applicatives.processor.validation;

import nl.wernerdegroot.applicatives.processor.domain.Method;
import nl.wernerdegroot.applicatives.processor.domain.Parameter;
import nl.wernerdegroot.applicatives.processor.domain.TypeParameter;
import nl.wernerdegroot.applicatives.processor.domain.type.Type;
import nl.wernerdegroot.applicatives.processor.domain.typeconstructor.TypeConstructor;

import java.util.Objects;

public class CovariantFinalizerValidator {

    public static Validated<Result> validate(Method method) {
        MethodValidation methodValidation = MethodValidation.of(method)
                .verifyCanImplementAbstractMethod()
                .verifyParameterCount("exactly 1", numberOfParameters -> numberOfParameters == 1)
                .verifyTypeParameterCount("exactly 1", numberOfTypeParameters -> numberOfTypeParameters == 1)
                .verifyTypeParametersAreUnbounded()
                .verifyHasReturnType();

        if (!methodValidation.isValid()) {
            return Validated.invalid(methodValidation.getErrorMessages());
        }

        TypeParameter typeParameter = method.getTypeParameters().get(0);

        Type returnType = methodValidation.getReturnType();

        String name = method.getName();

        Parameter parameter = method.getParameters().get(0);

        // Extract the type constructor from the return type:
        TypeConstructor finalizedTypeConstructor = returnType.asTypeConstructorWithPlaceholderFor(typeParameter.getName());

        // Extract the type constructor from the single parameter:
        TypeConstructor toFinalizeTypeConstructor = parameter.getType().asTypeConstructorWithPlaceholderFor(typeParameter.getName());

        return Validated.valid(Result.of(name, parameter.getType(), toFinalizeTypeConstructor, finalizedTypeConstructor));
    }

    public static class Result {

        private final String name;
        private final Type parameterType;
        private final TypeConstructor toFinalizeTypeConstructor;
        private final TypeConstructor finalizedTypeConstructor;

        public Result(String name, Type parameterType, TypeConstructor toFinalizeTypeConstructor, TypeConstructor finalizedTypeConstructor) {
            this.name = name;
            this.parameterType = parameterType;
            this.toFinalizeTypeConstructor = toFinalizeTypeConstructor;
            this.finalizedTypeConstructor = finalizedTypeConstructor;
        }

        public static Result of(String name, Type parameterType, TypeConstructor toFinalizeTypeConstructor, TypeConstructor finalizedTypeConstructor) {
            return new Result(name, parameterType, toFinalizeTypeConstructor, finalizedTypeConstructor);
        }

        public String getName() {
            return name;
        }

        public Type getParameterType() {
            return parameterType;
        }

        public TypeConstructor getToFinalizeTypeConstructor() {
            return toFinalizeTypeConstructor;
        }

        public TypeConstructor getFinalizedTypeConstructor() {
            return finalizedTypeConstructor;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Result that = (Result) o;
            return getName().equals(that.getName()) && getParameterType().equals(that.getParameterType()) && getToFinalizeTypeConstructor().equals(that.getToFinalizeTypeConstructor()) && getFinalizedTypeConstructor().equals(that.getFinalizedTypeConstructor());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getName(), getParameterType(), getToFinalizeTypeConstructor(), getFinalizedTypeConstructor());
        }

        @Override
        public String toString() {
            return "Result{" +
                    "name='" + name + '\'' +
                    ", parameterType=" + parameterType +
                    ", toFinalizeTypeConstructor=" + toFinalizeTypeConstructor +
                    ", finalizedTypeConstructor=" + finalizedTypeConstructor +
                    '}';
        }
    }
}
