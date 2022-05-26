package nl.wernerdegroot.applicatives.processor.validation;

import nl.wernerdegroot.applicatives.processor.domain.Method;
import nl.wernerdegroot.applicatives.processor.domain.TypeParameter;
import nl.wernerdegroot.applicatives.processor.domain.type.Type;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import static nl.wernerdegroot.applicatives.processor.domain.Modifier.PRIVATE;
import static nl.wernerdegroot.applicatives.processor.domain.Modifier.STATIC;
import static nl.wernerdegroot.applicatives.processor.domain.type.Type.OBJECT;

/**
 * Package-private class, to encapsulate validation rules common
 * to `CovariantInitializerValidator`, `CovariantAccumulatorValidator`
 * and `CovariantFinalizerValidator`.
 */
class MethodValidation {

    private final Method method;
    private final Set<String> errorMessages = new HashSet<>();

    public MethodValidation(Method method) {
        this.method = method;
    }

    public static MethodValidation of(Method method) {
        return new MethodValidation(method);
    }

    public MethodValidation verifyParameterCount(String description, Predicate<Integer> isValid) {
        if (!isValid.test(method.getParameters().size())) {
            errorMessages.add("Method requires " + description + " parameters, but found " + method.getParameters().size());
        }

        return this;
    }

    public MethodValidation verifyTypeParameterCount(String description, Predicate<Integer> isValid) {
        if (!isValid.test(method.getTypeParameters().size())) {
            errorMessages.add("Method requires " + description + " type parameters, but found " + method.getTypeParameters().size());
        }

        return this;
    }

    public MethodValidation verifyTypeParametersAreUnbounded() {
        boolean typeParametersHaveUpperBound = method.getTypeParameters()
                .stream()
                .map(TypeParameter::getUpperBounds)
                .flatMap(List::stream)
                .anyMatch(type -> !OBJECT.equals(type));

        if (typeParametersHaveUpperBound) {
            errorMessages.add("The type parameters need to be unbounded");
        }

        return this;

    }

    public MethodValidation verifyCanImplementAbstractMethod() {
        if (method.getModifiers().contains(STATIC)) {
            errorMessages.add("Method is static and cannot implement an abstract method");
        }

        if (method.getModifiers().contains(PRIVATE)) {
            errorMessages.add("Method is private and cannot implement an abstract method");
        }

        return this;
    }

    public MethodValidation verifyHasReturnType() {
        if (!method.getReturnType().isPresent()) {
            errorMessages.add("Method needs to return something");
        }

        return this;
    }

    public Type getReturnType() {
        return method.getReturnType().get();
    }

    public boolean isValid() {
        return errorMessages.isEmpty();
    }

    public Method getMethod() {
        return method;
    }

    public Set<String> getErrorMessages() {
        return errorMessages;
    }
}
