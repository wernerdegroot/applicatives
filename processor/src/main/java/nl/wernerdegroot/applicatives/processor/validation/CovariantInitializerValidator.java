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
import static nl.wernerdegroot.applicatives.processor.domain.type.Type.OBJECT;
import static nl.wernerdegroot.applicatives.processor.generator.TypeGenerator.generateFrom;

public class CovariantInitializerValidator {

    public static Validated<CovariantInitializer> validate(Method method) {
        if (method.getModifiers().contains(STATIC)) {
            return Validated.invalid("Method is static and cannot implement an abstract method");
        }

        if (method.getModifiers().contains(PRIVATE)) {
            return Validated.invalid("Method is private and cannot implement an abstract method");
        }

        String name = method.getName();
        List<TypeParameter> typeParameters = method.getTypeParameters();
        Optional<Type> optionalReturnType = method.getReturnType();
        List<Parameter> parameters = method.getParameters();

        // We require exactly one type parameter:
        int numberOfTypeParameters = typeParameters.size();
        if (numberOfTypeParameters != 1) {
            return Validated.invalid("Method requires exactly one type parameter, but found " + numberOfTypeParameters);
        }

        TypeParameter typeParameter = typeParameters.get(0);

        // We require the type parameter to be unbounded:
        boolean typeParameterHasUpperBound = typeParameter
                .getUpperBounds()
                .stream()
                .anyMatch(type -> !OBJECT.equals(type));

        if (typeParameterHasUpperBound) {
            return Validated.invalid("The type parameter needs to be unbounded");
        }

        // We require the method to have a return type:
        if (!optionalReturnType.isPresent()) {
            return Validated.invalid("Method needs to return something");
        }

        // Now that we are sure that there is a result type, extract it from the `Optional`:
        Type returnType = optionalReturnType.get();

        // We require exactly one parameter:
        int numberOfParameters = parameters.size();
        if (numberOfParameters != 1) {
            return Validated.invalid("Method requires exactly one parameter, but found " + numberOfParameters);
        }

        Parameter parameter = parameters.get(0);

        // Check if the parameter is as expected:
        Type expectedParameterType = typeParameter.asType();
        if (!Objects.equals(parameter.getType(), expectedParameterType)) {
            return Validated.invalid("Expected parameter to be " + generateFrom(expectedParameterType) + " but was " + generateFrom(parameter.getType()));
        }

        TypeConstructor permissiveAccumulationTypeConstructor = returnType.asTypeConstructorWithPlaceholderFor(typeParameter.getName());

        return Validated.valid(CovariantInitializer.of(name, returnType, permissiveAccumulationTypeConstructor));
    }
}