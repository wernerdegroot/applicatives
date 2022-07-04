package nl.wernerdegroot.applicatives.processor.domain;

import nl.wernerdegroot.applicatives.processor.domain.type.ArrayType;
import nl.wernerdegroot.applicatives.processor.domain.type.GenericType;
import nl.wernerdegroot.applicatives.processor.domain.type.Type;
import nl.wernerdegroot.applicatives.processor.domain.type.TypeArgument;
import nl.wernerdegroot.applicatives.processor.domain.typeconstructor.TypeConstructor;

import java.util.Objects;

public final class TypeParameterName {

    private final String typeParameterName;

    public TypeParameterName(String typeParameterName) {
        this.typeParameterName = typeParameterName;
    }

    public static TypeParameterName of(String typeParameterName) {
        return new TypeParameterName(typeParameterName);
    }

    public GenericType asType() {
        return Type.generic(this);
    }

    public TypeParameter extending(Type... types) {
        return TypeParameter.of(this, types);
    }

    public TypeParameter asTypeParameter() {
        return TypeParameter.of(this);
    }

    public TypeConstructor asTypeConstructor() {
        return TypeConstructor.generic(this);
    }

    public TypeArgument covariant() {
        return asType().covariant();
    }

    public TypeArgument contravariant() {
        return asType().contravariant();
    }

    public String raw() {
        return typeParameterName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TypeParameterName that = (TypeParameterName) o;
        return Objects.equals(typeParameterName, that.typeParameterName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(raw());
    }

    @Override
    public String toString() {
        return "TypeParameterName{" +
                "name='" + typeParameterName + '\'' +
                '}';
    }
}
