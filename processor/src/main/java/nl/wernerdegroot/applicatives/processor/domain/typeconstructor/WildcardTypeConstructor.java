package nl.wernerdegroot.applicatives.processor.domain.typeconstructor;

import nl.wernerdegroot.applicatives.processor.domain.BoundType;
import nl.wernerdegroot.applicatives.processor.domain.TypeParameterName;
import nl.wernerdegroot.applicatives.processor.domain.type.Type;
import nl.wernerdegroot.applicatives.processor.domain.type.WildcardType;

import java.util.Map;
import java.util.Objects;

public class WildcardTypeConstructor implements TypeConstructor {

    private final BoundType type;
    private final TypeConstructor bound;

    public WildcardTypeConstructor(BoundType type, TypeConstructor bound) {
        this.type = type;
        this.bound = bound;
    }

    public static WildcardTypeConstructor of(BoundType boundType, TypeConstructor bound) {
        return new WildcardTypeConstructor(boundType, bound);
    }

    @Override
    public WildcardTypeConstructor replaceAllTypeParameterNames(Map<TypeParameterName, TypeParameterName> replacement) {
        return TypeConstructor.wildcard(type, bound.replaceAllTypeParameterNames(replacement));
    }

    @Override
    public boolean canAcceptValueOfType(TypeConstructor that) {
        // If `this` is equal to `that` or some covariant/contravariant version of `that` we return `true`:
        return Objects.equals(this, that) || bound.canAcceptValueOfType(that);
    }

    @Override
    public WildcardType apply(Type toApplyTo) {
        return Type.wildcard(type, bound.apply(toApplyTo));
    }

    public BoundType getType() {
        return type;
    }

    public TypeConstructor getBound() {
        return bound;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WildcardTypeConstructor that = (WildcardTypeConstructor) o;
        return getType() == that.getType() && getBound().equals(that.getBound());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getType(), getBound());
    }

    @Override
    public String toString() {
        return "WildcardTypeConstructor{" +
                "type=" + type +
                ", bound=" + bound +
                '}';
    }
}
