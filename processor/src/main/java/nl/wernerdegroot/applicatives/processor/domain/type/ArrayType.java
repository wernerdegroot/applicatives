package nl.wernerdegroot.applicatives.processor.domain.type;

import nl.wernerdegroot.applicatives.processor.domain.TypeParameterName;
import nl.wernerdegroot.applicatives.processor.domain.typeconstructor.ArrayTypeConstructor;
import nl.wernerdegroot.applicatives.processor.domain.typeconstructor.TypeConstructor;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * Represent a Java array.
 */
public class ArrayType implements Type {

    // The type of the elements of the array:
    private final Type type;

    public ArrayType(Type type) {
        this.type = type;
    }

    public static ArrayType of(Type type) {
        return new ArrayType(type);
    }

    @Override
    public <R> R match(Function<GenericType, R> matchGeneric, Function<ConcreteType, R> matchConcrete, Function<WildcardType, R> matchWildcard, Function<ArrayType, R> matchArray) {
        return matchArray.apply(this);
    }

    @Override
    public ArrayTypeConstructor asTypeConstructor() {
        return TypeConstructor.array(type.asTypeConstructor());
    }

    @Override
    public TypeConstructor asTypeConstructorWithPlaceholderFor(TypeParameterName needle) {
        return TypeConstructor.array(type.asTypeConstructorWithPlaceholderFor(needle));
    }

    @Override
    public boolean contains(TypeParameterName needle) {
        return type.contains(needle);
    }

    @Override
    public ArrayType replaceAllTypeParameterNames(Map<TypeParameterName, TypeParameterName> replacement) {
        return Type.array(type.replaceAllTypeParameterNames(replacement));
    }

    public Type getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArrayType arrayType = (ArrayType) o;
        return getType().equals(arrayType.getType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getType());
    }

    @Override
    public String toString() {
        return "ArrayType{" +
                "type=" + type +
                '}';
    }
}
