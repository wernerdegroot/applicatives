package nl.wernerdegroot.applicatives.processor.domain.typeconstructor;

import nl.wernerdegroot.applicatives.processor.domain.TypeParameterName;
import nl.wernerdegroot.applicatives.processor.domain.type.Type;

import java.util.Map;
import java.util.Objects;

public class PlaceholderTypeConstructor implements TypeConstructor {

    @Override
    public PlaceholderTypeConstructor replaceAllTypeParameterNames(Map<TypeParameterName, TypeParameterName> replacement) {
        return this;
    }

    @Override
    public TypeConstructor replaceAll(TypeConstructor needle, TypeConstructor replacement) {
        if (Objects.equals(this, needle)) {
            return replacement;
        }

        return this;
    }

    @Override
    public Type apply(Type toApplyTo) {
        return toApplyTo;
    }

    @Override
    public boolean equals(Object that) {
        return that instanceof PlaceholderTypeConstructor;
    }

    @Override
    public int hashCode() {
        return 7;
    }

    @Override
    public String toString() {
        return "PlaceholderTypeConstructor{}";
    }
}
