package nl.wernerdegroot.applicatives.processor.validation;

import nl.wernerdegroot.applicatives.processor.domain.Method;
import nl.wernerdegroot.applicatives.processor.domain.TypeParameter;
import nl.wernerdegroot.applicatives.processor.domain.type.Type;
import nl.wernerdegroot.applicatives.processor.domain.typeconstructor.TypeConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static nl.wernerdegroot.applicatives.processor.generator.TypeGenerator.generateFrom;
import static nl.wernerdegroot.applicatives.processor.validation.Common.*;

public class AccumulatorValidator {

    public static Validated<String, Result> validate(Method method, ParametersAndTypeParametersValidator parametersAndTypeParametersValidator) {
        Set<String> errorMessages = new HashSet<>();

        verifyCanImplementAbstractMethod(method, errorMessages);
        verifyTypeParametersAreUnbounded(method, errorMessages);
        Type returnType = verifyHasReturnType(method, errorMessages);
        ParametersAndTypeParametersValidator.Result parametersAndTypeParameters = verifyParametersAndTypeParameters(method, parametersAndTypeParametersValidator, errorMessages);

        if (!errorMessages.isEmpty()) {
            return Validated.invalid(errorMessages);
        }

        // Assign a meaningful name to each of the (three) type parameters:
        TypeParameter leftInputTypeConstructorArgument = parametersAndTypeParameters.getLeftInputTypeConstructorArgument();
        TypeParameter rightInputTypeConstructorArgument = parametersAndTypeParameters.getRightInputTypeConstructorArgument();
        TypeParameter returnTypeConstructorArgument = parametersAndTypeParameters.getReturnTypeConstructorArgument();

        // Assign a meaningful name to the left and right parameters:
        Type leftParameterType = parametersAndTypeParameters.getLeftParameterType();
        Type rightParameterType = parametersAndTypeParameters.getRightParameterType();

        String name = method.getName();

        TypeConstructor accumulatedTypeConstructor = returnType.asTypeConstructorWithPlaceholderFor(returnTypeConstructorArgument.getName());
        TypeConstructor partiallyAccumulatedTypeConstructor = leftParameterType.asTypeConstructorWithPlaceholderFor(leftInputTypeConstructorArgument.getName());
        TypeConstructor inputTypeConstructor = rightParameterType.asTypeConstructorWithPlaceholderFor(rightInputTypeConstructorArgument.getName());

        if (!partiallyAccumulatedTypeConstructor.canAccept(accumulatedTypeConstructor)) {
            // Tweak the error message to not confuse people using the simple case where
            // parameter types and result type should be identical:
            if (Objects.equals(partiallyAccumulatedTypeConstructor, inputTypeConstructor)) {
                return Validated.invalid("No shared type constructor between parameters (" + generateFrom(leftParameterType) + " and " + generateFrom(rightParameterType) + ") and result (" + generateFrom(returnType) + ")");
            } else {
                return Validated.invalid("No shared type constructor between first parameter (" + generateFrom(leftParameterType) + ") and result (" + generateFrom(returnType) + ")");
            }
        }

        verifyNoCrossReferences(method.getTypeParameters(), leftParameterType, partiallyAccumulatedTypeConstructor, "type of the first parameter", errorMessages);
        verifyNoCrossReferences(method.getTypeParameters(), rightParameterType, inputTypeConstructor, "type of the second parameter", errorMessages);
        verifyNoCrossReferences(method.getTypeParameters(), returnType, accumulatedTypeConstructor, "return type", errorMessages);

        if (!errorMessages.isEmpty()) {
            return Validated.invalid(errorMessages);
        }

        return Validated.valid(
                AccumulatorValidator.Result.of(
                        name,
                        inputTypeConstructor,
                        partiallyAccumulatedTypeConstructor,
                        accumulatedTypeConstructor,
                        leftParameterType,
                        rightParameterType,
                        returnType
                )
        );
    }

