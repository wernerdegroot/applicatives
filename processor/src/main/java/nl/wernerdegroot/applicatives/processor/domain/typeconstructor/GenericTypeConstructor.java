package nl.wernerdegroot.applicatives.processor.domain.typeconstructor;

import nl.wernerdegroot.applicatives.processor.domain.TypeParameterName;
import nl.wernerdegroot.applicatives.processor.domain.type.GenericType;
import nl.wernerdegroot.applicatives.processor.domain.type.Type;

import java.util.Map;
import java.util.Objects;

public final class GenericTypeConstructor implements TypeConstructor {

    private final TypeParameterName name;

    public GenericTypeConstructor(TypeParameterName name) {
        this.name = name;
    }

    public static GenericTypeConstructor of(TypeParameterName name) {
        return new GenericTypeConstructor(name);
    }

    @Override
    public GenericTypeConstructor replaceTypeParameterNames(Map<TypeParameterName, TypeParameterName> replacement) {
        return TypeConstructor.generic(replacement.getOrDefault(name, name));
    }

    @Override
    public boolean referencesTypeParameter(TypeParameterName typeParameterName) {
        return Objects.equals(name, typeParameterName);
    }

    @Override
    public boolean canAccept(TypeConstructor that) {
        return this.equals(that);
    }

    @Override
    public GenericType apply(Type toApplyTo) {
        return Type.generic(name);
    }

    public TypeParameterName getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GenericTypeConstructor that = (GenericTypeConstructor) o;
        return Objects.equals(getName(), that.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }

    @Override
    public String toString() {
        return "GenericTypeConstructor{" +
                "name=" + name +
                '}';
    }
}
