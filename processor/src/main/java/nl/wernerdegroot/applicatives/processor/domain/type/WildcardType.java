package nl.wernerdegroot.applicatives.processor.domain.type;

import nl.wernerdegroot.applicatives.processor.domain.BoundType;
import nl.wernerdegroot.applicatives.processor.domain.TypeParameterName;
import nl.wernerdegroot.applicatives.processor.domain.typeconstructor.TypeConstructor;
import nl.wernerdegroot.applicatives.processor.domain.typeconstructor.WildcardTypeConstructor;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public class WildcardType implements Type {

    private final BoundType type;
    private final Type bound;

    public WildcardType(BoundType type, Type bound) {
        this.type = type;
        this.bound = bound;
    }

    public static WildcardType of(BoundType type, Type bound) {
        return new WildcardType(type, bound);
    }

    @Override
    public <R> R match(Function<GenericType, R> matchGeneric, Function<ConcreteType, R> matchConcrete, Function<WildcardType, R> matchWildcard, Function<ArrayType, R> matchArray) {
        return matchWildcard.apply(this);
    }

    @Override
    public WildcardTypeConstructor asTypeConstructor() {
        return TypeConstructor.wildcard(type, bound.asTypeConstructor());
    }

    @Override
    public TypeConstructor asTypeConstructorWithPlaceholderFor(TypeParameterName needle) {
        return TypeConstructor.wildcard(type, bound.asTypeConstructorWithPlaceholderFor(needle));
    }

    @Override
    public boolean contains(TypeParameterName needle) {
        return bound.contains(needle);
    }

    @Override
    public WildcardType replaceAllTypeParameterNames(Map<TypeParameterName, TypeParameterName> replacement) {
        return Type.wildcard(type, bound.replaceAllTypeParameterNames(replacement));
    }

    public BoundType getType() {
        return type;
    }

    public Type getBound() {
        return bound;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WildcardType that = (WildcardType) o;
        return getType() == that.getType() && getBound().equals(that.getBound());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getType(), getBound());
    }

    @Override
    public String toString() {
        return "WildcardType{" +
                "type=" + type +
                ", bound=" + bound +
                '}';
    }
}
