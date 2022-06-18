package nl.wernerdegroot.applicatives.processor.validation;

import nl.wernerdegroot.applicatives.processor.domain.Method;
import nl.wernerdegroot.applicatives.processor.domain.Parameter;
import nl.wernerdegroot.applicatives.processor.domain.TypeParameter;
import nl.wernerdegroot.applicatives.processor.domain.type.Type;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import static nl.wernerdegroot.applicatives.processor.domain.Modifier.PUBLIC;
import static nl.wernerdegroot.applicatives.processor.domain.Modifier.STATIC;
import static nl.wernerdegroot.applicatives.processor.domain.type.Type.OBJECT;

public class Common {

    public static void verifyParameterCount(List<Parameter> parameters, int expected, Set<String> errorMessages) {
        if (parameters.size() != expected) {
            errorMessages.add("Method requires exactly " + expected + " parameters, but found " + parameters.size());
        }
    }

    public static void verifyTypeParameterCount(List<TypeParameter> typeParameters, int expected, Set<String> errorMessages) {
        if (typeParameters.size() != expected) {
            errorMessages.add("Method requires exactly " + expected + " type parameters, but found " + typeParameters.size());
        }
    }

    public static Type verifyHasReturnType(Method method, Set<String> errorMessages) {
        Optional<Type> optionalReturnType = method.getReturnType();
        if (optionalReturnType.isPresent()) {
            return optionalReturnType.get();
        } else {
            errorMessages.add("Method needs to return something");
            return null;
        }
    }

    public static void verifyTypeParametersAreUnbounded(Method method, Set<String> errorMessages) {
        boolean typeParametersHaveUpperBound = method.getTypeParameters()
                .stream()
                .map(TypeParameter::getUpperBounds)
                .flatMap(List::stream)
                .anyMatch(type -> !OBJECT.equals(type));

        if (typeParametersHaveUpperBound) {
            errorMessages.add("The type parameters need to be unbounded");
        }
    }

    public static void verifyCanImplementAbstractMethod(Method method, Set<String> errorMessages) {
        if (method.getModifiers().contains(STATIC)) {
            errorMessages.add("Method is static and cannot implement an abstract method");
        }

        if (!method.getModifiers().contains(PUBLIC)) {
            errorMessages.add("Method needs to be public to implement an abstract method");
        }
    }
}
