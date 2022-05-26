package nl.wernerdegroot.applicatives.processor.validation;

import nl.wernerdegroot.applicatives.processor.domain.Method;
import nl.wernerdegroot.applicatives.processor.domain.Parameter;
import nl.wernerdegroot.applicatives.processor.domain.TypeParameter;
import nl.wernerdegroot.applicatives.processor.domain.type.Type;
import nl.wernerdegroot.applicatives.processor.domain.typeconstructor.TypeConstructor;

import java.util.Objects;

import static nl.wernerdegroot.applicatives.processor.generator.TypeGenerator.generateFrom;

public class CovariantInitializerValidator {

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

        // Check if the parameter is as expected:
        Type expectedParameterType = typeParameter.asType();
        if (!Objects.equals(parameter.getType(), expectedParameterType)) {
            return Validated.invalid("Expected parameter to be " + generateFrom(expectedParameterType) + " but was " + generateFrom(parameter.getType()));
        }

        TypeConstructor initializedTypeConstructor = returnType.asTypeConstructorWithPlaceholderFor(typeParameter.getName());

        return Validated.valid(Result.of(name, initializedTypeConstructor, returnType));
    }

    public static class Result {

        private final String name;
        private final TypeConstructor initializedTypeConstructor;
        private final Type returnType;

        public Result(String name, TypeConstructor initializedTypeConstructor, Type returnType) {
            this.name = name;
            this.returnType = returnType;
            this.initializedTypeConstructor = initializedTypeConstructor;
        }

        public static Result of(String name, TypeConstructor initializedTypeConstructor, Type returnType) {
            return new Result(name, initializedTypeConstructor, returnType);
        }

        public String getName() {
            return name;
        }

        public TypeConstructor getInitializedTypeConstructor() {
            return initializedTypeConstructor;
        }

        public Type getReturnType() {
            return returnType;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Result that = (Result) o;
            return getName().equals(that.getName()) && getInitializedTypeConstructor().equals(that.getInitializedTypeConstructor()) && getReturnType().equals(that.getReturnType());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getName(), getInitializedTypeConstructor(), getReturnType());
        }

        @Override
        public String toString() {
            return "Result{" +
                    "name='" + name + '\'' +
                    ", initializedTypeConstructor=" + initializedTypeConstructor +
                    ", returnType=" + returnType +
                    '}';
        }
    }
}
