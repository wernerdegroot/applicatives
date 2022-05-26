package nl.wernerdegroot.applicatives.processor.validation;

import nl.wernerdegroot.applicatives.processor.domain.Method;
import nl.wernerdegroot.applicatives.processor.domain.Parameter;
import nl.wernerdegroot.applicatives.processor.domain.TypeParameter;
import nl.wernerdegroot.applicatives.processor.domain.type.Type;
import nl.wernerdegroot.applicatives.processor.domain.typeconstructor.TypeConstructor;

import java.util.List;
import java.util.Objects;

import static nl.wernerdegroot.applicatives.processor.domain.type.Type.BI_FUNCTION;
import static nl.wernerdegroot.applicatives.processor.generator.TypeGenerator.generateFrom;

public class CovariantAccumulatorValidator {

    public static Validated<ValidCovariantAccumulator> validate(Method method) {

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

        // Now that we are sure that there is a return type, extract it from the `Optional`:
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

        return Validated.valid(
                ValidCovariantAccumulator.of(
                        name,
                        inputTypeConstructor,
                        partiallyAccumulatedTypeConstructor,
                        accumulatedTypeConstructor,
                        leftParameter.getType()
                )
        );
    }
}