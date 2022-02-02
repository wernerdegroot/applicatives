package nl.wernerdegroot.applicatives.processor.domain.typeconstructor;

import nl.wernerdegroot.applicatives.processor.domain.TypeParameterName;
import nl.wernerdegroot.applicatives.processor.domain.type.GenericType;
import nl.wernerdegroot.applicatives.processor.domain.type.Type;

import java.util.Map;
import java.util.Objects;

public class GenericTypeConstructor implements TypeConstructor {

    private final TypeParameterName name;

    public GenericTypeConstructor(TypeParameterName name) {
        this.name = name;
    }

    public static GenericTypeConstructor of(TypeParameterName name) {
        return new GenericTypeConstructor(name);
    }

    @Override
    public GenericTypeConstructor replaceAllTypeParameterNames(Map<TypeParameterName, TypeParameterName> replacement) {
        return TypeConstructor.generic(replacement.getOrDefault(name, name));
    }

    @Override
    public TypeConstructor replaceAll(TypeConstructor needle, TypeConstructor replacement) {
        if (Objects.equals(this, needle)) {
            return replacement;
        }

        return this;
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
        return getName().equals(that.getName());
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
