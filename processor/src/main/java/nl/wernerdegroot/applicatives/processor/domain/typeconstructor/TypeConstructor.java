package nl.wernerdegroot.applicatives.processor.domain.typeconstructor;

import nl.wernerdegroot.applicatives.processor.domain.FullyQualifiedName;
import nl.wernerdegroot.applicatives.processor.domain.TypeParameterName;
import nl.wernerdegroot.applicatives.processor.domain.type.Type;

import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static nl.wernerdegroot.applicatives.processor.domain.Variance.*;

/**
 * A type constructor is a construct that accepts a type to produce a type.
 * <p>
 * One example of such a type constructor is a {@link List List}
 * (without the type argument). As a type constructor, a bare {@link List List}
 * is not "complete" yet. We need to tell it what the type of the element is
 * before it can act as a type ({@link Type Type}).
 * As a mathematica formula, this would look like:
 * <pre>
 *     List&lt;*&gt; + T = List&lt;T&gt;
 * </pre>
 * In the language of {@link nl.wernerdegroot.applicatives.processor.domain} this would look like:
 * <pre>
 *     TypeConstructor list = TypeConstructor.concrete(
 *         FullyQualifiedName.of("java.util.List"),
 *         asList(TypeConstructor.placeholder())
 *     );
 *     Type t = Type.generic(TypeParameterName.of("T"));
 *
 *     Type listOfT = list.apply(t);
 * </pre>
 */
public interface TypeConstructor {

    TypeConstructor replaceAllTypeParameterNames(Map<TypeParameterName, TypeParameterName> replacement);

    /**
     * Checks whether a value of type {@code that} is assignable to a variable of type {@code this}. Returns true
     * if {@code this} is equal to {@code that} or some covariant or contravariant version of {@code that} (or
     * even a mix).
     *
     * @param that
     * @return
     */
    boolean canAccept(TypeConstructor that);

    /**
     * Provide a {@link Type Type} and get a {@link Type Type} in return.
     */
    Type apply(Type toApplyTo);

    /**
     * Provide a {@link Type Type} and get a {@link Type Type} in return.
     * This method assumes you are trying to apply a {@link nl.wernerdegroot.applicatives.processor.domain.type.GenericType GenericType}
     * handles the conversion from {@link TypeParameterName TypeParameterName} to
     * {@link nl.wernerdegroot.applicatives.processor.domain.type.GenericType GenericType} for you.
     */
    default Type apply(TypeParameterName toApplyTo) {
        return apply(toApplyTo.asType());
    }

    default ArrayTypeConstructor array() {
        return array(this);
    }

    default TypeConstructorArgument invariant() {
        return TypeConstructorArgument.of(INVARIANT, this);
    }

    default TypeConstructorArgument covariant() {
        return TypeConstructorArgument.of(COVARIANT, this);
    }

    default TypeConstructorArgument contravariant() {
        return TypeConstructorArgument.of(CONTRAVARIANT, this);
    }

    static GenericTypeConstructor generic(TypeParameterName name) {
        return new GenericTypeConstructor(name);
    }

    static ConcreteTypeConstructor concrete(FullyQualifiedName fullyQualifiedName, List<TypeConstructorArgument> typeConstructorArguments) {
        return new ConcreteTypeConstructor(fullyQualifiedName, typeConstructorArguments);
    }

    static ConcreteTypeConstructor concrete(FullyQualifiedName fullyQualifiedName, TypeConstructorArgument... typeConstructorArguments) {
        return new ConcreteTypeConstructor(fullyQualifiedName, asList(typeConstructorArguments));
    }

    static ArrayTypeConstructor array(TypeConstructor type) {
        return new ArrayTypeConstructor(type);
    }

    static PlaceholderTypeConstructor placeholder() {
        return new PlaceholderTypeConstructor();
    }
}
