package nl.wernerdegroot.applicatives.processor.domain.typeconstructor;

import nl.wernerdegroot.applicatives.processor.domain.BoundType;
import nl.wernerdegroot.applicatives.processor.domain.TypeParameterName;
import nl.wernerdegroot.applicatives.processor.domain.type.Type;
import nl.wernerdegroot.applicatives.processor.domain.type.WildcardType;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

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
    public boolean canAccept(TypeConstructor typeConstructor) {
        if (typeConstructor instanceof WildcardTypeConstructor) {
            WildcardTypeConstructor that = (WildcardTypeConstructor) typeConstructor;
            if (this.type == that.type) {
                // If both are of the same bound type, we ignore the wildcard on the right.
                return this.canAccept(that.bound);
            } else {
                // If both are of a different bound type, there is no way that they are compatible.
                return false;
            }
        } else {
            switch (type) {
                case EXTENDS:
                    // Since we don't check for subclasses, we will return `true` if
                    // the bound can accept a value of the type represented by `typeConstructor`.
                    return bound.canAccept(typeConstructor);
                case SUPER:
                    // In case of a contravariant wildcard type, we need to swap the order
                    // of comparison. Can `? super Dog` accept a value of type `Animal`?
                    // Only if `? extends Animal` can accept a value of type `Dog`!
                    return typeConstructor.canAccept(bound);
                default:
                    throw new NotImplementedException();
            }
        }
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
