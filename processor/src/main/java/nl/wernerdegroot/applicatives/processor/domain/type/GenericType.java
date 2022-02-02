package nl.wernerdegroot.applicatives.processor.domain.type;

import nl.wernerdegroot.applicatives.processor.domain.TypeParameterName;
import nl.wernerdegroot.applicatives.processor.domain.typeconstructor.GenericTypeConstructor;
import nl.wernerdegroot.applicatives.processor.domain.typeconstructor.TypeConstructor;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public class GenericType implements Type {

    private final TypeParameterName name;

    public GenericType(TypeParameterName name) {
        this.name = name;
    }

    public static GenericType of(TypeParameterName name) {
        return new GenericType(name);
    }

    @Override
    public <R> R match(Function<GenericType, R> matchGeneric, Function<ConcreteType, R> matchConcrete, Function<WildcardType, R> matchWildcard, Function<ArrayType, R> matchArray) {
        return matchGeneric.apply(this);
    }

    @Override
    public GenericTypeConstructor asTypeConstructor() {
        return TypeConstructor.generic(name);
    }

    @Override
    public TypeConstructor asTypeConstructorWithPlaceholderFor(TypeParameterName needle) {
        if (Objects.equals(name, needle)) {
            return TypeConstructor.placeholder();
        } else {
            return TypeConstructor.generic(name);
        }
    }

    @Override
    public boolean contains(TypeParameterName needle) {
        return Objects.equals(name, needle);
    }

    @Override
    public GenericType replaceAllTypeParameterNames(Map<TypeParameterName, TypeParameterName> replacement) {
        return Type.generic(replacement.getOrDefault(name, name));
    }

    public TypeParameterName getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GenericType that = (GenericType) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "GenericType{" +
                "name=" + name +
                '}';
    }
}
