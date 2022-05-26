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

    public static Validated<ValidCovariantInitializer> validate(Method method) {
        MethodValidation methodValidation = MethodValidation.of(method)
                .verifyCanImplementAbstractMethod()
                .verifyParameterCount("exactly 1", numberOfParameters -> numberOfParameters == 1)
                .verifyTypeParameterCount("exactly 1", numberOfTypeParameters -> numberOfTypeParameters == 1)
                .verifyTypeParametersAreUnbounded()
                .verifyHasReturnType();

        if (!methodValidation.isValid()) {
            return Validated.invalid(methodValidation.getErrorMessages());
        }

        String name = method.getName();

        TypeParameter typeParameter = method.getTypeParameters().get(0);

        Type returnType = methodValidation.getReturnType();

        Parameter parameter = method.getParameters().get(0);

        // Check if the parameter is as expected:
        Type expectedParameterType = typeParameter.asType();
        if (!Objects.equals(parameter.getType(), expectedParameterType)) {
            return Validated.invalid("Expected parameter to be " + generateFrom(expectedParameterType) + " but was " + generateFrom(parameter.getType()));
        }

        TypeConstructor initializedTypeConstructor = returnType.asTypeConstructorWithPlaceholderFor(typeParameter.getName());

        return Validated.valid(ValidCovariantInitializer.of(name, initializedTypeConstructor, returnType));
    }
}
