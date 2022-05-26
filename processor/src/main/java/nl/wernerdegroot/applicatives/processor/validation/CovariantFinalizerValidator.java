package nl.wernerdegroot.applicatives.processor.validation;

import nl.wernerdegroot.applicatives.processor.domain.Method;
import nl.wernerdegroot.applicatives.processor.domain.Parameter;
import nl.wernerdegroot.applicatives.processor.domain.TypeParameter;
import nl.wernerdegroot.applicatives.processor.domain.type.Type;
import nl.wernerdegroot.applicatives.processor.domain.typeconstructor.TypeConstructor;

public class CovariantFinalizerValidator {

    public static Validated<ValidCovariantFinalizer> validate(Method method) {
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

        // Extract the type constructor from the return type:
        TypeConstructor finalizedTypeConstructor = returnType.asTypeConstructorWithPlaceholderFor(typeParameter.getName());

        Parameter parameter = method.getParameters().get(0);

        // Extract the type constructor from the single parameter:
        TypeConstructor toFinalizeTypeConstructor = parameter.getType().asTypeConstructorWithPlaceholderFor(typeParameter.getName());

        return Validated.valid(ValidCovariantFinalizer.of(name, parameter.getType(), toFinalizeTypeConstructor, finalizedTypeConstructor));
    }
}
