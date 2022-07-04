package nl.wernerdegroot.applicatives.processor.domain;

import nl.wernerdegroot.applicatives.processor.domain.type.GenericType;
import nl.wernerdegroot.applicatives.processor.domain.type.Type;
import nl.wernerdegroot.applicatives.processor.domain.type.TypeArgument;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static nl.wernerdegroot.applicatives.processor.domain.type.Type.OBJECT;

public final class TypeParameter {

    private final TypeParameterName name;
    private final List<Type> upperBounds;

    public TypeParameter(TypeParameterName name, List<Type> upperBounds) {
        this.name = name;
        this.upperBounds = upperBounds.stream().filter(upperBound -> !Objects.equals(upperBound, OBJECT)).collect(toList());
    }

    public static TypeParameter of(TypeParameterName name, List<Type> upperBounds) {
        return new TypeParameter(name, upperBounds);
    }

    public static TypeParameter of(TypeParameterName name, Type... upperBounds) {
        return new TypeParameter(name, asList(upperBounds));
    }

    public GenericType asType() {
        return getName().asType();
    }

    public TypeArgument invariant() {
        return asType().invariant();
    }

    public TypeArgument covariant() {
        return asType().covariant();
    }

    public TypeArgument contravariant() {
        return asType().contravariant();
    }

    public TypeParameter replaceAllTypeParameterNames(Map<TypeParameterName, TypeParameterName> replacement) {
        List<Type> replaceUpperBounds = upperBounds.stream().map(bound -> bound.replaceAllTypeParameterNames(replacement)).collect(toList());
        TypeParameterName replacedName = replacement.getOrDefault(name, name);
        return TypeParameter.of(replacedName, replaceUpperBounds);
    }

    public TypeParameterName getName() {
        return name;
    }

    public List<Type> getUpperBounds() {
        return upperBounds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TypeParameter that = (TypeParameter) o;
        return Objects.equals(getName(), that.getName()) && Objects.equals(getUpperBounds(), that.getUpperBounds());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getUpperBounds());
    }

    @Override
    public String toString() {
        return "TypeParameter{" +
                "name=" + name +
                ", upperBounds=" + upperBounds +
                '}';
    }
}
