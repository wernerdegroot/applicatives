package nl.wernerdegroot.applicatives.processor.validation;

import nl.wernerdegroot.applicatives.processor.domain.Method;
import nl.wernerdegroot.applicatives.processor.domain.Parameter;
import nl.wernerdegroot.applicatives.processor.domain.TypeParameter;
import nl.wernerdegroot.applicatives.processor.domain.type.Type;
import nl.wernerdegroot.applicatives.processor.domain.typeconstructor.TypeConstructor;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static nl.wernerdegroot.applicatives.processor.domain.Modifier.PRIVATE;
import static nl.wernerdegroot.applicatives.processor.domain.Modifier.STATIC;
import static nl.wernerdegroot.applicatives.processor.domain.type.Type.BI_FUNCTION;
import static nl.wernerdegroot.applicatives.processor.domain.type.Type.OBJECT;
import static nl.wernerdegroot.applicatives.processor.generator.TypeGenerator.generateFrom;

public class CovariantAccumulatorValidator {

    public static Validated<CovariantAccumulator> validate(Method method) {
        if (method.getModifiers().contains(STATIC)) {
            return Validated.invalid("Method is static and cannot implement an abstract method");
        }

        if (method.getModifiers().contains(PRIVATE)) {
            return Validated.invalid("Method is private and cannot implement an abstract method");
        }

        List<TypeParameter> typeParameters = method.getTypeParameters();
        Optional<Type> optionalReturnType = method.getReturnType();
        List<Parameter> parameters = method.getParameters();

        // We require exactly three type parameters:
        int numberOfTypeParameters = typeParameters.size();
        if (numberOfTypeParameters != 3) {
            return Validated.invalid("Method requires exactly 3 type parameters, but found " + numberOfTypeParameters);
        }

        // We require the (three) type parameters to be unbounded:
        boolean typeParametersHaveUpperBound = typeParameters
                .stream()
                .map(TypeParameter::getUpperBounds)
                .flatMap(List::stream)
                .anyMatch(type -> !OBJECT.equals(type));
        if (typeParametersHaveUpperBound) {
            return Validated.invalid("The type parameters need to be unbounded");
        }

        // Assign a meaningful name to each of the (three) type parameters:
        TypeParameter leftInputTypeConstructorArgument = typeParameters.get(0);
        TypeParameter rightInputTypeConstructorArgument = typeParameters.get(1);
        TypeParameter returnTypeConstructorArgument = typeParameters.get(2);

        // We require the method to have a return type:
        if (!optionalReturnType.isPresent()) {
            return Validated.invalid("Method needs to return something");
        }

        // Now that we are sure that there is a return type, extract it from the `Optional`:
        Type returnType = optionalReturnType.get();

        String name = method.getName();

        // We require exactly three parameters:
        int numberOfParameters = parameters.size();
        if (numberOfParameters != 3) {
            return Validated.invalid("Method requires exactly 3 parameters, but found " + numberOfParameters);
        }

        // Assign a meaningful name to each of the (three) parameters:
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

        return Validated.valid(
                CovariantAccumulator.of(
                        name,
                        inputTypeConstructor,
                        partiallyAccumulatedTypeConstructor,
                        accumulatedTypeConstructor,
                        leftParameter.getType()
                )
        );
    }
}