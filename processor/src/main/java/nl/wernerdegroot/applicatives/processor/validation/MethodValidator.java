package nl.wernerdegroot.applicatives.processor.validation;

import nl.wernerdegroot.applicatives.processor.domain.Method;
import nl.wernerdegroot.applicatives.processor.domain.Parameter;
import nl.wernerdegroot.applicatives.processor.domain.TypeParameter;
import nl.wernerdegroot.applicatives.processor.domain.type.Type;
import nl.wernerdegroot.applicatives.processor.domain.typeconstructor.TypeConstructor;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import static nl.wernerdegroot.applicatives.processor.domain.Modifier.PRIVATE;
import static nl.wernerdegroot.applicatives.processor.domain.Modifier.STATIC;
import static nl.wernerdegroot.applicatives.processor.domain.type.Type.BI_FUNCTION;
import static nl.wernerdegroot.applicatives.processor.domain.type.Type.OBJECT;
import static nl.wernerdegroot.applicatives.processor.generator.TypeGenerator.generateFrom;

public class MethodValidator {

    public static ValidatedMethod validate(Method method) {
        if (method.getModifiers().contains(STATIC)) {
            return ValidatedMethod.invalid("Method is static and cannot implement an abstract method");
        }

        if (method.getModifiers().contains(PRIVATE)) {
            return ValidatedMethod.invalid("Method is private and cannot implement an abstract method");
        }

        List<TypeParameter> typeParameters = method.getTypeParameters();
        int numberOfTypeParameters = typeParameters.size();
        if (numberOfTypeParameters < 3) {
            return ValidatedMethod.invalid("Method needs at least 3 type parameters, but found only " + numberOfTypeParameters);
        }

        TypeParameter leftTypeParameter = typeParameters.get(0);
        TypeParameter rightTypeParameter = typeParameters.get(1);
        TypeParameter resultTypeParameter = typeParameters.get(2);
        List<TypeParameter> secondaryTypeParameters = typeParameters.subList(3, numberOfTypeParameters);

        boolean firstThreeTypeParametersHaveUpperBound = Stream.of(leftTypeParameter, rightTypeParameter, resultTypeParameter)
                .map(TypeParameter::getUpperBounds)
                .flatMap(List::stream)
                .anyMatch(type -> !OBJECT.equals(type));
        if (firstThreeTypeParametersHaveUpperBound) {
            return ValidatedMethod.invalid("The first 3 type parameters need to be unbounded");
        }

        Optional<Type> optionalResultType = method.getReturnType();
        if (!optionalResultType.isPresent()) {
            return ValidatedMethod.invalid("Method needs to return something");
        }
        Type resultType = optionalResultType.get();

        List<Parameter> parameters = method.getParameters();
        int numberOfParameters = parameters.size();
        if (numberOfParameters < 3) {
            return ValidatedMethod.invalid("Method needs at least 3 parameters, but found only " + numberOfParameters);
        }

        Parameter leftParameter = parameters.get(0);
        Parameter rightParameter = parameters.get(1);
        Parameter combineParameter = parameters.get(2);
        List<Parameter> secondaryParameters = parameters.subList(3, numberOfParameters);
        Type expectedCombineParameter = BI_FUNCTION.with(leftTypeParameter.contravariant(), rightTypeParameter.contravariant(), resultTypeParameter.covariant());

        if (!Objects.equals(combineParameter.getType(), expectedCombineParameter)) {
            return ValidatedMethod.invalid("Expected third argument to be a " + generateFrom(expectedCombineParameter) + " but was " + generateFrom(combineParameter.getType()));
        }

        for (Parameter secondaryParameter : secondaryParameters) {
            Type secondaryParameterType = secondaryParameter.getType();
            String secondaryParameterName = secondaryParameter.getName();
            if (secondaryParameterType.contains(leftTypeParameter, rightTypeParameter, resultTypeParameter)) {
                String message = String.format("Parameter with name \"%s\" cannot reference %s, %s or %s", secondaryParameterName, leftTypeParameter.getName().raw(), rightTypeParameter.getName().raw(), resultTypeParameter.getName().raw());
                return ValidatedMethod.invalid(message);
            }
        }

        // Compare if the type constructors for the left parameter and the right parameter are exactly the same:
        TypeConstructor leftTypeConstructor = leftParameter.getType().asTypeConstructorWithPlaceholderFor(leftTypeParameter.getName());
        TypeConstructor rightTypeConstructor = rightParameter.getType().asTypeConstructorWithPlaceholderFor(rightTypeParameter.getName());
        if (!Objects.equals(leftTypeConstructor, rightTypeConstructor)) {
            return ValidatedMethod.invalid("No shared type constructor between left parameter (" + generateFrom(leftParameter.getType()) + ") and right parameter (" + generateFrom(rightParameter.getType()) + ")");
        }

        // Pick any:
        TypeConstructor parameterTypeConstructor = leftTypeConstructor;

        // We also need to compare the type constructor for the parameters with the type constructor
        // for the result. We can be somewhat lenient, as long as the result can be passed as an
        // argument itself. This is the case when the parameter type is some covariant version of the
        // result type.
        TypeConstructor resultTypeConstructor = resultType.asTypeConstructorWithPlaceholderFor(resultTypeParameter.getName());
        if (!parameterTypeConstructor.canAccept(resultTypeConstructor)) {
            return ValidatedMethod.invalid("No shared type constructor between parameters (" + generateFrom(leftParameter.getType()) + " and " + generateFrom(rightParameter.getType()) + ") and result (" + generateFrom(resultType) + ")");
        }

        // Finally, we check whether we're dealing with an outer class or a static inner class.
        // Why is this required? Consider the following scenario:
        //
        //   class Outer<A extends B, B> {
        //     class Inner<B> {
        //       ...
        //     }
        //   }
        //
        // It is impossible to collapse these three type parameters of these two classes into a
        // single list of type parameters without carefully renaming some of them to avoid shadowing.
        // Instead of going through all that effort for this extreme edge-case, I'm just going avoid
        // it completely. If we'd like to be more sophisticated we could try:
        //
        //  * To support only a single class with type parameters in the hierarchy
        //  * Only support multiple classes with type parameters if their names don't conflict
        //  * Only support conflicts if the type parameter that is shadowed can be removed
        //    completely (isn't used as bound for any of the other type parameters)
        if (!method.getContainingClass().isOuterClass() && !method.getContainingClass().isStaticInnerClass()) {
            return ValidatedMethod.invalid("Only outer classes and static inner classes are supported");
        }
        List<TypeParameter> classTypeParameters = method.getContainingClass().getTypeParameters();

        return ValidatedMethod.valid(
                secondaryTypeParameters,
                secondaryParameters,
                parameterTypeConstructor,
                resultTypeConstructor,
                classTypeParameters
        );
    }
}