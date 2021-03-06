package nl.wernerdegroot.applicatives.processor.domain.typeconstructor;

import nl.wernerdegroot.applicatives.processor.domain.TypeParameterName;
import nl.wernerdegroot.applicatives.processor.domain.type.ArrayType;
import nl.wernerdegroot.applicatives.processor.domain.type.Type;

import java.util.Map;
import java.util.Objects;

public final class ArrayTypeConstructor implements TypeConstructor {

    private final TypeConstructor type;

    public ArrayTypeConstructor(TypeConstructor type) {
        this.type = type;
    }

    public static ArrayTypeConstructor of(TypeConstructor type) {
        return new ArrayTypeConstructor(type);
    }

    @Override
    public ArrayTypeConstructor replaceTypeParameterNames(Map<TypeParameterName, TypeParameterName> replacement) {
        return TypeConstructor.array(type.replaceTypeParameterNames(replacement));
    }

    @Override
    public boolean referencesTypeParameter(TypeParameterName typeParameterName) {
        return type.referencesTypeParameter(typeParameterName);
    }

    @Override
    public boolean canAccept(TypeConstructor typeConstructor) {
        if (typeConstructor instanceof ArrayTypeConstructor) {
            ArrayTypeConstructor that = (ArrayTypeConstructor) typeConstructor;
            return this.type.canAccept(that.type);
        } else {
            return false;
        }
    }

    @Override
    public ArrayType apply(Type toApplyTo) {
        return Type.array(type.apply(toApplyTo));
    }

    public TypeConstructor getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArrayTypeConstructor that = (ArrayTypeConstructor) o;
        return Objects.equals(getType(), that.getType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getType());
    }

    @Override
    public String toString() {
        return "ArrayTypeConstructor{" +
                "type=" + type +
                '}';
    }
}