    private static ParametersAndTypeParametersValidator.Result verifyParametersAndTypeParameters(Method method, ParametersAndTypeParametersValidator parametersAndTypeParametersValidator, Set<String> errorMessages) {
        return parametersAndTypeParametersValidator.validateTypeParametersAndParameters(method.getTypeParameters(), method.getParameters(), errorMessages);
    }

    private static void verifyNoCrossReferences(List<TypeParameter> typeParameters, Type type, TypeConstructor typeConstructor, String descriptionOfType, Set<String> errorMessages) {
        typeParameters
                .stream()
                .map(TypeParameter::getName)
                .filter(typeConstructor::referencesTypeParameter)
                .map(typeParameterName -> String.format("The %s (%s) is not allowed to reference type parameter '%s'", descriptionOfType, generateFrom(type), typeParameterName.raw()))
                .forEach(errorMessages::add);
    }

    public static final class Result {

        private final String name;
        private final TypeConstructor inputTypeConstructor;
        private final TypeConstructor partiallyAccumulatedTypeConstructor;
        private final TypeConstructor accumulatedTypeConstructor;
        private final Type firstParameterType;
        private final Type secondParameterType;
        private final Type returnType;

        public Result(String name, TypeConstructor inputTypeConstructor, TypeConstructor partiallyAccumulatedTypeConstructor, TypeConstructor accumulatedTypeConstructor, Type firstParameterType, Type secondParameterType, Type returnType) {
            this.name = name;
            this.inputTypeConstructor = inputTypeConstructor;
            this.partiallyAccumulatedTypeConstructor = partiallyAccumulatedTypeConstructor;
            this.accumulatedTypeConstructor = accumulatedTypeConstructor;
            this.firstParameterType = firstParameterType;
            this.secondParameterType = secondParameterType;
            this.returnType = returnType;
        }

        public static Result of(String name, TypeConstructor inputTypeConstructor, TypeConstructor partiallyAccumulatedTypeConstructor, TypeConstructor accumulatedTypeConstructor, Type firstParameterType, Type secondParameterType, Type returnType) {
            return new Result(name, inputTypeConstructor, partiallyAccumulatedTypeConstructor, accumulatedTypeConstructor, firstParameterType, secondParameterType, returnType);
        }

        public String getName() {
            return name;
        }

        public TypeConstructor getInputTypeConstructor() {
            return inputTypeConstructor;
        }

        public TypeConstructor getPartiallyAccumulatedTypeConstructor() {
            return partiallyAccumulatedTypeConstructor;
        }

        public TypeConstructor getAccumulatedTypeConstructor() {
            return accumulatedTypeConstructor;
        }

        public Type getFirstParameterType() {
            return firstParameterType;
        }

        public Type getSecondParameterType() {
            return secondParameterType;
        }

        public Type getReturnType() {
            return returnType;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Result result = (Result) o;
            return Objects.equals(getName(), result.getName()) && Objects.equals(getInputTypeConstructor(), result.getInputTypeConstructor()) && Objects.equals(getPartiallyAccumulatedTypeConstructor(), result.getPartiallyAccumulatedTypeConstructor()) && Objects.equals(getAccumulatedTypeConstructor(), result.getAccumulatedTypeConstructor()) && Objects.equals(getFirstParameterType(), result.getFirstParameterType()) && Objects.equals(getSecondParameterType(), result.getSecondParameterType()) && Objects.equals(getReturnType(), result.getReturnType());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getName(), getInputTypeConstructor(), getPartiallyAccumulatedTypeConstructor(), getAccumulatedTypeConstructor(), getFirstParameterType(), getSecondParameterType(), getReturnType());
        }

        @Override
        public String toString() {
            return "Result{" +
                    "name='" + name + '\'' +
                    ", inputTypeConstructor=" + inputTypeConstructor +
                    ", partiallyAccumulatedTypeConstructor=" + partiallyAccumulatedTypeConstructor +
                    ", accumulatedTypeConstructor=" + accumulatedTypeConstructor +
                    ", firstParameterType=" + firstParameterType +
                    ", secondParameterType=" + secondParameterType +
                    ", returnType=" + returnType +
                    '}';
        }
    }
}