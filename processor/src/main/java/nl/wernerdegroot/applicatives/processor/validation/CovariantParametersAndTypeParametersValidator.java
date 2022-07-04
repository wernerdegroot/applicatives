package nl.wernerdegroot.applicatives.processor.validation;

import nl.wernerdegroot.applicatives.processor.domain.Parameter;
import nl.wernerdegroot.applicatives.processor.domain.TypeParameter;
import nl.wernerdegroot.applicatives.processor.domain.type.Type;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static nl.wernerdegroot.applicatives.processor.domain.type.Type.BI_FUNCTION;
import static nl.wernerdegroot.applicatives.processor.generator.TypeGenerator.generateFrom;
import static nl.wernerdegroot.applicatives.processor.validation.Common.verifyParameterCount;
import static nl.wernerdegroot.applicatives.processor.validation.Common.verifyTypeParameterCount;

public class CovariantParametersAndTypeParametersValidator implements ParametersAndTypeParametersValidator {

    @Override
    public Result validateTypeParametersAndParameters(List<TypeParameter> typeParameters, List<Parameter> parameters, List<String> errorMessages) {
        List<String> additionalErrorMessages = new ArrayList<>();

        verifyParameterCount(parameters, 3, additionalErrorMessages);
        verifyTypeParameterCount(typeParameters, 3, additionalErrorMessages);

        if (!additionalErrorMessages.isEmpty()) {
            errorMessages.addAll(additionalErrorMessages);
            return null;
        }

        TypeParameter leftInputTypeConstructorArgument = typeParameters.get(0);
        TypeParameter rightInputTypeConstructorArgument = typeParameters.get(1);
        TypeParameter returnTypeConstructorArgument = typeParameters.get(2);

        Parameter leftParameter = parameters.get(0);
        Parameter rightParameter = parameters.get(1);
        Parameter combinatorParameter = parameters.get(2);

        // Check if the third parameter is as expected:
        Type expectedCombinatorType = BI_FUNCTION.with(leftInputTypeConstructorArgument.asType().contravariant(), rightInputTypeConstructorArgument.asType().contravariant(), returnTypeConstructorArgument.asType().covariant());
        if (!Objects.equals(combinatorParameter.getType(), expectedCombinatorType)) {
            additionalErrorMessages.add("Expected third argument to be a " + generateFrom(expectedCombinatorType) + " but was " + generateFrom(combinatorParameter.getType()));
        }

        if (additionalErrorMessages.isEmpty()) {
            return Result.of(
                    leftInputTypeConstructorArgument,
                    rightInputTypeConstructorArgument,
                    returnTypeConstructorArgument,
                    leftParameter.getType(),
                    rightParameter.getType()
            );
        } else {
            errorMessages.addAll(additionalErrorMessages);
            return null;
        }
    }
}
