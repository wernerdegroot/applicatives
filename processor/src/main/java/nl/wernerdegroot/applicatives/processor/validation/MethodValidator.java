package nl.wernerdegroot.applicatives.processor.validation;

import nl.wernerdegroot.applicatives.processor.domain.AccumulatorMethod;
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

public class MethodValidator {

    public static Validated<AccumulatorMethod> validate(Method method) {
        if (method.getModifiers().contains(STATIC)) {
            return Validated.invalid("Method is static and cannot implement an abstract method");
        }

        if (method.getModifiers().contains(PRIVATE)) {
            return Validated.invalid("Method is private and cannot implement an abstract method");
        }

        List<TypeParameter> typeParameters = method.getTypeParameters();
        Optional<Type> optionalResultType = method.getReturnType();
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
        TypeParameter resultTypeConstructorArgument = typeParameters.get(2);

        // We require the method to have a return type:
        if (!optionalResultType.isPresent()) {
            return Validated.invalid("Method needs to return something");
        }

        // Now that we are sure that there is a return type, extract it from the `Optional`:
        Type resultType = optionalResultType.get();

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
        Type expectedCombinatorParameter = BI_FUNCTION.with(leftInputTypeConstructorArgument.contravariant(), rightInputTypeConstructorArgument.contravariant(), resultTypeConstructorArgument.covariant());
        if (!Objects.equals(combinatorParameter.getType(), expectedCombinatorParameter)) {
            return Validated.invalid("Expected third argument to be a " + generateFrom(expectedCombinatorParameter) + " but was " + generateFrom(combinatorParameter.getType()));
        }

        TypeConstructor accumulationTypeConstructor = resultType.asTypeConstructorWithPlaceholderFor(resultTypeConstructorArgument.getName());
        TypeConstructor permissiveAccumulationTypeConstructor = leftParameter.getType().asTypeConstructorWithPlaceholderFor(leftInputTypeConstructorArgument.getName());
        TypeConstructor inputTypeConstructor = rightParameter.getType().asTypeConstructorWithPlaceholderFor(rightInputTypeConstructorArgument.getName());

        if (!permissiveAccumulationTypeConstructor.canAccept(accumulationTypeConstructor)) {
            // Tweak the error message to not confuse people using the simple case where
            // parameter types and result type should be identical:
            if (Objects.equals(permissiveAccumulationTypeConstructor, inputTypeConstructor)) {
                return Validated.invalid("No shared type constructor between parameters (" + generateFrom(leftParameter.getType()) + " and " + generateFrom(rightParameter.getType()) + ") and result (" + generateFrom(resultType) + ")");
            } else {
                return Validated.invalid("No shared type constructor between left parameter (" + generateFrom(leftParameter.getType()) + ") and result (" + generateFrom(resultType) + ")");
            }
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
        //    completely (isn't used as upper bound for any of the other type parameters)
        if (!method.getContainingClass().isOuterClass() && !method.getContainingClass().isStaticInnerClass()) {
            return Validated.invalid("Only outer classes and static inner classes are currently supported");
        }
        List<TypeParameter> classTypeParameters = method.getContainingClass().getTypeParameters();

        return Validated.valid(
                AccumulatorMethod.of(
                        accumulationTypeConstructor,
                        permissiveAccumulationTypeConstructor,
                        inputTypeConstructor,
                        classTypeParameters
                )
        );
    }
}