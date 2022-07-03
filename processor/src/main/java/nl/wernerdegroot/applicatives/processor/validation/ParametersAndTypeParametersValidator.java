package nl.wernerdegroot.applicatives.processor.validation;

import nl.wernerdegroot.applicatives.processor.domain.Parameter;
import nl.wernerdegroot.applicatives.processor.domain.TypeParameter;
import nl.wernerdegroot.applicatives.processor.domain.type.Type;

import java.util.List;
import java.util.Objects;

public interface ParametersAndTypeParametersValidator {
    Result validateTypeParametersAndParameters(List<TypeParameter> typeParameters, List<Parameter> parameters, List<String> errorMessages);

    class Result {
        private final TypeParameter leftInputTypeConstructorArgument;
        private final TypeParameter rightInputTypeConstructorArgument;
        private final TypeParameter returnTypeConstructorArgument;
        private final Type leftParameterType;
        private final Type rightParameterType;

        public Result(TypeParameter leftInputTypeConstructorArgument, TypeParameter rightInputTypeConstructorArgument, TypeParameter returnTypeConstructorArgument, Type leftParameterType, Type rightParameterType) {
            this.leftInputTypeConstructorArgument = leftInputTypeConstructorArgument;
            this.rightInputTypeConstructorArgument = rightInputTypeConstructorArgument;
            this.returnTypeConstructorArgument = returnTypeConstructorArgument;
            this.leftParameterType = leftParameterType;
            this.rightParameterType = rightParameterType;
        }

        public static Result of(TypeParameter leftInputTypeConstructorArgument, TypeParameter rightInputTypeConstructorArgument, TypeParameter returnTypeConstructorArgument, Type leftParameterType, Type rightParameterType) {
            return new Result(
                    leftInputTypeConstructorArgument,
                    rightInputTypeConstructorArgument,
                    returnTypeConstructorArgument,
                    leftParameterType,
                    rightParameterType
            );
        }

        public TypeParameter getLeftInputTypeConstructorArgument() {
            return leftInputTypeConstructorArgument;
        }

        public TypeParameter getRightInputTypeConstructorArgument() {
            return rightInputTypeConstructorArgument;
        }

        public TypeParameter getReturnTypeConstructorArgument() {
            return returnTypeConstructorArgument;
        }

        public Type getLeftParameterType() {
            return leftParameterType;
        }

        public Type getRightParameterType() {
            return rightParameterType;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Result that = (Result) o;
            return Objects.equals(getLeftInputTypeConstructorArgument(), that.getLeftInputTypeConstructorArgument()) && Objects.equals(getRightInputTypeConstructorArgument(), that.getRightInputTypeConstructorArgument()) && Objects.equals(getReturnTypeConstructorArgument(), that.getReturnTypeConstructorArgument()) && Objects.equals(getLeftParameterType(), that.getLeftParameterType()) && Objects.equals(getRightParameterType(), that.getRightParameterType());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getLeftInputTypeConstructorArgument(), getRightInputTypeConstructorArgument(), getReturnTypeConstructorArgument(), getLeftParameterType(), getRightParameterType());
        }

        @Override
        public String toString() {
            return "Result{" +
                    "leftInputTypeConstructorArgument=" + leftInputTypeConstructorArgument +
                    ", rightInputTypeConstructorArgument=" + rightInputTypeConstructorArgument +
                    ", returnTypeConstructorArgument=" + returnTypeConstructorArgument +
                    ", leftParameterType=" + leftParameterType +
                    ", rightParameterType=" + rightParameterType +
                    '}';
        }
    }
}
