package nl.wernerdegroot.applicatives.processor.validation;

import nl.wernerdegroot.applicatives.processor.domain.Method;
import nl.wernerdegroot.applicatives.processor.domain.Parameter;
import nl.wernerdegroot.applicatives.processor.domain.TypeParameter;
import nl.wernerdegroot.applicatives.processor.domain.type.Type;
import nl.wernerdegroot.applicatives.processor.domain.typeconstructor.TypeConstructor;

import java.util.Objects;

public class CovariantInitializerOrFinalizerValidator {

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

        // Extract the type constructor from the single parameter:
        TypeConstructor toInitializeOrFinalizeTypeConstructor = parameter.getType().asTypeConstructorWithPlaceholderFor(typeParameter.getName());

        // Extract the type constructor from the return type:
        TypeConstructor initializedOrFinalizedTypeConstructor = returnType.asTypeConstructorWithPlaceholderFor(typeParameter.getName());

        return Validated.valid(Result.of(name, parameter.getType(), toInitializeOrFinalizeTypeConstructor,  returnType, initializedOrFinalizedTypeConstructor));
    }

    public static final class Result {

        private final String name;
        private final Type parameterType;
        private final TypeConstructor toInitializeOrFinalizeTypeConstructor;
        private final Type returnType;
        private final TypeConstructor initializedOrFinalizedTypeConstructor;

        public Result(String name, Type parameterType, TypeConstructor toInitializeOrFinalizeTypeConstructor, Type returnType, TypeConstructor initializedOrFinalizedTypeConstructor) {
            this.name = name;
            this.parameterType = parameterType;
            this.toInitializeOrFinalizeTypeConstructor = toInitializeOrFinalizeTypeConstructor;
            this.returnType = returnType;
            this.initializedOrFinalizedTypeConstructor = initializedOrFinalizedTypeConstructor;
        }

        public static Result of(String name, Type parameterType, TypeConstructor toInitializeOrFinalizeTypeConstructor, Type returnType, TypeConstructor initializedOrFinalizedTypeConstructor) {
            return new Result(name, parameterType, toInitializeOrFinalizeTypeConstructor, returnType, initializedOrFinalizedTypeConstructor);
        }

        public String getName() {
            return name;
        }

        public Type getParameterType() {
            return parameterType;
        }

        public TypeConstructor getToInitializeOrFinalizeTypeConstructor() {
            return toInitializeOrFinalizeTypeConstructor;
        }

        public Type getReturnType() {
            return returnType;
        }

        public TypeConstructor getInitializedOrFinalizedTypeConstructor() {
            return initializedOrFinalizedTypeConstructor;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Result result = (Result) o;
            return Objects.equals(getName(), result.getName()) && Objects.equals(getParameterType(), result.getParameterType()) && Objects.equals(getToInitializeOrFinalizeTypeConstructor(), result.getToInitializeOrFinalizeTypeConstructor()) && Objects.equals(getReturnType(), result.getReturnType()) && Objects.equals(getInitializedOrFinalizedTypeConstructor(), result.getInitializedOrFinalizedTypeConstructor());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getName(), getParameterType(), getToInitializeOrFinalizeTypeConstructor(), getReturnType(), getInitializedOrFinalizedTypeConstructor());
        }

        @Override
        public String toString() {
            return "Result{" +
                    "name='" + name + '\'' +
                    ", parameterType=" + parameterType +
                    ", toInitializeOrFinalizeTypeConstructor=" + toInitializeOrFinalizeTypeConstructor +
                    ", returnType=" + returnType +
                    ", initializedOrFinalizedTypeConstructor=" + initializedOrFinalizedTypeConstructor +
                    '}';
        }
    }
}
