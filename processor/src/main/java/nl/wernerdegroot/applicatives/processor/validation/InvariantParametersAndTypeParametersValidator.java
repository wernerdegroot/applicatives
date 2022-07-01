package nl.wernerdegroot.applicatives.processor.validation;

import nl.wernerdegroot.applicatives.processor.domain.Parameter;
import nl.wernerdegroot.applicatives.processor.domain.TypeParameter;
import nl.wernerdegroot.applicatives.processor.domain.type.Type;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static nl.wernerdegroot.applicatives.processor.domain.type.Type.BI_FUNCTION;
import static nl.wernerdegroot.applicatives.processor.domain.type.Type.FUNCTION;
import static nl.wernerdegroot.applicatives.processor.generator.TypeGenerator.generateFrom;
import static nl.wernerdegroot.applicatives.processor.validation.Common.verifyParameterCount;
import static nl.wernerdegroot.applicatives.processor.validation.Common.verifyTypeParameterCount;

public class InvariantParametersAndTypeParametersValidator implements ParametersAndTypeParametersValidator {

    @Override
    public Result validateTypeParametersAndParameters(List<TypeParameter> typeParameters, List<Parameter> parameters, Set<String> errorMessages) {
        Set<String> additionalErrorMessages = new HashSet<>();

        verifyParameterCount(parameters, 6, additionalErrorMessages);
        verifyTypeParameterCount(typeParameters, 4, additionalErrorMessages);

        if (!additionalErrorMessages.isEmpty()) {
            errorMessages.addAll(additionalErrorMessages);
            return null;
        }

        TypeParameter leftInputTypeConstructorArgument = typeParameters.get(0);
        TypeParameter rightInputTypeConstructorArgument = typeParameters.get(1);
        TypeParameter intermediateTypeConstructorArgument = typeParameters.get(2);
        TypeParameter returnTypeConstructorArgument = typeParameters.get(3);

        Parameter leftParameter = parameters.get(0);
        Parameter rightParameter = parameters.get(1);
        Parameter combinatorParameter = parameters.get(2);
        Parameter toIntermediateParameter = parameters.get(3);
        Parameter extractLeftParameter = parameters.get(4);
        Parameter extractRightParameter = parameters.get(5);

        // Check if the third parameter is as expected:
        Type expectedCombinatorType = BI_FUNCTION.with(leftInputTypeConstructorArgument.asType().contravariant(), rightInputTypeConstructorArgument.asType().contravariant(), returnTypeConstructorArgument.asType().covariant());
        if (!Objects.equals(combinatorParameter.getType(), expectedCombinatorType)) {
            additionalErrorMessages.add("Expected third argument to be a " + generateFrom(expectedCombinatorType) + " but was " + generateFrom(combinatorParameter.getType()));
        }

        // Check if the fourth parameter is as expected:
        Type expectedToIntermediateType = FUNCTION.with(returnTypeConstructorArgument.asType().contravariant(), intermediateTypeConstructorArgument.asType().covariant());
        if (!Objects.equals(toIntermediateParameter.getType(), expectedToIntermediateType)) {
            additionalErrorMessages.add("Expected fourth argument to be a " + generateFrom(expectedToIntermediateType) + " but was " + generateFrom(toIntermediateParameter.getType()));
        }

        // Check if the fifth parameter is as expected:
        Type expectedExtractLeftType = FUNCTION.with(intermediateTypeConstructorArgument.asType().contravariant(), leftInputTypeConstructorArgument.asType().covariant());
        if (!Objects.equals(extractLeftParameter.getType(), expectedExtractLeftType)) {
            additionalErrorMessages.add("Expected fifth argument to be a " + generateFrom(expectedExtractLeftType) + " but was " + generateFrom(extractLeftParameter.getType()));
        }

        // Check if the sixth parameter is as expected:
        Type expectedExtractRightType = FUNCTION.with(intermediateTypeConstructorArgument.asType().contravariant(), rightInputTypeConstructorArgument.asType().covariant());
        if (!Objects.equals(extractRightParameter.getType(), expectedExtractRightType)) {
            additionalErrorMessages.add("Expected sixth argument to be a " + generateFrom(expectedExtractRightType) + " but was " + generateFrom(extractRightParameter.getType()));
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