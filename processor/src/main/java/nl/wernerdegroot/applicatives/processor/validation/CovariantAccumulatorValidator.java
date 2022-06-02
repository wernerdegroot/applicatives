package nl.wernerdegroot.applicatives.processor.validation;

import nl.wernerdegroot.applicatives.processor.domain.Method;
import nl.wernerdegroot.applicatives.processor.domain.Parameter;
import nl.wernerdegroot.applicatives.processor.domain.TypeParameter;
import nl.wernerdegroot.applicatives.processor.domain.type.Type;
import nl.wernerdegroot.applicatives.processor.domain.typeconstructor.TypeConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static java.util.stream.Collectors.toSet;
import static nl.wernerdegroot.applicatives.processor.domain.type.Type.BI_FUNCTION;
import static nl.wernerdegroot.applicatives.processor.generator.TypeGenerator.generateFrom;

public class CovariantAccumulatorValidator {

    public static Validated<Result> validate(Method method) {

        MethodValidation methodValidation = MethodValidation.of(method)
                .verifyCanImplementAbstractMethod()
                .verifyParameterCount("exactly 3", numberOfParameters -> numberOfParameters == 3)
                .verifyTypeParameterCount("exactly 3", numberOfTypeParameters -> numberOfTypeParameters == 3)
                .verifyTypeParametersAreUnbounded()
                .verifyHasReturnType();

        if (!methodValidation.isValid()) {
            return Validated.invalid(methodValidation.getErrorMessages());
        }

        // Assign a meaningful name to each of the (three) type parameters:
        List<TypeParameter> typeParameters = method.getTypeParameters();
        TypeParameter leftInputTypeConstructorArgument = typeParameters.get(0);
        TypeParameter rightInputTypeConstructorArgument = typeParameters.get(1);
        TypeParameter returnTypeConstructorArgument = typeParameters.get(2);

        Type returnType = methodValidation.getReturnType();

        String name = method.getName();

        // Assign a meaningful name to each of the (three) parameters:
        List<Parameter> parameters = method.getParameters();
        Parameter leftParameter = parameters.get(0);
        Parameter rightParameter = parameters.get(1);
        Parameter combinatorParameter = parameters.get(2);

        // Check if the third parameter is as expected:
        Type expectedCombinatorParameter = BI_FUNCTION.with(leftInputTypeConstructorArgument.contravariant(), rightInputTypeConstructorArgument.contravariant(), returnTypeConstructorArgument.covariant());
        if (!Objects.equals(combinatorParameter.getType(), expectedCombinatorParameter)) {
            return Validated.invalid("Expected third argument to be a " + generateFrom(expectedCombinatorParameter) + " but was " + generateFrom(combinatorParameter.getType()));
        }

        TypeConstructor accumulatedTypeConstructor = returnType.asTypeConstructorWithPlaceholderFor(returnTypeConstructorArgument.getName());
        TypeConstructor partiallyAccumulatedTypeConstructor = leftParameter.getType().asTypeConstructorWithPlaceholderFor(leftInputTypeConstructorArgument.getName());
        TypeConstructor inputTypeConstructor = rightParameter.getType().asTypeConstructorWithPlaceholderFor(rightInputTypeConstructorArgument.getName());

        if (!partiallyAccumulatedTypeConstructor.canAccept(accumulatedTypeConstructor)) {
            // Tweak the error message to not confuse people using the simple case where
            // parameter types and result type should be identical:
            if (Objects.equals(partiallyAccumulatedTypeConstructor, inputTypeConstructor)) {
                return Validated.invalid("No shared type constructor between parameters (" + generateFrom(leftParameter.getType()) + " and " + generateFrom(rightParameter.getType()) + ") and result (" + generateFrom(returnType) + ")");
            } else {
                return Validated.invalid("No shared type constructor between first parameter (" + generateFrom(leftParameter.getType()) + ") and result (" + generateFrom(returnType) + ")");
            }
        }

        Set<String> errorMessages = new HashSet<>();
        errorMessages.addAll(checkCrossReferences(typeParameters, rightParameter.getType(), inputTypeConstructor, "type of the second parameter"));
        errorMessages.addAll(checkCrossReferences(typeParameters, leftParameter.getType(), partiallyAccumulatedTypeConstructor, "type of the first parameter"));
        errorMessages.addAll(checkCrossReferences(typeParameters, returnType, accumulatedTypeConstructor, "return type"));
        if (!errorMessages.isEmpty()) {
            return Validated.invalid(errorMessages);
        }

        return Validated.valid(
                Result.of(
                        name,
                        inputTypeConstructor,
                        partiallyAccumulatedTypeConstructor,
                        accumulatedTypeConstructor,
                        leftParameter.getType(),
                        rightParameter.getType(),
                        returnType
                )
        );
    }

    private static Set<String> checkCrossReferences(List<TypeParameter> typeParameters, Type type, TypeConstructor typeConstructor, String descriptionOfType) {
        return typeParameters
                .stream()
                .map(TypeParameter::getName)
                .filter(typeConstructor::referencesTypeParameter)
                .map(typeParameterName -> String.format("The %s (%s) is not allowed to reference type parameter '%s'", descriptionOfType, generateFrom(type), typeParameterName.raw()))
                .collect(toSet());
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